package domain;

import java.io.Serializable;

/**
 * Goal area of one side. The posts and the crossbar are static bodies in the physics library;
 * this class only knows when the ball is inside.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Goal implements Serializable {

    /** Side that defends the goal. */
    private final String side;
    /** Left edge, in pixels. */
    private final double x;
    /** Top edge, under the crossbar, in pixels. */
    private final double y;
    /** Width, in pixels. */
    private final double width;
    /** Height, in pixels. */
    private final double height;

    /**
     * Creates a goal area.
     *
     * @param side   "LEFT" or "RIGHT": side that defends it
     * @param x      left edge, in pixels
     * @param y      top edge (under the crossbar), in pixels
     * @param width  width, in pixels
     * @param height height, in pixels
     */
    public Goal(String side, double x, double y, double width, double height) {
        this.side = side;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Tells whether the ball crossed the line: it is under the crossbar
     * and completely past the front of the goal.
     *
     * @param ball the real ball
     * @return true if it is a goal
     */
    public boolean contains(Ball ball) {
        if (ball.getY() < y) {
            return false;
        }
        if ("LEFT".equals(side)) {
            return ball.getX() + ball.getWidth() <= x + width;
        }
        return ball.getX() >= x;
    }

    /**
     * Returns the side that defends this goal.
     *
     * @return "LEFT" or "RIGHT"
     */
    public String getSide() {
        return side;
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
     * Returns the top edge, under the crossbar.
     *
     * @return top edge, in pixels
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the width.
     *
     * @return width, in pixels
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height.
     *
     * @return height, in pixels
     */
    public double getHeight() {
        return height;
    }
}
