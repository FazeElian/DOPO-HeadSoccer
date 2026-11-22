package domain;

import java.io.Serializable;

/**
 * Points of a player: they are won playing and spent in the store.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Wallet implements Serializable {

    /** Version of the saved data, so a progress file of this version can be read again. */
    private static final long serialVersionUID = 1L;

    /** Points the player has. */
    private int points;

    /**
     * Creates a wallet.
     *
     * @param points initial points (a negative amount counts as 0)
     */
    public Wallet(int points) {
        this.points = Math.max(0, points);
    }

    /**
     * Adds points.
     *
     * @param amount points won (a negative amount is ignored)
     */
    public void add(int amount) {
        points += Math.max(0, amount);
    }

    /**
     * Spends points.
     *
     * @param amount points to pay
     * @throws DOPOSoccerException NOT_ENOUGH_POINTS if the wallet has less than the amount
     */
    public void spend(int amount) throws DOPOSoccerException {
        if (amount > points) {
            throw new DOPOSoccerException(DOPOSoccerException.NOT_ENOUGH_POINTS);
        }
        points -= amount;
    }

    /**
     * Returns the points.
     *
     * @return points the player has
     */
    public int getPoints() {
        return points;
    }
}
