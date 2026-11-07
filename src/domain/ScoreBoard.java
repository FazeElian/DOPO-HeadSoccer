package domain;

import java.io.Serializable;

/**
 * Goals of each side during a match.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ScoreBoard implements Serializable {

    /** Goals of the left side. */
    private int leftGoals;
    /** Goals of the right side. */
    private int rightGoals;

    /**
     * Creates a scoreboard at 0 - 0.
     */
    public ScoreBoard() {
        reset();
    }

    /**
     * Adds a goal to a side. Unknown sides are ignored.
     *
     * @param side "LEFT" or "RIGHT": the side that scored
     */
    public void addGoal(String side) {
        if ("LEFT".equals(side)) {
            leftGoals++;
        } else if ("RIGHT".equals(side)) {
            rightGoals++;
        }
    }

    /**
     * Returns the goals of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return its goals, or 0 for an unknown side
     */
    public int getGoals(String side) {
        if ("LEFT".equals(side)) {
            return leftGoals;
        }
        return "RIGHT".equals(side) ? rightGoals : 0;
    }

    /**
     * Tells whether both sides have the same goals.
     *
     * @return true if it is a tie
     */
    public boolean isTied() {
        return leftGoals == rightGoals;
    }

    /**
     * Puts the scoreboard back at 0 - 0.
     */
    public final void reset() {
        leftGoals = 0;
        rightGoals = 0;
    }
}
