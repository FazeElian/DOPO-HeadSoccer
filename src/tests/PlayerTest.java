package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Player}: life, stun, power bar, aura, effects and movement.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class PlayerTest {

    private Player player;
    private Stats stats;

    @BeforeEach
    public void setUp() {
        stats = new Stats();
        player = new Player("LEFT", new GameCharacter("Colombia", new IllusionShot()), stats,
                new HumanController(), 100, 100);
    }

    @Test
    public void shouldStartWithFullLifeAndEmptyPower() {
        assertEquals(Stats.BASE_LIFE, player.getLife());
        assertEquals(Stats.BASE_LIFE, player.getMaxLife());
        assertEquals(0, player.getPowerCharge(), 1e-9);
        assertFalse(player.hasAura());
        assertTrue(player.canAct());
        assertTrue(player.getStates().isEmpty());
    }

    @Test
    public void shouldLookTowardsTheRivalGoal() {
        assertEquals(1, player.getDirection(), 1e-9);
        Player right = new Player("RIGHT", new GameCharacter("Argentina", new LightningShot()), new Stats(),
                new HumanController(), 0, 0);
        assertEquals(-1, right.getDirection(), 1e-9);
    }

    @Test
    public void shouldLoseLife() {
        player.takeDamage(30);
        assertEquals(70, player.getLife());
    }

    @Test
    public void shouldIgnoreZeroOrNegativeDamage() {
        player.takeDamage(0);
        player.takeDamage(-20);
        assertEquals(Stats.BASE_LIFE, player.getLife());
    }

    @Test
    public void shouldBeStunnedWithoutLife() {
        player.takeDamage(500);
        assertEquals(0, player.getLife());
        assertFalse(player.canAct());
        assertEquals(List.of(StunEffect.NAME), player.getStates());
    }

    @Test
    public void shouldNotTakeDamageWhileStunned() {
        player.takeDamage(Stats.BASE_LIFE);
        player.takeDamage(10);
        assertEquals(0, player.getLife());
    }

    @Test
    public void shouldRecoverFullLifeAfterTheStun() {
        player.takeDamage(Stats.BASE_LIFE);
        player.update(StunEffect.DURATION + 0.1);
        assertEquals(Stats.BASE_LIFE, player.getLife());
        assertTrue(player.canAct());
        assertTrue(player.getStates().isEmpty());
    }

    @Test
    public void shouldHaveMoreLifeWithSurvival() throws DOPOSoccerException {
        stats.upgrade("SURVIVAL");
        Player strong = new Player("LEFT", new GameCharacter("Colombia", new IllusionShot()), stats,
                new HumanController(), 0, 0);
        assertEquals(110, strong.getMaxLife());
    }

    @Test
    public void shouldFillThePowerBarInPowerRateSeconds() {
        player.update(Player.POWER_RATE / 2);
        assertEquals(0.5, player.getPowerCharge(), 1e-9);
        assertFalse(player.isPowerReady());
        player.update(Player.POWER_RATE);
        assertEquals(1, player.getPowerCharge(), 1e-9);
        assertTrue(player.isPowerReady());
    }

    @Test
    public void shouldNotActivateThePowerWithoutAFullBar() {
        player.update(1);
        player.activatePower();
        assertFalse(player.hasAura());
    }

    @Test
    public void shouldSpendTheBarWhenTheAuraIsActivated() {
        player.update(Player.POWER_RATE);
        player.activatePower();
        assertTrue(player.hasAura());
        assertEquals(0, player.getPowerCharge(), 1e-9);
    }

    @Test
    public void shouldLoseTheAuraWhenItIsUsed() {
        player.update(Player.POWER_RATE);
        player.activatePower();
        player.useAura();
        assertFalse(player.hasAura());
    }

    @Test
    public void shouldGetAQuarterOfBarForAGoalAgainst() {
        player.onGoalAgainst();
        assertEquals(Player.GOAL_AGAINST_BONUS, player.getPowerCharge(), 1e-9);
    }

    @Test
    public void shouldNotPassAFullBar() {
        player.update(Player.POWER_RATE);
        player.onGoalAgainst();
        assertEquals(1, player.getPowerCharge(), 1e-9);
    }

    @Test
    public void shouldShowTheNameOfItsEffect() {
        player.applyEffect(new ElectrocutedEffect());
        assertEquals(List.of(ElectrocutedEffect.NAME), player.getStates());
        assertTrue(player.canAct());
    }

    @Test
    public void shouldLoseTheEffectWhenItExpires() {
        player.applyEffect(new ElectrocutedEffect());
        player.update(ElectrocutedEffect.DURATION + 0.1);
        assertTrue(player.getStates().isEmpty());
    }

    @Test
    public void shouldNotActWhilePushed() {
        player.applyEffect(new PushEffect());
        assertFalse(player.canAct());
    }

    @Test
    public void shouldRunAtItsSpeed() {
        player.moveRight();
        assertEquals(Player.BASE_SPEED, player.getVx(), 1e-9);
        player.moveLeft();
        assertEquals(-Player.BASE_SPEED, player.getVx(), 1e-9);
        player.stop();
        assertEquals(0, player.getVx(), 1e-9);
    }

    @Test
    public void shouldRunAtHalfSpeedWhenElectrocuted() {
        player.applyEffect(new ElectrocutedEffect());
        player.moveRight();
        assertEquals(Player.BASE_SPEED * ElectrocutedEffect.SPEED_FACTOR, player.getVx(), 1e-9);
    }

    @Test
    public void shouldJumpOnlyFromTheGround() {
        player.jump();
        assertEquals(0, player.getVy(), 1e-9);
        player.setOnGround(true);
        player.jump();
        assertEquals(-Player.BASE_JUMP, player.getVy(), 1e-9);
        assertFalse(player.isOnGround());
    }

    @Test
    public void shouldNotJumpWhenElectrocuted() {
        player.applyEffect(new ElectrocutedEffect());
        player.setOnGround(true);
        player.jump();
        assertEquals(0, player.getVy(), 1e-9);
    }
}
