package domain;

import java.io.Serializable;

/**
 * Rule that says when a match ends and who wins (Strategy pattern).
 * The match is always the same; only this rule changes.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public abstract class MatchType implements Serializable {

    /**
     * Creates the rule. Subclasses call it with super().
     */
    public MatchType() {
        // No common state.
    }

    /**
     * Notice of a goal. By default nothing.
     *
     * @param scorer "LEFT" or "RIGHT": the side that scored
     * @param match  the match being played
     */
    public void onGoal(String scorer, Match match) {
        // Most rules only look at the scoreboard.
    }

    /**
     * Tells whether the match ended.
     *
     * @param match the match being played
     * @return true if it ended
     */
    public abstract boolean isMatchOver(Match match);

    /**
     * Returns the winner.
     *
     * @param match the match being played
     * @return "LEFT", "RIGHT" or null if nobody has won
     */
    public abstract String getWinner(Match match);

    /**
     * Returns the seconds left. By default there is no time.
     *
     * @param match the match being played
     * @return seconds left, or -1 if the match has no time limit
     */
    public int getTimeLeft(Match match) {
        return -1;
    }

    /**
     * Tells whether the match is in sudden death. By default false.
     *
     * @return true in sudden death
     */
    public boolean isSuddenDeath() {
        return false;
    }

    /**
     * Leaves the rule ready for another match. By default nothing.
     */
    public void reset() {
        // Rules without state have nothing to reset.
    }

    /**
     * Returns the name of the rule.
     *
     * @return "TIME" or "GOALS"
     */
    public abstract String getName();
}
