package domain;

import java.io.Serializable;

/**
 * Game mode: how many matches are played. It keeps the configuration chosen by the users
 * and creates each {@link Match} with it. A new mode is a new subclass.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public abstract class GameMode implements Serializable {

    /** Current match; the mode creates it. */
    protected Match match;
    /** Character of the left player. */
    protected final GameCharacter leftCharacter;
    /** Character of the right player (the first rival in a tournament). */
    protected final GameCharacter rightCharacter;
    /** Stats of the left profile. */
    protected final Stats leftStats;
    /** Stats of the right profile. */
    protected final Stats rightStats;
    /** Who controls the left player. */
    protected final PlayerController leftController;
    /** Who controls the right player. */
    protected final PlayerController rightController;
    /** Rule to end each match. */
    protected final MatchType type;
    /** Size of the field. */
    protected final String resolution;
    /** Ball chosen to play. */
    protected final BallSkin skin;

    /**
     * Keeps the configuration of the game.
     *
     * @param leftCharacter   character of the left player
     * @param leftStats       stats of the left player
     * @param leftController  who controls the left player
     * @param rightCharacter  character of the right player
     * @param rightStats      stats of the right player
     * @param rightController who controls the right player
     * @param type            rule to end each match
     * @param resolution      "SMALL", "MEDIUM" or "LARGE"
     * @param skin            ball chosen to play
     */
    public GameMode(GameCharacter leftCharacter, Stats leftStats, PlayerController leftController,
                    GameCharacter rightCharacter, Stats rightStats, PlayerController rightController,
                    MatchType type, String resolution, BallSkin skin) {
        this.leftCharacter = leftCharacter;
        this.leftStats = leftStats;
        this.leftController = leftController;
        this.rightCharacter = rightCharacter;
        this.rightStats = rightStats;
        this.rightController = rightController;
        this.type = type;
        this.resolution = resolution;
        this.skin = skin;
        this.match = null;
    }

    /**
     * Creates the first match.
     *
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if the resolution is not valid
     */
    public abstract void start() throws DOPOSoccerException;

    /**
     * Creates a match with the configuration and a rival on the right. It resets the rule first.
     *
     * @param rival character of the right player
     * @return the new match
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if the resolution is not valid
     */
    protected Match createMatch(GameCharacter rival) throws DOPOSoccerException {
        type.reset();
        return new Match(leftCharacter, leftStats, leftController, rival, rightStats, rightController,
                type, resolution, skin);
    }

    /**
     * Passes the tick to the current match.
     *
     * @param dt elapsed time, in seconds
     */
    public void update(double dt) {
        if (match != null) {
            match.update(dt);
        }
    }

    /**
     * Goes to the next match. By default a mode has only one.
     *
     * @throws DOPOSoccerException INVALID_STATE always, unless a subclass has more matches
     */
    public void nextMatch() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.INVALID_STATE);
    }

    /**
     * Tells whether the mode is over.
     *
     * @return true if no more matches will be played
     */
    public abstract boolean isOver();

    /**
     * Returns the current match.
     *
     * @return the match, or null before {@link #start()}
     */
    public Match getMatch() {
        return match;
    }

    /**
     * Returns the current round.
     *
     * @return 1 by default
     */
    public int getRound() {
        return 1;
    }

    /**
     * Returns the name of the mode.
     *
     * @return "ARCADE" or "TOURNAMENT"
     */
    public abstract String getName();
}
