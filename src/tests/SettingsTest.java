package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Settings}: volumes from 0 to 100.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class SettingsTest {

    private Settings settings;

    @BeforeEach
    public void setUp() {
        settings = new Settings();
    }

    @Test
    public void shouldStartWithTheDefaultVolumes() {
        assertEquals(Settings.DEFAULT_MUSIC_VOLUME, settings.getMusicVolume());
        assertEquals(Settings.DEFAULT_EFFECTS_VOLUME, settings.getEffectsVolume());
    }

    @Test
    public void shouldChangeTheVolumes() throws DOPOSoccerException {
        settings.setMusicVolume(30);
        settings.setEffectsVolume(90);
        assertEquals(30, settings.getMusicVolume());
        assertEquals(90, settings.getEffectsVolume());
    }

    @Test
    public void shouldAcceptTheLimits() throws DOPOSoccerException {
        settings.setMusicVolume(Settings.MIN_VOLUME);
        settings.setEffectsVolume(Settings.MAX_VOLUME);
        assertEquals(0, settings.getMusicVolume());
        assertEquals(100, settings.getEffectsVolume());
    }

    @Test
    public void shouldNotAcceptAVolumeOutOfRange() {
        try {
            settings.setMusicVolume(101);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_VOLUME, e.getMessage());
        }
        try {
            settings.setEffectsVolume(-1);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_VOLUME, e.getMessage());
        }
    }

    @Test
    public void shouldKeepTheVolumeWhenTheNewOneIsInvalid() throws DOPOSoccerException {
        settings.setMusicVolume(40);
        try {
            settings.setMusicVolume(500);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_VOLUME, e.getMessage());
        }
        assertEquals(40, settings.getMusicVolume());
    }

    @Test
    public void shouldGoBackToTheDefaultsWhenReset() throws DOPOSoccerException {
        settings.setMusicVolume(10);
        settings.setEffectsVolume(20);
        settings.reset();
        assertEquals(Settings.DEFAULT_MUSIC_VOLUME, settings.getMusicVolume());
        assertEquals(Settings.DEFAULT_EFFECTS_VOLUME, settings.getEffectsVolume());
    }
}
