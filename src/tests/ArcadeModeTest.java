package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ArcadeMode}: it has one match, it ends with that match and it has no next match.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ArcadeModeTest {

    private static ArcadeMode newMode(String resolution) throws DOPOSoccerException {
        return new ArcadeMode(new GameCharacter("Colombia", new IllusionShot()), new Stats(), new HumanController(),
                new GameCharacter("Argentina", new LightningShot()), new Stats(), new HumanController(),
                new GoalsMatch(1), resolution, new BallSkin("Clásico", 0));
    }

    @Test
    public void shouldHaveNoMatchBeforeStarting() throws DOPOSoccerException {
        ArcadeMode mode = newMode("MEDIUM");
        assertNull(mode.getMatch());
        assertFalse(mode.isOver());
        assertEquals("ARCADE", mode.getName());
    }

    @Test
    public void shouldCreateItsMatchWhenStarting() throws DOPOSoccerException {
        ArcadeMode mode = newMode("MEDIUM");
        mode.start();
        assertNotNull(mode.getMatch());
        assertEquals(1, mode.getRound());
        assertFalse(mode.isOver());
    }

    @Test
    public void shouldEndWithItsMatch() throws DOPOSoccerException {
        ArcadeMode mode = newMode("MEDIUM");
        mode.start();
        mode.getMatch().getScoreBoard().addGoal("RIGHT");
        assertTrue(mode.isOver());
    }

    @Test
    public void shouldNotHaveANextMatch() throws DOPOSoccerException {
        ArcadeMode mode = newMode("MEDIUM");
        mode.start();
        try {
            mode.nextMatch();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_STATE, e.getMessage());
        }
    }

    @Test
    public void shouldNotStartWithAnUnknownResolution() throws DOPOSoccerException {
        ArcadeMode mode = newMode("HUGE");
        try {
            mode.start();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_RESOLUTION, e.getMessage());
        }
        assertNull(mode.getMatch());
    }
}
