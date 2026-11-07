package domain;

/**
 * Tornado, the shot of Nigeria: a whirlwind close to the ground that bounces fast.
 * If it hits the rival, the rival is thrown up and stays unconscious.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class TornadoShot implements PowerShot {

    /** Name of the shot. */
    public static final String NAME = "Tornado";
    /** Damage before the kick multiplier. */
    public static final int BASE_DAMAGE = 40;
    /** Horizontal speed of the ball, in pixels per second. */
    public static final double SPEED = 900;
    /** Downward speed that makes it bounce on the ground, in pixels per second. */
    public static final double BOUNCE_SPEED = 350;

    /**
     * Creates the shot.
     */
    public TornadoShot() {
        // No state: every value is a constant.
    }

    /**
     * Returns the name of the shot.
     *
     * @return "Tornado"
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Returns the base damage.
     *
     * @return 40
     */
    @Override
    public int getBaseDamage() {
        return BASE_DAMAGE;
    }

    /**
     * Creates the effect for the rival.
     *
     * @return a new {@link UnconsciousEffect}
     */
    @Override
    public Effect createEffect() {
        return new UnconsciousEffect();
    }

    /**
     * Shoots the ball low and downwards, so it bounces along the ground towards the rival goal.
     *
     * @param match   the match being played
     * @param shooter player who shoots
     */
    @Override
    public void launch(Match match, Player shooter) {
        match.getBall().launchPowerShot(this, shooter.getSide(), shooter.getDirection() * SPEED, BOUNCE_SPEED);
    }

    /**
     * The shot falls and bounces like a normal ball.
     *
     * @return false
     */
    @Override
    public boolean ignoresGravity() {
        return false;
    }
}
