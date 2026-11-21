package domain;

import java.util.Random;

/**
 * A match of the tournament bracket: which two teams play it, in which round, and how it ended.
 * It is not played on screen: the match of the player gets the result of the real {@link Match},
 * and the matches between two machines are simulated.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class BracketMatch {
    /** Round of the tournament it belongs to, from 1 (quarterfinals). */
    private final int currentRound;
    /** Team on the left (the player, in the matches of the player). */
    private final GameCharacter leftCharacter;
    /** Team on the right. */
    private final GameCharacter rightCharacter;
    /** Goals of the left team. */
    private int leftGoals = 0;
    /** Goals of the right team. */
    private int rightGoals = 0;
    /** Whether it already has a result. */
    private boolean played;

    /**
     * Creates a match of the bracket, still without a result.
     *
     * @param round          round of the tournament, from 1 (quarterfinals)
     * @param leftCharacter  team on the left
     * @param rightCharacter team on the right
     */
    public BracketMatch(
        int round,
        GameCharacter leftCharacter,
        GameCharacter rightCharacter
    ) {
        this.leftCharacter = leftCharacter;
        this.rightCharacter = rightCharacter;
        this.currentRound = round;
    }

    /**
     * Saves the result of the match and marks it as played.
     *
     * @param leftGoals  goals of the left team
     * @param rightGoals goals of the right team
     */
    public void setResult(
        int leftGoals,
        int rightGoals
    ) {
        this.leftGoals = leftGoals;
        this.rightGoals = rightGoals;
        this.played = true;
    }

    /**
     * Gives a random result to a match between two machines (from 0 to 3 goals each).
     * It never ends in a tie: on a tie, one more goal goes to a random side.
     *
     * @param random decides the goals
     */
    public void simulate(Random random) {
        int leftIA = random.nextInt(4);
        int rightIA = random.nextInt(4);

        if(leftIA == rightIA) {
            if(random.nextBoolean()) {
                leftIA++;
            } else {
                rightIA++;
            }
        }

        setResult(leftIA, rightIA);
    }

    /**
     * Returns the team that won the match.
     *
     * @return the winner, or null if it was not played yet or it is a tie
     */
    public GameCharacter getWinner() {
        if(!played || leftGoals == rightGoals) {
            return null;
        }
        return leftGoals > rightGoals ? leftCharacter : rightCharacter;
    }

    /**
     * Tells whether a team plays this match.
     *
     * @param character the team
     * @return true if it is the left or the right team
     */
    public boolean includes(GameCharacter character) {
        return leftCharacter == character || rightCharacter == character;
    }

    /**
     * Returns the round of the tournament it belongs to.
     *
     * @return 1 (quarterfinals), 2 (semifinal) or 3 (final)
     */
    public int getRound() {
        return currentRound;
    }

    /**
     * Returns the name of its round, as the screens show it.
     *
     * @return "CUARTOS", "SEMIFINAL" or "FINAL"
     */
    public String getDisplayRound() {
        return TournamentMode.ROUND_NAMES[currentRound - 1];
    }

    /**
     * Returns the team on the left.
     *
     * @return the left team
     */
    public GameCharacter getLeftCharacter() {
        return leftCharacter;
    }

    /**
     * Returns the team on the right.
     *
     * @return the right team
     */
    public GameCharacter getRightCharacter() {
        return rightCharacter;
    }

    /**
     * Tells whether the match already has a result.
     *
     * @return true after {@link #setResult} or {@link #simulate}
     */
    public boolean isPlayed() {
        return played;
    }
}
