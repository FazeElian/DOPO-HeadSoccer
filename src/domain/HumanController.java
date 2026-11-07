package domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller of a person: it remembers which action keys are pressed.
 * The interface translates the keys and calls the facade, which calls {@link #press} and {@link #release}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class HumanController implements PlayerController {

    /** Action keys being pressed. */
    private final HashSet<String> pressed;

    /**
     * Creates a controller with no key pressed.
     */
    public HumanController() {
        this.pressed = new HashSet<>();
    }

    /**
     * Marks an action key as pressed.
     *
     * @param action "MOVE_LEFT", "MOVE_RIGHT", "JUMP", "KICK" or "POWER"
     */
    @Override
    public void press(String action) {
        pressed.add(action);
    }

    /**
     * Marks an action key as released.
     *
     * @param action the released action
     */
    @Override
    public void release(String action) {
        pressed.remove(action);
    }

    /**
     * Returns the pressed actions.
     *
     * @param self  the player being controlled
     * @param match the match being played
     * @return a copy of the pressed actions
     */
    @Override
    public Set<String> nextActions(Player self, Match match) {
        return new HashSet<>(pressed);
    }
}
