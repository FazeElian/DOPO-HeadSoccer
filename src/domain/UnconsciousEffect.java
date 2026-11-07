package domain;

/**
 * Unconscious, from Tornado (Nigeria): the rival is thrown up in the air and for 3 seconds
 * cannot move, jump or kick.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class UnconsciousEffect extends Effect {

    /** Name shown on screen. */
    public static final String NAME = "Inconsciente";
    /** Seconds it lasts. */
    public static final double DURATION = 3;
    /** Upward speed it is thrown with, in pixels per second. */
    public static final double LAUNCH_SPEED = 750;

    /**
     * Creates the effect.
     */
    public UnconsciousEffect() {
        super(DURATION);
    }

    /**
     * Throws the player up in the air.
     *
     * @param p the affected player
     */
    @Override
    public void onApply(Player p) {
        p.setVelocity(p.getVx(), -LAUNCH_SPEED);
        p.setOnGround(false);
    }

    /**
     * An unconscious player cannot move or kick.
     *
     * @return false
     */
    @Override
    public boolean canAct() {
        return false;
    }

    /**
     * An unconscious player cannot jump.
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
     * @return "Inconsciente"
     */
    @Override
    public String getName() {
        return NAME;
    }
}
