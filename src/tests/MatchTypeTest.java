package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests of the rules of a match: {@link TimeMatch} (with sudden death) and {@link GoalsMatch}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class MatchTypeTest {

    private static Match newMatch(MatchType type) throws DOPOSoccerException {
        return new Match(new GameCharacter("Colombia", new IllusionShot()), new Stats(), new HumanController(),
                new GameCharacter("Argentina", new LightningShot()), new Stats(), new HumanController(),
                type, "MEDIUM", new BallSkin("Clásico", 0));
    }

    // Advances a match the given seconds, 60 ticks per second
    private static void play(Match match, double seconds) {
        for (int i = 0; i < Math.round(seconds * 60); i++) {
            match.update(1 / 60.0);
        }
    }

    @Test
    public void shouldNotAcceptZeroMinutes() {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> new TimeMatch(0));
        assertEquals(DOPOSoccerException.INVALID_MINUTES, e.getMessage());
    }

    @Test
    public void shouldNotAcceptZeroGoals() {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> new GoalsMatch(0));
        assertEquals(DOPOSoccerException.INVALID_GOAL_TARGET, e.getMessage());
    }

    @Test
    public void shouldKeepTheTimeInSeconds() throws DOPOSoccerException {
        TimeMatch type = new TimeMatch(2);
        assertEquals(120, type.getSeconds());
        assertEquals("TIME", type.getName());
    }

    @Test
    public void shouldCountTheTimeDown() throws DOPOSoccerException {
        Match match = newMatch(new TimeMatch(1));
        assertEquals(60, match.getType().getTimeLeft(match));
        play(match, 10);
        assertEquals(50, match.getType().getTimeLeft(match));
        assertFalse(match.isFinished());
    }

    @Test
    public void shouldGoToSuddenDeathWhenTiedAtTheEnd() throws DOPOSoccerException {
        Match match = newMatch(new TimeMatch(1));
        play(match, 61);
        assertTrue(match.getScoreBoard().isTied());
        assertFalse(match.isFinished());
        assertTrue(match.getType().isSuddenDeath());
        assertEquals(0, match.getType().getTimeLeft(match));
    }

    @Test
    public void shouldEndTheSuddenDeathWithTheNextGoal() throws DOPOSoccerException {
        Match match = newMatch(new TimeMatch(1));
        play(match, 61);
        match.getScoreBoard().addGoal("RIGHT");
        assertTrue(match.isFinished());
        assertEquals("RIGHT", match.getWinner());
    }

    @Test
    public void shouldEndAtTheTimeWhenSomeoneWins() throws DOPOSoccerException {
        Match match = newMatch(new TimeMatch(1));
        match.getScoreBoard().addGoal("LEFT");
        play(match, 61);
        assertTrue(match.isFinished());
        assertFalse(match.getType().isSuddenDeath());
        assertEquals("LEFT", match.getWinner());
    }

    @Test
    public void shouldNotHaveWinnerBeforeTheEnd() throws DOPOSoccerException {
        Match match = newMatch(new TimeMatch(1));
        match.getScoreBoard().addGoal("LEFT");
        assertNull(match.getWinner());
    }

    @Test
    public void shouldLeaveSuddenDeathWhenReset() throws DOPOSoccerException {
        Match match = newMatch(new TimeMatch(1));
        play(match, 61);
        match.getType().reset();
        assertFalse(match.getType().isSuddenDeath());
    }

    @Test
    public void shouldEndWhenSomeoneGetsTheTarget() throws DOPOSoccerException {
        Match match = newMatch(new GoalsMatch(2));
        match.getScoreBoard().addGoal("LEFT");
        assertFalse(match.isFinished());
        match.getScoreBoard().addGoal("LEFT");
        assertTrue(match.isFinished());
        assertEquals("LEFT", match.getWinner());
    }

    @Test
    public void shouldHaveNoTimeLimitByGoals() throws DOPOSoccerException {
        Match match = newMatch(new GoalsMatch(3));
        assertEquals(-1, match.getType().getTimeLeft(match));
        assertEquals("GOALS", match.getType().getName());
        assertEquals(3, ((GoalsMatch) match.getType()).getTarget());
    }

    @Test
    public void shouldHaveNoWinnerWhenEndedByHand() throws DOPOSoccerException {
        Match match = newMatch(new GoalsMatch(1));
        match.end();
        match.getScoreBoard().addGoal("RIGHT");
        assertTrue(match.isFinished());
        assertTrue(match.wasEnded());
        assertNull(match.getWinner());
    }
}
