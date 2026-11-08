package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tournament of 8 teams: quarterfinals, semifinal and final against the machine.
 * Under construction: creating it throws {@link DOPOSoccerException#UNDER_CONSTRUCTION},
 * so its rules are never reached.
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

    /** Teams still in the tournament; the first one is the player. */
    private ArrayList<GameCharacter> alive;
    /** Current round, from 1. */
    private int round;
    /** Decides the matches between the other teams. */
    private Random random;

    /**
     * Creates the tournament. Under construction.
     *
     * @param leftCharacter   character of the player
     * @param leftStats       stats of the player
     * @param leftController  who controls the player
     * @param rightStats      stats of the rivals
     * @param rightController who controls the rivals
     * @param type            rule of each match
     * @param resolution      "SMALL", "MEDIUM" or "LARGE"
     * @param skin            ball chosen to play
     * @param others          the other 7 teams
     * @param random          decides the matches between the other teams
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public TournamentMode(GameCharacter leftCharacter, Stats leftStats, PlayerController leftController,
                          Stats rightStats, PlayerController rightController, MatchType type, String resolution,
                          BallSkin skin, List<GameCharacter> others, Random random) throws DOPOSoccerException {
        super(leftCharacter, leftStats, leftController, null, rightStats, rightController, type, resolution, skin);
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Starts the quarterfinal. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    @Override
    public void start() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Goes to the next round. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    @Override
    public void nextMatch() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the player won the current round. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public boolean hasWonRound() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Under construction: never reached, the constructor throws.
     *
     * @return false
     */
    @Override
    public boolean isOver() {
        return false;
    }

    /**
     * Tells whether the player won the final. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public boolean isChampion() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the teams still in the tournament. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public List<String> getTeams() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the name of the current round. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public String getRoundName() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the prize of the tournament. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getPrize() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Under construction: never reached, the constructor throws.
     *
     * @return the current round
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
        return "TOURNAMENT";
    }
}
