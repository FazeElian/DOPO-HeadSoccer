package domain;

/**
 * Illusion, the shot of the United States: the ball splits into 8 identical balls; 7 are fake
 * and disappear after {@link #DURATION} seconds. If the real one hits the rival, it gets confused.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class IllusionShot implements PowerShot {

    /** Name of the shot. */
    public static final String NAME = "Ilusión";
    /** Damage before the kick multiplier. */
    public static final int BASE_DAMAGE = 30;
    /** Number of fake balls. */
    public static final int FAKE_BALLS = 7;
    /** Seconds the fake balls last. */
    public static final double DURATION = 2.5;
    /** Horizontal speed of the real ball, in pixels per second. */
    public static final double SPEED = 850;
    /** Upward speed of the real ball, in pixels per second. */
    public static final double LIFT = 350;

    /**
     * Creates the shot.
     */
    public IllusionShot() {
        // No state: every value is a constant.
    }

    /**
     * Returns the name of the shot.
     *
     * @return "Ilusión"
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Returns the base damage.
     *
     * @return 30
     */
    @Override
    public int getBaseDamage() {
        return BASE_DAMAGE;
    }

    /**
     * Creates the effect for the rival.
     *
     * @return a new {@link IllusionEffect}
     */
    @Override
    public Effect createEffect() {
        return new IllusionEffect();
    }

    /**
     * Shoots the real ball towards the rival goal and creates the fake balls next to it.
     *
     * @param match   the match being played
     * @param shooter player who shoots
     */
    @Override
    public void launch(Match match, Player shooter) {
        match.getBall().launchPowerShot(this, shooter.getSide(), shooter.getDirection() * SPEED, -LIFT);
        match.spawnFakeBalls(FAKE_BALLS, DURATION);
    }

    /**
     * The balls fall and bounce like normal balls.
     *
     * @return false
     */
    @Override
    public boolean ignoresGravity() {
        return false;
    }
}
