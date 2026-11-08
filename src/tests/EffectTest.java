package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Effect} and its five effects: duration, what the player can do and the push or launch.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class EffectTest {

    private static Player newPlayer(String side) {
        return new Player(side, new GameCharacter("Colombia", new IllusionShot()), new Stats(),
                new HumanController(), 100, 100);
    }

    @Test
    public void shouldCountDownAndExpire() {
        Effect stun = new StunEffect();
        assertEquals(StunEffect.DURATION, stun.getRemaining(), 1e-9);
        assertFalse(stun.isExpired());
        stun.update(1);
        assertEquals(StunEffect.DURATION - 1, stun.getRemaining(), 1e-9);
        stun.update(10);
        assertEquals(0, stun.getRemaining(), 1e-9);
        assertTrue(stun.isExpired());
    }

    @Test
    public void shouldNotLetAStunnedPlayerActNorJump() {
        Effect stun = new StunEffect();
        assertFalse(stun.canAct());
        assertFalse(stun.canJump());
        assertEquals("Aturdido", stun.getName());
    }

    @Test
    public void shouldMakeAnElectrocutedPlayerSlowAndUnableToJump() {
        Effect e = new ElectrocutedEffect();
        assertTrue(e.canAct());
        assertFalse(e.canJump());
        assertEquals(ElectrocutedEffect.SPEED_FACTOR, e.getSpeedFactor(), 1e-9);
        assertEquals("Electrocutado", e.getName());
    }

    @Test
    public void shouldNotChangeAnythingWithIllusion() {
        Effect e = new IllusionEffect();
        assertTrue(e.canAct());
        assertTrue(e.canJump());
        assertEquals(1.0, e.getSpeedFactor(), 1e-9);
        assertEquals(IllusionEffect.DURATION, e.getRemaining(), 1e-9);
    }

    @Test
    public void shouldPushTheLeftPlayerTowardsItsOwnGoal() {
        Player p = newPlayer("LEFT");
        new PushEffect().onApply(p);
        assertEquals(-PushEffect.PUSH_SPEED, p.getVx(), 1e-9);
        assertEquals(-PushEffect.LIFT_SPEED, p.getVy(), 1e-9);
        assertFalse(p.isOnGround());
    }

    @Test
    public void shouldPushTheRightPlayerTowardsItsOwnGoal() {
        Player p = newPlayer("RIGHT");
        new PushEffect().onApply(p);
        assertEquals(PushEffect.PUSH_SPEED, p.getVx(), 1e-9);
    }

    @Test
    public void shouldNotLetAPushedPlayerAct() {
        assertFalse(new PushEffect().canAct());
    }

    @Test
    public void shouldLaunchAnUnconsciousPlayerUp() {
        Player p = newPlayer("LEFT");
        p.setOnGround(true);
        new UnconsciousEffect().onApply(p);
        assertEquals(-UnconsciousEffect.LAUNCH_SPEED, p.getVy(), 1e-9);
        assertFalse(p.isOnGround());
        assertFalse(new UnconsciousEffect().canAct());
        assertFalse(new UnconsciousEffect().canJump());
    }
}
