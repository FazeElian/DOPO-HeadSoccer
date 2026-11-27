package domain;

import domain.enums.AIProfile;
import domain.enums.Action;
import domain.enums.Side;
import java.util.HashSet;
import java.util.Set;

/**
 * Machine that plays well: it reacts faster, clears the ball when it is in its half,
 * attacks when it is closer to the ball than the rival and goes back to guard its goal
 * when the rival has the ball in the other half.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ExpertAI extends AIController {

    /** Seconds between two decisions of the expert. */
    public static final double EXPERT_REACTION_TIME = 0.1;

    /**
     * Creates the expert profile.
     */
    public ExpertAI() {
        super();
    }

    @Override
    protected Set<String> decide(Player self, Match match) {
        Set<String> actions = new HashSet<>();
        Ball ball = match.getBall();
        Player rival = match.getOpponent(self);

        if (isBallInOwnHalf(self, match) || isCloserToBall(self, rival, ball)) {
            playBall(self, ball, actions);             // clears it or attacks
            if (isNearRivalGoal(self, match)) {
                actions.add(Action.KICK.getName());                   // shoots as soon as it is in front of the goal
            }
        } else {
            guard(self, match, actions);   // the rival has the ball: goes back to its goal
        }
        return actions;
    }

    /**
     * Tells whether the player is closer to the ball than the rival, measured on the field width.
     *
     * @param self  the player
     * @param rival the other player
     * @param ball  the ball
     * @return true if it is closer or at the same distance
     */
    private boolean isCloserToBall(Player self, Player rival, Ball ball) {
        double ballX = ball.getX() + ball.getWidth() / 2;
        double mine = Math.abs(ballX - (self.getX() + self.getWidth() / 2));
        double theirs = Math.abs(ballX - (rival.getX() + rival.getWidth() / 2));
        return mine <= theirs;
    }

    /**
     * Tells whether the player is in the last quarter of the field, next to the rival goal.
     *
     * @param self  the player
     * @param match the match
     * @return true if it is in front of the rival goal
     */
    private boolean isNearRivalGoal(Player self, Match match) {
        double center = self.getX() + self.getWidth() / 2;
        double width = match.getFieldWidth();
        return Side.LEFT.getName().equals(self.getSide()) ? center > width * 0.75 : center < width * 0.25;
    }

    @Override
    protected double getReactionTime() {
        return EXPERT_REACTION_TIME;
    }

    @Override
    public String getProfile() {
        return AIProfile.EXPERT.getName();
    }
}
