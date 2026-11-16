package domain;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Machine that plays badly: sometimes it goes for the ball and sometimes it does anything.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ClumsyAI extends AIController {

    /** Chance of going for the ball in each decision. */
    public static final double FOCUS = 0.4;
    /** Things it does when it is not going for the ball. */
    private static final String[] MISTAKES = {"MOVE_LEFT", "MOVE_RIGHT", "JUMP", "KICK"};

    /** Source of its mistakes. */
    private final Random random;

    /**
     * Creates the clumsy profile.
     */
    public ClumsyAI() {
        super();
        random = new Random();
    }

    @Override
    protected Set<String> decide(Player self, Match match) {
        Set<String> actions = new HashSet<>();
        if (random.nextDouble() < FOCUS) {
            playBall(self, match.getBall(), actions);
        } else {
            actions.add(MISTAKES[random.nextInt(MISTAKES.length)]);
        }
        return actions;
    }

    @Override
    public String getProfile() {
        return "CLUMSY";
    }
}
