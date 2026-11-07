package domain;

import java.io.Serializable;

/**
 * A ball of the store catalog. It only changes the picture of the ball: the physics is the same.
 * In this version every match uses the free ball; the store is under construction.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class BallSkin implements Serializable {

    /** Name of the ball and of its image. */
    private final String name;
    /** Price in points. */
    private final int price;

    /**
     * Creates a ball of the catalog.
     *
     * @param name  name shown in the store; it is also the name of its image
     * @param price price in points; 0 for the free one
     */
    public BallSkin(String name, int price) {
        this.name = name;
        this.price = Math.max(0, price);
    }

    /**
     * Returns the name of the ball.
     *
     * @return the name, also used to load its image
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the price of the ball.
     *
     * @return price in points
     */
    public int getPrice() {
        return price;
    }
}
