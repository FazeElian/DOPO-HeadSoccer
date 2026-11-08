package domain;

import java.util.Set;

/**
 * Expert machine: attacks when it is losing or the ball is in the rival half, defends otherwise,
 * reacts faster and only uses the power shot near the rival goal.
 * Under construction: creating it throws {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ExpertAI extends AIController {

    /** Seconds between two decisions, faster than the other profiles. */
    public static final double EXPERT_REACTION_TIME = 0.1;

    /** Strategy to attack. */
    private OffensiveAI attack;
    /** Strategy to defend. */
    private DefensiveAI defense;

    /**
     * Creates the profile. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public ExpertAI() throws DOPOSoccerException {
        super();
    }

    /**
     * Decides the actions of this tick. Under construction.
     *
     * @param self  the player it controls
     * @param match the match
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    @Override
    protected Set<String> decide(Player self, Match match) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the player is near the rival goal. Under construction.
     *
     * @param self  the player
     * @param match the match
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    private static boolean isNearRivalGoal(Player self, Match match) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the seconds between two decisions.
     *
     * @return {@link #EXPERT_REACTION_TIME}
     */
    @Override
    protected double getReactionTime() {
        return EXPERT_REACTION_TIME;
    }

    /**
     * Returns the name of the profile.
     *
     * @return "EXPERT"
     */
    @Override
    public String getProfile() {
        return "EXPERT";
    }
}
