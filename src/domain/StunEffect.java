package domain;

/**
 * Stunned: the life of the player reached 0 because of the rival's kicks or power shots.
 * For 3 seconds it cannot move, jump or kick; then its life goes back to the maximum.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class StunEffect extends Effect {

    /** Name shown on screen. */
    public static final String NAME = "Aturdido";
    /** Seconds it lasts. */
    public static final double DURATION = 3;

    /**
     * Creates the stun.
     */
    public StunEffect() {
        super(DURATION);
    }

    /**
     * A stunned player cannot move or kick.
     *
     * @return false
     */
    @Override
    public boolean canAct() {
        return false;
    }

    /**
     * A stunned player cannot jump.
     *
     * @return false
     */
    @Override
    public boolean canJump() {
        return false;
    }

    /**
     * Returns the name shown on screen.
     *
     * @return "Aturdido"
     */
    @Override
    public String getName() {
        return NAME;
    }
}
