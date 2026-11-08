package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ScoreBoard}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class ScoreBoardTest {

    private ScoreBoard score;

    @BeforeEach
    public void setUp() {
        score = new ScoreBoard();
    }

    @Test
    public void shouldStartAtZeroAndTied() {
        assertEquals(0, score.getGoals("LEFT"));
        assertEquals(0, score.getGoals("RIGHT"));
        assertTrue(score.isTied());
    }

    @Test
    public void shouldCountTheGoalsOfEachSide() {
        score.addGoal("LEFT");
        score.addGoal("LEFT");
        score.addGoal("RIGHT");
        assertEquals(2, score.getGoals("LEFT"));
        assertEquals(1, score.getGoals("RIGHT"));
        assertFalse(score.isTied());
    }

    @Test
    public void shouldIgnoreAnUnknownSide() {
        score.addGoal("CENTER");
        assertEquals(0, score.getGoals("LEFT"));
        assertEquals(0, score.getGoals("RIGHT"));
        assertEquals(0, score.getGoals("CENTER"));
    }

    @Test
    public void shouldGoBackToZeroWhenReset() {
        score.addGoal("LEFT");
        score.addGoal("RIGHT");
        score.reset();
        assertEquals(0, score.getGoals("LEFT"));
        assertEquals(0, score.getGoals("RIGHT"));
        assertTrue(score.isTied());
    }
}
