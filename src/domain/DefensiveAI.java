package domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Machine that guards its goal: it only goes for the ball when the ball is in its half.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class DefensiveAI extends AIController {

    /** Distance from its goal, in pixels, where it waits. */
    public static final double GUARD_DISTANCE = 60;

    /**
     * Creates the defensive profile.
     */
    public DefensiveAI() {
        super();
    }

    @Override
    protected Set<String> decide(Player self, Match match) {
        Set<String> actions = new HashSet<>();
        if (isBallInOwnHalf(self, match)) {
            playBall(self, match.getBall(), actions);
        } else {
            guard(self, match, actions);
        }
        return actions;
    }

    /**
     * Goes back to wait {@link #GUARD_DISTANCE} pixels in front of its own goal.
     * The expert uses it too when the rival has the ball.
     *
     * @param self    the player
     * @param match   the match
     * @param actions where the movement is added
     */
    protected static void guard(Player self, Match match, Set<String> actions) {
        double[] goal = match.getGoalBounds(self.getSide()); // {x, y, width, height}
        double spot = "LEFT".equals(self.getSide())
                ? goal[0] + goal[2] + GUARD_DISTANCE
                : goal[0] - GUARD_DISTANCE;
        moveTo(self, spot, actions);
    }

    @Override
    public String getProfile() {
        return "DEFENSIVE";
    }
}
