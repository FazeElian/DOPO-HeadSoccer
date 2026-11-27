package domain.enums;

/**
 * Kind of match: by time, by goals or survival.
 * Each value keeps the text that the domain and the interface send and compare.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public enum MatchKind {
    /** "TIME". */
    TIME("TIME"),
    /** "GOALS". */
    GOALS("GOALS"),
    /** "SURVIVAL". */
    SURVIVAL("SURVIVAL");

    /** Text of the value. */
    private final String name;

    /**
     * Creates the value with its text.
     *
     * @param name text of the value
     */
    MatchKind(String name) {
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
