package domain;

/**
 * Confusion, from Illusion (United States): for 2.5 seconds the rival does not know which ball is the real one.
 * The confusion is visual (the fake balls look the same), so it does not block any action;
 * it exists to show the state on screen.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class IllusionEffect extends Effect {

    /** Name shown on screen. */
    public static final String NAME = "Confusión";
    /** Seconds it lasts. */
    public static final double DURATION = 2.5;

    /**
     * Creates the effect.
     */
    public IllusionEffect() {
        super(DURATION);
    }

    /**
     * Returns the name shown on screen.
     *
     * @return "Confusión"
     */
    @Override
    public String getName() {
        return NAME;
    }
}
