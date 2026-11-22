package domain;

/**
 * Arcade: a match between the two chosen characters, by time or by goals. In survival it is a series
 * against the machine: while the left player wins, a new match is played.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ArcadeMode extends GameMode {
    /** Number of the current match: 1 in a single match, it grows in a survival series. */
    private int round;

    /**
     * Keeps the configuration of the game.
     *
     * @param leftCharacter   character of the left player
     * @param leftStats       stats of the left player
     * @param leftController  who controls the left player
     * @param rightCharacter  character of the right player
     * @param rightStats      stats of the right player
     * @param rightController who controls the right player
     * @param type            rule to end the match
     * @param resolution      "SMALL", "MEDIUM" or "LARGE"
     * @param skin            ball to play with
     * @param stadium         stadium of the match ("default", "rain"...)
     */
    public ArcadeMode(GameCharacter leftCharacter, Stats leftStats, PlayerController leftController,
                      GameCharacter rightCharacter, Stats rightStats, PlayerController rightController,
                      MatchType type, String resolution, BallSkin skin, String stadium) {
        super(leftCharacter, leftStats, leftController, rightCharacter, rightStats, rightController,
                type, resolution, skin, stadium);
        this.round = 1;
    }

    /**
     * Creates the only match.
     *
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if the resolution is not valid
     */
    @Override
    public void start() throws DOPOSoccerException {
        round = 1;
        match = createMatch(rightCharacter);
    }

    /**
     * Tells whether the mode ended: the match ended and, in survival, the left player lost it.
     *
     * @return true once the match is finished and no other match of the series is played
     */
    @Override
    public boolean isOver() {
        return match != null && match.isFinished() && !type.continuesAfter(match.getWinner());
    }

    /**
     * Returns the name of the mode.
     *
     * @return "ARCADE"
     */
    @Override
    public String getName() {
        return "ARCADE";
    }

    /**
     * Creates the next match of a survival series.
     *
     * @throws DOPOSoccerException INVALID_STATE if the match has not ended or the mode is over
     */
    @Override
    public void nextMatch() throws DOPOSoccerException {
        if (match == null || !match.isFinished() || isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_STATE);
        }
        round++;
        match = createMatch(rightCharacter);
    }

    /**
     * Returns the number of the current match.
     *
     * @return 1 in a single match; 1, 2, 3... in a survival series
     */
    @Override
    public int getRound() {
        return round;
    }
}
