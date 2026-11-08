package domain;

import java.util.Set;

/**
 * Defensive machine: plays the ball in its half and goes back to guard its goal otherwise.
 * Under construction: creating it throws {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class DefensiveAI extends AIController {

    /** Distance, in pixels, in front of its goal where it waits. */
    public static final double GUARD_DISTANCE = 60;

    /**
     * Creates the profile. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public DefensiveAI() throws DOPOSoccerException {
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
     * Returns the name of the profile.
     *
     * @return "DEFENSIVE"
     */
    @Override
    public String getProfile() {
        return "DEFENSIVE";
    }
}
