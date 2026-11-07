package domain;

/**
 * Error of the game: an invalid choice, an action that cannot be done at that moment,
 * or an option that is still under construction in this mock-up.
 * Every message is a constant written in Spanish, so the interface can show it to the user as it is.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class DOPOSoccerException extends Exception {

    /** The option exists in the design but is not built in this version. */
    public static final String UNDER_CONSTRUCTION = "Opción en construcción.";
    /** The modality is not "PVP" or "PVM". */
    public static final String UNKNOWN_MODALITY = "La modalidad no es jugador vs jugador ni jugador vs máquina.";
    /** The game mode is not "ARCADE" or "TOURNAMENT". */
    public static final String UNKNOWN_MODE = "El modo de juego no es Arcade ni Torneo.";
    /** The match type is not "TIME", "GOALS" or "SURVIVAL". */
    public static final String UNKNOWN_MATCH_TYPE = "El tipo de partido no es por tiempo, por goles ni supervivencia.";
    /** The resolution is not "SMALL", "MEDIUM" or "LARGE". */
    public static final String UNKNOWN_RESOLUTION = "La resolución no es pequeña, mediana ni grande.";
    /** No character has that country. */
    public static final String UNKNOWN_CHARACTER = "El personaje no existe.";
    /** No ball of the catalog has that name. */
    public static final String UNKNOWN_BALL = "El balón no existe.";
    /** The side is not "LEFT" or "RIGHT". */
    public static final String UNKNOWN_SIDE = "El lado no es izquierdo ni derecho.";
    /** The minutes of a time match are 0 or less. */
    public static final String INVALID_MINUTES = "Los minutos del partido deben ser mayores que 0.";
    /** The goal target is 0 or less. */
    public static final String INVALID_GOAL_TARGET = "La meta de goles debe ser mayor que 0.";
    /** There is no game being played. */
    public static final String GAME_NOT_STARTED = "No hay ningún partido en curso.";
    /** The action does not make sense right now. */
    public static final String INVALID_STATE = "Esa acción no se puede hacer en este momento.";
    /** The match already ended. */
    public static final String GAME_OVER = "El juego ya terminó.";
    /** The stat is not "SPEED", "JUMP", "KICK" or "SURVIVAL". */
    public static final String UNKNOWN_STAT = "La estadística no es speed, jump, kick ni survival.";
    /** The stat is already at its maximum. */
    public static final String STAT_AT_MAX = "Esa estadística ya está en 10.";

    /**
     * Creates the exception with one of the message constants.
     *
     * @param message one of the constants of this class
     */
    public DOPOSoccerException(String message) {
        super(message);
    }

    /**
     * Creates the exception with one of the message constants and the error that caused it,
     * so the error log keeps the original trace.
     *
     * @param message one of the constants of this class
     * @param cause   the original error
     */
    public DOPOSoccerException(String message, Throwable cause) {
        super(message, cause);
    }
}
