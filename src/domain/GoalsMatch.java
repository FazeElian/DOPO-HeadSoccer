package domain;

/**
 * Match by goals: the first side to reach the target wins, with no time limit.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class GoalsMatch extends MatchType {

    /** Goals needed to win. */
    private final int target;

    /**
     * Creates a match by goals.
     *
     * @param target goals needed to win
     * @throws DOPOSoccerException INVALID_GOAL_TARGET if it is 0 or less
     */
    public GoalsMatch(int target) throws DOPOSoccerException {
        super();
        if (target <= 0) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_GOAL_TARGET);
        }
        this.target = target;
    }

    /**
     * Tells whether a side reached the target.
     *
     * @param match the match being played
     * @return true if it ended
     */
    @Override
    public boolean isMatchOver(Match match) {
        return getWinner(match) != null;
    }

    /**
     * Returns the side that reached the target.
     *
     * @param match the match being played
     * @return "LEFT", "RIGHT" or null if nobody has reached it
     */
    @Override
    public String getWinner(Match match) {
        ScoreBoard score = match.getScoreBoard();
        if (score.getGoals("LEFT") >= target) {
            return "LEFT";
        }
        return score.getGoals("RIGHT") >= target ? "RIGHT" : null;
    }

    /**
     * Returns the goal target.
     *
     * @return goals needed to win
     */
    public int getTarget() {
        return target;
    }

    /**
     * Returns the name of the rule.
     *
     * @return "GOALS"
     */
    @Override
    public String getName() {
        return "GOALS";
    }
}
