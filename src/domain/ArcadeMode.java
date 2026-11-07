package domain;

/**
 * Arcade: one match between the two chosen characters. It is the mode of version one.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ArcadeMode extends GameMode {

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
     */
    public ArcadeMode(GameCharacter leftCharacter, Stats leftStats, PlayerController leftController,
                      GameCharacter rightCharacter, Stats rightStats, PlayerController rightController,
                      MatchType type, String resolution, BallSkin skin) {
        super(leftCharacter, leftStats, leftController, rightCharacter, rightStats, rightController,
                type, resolution, skin);
    }

    /**
     * Creates the only match.
     *
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if the resolution is not valid
     */
    @Override
    public void start() throws DOPOSoccerException {
        match = createMatch(rightCharacter);
    }

    /**
     * Tells whether the match ended.
     *
     * @return true once the match is finished
     */
    @Override
    public boolean isOver() {
        return match != null && match.isFinished();
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
}
