package domain;

import java.util.Random;
import java.util.Set;

/**
 * Clumsy machine: moves, jumps and kicks at random.
 * Under construction: creating it throws {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ClumsyAI extends AIController {

    /** Decides the random actions. */
    private Random random;

    /**
     * Creates the profile. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public ClumsyAI() throws DOPOSoccerException {
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
     * @return "CLUMSY"
     */
    @Override
    public String getProfile() {
        return "CLUMSY";
    }
}
