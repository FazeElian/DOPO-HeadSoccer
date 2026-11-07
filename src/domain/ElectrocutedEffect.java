package domain;

/**
 * Electrocuted, from Lightning (Cameroon): for 3 seconds the rival cannot jump
 * and runs at 50 % of its speed.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ElectrocutedEffect extends Effect {

    /** Name shown on screen. */
    public static final String NAME = "Electrocutado";
    /** Seconds it lasts. */
    public static final double DURATION = 3;
    /** Share of the running speed the player keeps. */
    public static final double SPEED_FACTOR = 0.5;

    /**
     * Creates the effect.
     */
    public ElectrocutedEffect() {
        super(DURATION);
    }

    /**
     * An electrocuted player cannot jump.
     *
     * @return false
     */
    @Override
    public boolean canJump() {
        return false;
    }

    /**
     * An electrocuted player runs at half speed.
     *
     * @return {@link #SPEED_FACTOR}
     */
    @Override
    public double getSpeedFactor() {
        return SPEED_FACTOR;
    }

    /**
     * Returns the name shown on screen.
     *
     * @return "Electrocutado"
     */
    @Override
    public String getName() {
        return NAME;
    }
}
