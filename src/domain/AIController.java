package domain;

import java.util.HashSet;
import java.util.Set;

/**
 * The machine as a player: every reaction time it decides which actions to do
 * (Template Method: each profile implements {@link #decide}).
 * Under construction: creating any profile throws {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public abstract class AIController implements PlayerController {

    /** Seconds between two decisions. */
    public static final double REACTION_TIME = 0.2;
    /** Distance to the ball, in pixels, to try a kick. */
    public static final double KICK_DISTANCE = 75;

    /** Seconds left to decide again. */
    private double cooldown;
    /** Time of the match the last time it was asked. */
    private double lastElapsed;
    /** Actions decided the last time; they are repeated until the next decision. */
    private HashSet<String> lastActions;

    /**
     * Creates the machine. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public AIController() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Under construction: never reached, the constructor throws.
     *
     * @param self  the player it controls
     * @param match the match
     * @return no actions
     */
    @Override
    public Set<String> nextActions(Player self, Match match) {
        return new HashSet<>();
    }

    /**
     * Decides the actions of this tick. Each profile implements it.
     *
     * @param self  the player it controls
     * @param match the match
     * @return actions to do
     * @throws DOPOSoccerException UNDER_CONSTRUCTION
     */
    protected abstract Set<String> decide(Player self, Match match) throws DOPOSoccerException;

    /**
     * Returns the seconds between two decisions.
     *
     * @return {@link #REACTION_TIME}
     */
    protected double getReactionTime() {
        return REACTION_TIME;
    }

    /**
     * Returns the name of the profile.
     *
     * @return "CLUMSY", "OFFENSIVE", "DEFENSIVE" or "EXPERT"
     */
    public abstract String getProfile();

    /**
     * Moves the player towards a point. Under construction.
     *
     * @param self    the player
     * @param targetX x to reach
     * @param actions where the movement is added
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    protected static void moveTo(Player self, double targetX, Set<String> actions) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Goes for the ball: gets behind it, kicks when near and jumps when it is above. Under construction.
     *
     * @param self    the player
     * @param ball    the real ball
     * @param actions where the actions are added
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    protected static void playBall(Player self, Ball ball, Set<String> actions) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the ball is near the player. Under construction.
     *
     * @param self     the player
     * @param ball     the real ball
     * @param distance distance in pixels
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    protected static boolean isBallNear(Player self, Ball ball, double distance) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the ball is in the half of the player. Under construction.
     *
     * @param self  the player
     * @param match the match
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    protected static boolean isBallInOwnHalf(Player self, Match match) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }
}
