package domain;

import domain.enums.MatchKind;
import domain.enums.Side;

/**
 * Match by time: it lasts the chosen minutes and the side with more goals wins.
 * If there is a tie when time is over, it goes to sudden death: the first goal wins.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class TimeMatch extends MatchType {

    /** Duration of the match. */
    private final int seconds;
    /** Whether the time is over with a tie. */
    private boolean suddenDeath;

    /**
     * Creates a match by time.
     *
     * @param minutes duration of the match
     * @throws DOPOSoccerException INVALID_MINUTES if it is 0 or less
     */
    public TimeMatch(int minutes) throws DOPOSoccerException {
        super();
        if (minutes <= 0) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_MINUTES);
        }
        this.seconds = minutes * 60;
        this.suddenDeath = false;
    }

    /**
     * Tells whether the match ended: the time is over and there is no tie.
     * If there is a tie, it starts the sudden death.
     *
     * @param match the match being played
     * @return true if it ended
     */
    @Override
    public boolean isMatchOver(Match match) {
        if (match.getElapsed() < seconds) {
            return false;
        }
        if (match.getScoreBoard().isTied()) {
            suddenDeath = true;
            return false;
        }
        return true;
    }

    /**
     * Returns the side with more goals.
     *
     * @param match the match being played
     * @return "LEFT", "RIGHT" or null if it is a tie
     */
    @Override
    public String getWinner(Match match) {
        ScoreBoard score = match.getScoreBoard();
        if (score.isTied()) {
            return null;
        }
        return score.getGoals(Side.LEFT.getName()) > score.getGoals(Side.RIGHT.getName()) ? Side.LEFT.getName() : Side.RIGHT.getName();
    }

    /**
     * Returns the seconds left.
     *
     * @param match the match being played
     * @return seconds left, 0 in sudden death
     */
    @Override
    public int getTimeLeft(Match match) {
        return (int) Math.ceil(Math.max(0, seconds - match.getElapsed()));
    }

    /**
     * Tells whether the match is in sudden death.
     *
     * @return true in sudden death
     */
    @Override
    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    /**
     * Returns the duration of the match.
     *
     * @return duration, in seconds (the chosen minutes times 60)
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Removes the sudden death for a new match.
     */
    @Override
    public void reset() {
        suddenDeath = false;
    }

    /**
     * Returns the name of the rule.
     *
     * @return "TIME"
     */
    @Override
    public String getName() {
        return MatchKind.TIME.getName();
    }
}
