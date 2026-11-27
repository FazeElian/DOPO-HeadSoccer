package domain.enums;

/**
 * Event of a match that the interface plays as a sound.
 * Each value keeps the text that the domain and the interface send and compare.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public enum MatchEvent {
    /** "KICK". */
    KICK("KICK"),
    /** "POST". */
    POST("POST"),
    /** "GOAL". */
    GOAL("GOAL");

    /** Text of the value. */
    private final String name;

    /**
     * Creates the value with its text.
     *
     * @param name text of the value
     */
    MatchEvent(String name) {
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
