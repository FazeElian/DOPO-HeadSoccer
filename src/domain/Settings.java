package domain;

import java.io.Serializable;

/**
 * Settings of the game: volume of the music and of the sound effects, from 0 to 100.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Settings implements Serializable {

    /** Volume of the music when the game starts or the settings are reset. */
    public static final int DEFAULT_MUSIC_VOLUME = 70;
    /** Volume of the sound effects when the game starts or the settings are reset. */
    public static final int DEFAULT_EFFECTS_VOLUME = 70;
    /** Lowest volume (silence). */
    public static final int MIN_VOLUME = 0;
    /** Highest volume. */
    public static final int MAX_VOLUME = 100;

    /** Volume of the music. */
    private int musicVolume;
    /** Volume of the sound effects. */
    private int effectsVolume;

    /**
     * Creates the settings with the default volumes.
     */
    public Settings() {
        this.musicVolume = DEFAULT_MUSIC_VOLUME;
        this.effectsVolume = DEFAULT_EFFECTS_VOLUME;
    }

    /**
     * Returns the volume of the music.
     *
     * @return the volume, from 0 to 100
     */
    public int getMusicVolume() {
        return musicVolume;
    }

    /**
     * Changes the volume of the music.
     *
     * @param volume new volume, from 0 to 100
     * @throws DOPOSoccerException INVALID_VOLUME if it is out of 0..100
     */
    public void setMusicVolume(int volume) throws DOPOSoccerException {
        musicVolume = checked(volume);
    }

    /**
     * Returns the volume of the sound effects.
     *
     * @return the volume, from 0 to 100
     */
    public int getEffectsVolume() {
        return effectsVolume;
    }

    /**
     * Changes the volume of the sound effects.
     *
     * @param volume new volume, from 0 to 100
     * @throws DOPOSoccerException INVALID_VOLUME if it is out of 0..100
     */
    public void setEffectsVolume(int volume) throws DOPOSoccerException {
        effectsVolume = checked(volume);
    }

    /**
     * Puts both volumes back to their default values.
     */
    public void reset() {
        musicVolume = DEFAULT_MUSIC_VOLUME;
        effectsVolume = DEFAULT_EFFECTS_VOLUME;
    }

    /**
     * Checks that a volume is between 0 and 100.
     *
     * @param volume the volume
     * @return the same volume
     * @throws DOPOSoccerException INVALID_VOLUME if it is out of 0..100
     */
    private static int checked(int volume) throws DOPOSoccerException {
        if (volume < MIN_VOLUME || volume > MAX_VOLUME) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_VOLUME);
        }
        return volume;
    }
}
