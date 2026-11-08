package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Stats}: levels from 1 to 10, multipliers, life and upgrade costs.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class StatsTest {

    private Stats stats;

    @BeforeEach
    public void setUp() {
        stats = new Stats();
    }

    @Test
    public void shouldStartEveryStatAtTheMinimum() throws DOPOSoccerException {
        for (String stat : new String[]{"SPEED", "JUMP", "KICK", "SURVIVAL"}) {
            assertEquals(Stats.MIN_STAT, stats.get(stat));
        }
    }

    @Test
    public void shouldNotKnowAnUnknownStat() {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> stats.get("MAGIC"));
        assertEquals(DOPOSoccerException.UNKNOWN_STAT, e.getMessage());
    }

    @Test
    public void shouldNotKnowANullStat() {
        assertThrows(DOPOSoccerException.class, () -> stats.get(null));
    }

    @Test
    public void shouldHaveMultipliersOfOneAtLevelOne() {
        assertEquals(1.0, stats.getSpeedMultiplier(), 1e-9);
        assertEquals(1.0, stats.getJumpMultiplier(), 1e-9);
        assertEquals(1.0, stats.getKickMultiplier(), 1e-9);
    }

    @Test
    public void shouldHaveBaseLifeAtLevelOne() {
        assertEquals(Stats.BASE_LIFE, stats.getMaxLife());
    }

    @Test
    public void shouldAddTenPercentForEachLevel() throws DOPOSoccerException {
        stats.upgrade("SPEED");
        stats.upgrade("SPEED");
        assertEquals(3, stats.get("SPEED"));
        assertEquals(1.2, stats.getSpeedMultiplier(), 1e-9);
    }

    @Test
    public void shouldGiveMoreLifeWithSurvival() throws DOPOSoccerException {
        stats.upgrade("SURVIVAL");
        assertEquals(110, stats.getMaxLife());
    }

    @Test
    public void shouldUpgradeOnlyTheChosenStat() throws DOPOSoccerException {
        stats.upgrade("KICK");
        assertEquals(2, stats.get("KICK"));
        assertEquals(1, stats.get("SPEED"));
        assertEquals(1, stats.get("JUMP"));
        assertEquals(1, stats.get("SURVIVAL"));
    }

    @Test
    public void shouldCostTheBaseForTheFirstUpgrade() throws DOPOSoccerException {
        assertEquals(Stats.BASE_UPGRADE_COST, stats.getUpgradeCost("JUMP"));
    }

    @Test
    public void shouldDoubleTheCostForEachLevel() throws DOPOSoccerException {
        stats.upgrade("JUMP");
        assertEquals(1000, stats.getUpgradeCost("JUMP"));
        stats.upgrade("JUMP");
        assertEquals(2000, stats.getUpgradeCost("JUMP"));
    }

    @Test
    public void shouldNotUpgradeAboveTheMaximum() throws DOPOSoccerException {
        for (int i = Stats.MIN_STAT; i < Stats.MAX_STAT; i++) {
            stats.upgrade("SPEED");
        }
        assertEquals(Stats.MAX_STAT, stats.get("SPEED"));
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, () -> stats.upgrade("SPEED"));
        assertEquals(DOPOSoccerException.STAT_AT_MAX, e.getMessage());
        assertEquals(Stats.MAX_STAT, stats.get("SPEED"));
    }

    @Test
    public void shouldNotUpgradeAnUnknownStat() {
        assertThrows(DOPOSoccerException.class, () -> stats.upgrade("MAGIC"));
    }
}
