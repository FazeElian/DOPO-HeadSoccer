package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link HumanController}: the keys pressed are the actions of the player.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class HumanControllerTest {

    private HumanController controller;

    @BeforeEach
    public void setUp() {
        controller = new HumanController();
    }

    @Test
    public void shouldStartWithoutActions() {
        assertTrue(controller.nextActions(null, null).isEmpty());
    }

    @Test
    public void shouldReturnThePressedActions() {
        controller.press("JUMP");
        controller.press("KICK");
        assertEquals(Set.of("JUMP", "KICK"), controller.nextActions(null, null));
    }

    @Test
    public void shouldForgetAReleasedAction() {
        controller.press("MOVE_LEFT");
        controller.press("KICK");
        controller.release("MOVE_LEFT");
        assertEquals(Set.of("KICK"), controller.nextActions(null, null));
    }

    @Test
    public void shouldNotRepeatAnActionPressedTwice() {
        controller.press("JUMP");
        controller.press("JUMP");
        assertEquals(1, controller.nextActions(null, null).size());
    }

    @Test
    public void shouldReturnACopy() {
        controller.press("JUMP");
        controller.nextActions(null, null).clear();
        assertEquals(Set.of("JUMP"), controller.nextActions(null, null));
    }
}
