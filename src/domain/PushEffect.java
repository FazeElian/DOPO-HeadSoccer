package domain;

/**
 * Push, from Blue Aura (South Korea): the rival is thrown towards its own goal
 * and cannot control itself for 1.5 seconds.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class PushEffect extends Effect {

    /** Name shown on screen. */
    public static final String NAME = "Empuje";
    /** Seconds it lasts. */
    public static final double DURATION = 1.5;
    /** Horizontal speed of the push, in pixels per second. */
    public static final double PUSH_SPEED = 900;
    /** Small lift so the push does not stick to the ground, in pixels per second. */
    public static final double LIFT_SPEED = 250;

    /**
     * Creates the push.
     */
    public PushEffect() {
        super(DURATION);
    }

    /**
     * Throws the player towards the goal it defends.
     *
     * @param p the affected player
     */
    @Override
    public void onApply(Player p) {
        double towardsOwnGoal = "LEFT".equals(p.getSide()) ? -1 : 1;
        p.setVelocity(towardsOwnGoal * PUSH_SPEED, -LIFT_SPEED);
        p.setOnGround(false);
    }

    /**
     * While being pushed the player does not control itself.
     *
     * @return false
     */
    @Override
    public boolean canAct() {
        return false;
    }

    /**
     * Returns the name shown on screen.
     *
     * @return "Empuje"
     */
    @Override
    public String getName() {
        return NAME;
    }
}
