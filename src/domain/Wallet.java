package domain;

import java.io.Serializable;

/**
 * Points of a player, earned in the matches and spent in the store.
 * Under construction: the store does not exist yet, so its methods throw
 * {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Wallet implements Serializable {

    /** Points available. */
    private int points;

    /**
     * Creates a wallet.
     *
     * @param points initial points; negative values count as 0
     */
    public Wallet(int points) {
        this.points = Math.max(0, points);
    }

    /**
     * Adds points. Under construction (store).
     *
     * @param amount points to add
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void add(int amount) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Spends points. Under construction (store).
     *
     * @param amount points to spend
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void spend(int amount) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the points available. Under construction (store).
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getPoints() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }
}
