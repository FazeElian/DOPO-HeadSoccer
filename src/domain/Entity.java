package domain;

import java.io.Serializable;

/**
 * Anything that moves on the field: the players, the ball and the fake balls.
 * It keeps a position, a velocity and a rectangular box, in pixels, pixels per second and seconds.
 * Entities do not move by themselves: {@link PhysicsEngine} moves them with the physics library
 * and copies the result back.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public abstract class Entity implements Serializable {

    /** Left edge of the box, in pixels. */
    protected double x;
    /** Top edge of the box, in pixels. */
    protected double y;
    /** Horizontal velocity, in pixels per second. */
    protected double vx;
    /** Vertical velocity, in pixels per second (positive goes down). */
    protected double vy;
    /** Width of the box, in pixels. */
    protected double width;
    /** Height of the box, in pixels. */
    protected double height;

    /**
     * Creates a still entity. Subclasses call it with super(...).
     *
     * @param x      left edge, in pixels
     * @param y      top edge, in pixels
     * @param width  box width, in pixels
     * @param height box height, in pixels
     */
    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vx = 0;
        this.vy = 0;
    }

    /**
     * Tells whether the boxes of this entity and another one overlap.
     *
     * @param other the other entity
     * @return true if both boxes overlap
     */
    public boolean collidesWith(Entity other) {
        return x < other.x + other.width && other.x < x + width
                && y < other.y + other.height && other.y < y + height;
    }

    /**
     * Places the entity at a new position without changing its velocity.
     *
     * @param x new left edge, in pixels
     * @param y new top edge, in pixels
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Changes the velocity of the entity.
     *
     * @param vx horizontal velocity, in pixels per second
     * @param vy vertical velocity, in pixels per second (positive goes down)
     */
    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Returns the left edge.
     *
     * @return left edge, in pixels
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the top edge.
     *
     * @return top edge, in pixels
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the horizontal velocity.
     *
     * @return horizontal velocity, in pixels per second
     */
    public double getVx() {
        return vx;
    }

    /**
     * Returns the vertical velocity.
     *
     * @return vertical velocity, in pixels per second (positive goes down)
     */
    public double getVy() {
        return vy;
    }

    /**
     * Returns the box width.
     *
     * @return width, in pixels
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the box height.
     *
     * @return height, in pixels
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns how much gravity acts on the entity. By default all of it.
     *
     * @return 1 for full gravity, 0 for none
     */
    public double getGravityScale() {
        return 1;
    }

    /**
     * Asks the physics for the body of this kind of entity (double dispatch): each subclass calls
     * the method of {@link PhysicsEngine} that creates its own body, so the engine never checks types.
     *
     * @param engine the physics adapter of the match
     */
    abstract void addTo(PhysicsEngine engine);
}
