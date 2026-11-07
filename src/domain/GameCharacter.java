package domain;

import java.io.Serializable;

/**
 * A character: a country (a team) with a power shot. There are 8 teams sharing the 4 power shots of Annex 1;
 * the 4 countries of the Annex use their own shot. A character has no stats: stats belong to the player.
 * Some characters are locked and are bought in the store.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class GameCharacter implements Serializable {

    /** Country of the character, also its name. */
    private final String country;
    /** Its power shot. */
    private final PowerShot powerShot;
    /** Price in points to unlock it; 0 if it is free. */
    private final int price;

    /**
     * Creates a free character.
     *
     * @param country   country it represents; it is also its name
     * @param powerShot its power shot
     */
    public GameCharacter(String country, PowerShot powerShot) {
        this(country, powerShot, 0);
    }

    /**
     * Creates a character that may have to be bought.
     *
     * @param country   country it represents; it is also its name
     * @param powerShot its power shot
     * @param price     price in points to unlock it; 0 if it is free
     */
    public GameCharacter(String country, PowerShot powerShot, int price) {
        this.country = country;
        this.powerShot = powerShot;
        this.price = Math.max(0, price);
    }

    /**
     * Returns the country of the character.
     *
     * @return the country, used as its name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Returns the power shot of the character.
     *
     * @return its power shot
     */
    public PowerShot getPowerShot() {
        return powerShot;
    }

    /**
     * Returns the name of its power shot.
     *
     * @return the name of the shot
     */
    public String getPowerName() {
        return powerShot.getName();
    }

    /**
     * Returns the price to unlock the character.
     *
     * @return price in points; 0 if it is free
     */
    public int getPrice() {
        return price;
    }

    /**
     * Tells whether the character is free for everybody.
     *
     * @return true if its price is 0
     */
    public boolean isFree() {
        return price == 0;
    }
}
