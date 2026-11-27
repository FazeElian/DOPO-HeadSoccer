package domain.enums;

/**
 * Profile of a player moved by the machine.
 * Each value keeps the text that the domain and the interface send and compare.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public enum AIProfile {
    /** "CLUMSY". */
    CLUMSY("CLUMSY"),
    /** "OFFENSIVE". */
    OFFENSIVE("OFFENSIVE"),
    /** "DEFENSIVE". */
    DEFENSIVE("DEFENSIVE"),
    /** "EXPERT". */
    EXPERT("EXPERT");

    /** Text of the value. */
    private final String name;

    /**
     * Creates the value with its text.
     *
     * @param name text of the value
     */
    AIProfile(String name) {
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
