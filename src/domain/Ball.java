package domain;

/**
 * The real ball of a match. Gravity, rebounds and hits are computed by the physics library.
 * After a power shot the ball carries that shot: if it touches the rival, the rival gets its damage
 * and effect.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Ball extends Entity {

    /** Share of speed the ball keeps after a rebound; it is given to the physics library. */
    public static final double RESTITUTION = 0.7;
    /** Below this speed, in pixels per second, a power shot is over and the ball loses its charge. */
    public static final double MIN_SHOT_SPEED = 200;

    /** Radius, in pixels. */
    private final double radius;
    /** Ball of the catalog used to draw it. */
    private final BallSkin skin;
    /** Power shot the ball carries, or null. */
    private PowerShot charge;
    /** Side that launched the power shot, or null. */
    private String shooter;

    /**
     * Creates a still ball with no charge.
     *
     * @param x      left edge of its box, in pixels
     * @param y      top edge of its box, in pixels
     * @param radius radius, in pixels; the box is 2 * radius wide and high
     * @param skin   ball chosen by the player, only used to draw it
     */
    public Ball(double x, double y, double radius, BallSkin skin) {
        super(x, y, 2 * radius, 2 * radius);
        this.radius = radius;
        this.skin = skin;
        this.charge = null;
        this.shooter = null;
    }

    /**
     * Kicks the ball: it leaves with this velocity, whatever it had before.
     *
     * @param ix horizontal velocity of the kick, in pixels per second
     * @param iy vertical velocity of the kick, in pixels per second (negative goes up)
     */
    public void applyImpulse(double ix, double iy) {
        setVelocity(ix, iy);
    }

    /**
     * Launches a power shot: the ball leaves with this velocity and carries the shot.
     *
     * @param shot    the power shot
     * @param shooter "LEFT" or "RIGHT": side of the player who shot
     * @param vx      horizontal velocity, in pixels per second
     * @param vy      vertical velocity, in pixels per second
     */
    public void launchPowerShot(PowerShot shot, String shooter, double vx, double vy) {
        this.charge = shot;
        this.shooter = shooter;
        setVelocity(vx, vy);
    }

    /**
     * Tells whether the ball carries a power shot.
     *
     * @return true while the shot is on its way
     */
    public boolean isPowerShot() {
        return charge != null;
    }

    /**
     * Removes the power shot: it hit the rival, a goal was scored or it lost its speed.
     */
    public void clearCharge() {
        charge = null;
        shooter = null;
    }

    /**
     * Leaves the ball still at a point, with no charge. Used for the kick-off.
     *
     * @param x left edge, in pixels
     * @param y top edge, in pixels
     */
    public void reset(double x, double y) {
        setPosition(x, y);
        setVelocity(0, 0);
        clearCharge();
    }

    /**
     * Returns the speed of the ball.
     *
     * @return speed, in pixels per second
     */
    public double getSpeed() {
        return Math.hypot(vx, vy);
    }

    /**
     * Returns the power shot the ball carries.
     *
     * @return the shot, or null if there is none
     */
    public PowerShot getCharge() {
        return charge;
    }

    /**
     * Returns the side that launched the current power shot.
     *
     * @return "LEFT", "RIGHT" or null if there is no shot
     */
    public String getShooter() {
        return shooter;
    }

    /**
     * Returns the chosen ball, to draw it.
     *
     * @return the ball of the catalog
     */
    public BallSkin getSkin() {
        return skin;
    }

    /**
     * Returns the radius.
     *
     * @return radius, in pixels
     */
    public double getRadius() {
        return radius;
    }

    /**
     * A straight power shot flies without gravity; otherwise the ball falls normally.
     *
     * @return 0 during a straight power shot, 1 otherwise
     */
    @Override
    public double getGravityScale() {
        return isPowerShot() && charge.ignoresGravity() ? 0 : 0.7;
    }

    /**
     * Asks the physics for the body of the real ball.
     *
     * @param engine the physics adapter of the match
     */
    @Override
    void addTo(PhysicsEngine engine) {
        engine.addBall(this);
    }
}
