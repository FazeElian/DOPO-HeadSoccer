package domain;

/**
 * Fake ball of the Illusion shot. It looks and bounces like the real one,
 * but it does not score or hit anybody, and it disappears when its time is over.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class FakeBall extends Entity {

    /** Radius, in pixels. */
    private final double radius;
    /** Seconds left before it disappears. */
    private double remaining;

    /**
     * Creates a fake ball.
     *
     * @param x        left edge of its box, in pixels
     * @param y        top edge of its box, in pixels
     * @param radius   radius, the same as the real ball
     * @param duration seconds it lasts
     */
    public FakeBall(double x, double y, double radius, double duration) {
        super(x, y, 2 * radius, 2 * radius);
        this.radius = radius;
        this.remaining = duration;
    }

    /**
     * Counts down its time.
     *
     * @param dt elapsed time, in seconds
     */
    public void update(double dt) {
        remaining = Math.max(0, remaining - dt);
    }

    /**
     * Tells whether its time is over.
     *
     * @return true if it must disappear
     */
    public boolean isExpired() {
        return remaining <= 0;
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
     * Asks the physics for the body of a fake ball.
     *
     * @param engine the physics adapter of the match
     */
    @Override
    void addTo(PhysicsEngine engine) {
        engine.addFakeBall(this);
    }
}
