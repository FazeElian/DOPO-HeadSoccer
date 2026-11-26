package domain.enums;

/**
 * Action that a controller asks a player to do.
 * Each value keeps the text that the domain and the interface send and compare.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public enum Action {
    /** "MOVE_LEFT". */
    MOVE_LEFT("MOVE_LEFT"),
    /** "MOVE_RIGHT". */
    MOVE_RIGHT("MOVE_RIGHT"),
    /** "JUMP". */
    JUMP("JUMP"),
    /** "KICK". */
    KICK("KICK"),
    /** "POWER". */
    POWER("POWER");

    /** Text of the value. */
    private final String name;

    /**
     * Creates the value with its text.
     *
     * @param name text of the value
     */
    Action(String name) {
        this.name = name;
    }

    /**
     * Returns the text of the value.
     *
     * @return the text, the same as the name of the constant
     */
    public String getName() {
        return name;
    }
}
