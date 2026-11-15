package domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Controller of a player moved by the machine (Template Method): {@link #nextActions} is the fixed
 * part (it decides again only after its reaction time) and {@link #decide} is what each profile does.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public abstract class AIController implements PlayerController {

    /** Seconds between two decisions. */
    public static final double REACTION_TIME = 0.2;
    /** Distance to the ball, in pixels, from which it kicks. */
    public static final double KICK_DISTANCE = 75;
    /** How close to a point, in pixels, counts as being there. */
    private static final double ARRIVAL = 25;

    /** Seconds left until the next decision. */
    private double cooldown;
    /** Time of the match in the last tick. */
    private double lastElapsed;
    /** Actions of the last decision. */
    private HashSet<String> lastActions;

    /**
     * Creates the controller, ready to decide in its first tick.
     */
    public AIController() {
        cooldown = 0;
        lastElapsed = 0;
        lastActions = new HashSet<>();
    }

    @Override
    public Set<String> nextActions(Player self, Match match) {
        double now = match.getElapsed();
        cooldown -= Math.max(0, now - lastElapsed); // a new match starts again in 0
        lastElapsed = now;
        if (cooldown <= 0) {
            lastActions = new HashSet<>(decide(self, match));
            cooldown = getReactionTime();
        }
        return new HashSet<>(lastActions);
    }

    /**
     * Decides the actions of this moment. Each profile implements it.
     *
     * @param self  the player it controls
     * @param match the match
     * @return actions to do
     */
    protected abstract Set<String> decide(Player self, Match match);

    /**
     * Returns the seconds between two decisions.
     *
     * @return reaction time, in seconds
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
     * Moves the player towards a point.
     *
     * @param self    the player
     * @param targetX x to reach with its center
     * @param actions where the movement is added
     */
    protected static void moveTo(Player self, double targetX, Set<String> actions) {
        double center = self.getX() + self.getWidth() / 2;
        if (targetX > center + ARRIVAL) {
            actions.add("MOVE_RIGHT");
        } else if (targetX < center - ARRIVAL) {
            actions.add("MOVE_LEFT");
        }
    }

    /**
     * Goes for the ball: gets behind it, kicks when near, jumps when it is above
     * and turns on its power when the bar is full.
     *
     * @param self    the player
     * @param ball    the real ball
     * @param actions where the actions are added
     */
    protected static void playBall(Player self, Ball ball, Set<String> actions) {
        double ballCenter = ball.getX() + ball.getWidth() / 2;
        // Behind the ball: on the side of its own goal, to push it to the rival goal
        moveTo(self, ballCenter - self.getDirection() * self.getWidth() / 2, actions);
        if (isBallNear(self, ball, KICK_DISTANCE)) {
            actions.add("KICK");
            if (self.isPowerReady()) {
                actions.add("POWER");
            }
        }
        boolean above = ball.getY() + ball.getHeight() < self.getY();
        if (above && isBallNear(self, ball, 2 * KICK_DISTANCE)) {
            actions.add("JUMP");
        }
    }

    /**
     * Tells whether the ball is near the player.
     *
     * @param self     the player
     * @param ball     the real ball
     * @param distance distance in pixels, from the edge of the player
     * @return true if the ball is at that distance or less
     */
    protected static boolean isBallNear(Player self, Ball ball, double distance) {
        double dx = (ball.getX() + ball.getWidth() / 2) - (self.getX() + self.getWidth() / 2);
        double dy = (ball.getY() + ball.getHeight() / 2) - (self.getY() + self.getHeight() / 2);
        return Math.hypot(dx, dy) <= distance + self.getWidth() / 2;
    }

    /**
     * Tells whether the ball is in the half of the player.
     *
     * @param self  the player
     * @param match the match
     * @return true if the ball is on its side of the field
     */
    protected static boolean isBallInOwnHalf(Player self, Match match) {
        boolean ballOnLeft = match.getBallX() < match.getFieldWidth() / 2;
        return "LEFT".equals(self.getSide()) == ballOnLeft;
    }
}
