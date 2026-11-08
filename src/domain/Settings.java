package domain;

import java.io.Serializable;

/**
 * Settings of the game: volume of the music and of the sound effects, from 0 to 100.
 * Under construction: its methods throw {@link DOPOSoccerException#UNDER_CONSTRUCTION}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Settings implements Serializable {

    /** Volume of the music when the game starts or the settings are reset. */
    public static final int DEFAULT_MUSIC_VOLUME = 100;
    /** Volume of the sound effects when the game starts or the settings are reset. */
    public static final int DEFAULT_EFFECTS_VOLUME = 100;

    /** Volume of the music. */
    private int musicVolume;
    /** Volume of the sound effects. */
    private int effectsVolume;

    /**
     * Creates the settings with both volumes at 100.
     */
    public Settings() {
        this.musicVolume = DEFAULT_MUSIC_VOLUME;
        this.effectsVolume = DEFAULT_EFFECTS_VOLUME;
    }

    /**
     * Returns the volume of the music. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getMusicVolume() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Changes the volume of the music. Under construction.
     *
     * @param volume new volume, from 0 to 100
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void setMusicVolume(int volume) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the volume of the sound effects. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getEffectsVolume() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Changes the volume of the sound effects. Under construction.
     *
     * @param volume new volume, from 0 to 100
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void setEffectsVolume(int volume) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Puts both volumes back to 100. Under construction.
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void reset() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }
}
