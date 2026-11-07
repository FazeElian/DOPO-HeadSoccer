package domain;

/**
 * Blue Aura, the shot of South Korea: straight and horizontal, at high speed.
 * If it hits the rival, it pushes it towards its own goal.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class BlueAuraShot implements PowerShot {

    /** Name of the shot. */
    public static final String NAME = "Aura Azul";
    /** Damage before the kick multiplier. */
    public static final int BASE_DAMAGE = 40;
    /** Speed of the ball, in pixels per second. */
    public static final double SPEED = 1400;

    /**
     * Creates the shot.
     */
    public BlueAuraShot() {
        // No state: every value is a constant.
    }

    /**
     * Returns the name of the shot.
     *
     * @return "Aura Azul"
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
     * @return a new {@link PushEffect}
     */
    @Override
    public Effect createEffect() {
        return new PushEffect();
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
