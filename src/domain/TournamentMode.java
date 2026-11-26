package domain;

import domain.enums.ModeType;
import domain.enums.Side;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Tournament of 8 teams against the machine: quarterfinals, semifinal and final.
 * The player plays its match on screen; the other matches of each round are simulated.
 * The player is always on the left of its {@link BracketMatch}, like in the {@link Match} on screen.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class TournamentMode extends GameMode {

    /** Points for the champion. */
    public static final int PRIZE = 500;
    /** Teams of the bracket. */
    public static final int TEAMS = 8;
    /** Names of the rounds, in order. */
    public static final String[] ROUND_NAMES = {"CUARTOS", "SEMIFINAL", "FINAL"};

    /** Teams that can be rivals of the player (every team but its own). */
    private final List<GameCharacter> others;
    /** Matches of every round played so far, in bracket order. */
    private final ArrayList<BracketMatch> bracket = new ArrayList<>();

    /** Current round, from 1. */
    private int round;
    /** Decides the matches between the other teams. */
    private final Random random;

    /**
     * Creates the tournament. The bracket is drawn in {@link #start()}.
     *
     * @param leftCharacter   character of the player
     * @param leftStats       stats of the player
     * @param leftController  who controls the player
     * @param rightStats      stats of the rivals
     * @param rightController who controls the rivals
     * @param type            rule of each match
     * @param resolution      "SMALL", "MEDIUM" or "LARGE"
     * @param skin            ball chosen to play
     * @param others          the teams that can be rivals (at least 7)
     * @param random          draws the bracket, the stadium of each match and the matches between the other teams
     * @throws DOPOSoccerException INVALID_STATE if there are less than 7 rivals
     */
    public TournamentMode(GameCharacter leftCharacter, Stats leftStats, PlayerController leftController,
                          Stats rightStats, PlayerController rightController, MatchType type, String resolution,
                          BallSkin skin, List<GameCharacter> others, Random random)
            throws DOPOSoccerException {
        // Each match draws its own stadium (randomStadium), so the mode keeps the default one
        super(leftCharacter, leftStats, leftController, null, rightStats, rightController, type, resolution, skin,
                STADIUMS.get(0));
        if(others.size() < TEAMS - 1) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_STATE);
        }
        this.others = new ArrayList<>(others);
        this.random = random;
    }

    /**
     * Draws the quarterfinals: 7 random rivals, the player against the first one and the other 6 in pairs.
     * Then creates the match of the player.
     *
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if the resolution is not valid
     */
    @Override
    public void start() throws DOPOSoccerException {
        bracket.clear();
        round = 1;

        List<GameCharacter> rivals = new ArrayList<>(others);
        Collections.shuffle(rivals, random);
        
        // add a rival to the player
        bracket.add(new BracketMatch(round, leftCharacter, rivals.get(0)));
        
        // Create bracket for every IA vs IA match
        for (int i = 1; i < TEAMS - 1; i += 2) {
            bracket.add(new BracketMatch(round, rivals.get(i), rivals.get(i + 1)));
        }

        match = createMatch(getRival(), randomStadium());
    }

    /**
     * Returns the teams of a round in bracket order: each 2 consecutive teams play each other.
     *
     * @param round 1 (quarterfinals), 2 (semifinal) or 3 (final)
     * @return the teams, empty if the round was not reached yet
     */
    public List<GameCharacter> getTeams(int round) {
        List<GameCharacter> teams = new ArrayList<>();
        for(BracketMatch b: matchesOf(round)) {
            teams.add(b.getLeftCharacter());
            teams.add(b.getRightCharacter());
        }
        return teams;
    }

    /**
     * Returns the rival of the player in the current round.
     *
     * @return the team on the right of the match of the player
     */
    public GameCharacter getRival() {
        return playerMatch().getRightCharacter();
    }

    /**
     * Returns the match of the player in the current round.
     *
     * @return its match, or null if the player is not in the round
     */
    private BracketMatch playerMatch() {
        for (BracketMatch b : matchesOf(round)) {
            if (b.includes(leftCharacter)) return b;
        }
        return null;
    }

    /**
     * Returns the matches of a round, in bracket order.
     *
     * @param round 1 (quarterfinals), 2 (semifinal) or 3 (final)
     * @return its matches, empty if the round was not reached yet
     */
    private List<BracketMatch> matchesOf(int round) {
        List<BracketMatch> list = new ArrayList<>();
        for (BracketMatch b : bracket) {
            if (b.getRound() == round) list.add(b);
        }
        return list;
    }

    /**
     * Returns how many matches of the bracket the player won.
     *
     * @return wins of the player, from 0 to 3
     */
    public int getWins() {
        int wins = 0;
        for (BracketMatch b : bracket) {
            if (b.includes(leftCharacter) && b.getWinner() == leftCharacter) wins++;
        }

        if (isChampion()) {
            wins++;
        }
        return wins;
    }

    /**
     * Goes to the next round: saves the result of the player, simulates the other matches of the round,
     * pairs the winners (the player on the left) and creates the next match of the player.
     *
     * @throws DOPOSoccerException INVALID_STATE if the current match is not over,
     *                             or GAME_OVER if the tournament already ended
     */
    @Override
    public void nextMatch() throws DOPOSoccerException {
        if(match == null || !match.isFinished()) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_STATE);
        }

        if(isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.GAME_OVER);
        }

        int leftGoals = match.getGoals(Side.LEFT.getName());
        int rightGoals = match.getGoals(Side.RIGHT.getName());
        playerMatch().setResult(leftGoals, rightGoals);

        // The matches between two machines are simulated (never a tie)
        for (BracketMatch b : matchesOf(round)) {
            if (!b.isPlayed()) {
                b.simulate(random);
            }
        }

        List<GameCharacter> winners = new ArrayList<>();
        for(BracketMatch b: matchesOf(round)) {
            winners.add(b.getWinner());
        }

        round++;

        // Winner of match 1 vs winner of match 2
        for(int i = 0; i < winners.size(); i += 2) {
            GameCharacter a = winners.get(i);
            GameCharacter b = winners.get(i + 1);
            if (b == leftCharacter) {
                b = a;
                a = leftCharacter;
            }
            bracket.add(new BracketMatch(round, a, b));
        }
        match = createMatch(getRival(), randomStadium());
    }

    /**
     * Tells whether the player won the current match.
     *
     * @return true if the match is over and the winner is the left side
     */
    public boolean playerWonMatch() {
        return match != null && match.isFinished() && Side.LEFT.getName().equals(match.getWinner());
    }

    /**
     * Tells whether the tournament ended: the player lost a match or won the final.
     * A match ended by hand has no winner, so it counts as lost.
     *
     * @return true if it ended
     */
    @Override
    public boolean isOver() {
        return match != null && match.isFinished() && (!playerWonMatch() || round == getRounds());
    }

    /**
     * Tells whether the player won the tournament.
     *
     * @return true if the current round is the final and the player won it
     */
    public boolean isChampion() {
        return round == getRounds() && playerWonMatch();
    }

    /**
     * Returns the name of the current round, as the screens show it.
     *
     * @return "CUARTOS", "SEMIFINAL" or "FINAL"
     */
    public String getRoundName() {
        return ROUND_NAMES[round - 1];
    }

    /**
     * Returns the extra points the player wins in this tournament: the prize, only for the champion.
     *
     * @return {@link #PRIZE} if the player is the champion, 0 if not
     */
    public int getPrize() {
        return isChampion() ? PRIZE : 0;
    }

    /**
     * Returns the current round.
     *
     * @return 1 (quarterfinals), 2 (semifinal) or 3 (final)
     */
    @Override
    public int getRound() {
        return round;
    }

    /**
     * Returns how many rounds the tournament has.
     *
     * @return 3
     */
    public int getRounds() {
        return ROUND_NAMES.length;
    }

    /**
     * Returns the name of the mode.
     *
     * @return "TOURNAMENT"
     */
    @Override
    public String getName() {
        return ModeType.TOURNAMENT.getName();
    }

    /**
     * Draws the stadium of a match: every match of the tournament is played in a random one.
     *
     * @return one of {@link #STADIUMS}
     */
    private String randomStadium() {
        return STADIUMS.get(random.nextInt(STADIUMS.size()));
    }
}
