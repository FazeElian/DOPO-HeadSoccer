package domain;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Profile of one of the two players: name, points, stats and what was bought in the store.
 * The stats work (they are created once and used in every match of this player); everything
 * of the store is under construction and throws {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class PlayerProfile implements Serializable {

    /** Name of the player. */
    private final String name;
    /** Points of the player. */
    private final Wallet wallet;
    /** Stats of the player, used by every character this player chooses. */
    private final Stats stats;
    /** Names of the balls this player can use. */
    private final HashSet<String> unlockedBalls;
    /** Countries of the characters this player bought. */
    private final HashSet<String> unlockedCharacters;
    /** Ball chosen for the matches. */
    private BallSkin selectedBall;

    /**
     * Creates a profile with 0 points, every stat at 1 and only the free ball.
     *
     * @param name        name of the player
     * @param defaultBall free ball, unlocked and selected from the start
     */
    public PlayerProfile(String name, BallSkin defaultBall) {
        this.name = name;
        this.wallet = new Wallet(0);
        this.stats = new Stats();
        this.unlockedBalls = new HashSet<>();
        this.unlockedBalls.add(defaultBall.getName());
        this.unlockedCharacters = new HashSet<>();
        this.selectedBall = defaultBall;
    }

    /**
     * Adds points earned in a match. Under construction (store).
     *
     * @param amount points to add
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void addPoints(int amount) throws DOPOSoccerException {
        wallet.add(amount);
    }

    /**
     * Returns the points of the player. Under construction (store).
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getPoints() throws DOPOSoccerException {
        return wallet.getPoints();
    }

    /**
     * Buys one point of a stat. Under construction (store).
     *
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void upgrade(String stat) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the player can use a ball. Under construction (store).
     *
     * @param ballName name of the ball
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public boolean isBallUnlocked(String ballName) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Buys a ball. Under construction (store).
     *
     * @param ball ball of the catalog
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void unlockBall(BallSkin ball) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Chooses the ball for the next matches. Under construction (store).
     *
     * @param ball ball of the catalog
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void selectBall(BallSkin ball) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the player can use a character. Under construction (store).
     *
     * @param character a character
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public boolean isCharacterUnlocked(GameCharacter character) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Buys a locked character. Under construction (store).
     *
     * @param character a character of the game
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void unlockCharacter(GameCharacter character) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the chosen ball. Under construction (store).
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public BallSkin getSelectedBall() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the stats of the player.
     *
     * @return the stats
     */
    public Stats getStats() {
        return stats;
    }

    /**
     * Returns the name of the player.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
