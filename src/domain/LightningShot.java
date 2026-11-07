package domain;

/**
 * Lightning, the shot of Cameroon: straight and covered with electricity.
 * If it hits the rival, the rival is electrocuted.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class LightningShot implements PowerShot {

    /** Name of the shot. */
    public static final String NAME = "Rayo";
    /** Damage before the kick multiplier. */
    public static final int BASE_DAMAGE = 60;
    /** Speed of the ball, in pixels per second. */
    public static final double SPEED = 1100;

    /**
     * Creates the shot.
     */
    public LightningShot() {
        // No state: every value is a constant.
    }

    /**
     * Returns the name of the shot.
     *
     * @return "Rayo"
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Returns the base damage.
     *
     * @return 60
     */
    @Override
    public int getBaseDamage() {
        return BASE_DAMAGE;
    }

    /**
     * Creates the effect for the rival.
     *
     * @return a new {@link ElectrocutedEffect}
     */
    @Override
    public Effect createEffect() {
        return new ElectrocutedEffect();
    }

    /**
     * Shoots the ball straight towards the rival goal.
     *
     * @param match   the match being played
     * @param shooter player who shoots
     */
    @Override
    public void launch(Match match, Player shooter) {
        match.getBall().launchPowerShot(this, shooter.getSide(), shooter.getDirection() * SPEED, 0);
    }

    /**
     * The shot flies straight.
     *
     * @return true
     */
    @Override
    public boolean ignoresGravity() {
        return true;
    }
}
