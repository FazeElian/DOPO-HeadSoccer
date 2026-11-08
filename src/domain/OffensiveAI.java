package domain;

import java.util.Set;

/**
 * Offensive machine: always goes for the ball and uses the power shot as soon as it can.
 * Under construction: creating it throws {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class OffensiveAI extends AIController {

    /**
     * Creates the profile. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public OffensiveAI() throws DOPOSoccerException {
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
     * @return "OFFENSIVE"
     */
    @Override
    public String getProfile() {
        return "OFFENSIVE";
    }
}
