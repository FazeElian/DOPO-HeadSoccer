package domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Machine that always goes for the ball to attack.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class OffensiveAI extends AIController {

    /**
     * Creates the offensive profile.
     */
    public OffensiveAI() {
        super();
    }

    @Override
    protected Set<String> decide(Player self, Match match) {
        Set<String> actions = new HashSet<>();
        playBall(self, match.getBall(), actions);
        return actions;
    }

    @Override
    public String getProfile() {
        return "OFFENSIVE";
    }
}
