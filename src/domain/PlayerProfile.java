package domain;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Progress of a player: its points, its stats and what it bought in the store.
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
    /** Stats of the player. */
    private final Stats stats;
    /** Names of the balls it bought. */
    private final HashSet<String> unlockedBalls;
    /** Countries of the characters it bought. */
    private final HashSet<String> unlockedCharacters;
    /** Ball it plays with. */
    private BallSkin selectedBall;

    /**
     * Creates a profile without points, with the stats at the minimum.
     *
     * @param name        name of the player
     * @param defaultBall ball it starts with
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
     * Adds points to the wallet.
     *
     * @param amount points won
     */
    public void addPoints(int amount) {
        wallet.add(amount);
    }

    /**
     * Returns the points.
     *
     * @return points of the player
     */
    public int getPoints() {
        return wallet.getPoints();
    }

    /**
     * Pays and upgrades a stat one level.
     *
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @throws DOPOSoccerException UNKNOWN_STAT, STAT_AT_MAX or NOT_ENOUGH_POINTS
     */
    public void upgrade(String stat) throws DOPOSoccerException {
        wallet.spend(getUpgradeCost(stat));
        stats.upgrade(stat);
    }

    /**
     * Tells whether the player has a ball (the free balls are of everyone).
     *
     * @param ball the ball
     * @return true if it is free or the player bought it
     */
    public boolean isBallUnlocked(BallSkin ball) {
        return ball.getPrice() == 0 || unlockedBalls.contains(ball.getName());
    }

    /**
     * Buys a ball.
     *
     * @param ball the ball
     * @throws DOPOSoccerException ALREADY_OWNED or NOT_ENOUGH_POINTS
     */
    public void unlockBall(BallSkin ball) throws DOPOSoccerException {
        if (isBallUnlocked(ball)) {
            throw new DOPOSoccerException(DOPOSoccerException.ALREADY_OWNED);
        }
        wallet.spend(ball.getPrice());
        unlockedBalls.add(ball.getName());
    }

    /**
     * Chooses the ball the player plays with.
     *
     * @param ball the ball
     * @throws DOPOSoccerException BALL_LOCKED if the player doesn't have it
     */
    public void selectBall(BallSkin ball) throws DOPOSoccerException {
        if (!isBallUnlocked(ball)) {
            throw new DOPOSoccerException(DOPOSoccerException.BALL_LOCKED);
        }
        selectedBall = ball;
    }

    /**
     * Tells whether the player has a character (the free characters are of everyone).
     *
     * @param character the character
     * @return true if it is free or the player bought it
     */
    public boolean isCharacterUnlocked(GameCharacter character) {
        return character.isFree() || unlockedCharacters.contains(character.getCountry());
    }

    /**
     * Buys a character.
     *
     * @param character the character
     * @throws DOPOSoccerException ALREADY_OWNED or NOT_ENOUGH_POINTS
     */
    public void unlockCharacter(GameCharacter character) throws DOPOSoccerException {
        if (isCharacterUnlocked(character)) {
            throw new DOPOSoccerException(DOPOSoccerException.ALREADY_OWNED);
        }
        wallet.spend(character.getPrice());
        unlockedCharacters.add(character.getCountry());
    }

    /**
     * Returns the ball the player plays with.
     *
     * @return the selected ball
     */
    public BallSkin getSelectedBall() {
        return selectedBall;
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

    /**
     * Returns the level of one of its stats.
     *
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @return level from 1 to 10
     * @throws DOPOSoccerException UNKNOWN_STAT
     */
    public int getStat(String stat) throws DOPOSoccerException {
        return stats.get(stat);
    }

    /**
     * Returns what the next level of one of its stats costs.
     *
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @return cost in points
     * @throws DOPOSoccerException UNKNOWN_STAT or STAT_AT_MAX
     */
    public int getUpgradeCost(String stat) throws DOPOSoccerException {
        if (stats.get(stat) >= Stats.MAX_STAT) {
            throw new DOPOSoccerException(DOPOSoccerException.STAT_AT_MAX);
        }
        return stats.getUpgradeCost(stat);
    }
}
