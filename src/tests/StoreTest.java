package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the store: {@link Wallet} and what a {@link PlayerProfile} buys with its points.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class StoreTest {

    private PlayerProfile profile;
    private BallSkin classic;
    private BallSkin golden;
    private GameCharacter spain;

    @BeforeEach
    public void setUp() {
        classic = new BallSkin("Clásico", 0);
        golden = new BallSkin("Dorado", 500);
        spain = new GameCharacter("España", new BlueAuraShot(), 800);
        profile = new PlayerProfile("J1", classic);
    }

    // wallet

    @Test
    public void shouldAddAndSpendPoints() throws DOPOSoccerException {
        Wallet wallet = new Wallet(100);
        wallet.add(50);
        wallet.spend(120);
        assertEquals(30, wallet.getPoints());
    }

    @Test
    public void shouldNotSpendMoreThanItHas() {
        Wallet wallet = new Wallet(100);
        try {
            wallet.spend(101);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
        assertEquals(100, wallet.getPoints());
    }

    @Test
    public void shouldIgnoreNegativePoints() {
        Wallet wallet = new Wallet(-20);
        wallet.add(-5);
        assertEquals(0, wallet.getPoints());
    }

    // profile

    @Test
    public void shouldStartWithTheFreeThings() {
        assertEquals(0, profile.getPoints());
        assertTrue(profile.isBallUnlocked(classic));
        assertFalse(profile.isBallUnlocked(golden));
        assertFalse(profile.isCharacterUnlocked(spain));
        assertTrue(profile.isCharacterUnlocked(new GameCharacter("Colombia", new IllusionShot())));
        assertEquals(classic, profile.getSelectedBall());
    }

    @Test
    public void shouldBuyACharacter() throws DOPOSoccerException {
        profile.addPoints(1000);
        profile.unlockCharacter(spain);
        assertTrue(profile.isCharacterUnlocked(spain));
        assertEquals(200, profile.getPoints());
    }

    @Test
    public void shouldNotBuyACharacterWithoutPoints() {
        profile.addPoints(799);
        try {
            profile.unlockCharacter(spain);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
        assertFalse(profile.isCharacterUnlocked(spain));
        assertEquals(799, profile.getPoints());
    }

    @Test
    public void shouldNotBuyTwice() throws DOPOSoccerException {
        profile.addPoints(2000);
        profile.unlockCharacter(spain);
        try {
            profile.unlockCharacter(spain);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.ALREADY_OWNED, e.getMessage());
        }
        assertEquals(1200, profile.getPoints());
    }

    @Test
    public void shouldBuyAndSelectABall() throws DOPOSoccerException {
        try {
            profile.selectBall(golden);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.BALL_LOCKED, e.getMessage());
        }
        profile.addPoints(500);
        profile.unlockBall(golden);
        profile.selectBall(golden);
        assertEquals(golden, profile.getSelectedBall());
        assertEquals(0, profile.getPoints());
    }

    @Test
    public void shouldPayToUpgradeAStat() throws DOPOSoccerException {
        profile.addPoints(1600);
        profile.upgrade("SPEED");   // 500
        profile.upgrade("SPEED");   // 1000
        assertEquals(3, profile.getStats().get("SPEED"));
        assertEquals(100, profile.getPoints());
        try {
            profile.upgrade("SPEED");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
        assertEquals(3, profile.getStats().get("SPEED"));
    }

    @Test
    public void shouldNotChargeForAStatAtItsMaximum() throws DOPOSoccerException {
        profile.addPoints(1_000_000);
        for (int i = 0; i < 9; i++) {
            profile.upgrade("KICK");
        }
        int points = profile.getPoints();
        try {
            profile.upgrade("KICK");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.STAT_AT_MAX, e.getMessage());
        }
        try {
            profile.getUpgradeCost("KICK");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.STAT_AT_MAX, e.getMessage());
        }
        assertEquals(points, profile.getPoints());
    }
}
