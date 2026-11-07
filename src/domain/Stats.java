package domain;

import java.io.Serializable;

/**
 * Stats of a player, from 1 to 10. They belong to the player, not to the character,
 * so they apply to any character the player chooses. Each point above 1 adds 10 % to the base value.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Stats implements Serializable {

    /** Lowest level of a stat. */
    public static final int MIN_STAT = 1;
    /** Highest level of a stat. */
    public static final int MAX_STAT = 10;
    /** Price of the first upgrade point; each next point of the same stat costs double. */
    public static final int BASE_UPGRADE_COST = 500;
    /** Life with Survival at 1. */
    public static final int BASE_LIFE = 100;

    /** Level of Speed. */
    private int speed;
    /** Level of Jump. */
    private int jump;
    /** Level of Kick. */
    private int kick;
    /** Level of Survival. */
    private int survival;

    /**
     * Creates the stats of a new player: everything at {@link #MIN_STAT}.
     */
    public Stats() {
        speed = MIN_STAT;
        jump = MIN_STAT;
        kick = MIN_STAT;
        survival = MIN_STAT;
    }

    /**
     * Returns the level of a stat.
     *
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @return the level, from 1 to 10
     * @throws DOPOSoccerException UNKNOWN_STAT if the name is not valid
     */
    public int get(String stat) throws DOPOSoccerException {
        switch (stat == null ? "" : stat) {
            case "SPEED":
                return speed;
            case "JUMP":
                return jump;
            case "KICK":
                return kick;
            case "SURVIVAL":
                return survival;
            default:
                throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_STAT);
        }
    }

    /**
     * Returns the kick multiplier: 1 + 0.10 * (Kick - 1). Kick 5 gives 1.4.
     * It multiplies the damage of kicks and power shots, not the speed of the ball.
     *
     * @return the kick multiplier
     */
    public double getKickMultiplier() {
        return factor(kick);
    }

    /**
     * Returns the maximum life: 100 * (1 + 0.10 * (Survival - 1)). Survival 3 gives 120.
     *
     * @return the maximum life
     */
    public int getMaxLife() {
        return (int) Math.round(BASE_LIFE * factor(survival));
    }

    /**
     * Returns the multiplier of the base running speed: 1 + 0.10 * (Speed - 1).
     *
     * @return the speed multiplier
     */
    public double getSpeedMultiplier() {
        return factor(speed);
    }

    /**
     * Returns the multiplier of the base jump: 1 + 0.10 * (Jump - 1).
     *
     * @return the jump multiplier
     */
    public double getJumpMultiplier() {
        return factor(jump);
    }

    /**
     * Returns the price of the next point of a stat: 500, 1000, 2000... (500 * 2^(level - 1)).
     *
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @return price in points of the next point
     * @throws DOPOSoccerException UNKNOWN_STAT if the name is not valid
     */
    public int getUpgradeCost(String stat) throws DOPOSoccerException {
        return BASE_UPGRADE_COST << (get(stat) - 1);
    }

    /**
     * Raises a stat one point. It does not charge anything: the store does.
     *
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @throws DOPOSoccerException UNKNOWN_STAT if the name is not valid,
     *                             STAT_AT_MAX if it is already at 10
     */
    public void upgrade(String stat) throws DOPOSoccerException {
        if (get(stat) >= MAX_STAT) {
            throw new DOPOSoccerException(DOPOSoccerException.STAT_AT_MAX);
        }
        switch (stat) {
            case "SPEED":
                speed++;
                break;
            case "JUMP":
                jump++;
                break;
            case "KICK":
                kick++;
                break;
            default:
                survival++;
                break;
        }
    }

    /**
     * Turns a level into its multiplier.
     *
     * @param level a stat level
     * @return 1 + 0.10 * (level - 1)
     */
    private static double factor(int level) {
        return 1 + 0.10 * (level - 1);
    }
}
