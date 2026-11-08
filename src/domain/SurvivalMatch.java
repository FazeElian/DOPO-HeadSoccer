package domain;

/**
 * Survival: each player starts with 3 balls and loses one for each goal received.
 * Under construction: creating it throws {@link DOPOSoccerException#UNDER_CONSTRUCTION},
 * so its rules are never reached.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class SurvivalMatch extends MatchType {

    /** Balls of each player at the start. */
    public static final int INITIAL_BALLS = 3;

    /** Balls left of the left player. */
    private int leftBalls;
    /** Balls left of the right player. */
    private int rightBalls;

    /**
     * Creates the rule. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public SurvivalMatch() throws DOPOSoccerException {
        super();
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Under construction: never reached, the constructor throws.
     *
     * @param match the match
     * @return false
     */
    @Override
    public boolean isMatchOver(Match match) {
        return false;
    }

    /**
     * Under construction: never reached, the constructor throws.
     *
     * @param match the match
     * @return null
     */
    @Override
    public String getWinner(Match match) {
        return null;
    }

    /**
     * Returns the balls left of a player. Under construction.
     *
     * @param side "LEFT" or "RIGHT"
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getBalls(String side) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the series continues after a won match. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public boolean continuesAfterWin() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the name of the rule.
     *
     * @return "SURVIVAL"
     */
    @Override
    public String getName() {
        return "SURVIVAL";
    }
}
