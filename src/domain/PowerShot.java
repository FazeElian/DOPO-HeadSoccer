package domain;

import java.io.Serializable;

/**
 * The unique power shot of a character (Strategy pattern). It decides how the ball leaves,
 * how much damage it does and which effect it puts on the rival it hits.
 * A new character only needs a new shot and a new effect.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public interface PowerShot extends Serializable {

    /**
     * Returns the name of the shot.
     *
     * @return name shown on screen
     */
    String getName();

    /**
     * Returns the damage before the kick multiplier of the shooter.
     *
     * @return base damage
     */
    int getBaseDamage();

    /**
     * Creates a new effect for the rival that is hit.
     *
     * @return a new effect
     */
    Effect createEffect();

    /**
     * Launches the ball in the way of this shot, with {@link Ball#launchPowerShot}.
     *
     * @param match   the match being played
     * @param shooter player who shoots
     */
    void launch(Match match, Player shooter);

    /**
     * Tells whether the ball flies straight while it carries this shot.
     *
     * @return true if gravity does not act on the shot
     */
    boolean ignoresGravity();
}
