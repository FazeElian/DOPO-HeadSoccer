package domain;

import java.io.Serializable;
import java.util.Set;

/**
 * Who decides the actions of a player: a person with the keyboard or the machine (Strategy pattern).
 * The player does not know which one it has.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public interface PlayerController extends Serializable {

    /**
     * Returns the actions of this tick.
     *
     * @param self  the player being controlled
     * @param match the match being played
     * @return a set with "MOVE_LEFT", "MOVE_RIGHT", "JUMP", "KICK" and/or "POWER"
     */
    Set<String> nextActions(Player self, Match match);

    /**
     * An action key was pressed. By default it is ignored (the machine does not use the keyboard).
     *
     * @param action "MOVE_LEFT", "MOVE_RIGHT", "JUMP", "KICK" or "POWER"
     */
    default void press(String action) {
        // Only a person reacts to the keyboard.
    }

    /**
     * An action key was released. By default it is ignored.
     *
     * @param action the released action
     */
    default void release(String action) {
        // Only a person reacts to the keyboard.
    }
}
