package presentation;

import javax.sound.sampled.*;
import java.io.File;

/** Plays the sound effects and the music of the game (WAV files in assets/sounds). */
public class SoundPlayer {
    private Clip music;
    private float effectsVolume = 1f; // 0..1
    private float musicVolume = 1f;

    // Short effect: a new clip each time, so two kicks can sound at the same time
    public void play(String name) {
        try {
            Clip clip = open("assets/sounds/" + name + ".wav");
            setVolume(clip, effectsVolume);
            clip.addLineListener(e -> { if (e.getType() == LineEvent.Type.STOP) clip.close(); });
            clip.start();
        } catch (Exception e) {
            // without the sound the game goes on
        }
    }

    // Music in a loop
    public void startMusic(String name) {
        stopMusic();
        try {
            music = open("assets/sounds/" + name + ".wav");
            setVolume(music, musicVolume);
            music.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            music = null;
        }
    }

    public void stopMusic() {
        if (music != null) { music.stop(); music.close(); music = null; }
    }

    // Volumes 0..100 (from the settings sliders)
    public void setMusicVolume(int percent) {
        musicVolume = percent / 100f;
        if (music != null) setVolume(music, musicVolume);
    }

    public void setEffectsVolume(int percent) {
        effectsVolume = percent / 100f;
    }

    private Clip open(String path) throws Exception {
        AudioInputStream in = AudioSystem.getAudioInputStream(new File(path));
        Clip clip = AudioSystem.getClip();
        clip.open(in);
        return clip;
    }

    // 0..1 -> decibels (0 = silence)
    private void setVolume(Clip clip, float volume) {
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float db = volume <= 0 ? gain.getMinimum() : (float) (20 * Math.log10(volume));
        gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
    }
}