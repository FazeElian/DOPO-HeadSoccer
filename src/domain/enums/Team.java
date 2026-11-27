package domain.enums;

/**
 * Team (country) of a character.
 * Each value keeps the text that the domain and the interface send and compare.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public enum Team {
    /** "Colombia". */
    COLOMBIA("Colombia"),
    /** "Argentina". */
    ARGENTINA("Argentina"),
    /** "Corea del Sur". */
    COREA_DEL_SUR("Corea del Sur"),
    /** "Camerún". */
    CAMERUN("Camerún"),
    /** "Nigeria". */
    NIGERIA("Nigeria"),
    /** "Estados Unidos". */
    ESTADOS_UNIDOS("Estados Unidos"),
    /** "España". */
    ESPANA("España"),
    /** "Japón". */
    JAPON("Japón");

    /** Text of the value. */
    private final String name;

    /**
     * Creates the value with its text.
     *
     * @param name text of the value
     */
    Team(String name) {
        this.name = name;
    }

    /**
     * Returns the text of the value.
     *
     * @return the name of the country, as the domain uses it
     */
    public String getName() {
        return name;
    }
}
