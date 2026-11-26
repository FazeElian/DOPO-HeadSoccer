package domain;

import domain.enums.AIProfile;
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

    @Override
    public String getProfile() {
        return AIProfile.DEFENSIVE.getName();
    }
}
