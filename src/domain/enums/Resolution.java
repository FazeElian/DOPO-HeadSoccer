package domain.enums;

/**
 * Resolution of the field.
 * Each value keeps the text that the domain and the interface send and compare.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public enum Resolution {
    /** "SMALL". */
    SMALL("SMALL"),
    /** "MEDIUM". */
    MEDIUM("MEDIUM"),
    /** "LARGE". */
    LARGE("LARGE");

    /** Text of the value. */
    private final String name;

    /**
     * Creates the value with its text.
     *
     * @param name text of the value
     */
    Resolution(String name) {
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
