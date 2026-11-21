package domain;

/**
 * Survival: each player starts with 3 balls and loses one for each goal received;
 * the match ends when a player has no balls left. It is a series against the machine:
 * the left player keeps its balls from one match to the next, and the rival starts
 * every match with 3. The series ends when the left player has no balls left.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class SurvivalMatch extends MatchType {

    /** Balls of each player at the start. */
    public static final int INITIAL_BALLS = 3;

    /** Balls left of the left player (they are kept for the whole series). */
    private int leftBalls;
    /** Balls left of the right player (3 again in every match). */
    private int rightBalls;

    /**
     * Creates the rule with 3 balls for each player.
     */
    public SurvivalMatch() {
        super();
        this.leftBalls = INITIAL_BALLS;
        this.rightBalls = INITIAL_BALLS;
    }

    /**
     * The player that received the goal loses a ball.
     *
     * @param scorer "LEFT" or "RIGHT": the side that scored
     * @param match  the match being played
     */
    @Override
    public void onGoal(String scorer, Match match) {
        if ("LEFT".equals(scorer)) {
            rightBalls = Math.max(0, rightBalls - 1);
        } else if ("RIGHT".equals(scorer)) {
            leftBalls = Math.max(0, leftBalls - 1);
        }
    }

    /**
     * The match ends when a player has no balls left.
     *
     * @param match the match being played
     * @return true if a player has 0 balls
     */
    @Override
    public boolean isMatchOver(Match match) {
        return leftBalls == 0 || rightBalls == 0;
    }

    /**
     * The winner is the player that still has balls.
     *
     * @param match the match being played
     * @return "LEFT", "RIGHT" or null if both still have balls
     */
    @Override
    public String getWinner(Match match) {
        if (leftBalls == 0) {
            return "RIGHT";
        }
        return rightBalls == 0 ? "LEFT" : null;
    }

    /**
     * Returns the balls left of a player.
     *
     * @param side "LEFT" or "RIGHT"
     * @return the balls left
     * @throws DOPOSoccerException UNKNOWN_SIDE if the side is not LEFT or RIGHT
     */
    @Override
    public int getBalls(String side) throws DOPOSoccerException {
        if ("LEFT".equals(side)) {
            return leftBalls;
        }
        if ("RIGHT".equals(side)) {
            return rightBalls;
        }
        throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_SIDE);
    }

    /**
     * The series continues while the left player wins.
     *
     * @param winner the winner of the match that ended
     * @return true if the left player won
     */
    @Override
    public boolean continuesAfter(String winner) {
        return "LEFT".equals(winner);
    }

    /**
     * Gets the rule ready for the next match of the series: the rival gets 3 balls again,
     * the left player keeps the balls it has.
     */
    @Override
    public void reset() {
        rightBalls = INITIAL_BALLS;
    }

    /**
     * Returns the name of the rule.
     *
     * @return "SURVIVAL"
     */
    @Override
    public String getName() {
        return "SURVIVAL";
    }
}