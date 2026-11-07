package domain;

import java.io.Serializable;

/**
 * Something that changes what a player can do for some seconds.
 * The player never asks which effect it has: it asks the effect whether it can act or jump
 * and how fast it can run. A new effect is a new subclass.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public abstract class Effect implements Serializable {

    /** Seconds left. */
    private double remaining;

    /**
     * Creates the effect.
     *
     * @param duration seconds it lasts
     */
    public Effect(double duration) {
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
     * @return true if it must be removed
     */
    public boolean isExpired() {
        return remaining <= 0;
    }

    /**
     * What happens when the player receives it, for example being pushed or thrown up.
     * By default nothing.
     *
     * @param p the affected player
     */
    public void onApply(Player p) {
        // Most effects only change what the player can do.
    }

    /**
     * Tells whether the player can move and kick. By default true.
     *
     * @return true if the player can act
     */
    public boolean canAct() {
        return true;
    }

    /**
     * Tells whether the player can jump. By default true.
     *
     * @return true if the player can jump
     */
    public boolean canJump() {
        return true;
    }

    /**
     * Returns the share of its running speed the player keeps. By default 1.
     *
     * @return speed factor, from 0 to 1
     */
    public double getSpeedFactor() {
        return 1;
    }

    /**
     * Returns the seconds left.
     *
     * @return seconds left
     */
    public double getRemaining() {
        return remaining;
    }

    /**
     * Returns the name shown on screen.
     *
     * @return name of the effect
     */
    public abstract String getName();
}
