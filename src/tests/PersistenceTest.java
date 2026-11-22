package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests of the persistence in {@link DOPOSoccer}: the profiles of both players are saved in a fixed
 * progress file and loaded again, and a game being played is saved in a file and opened again.
 * Every test uses its own temporary folder, so the real files of the game are never touched.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class PersistenceTest {

    /** Temporary folder of each test, deleted by JUnit at the end. */
    @TempDir
    File folder;

    /**
     * Writes a progress file where player 1 has some points, like the one the game saves,
     * so the tests have points to spend in the store.
     *
     * @param file   the progress file
     * @param points points of player 1
     * @throws IOException if the file cannot be written
     */
    private static void writeProgress(File file, int points) throws IOException {
        BallSkin classic = new BallSkin(DOPOSoccer.DEFAULT_BALL, 0);
        PlayerProfile[] profiles = {new PlayerProfile("J1", classic), new PlayerProfile("J2", classic)};
        profiles[0].addPoints(points);
        try (FileOutputStream stream = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(profiles);
        }
    }

    /**
     * Writes a file that is not a saved game nor a progress file.
     *
     * @param file the file
     * @throws IOException if the file cannot be written
     */
    private static void writeGarbage(File file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write("esto no es una partida".getBytes());
        }
    }

    /**
     * Advances a game some frames of 1/60 s.
     *
     * @param game   the game
     * @param frames how many frames
     */
    private static void play(DOPOSoccer game, int frames) {
        for (int i = 0; i < frames; i++) {
            game.update(1 / 60.0);
        }
    }

    // progress (profiles)

    @Test
    public void shouldNotHaveProgressWithoutAFile() {
        DOPOSoccer game = new DOPOSoccer(new File(folder, "progress"));
        assertFalse(game.hasSavedProgress());
    }

    @Test
    public void shouldCreateTheProgressFileWhenSaving() throws DOPOSoccerException {
        DOPOSoccer game = new DOPOSoccer(new File(folder, "progress"));
        game.saveProgress();
        assertTrue(game.hasSavedProgress());
    }

    @Test
    public void shouldKeepTheProfilesInAnotherGame() throws IOException, DOPOSoccerException {
        File file = new File(folder, "progress");
        writeProgress(file, 3000);

        DOPOSoccer first = new DOPOSoccer(file);
        first.loadProgress();
        first.upgrade("LEFT", "SPEED");
        first.buyCharacter("LEFT", "España");
        first.buyBall("LEFT", "Dorado");
        first.selectBall("LEFT", "Dorado");
        int points = first.getPoints("LEFT");
        first.saveProgress();

        DOPOSoccer second = new DOPOSoccer(file);
        assertTrue(second.hasSavedProgress());
        second.loadProgress();
        assertEquals(points, second.getPoints("LEFT"));
        assertEquals(Stats.MIN_STAT + 1, second.getStat("LEFT", "SPEED"));
        assertTrue(second.isCharacterUnlocked("LEFT", "España"));
        assertTrue(second.isBallUnlocked("LEFT", "Dorado"));
        assertEquals("Dorado", second.getSelectedBall("LEFT"));
        assertEquals(0, second.getPoints("RIGHT"));
    }

    @Test
    public void shouldNotSaveTheVolumes() throws DOPOSoccerException {
        File file = new File(folder, "progress");
        DOPOSoccer first = new DOPOSoccer(file);
        first.setMusicVolume(30);
        first.saveProgress();

        DOPOSoccer second = new DOPOSoccer(file);
        second.loadProgress();
        assertEquals(Settings.DEFAULT_MUSIC_VOLUME, second.getMusicVolume());
    }

    @Test
    public void shouldKeepTheProfilesWhenTheFileIsDamaged() throws IOException, DOPOSoccerException {
        File file = new File(folder, "progress");
        writeProgress(file, 3000);
        DOPOSoccer game = new DOPOSoccer(file);
        game.loadProgress();

        writeGarbage(file);
        try {
            game.loadProgress();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.LOAD_FAILED, e.getMessage());
        }
        assertEquals(3000, game.getPoints("LEFT"));
        assertEquals(Stats.MIN_STAT, game.getStat("LEFT", "SPEED"));
    }

    @Test
    public void shouldNotSaveTheProgressWhereItCannotWrite() {
        // A folder cannot be opened as a file to write in it
        DOPOSoccer game = new DOPOSoccer(folder);
        try {
            game.saveProgress();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.SAVE_FAILED, e.getMessage());
        }
    }

    // saved games

    @Test
    public void shouldOpenASavedGameWhereItWas() throws DOPOSoccerException {
        File saved = new File(folder, "partida.dat");
        DOPOSoccer first = new DOPOSoccer(new File(folder, "progress1"));
        first.selectStadium("rain");
        first.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "TIME", 1);
        play(first, 600); // 10 seconds
        first.pause();
        double leftX = first.getPlayerX("LEFT");
        first.saveGame(saved);

        DOPOSoccer second = DOPOSoccer.openGame(saved);
        assertEquals("ARCADE", second.getMode());
        assertEquals("PVP", second.getModality());
        assertEquals("TIME", second.getMatchType());
        assertEquals("Colombia", second.getCharacter("LEFT"));
        assertEquals("Argentina", second.getCharacter("RIGHT"));
        assertEquals(50, second.getTimeLeft());
        assertEquals(leftX, second.getPlayerX("LEFT"), 1e-9);
        assertEquals("rain", second.getStadium()); // the stadium travels with the match
        assertTrue(second.isPaused()); // it opens paused
    }

    @Test
    public void shouldGoOnPlayingAnOpenedGame() throws DOPOSoccerException {
        File saved = new File(folder, "partida.dat");
        DOPOSoccer first = new DOPOSoccer(new File(folder, "progress1"));
        first.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "TIME", 1);
        play(first, 600);
        first.pause();
        first.saveGame(saved);

        DOPOSoccer second = DOPOSoccer.openGame(saved);
        second.resume();
        play(second, 600); // the physics world is built again from the saved players and ball
        assertEquals(40, second.getTimeLeft());
        assertFalse(second.isMatchOver());
    }

    @Test
    public void shouldOpenASavedTournament() throws DOPOSoccerException {
        File saved = new File(folder, "torneo.dat");
        DOPOSoccer first = new DOPOSoccer(new File(folder, "progress1"));
        first.newGame("PVM", "CLUMSY", "MEDIUM", "TOURNAMENT", "Colombia", "Colombia", "GOALS", 3);
        first.saveGame(saved);

        DOPOSoccer second = DOPOSoccer.openGame(saved);
        assertEquals("TOURNAMENT", second.getMode());
        assertEquals(first.getTournamentTeams(), second.getTournamentTeams());
        assertEquals(first.getTournamentRival(), second.getTournamentRival());
        assertEquals("CUARTOS", second.getTournamentRound());
        assertEquals(3, second.getGoalTarget());
    }

    @Test
    public void shouldOpenASavedSurvivalSeries() throws DOPOSoccerException {
        File saved = new File(folder, "supervivencia.dat");
        DOPOSoccer first = new DOPOSoccer(new File(folder, "progress1"));
        first.newGame("PVM", "CLUMSY", "MEDIUM", "ARCADE", "Colombia", "Argentina", "SURVIVAL", 0);
        first.saveGame(saved);

        DOPOSoccer second = DOPOSoccer.openGame(saved);
        assertEquals("SURVIVAL", second.getMatchType());
        assertEquals(1, second.getRound());
        assertEquals(SurvivalMatch.INITIAL_BALLS, second.getBalls("LEFT"));
        assertEquals(SurvivalMatch.INITIAL_BALLS, second.getBalls("RIGHT"));
    }

    @Test
    public void shouldNotSaveAFinishedGame() throws DOPOSoccerException {
        DOPOSoccer game = new DOPOSoccer(new File(folder, "progress"));
        game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3);
        game.endGame();
        try {
            game.saveGame(new File(folder, "partida.dat"));
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_OVER, e.getMessage());
        }
    }

    @Test
    public void shouldNotSaveAGameWhereItCannotWrite() throws DOPOSoccerException {
        DOPOSoccer game = new DOPOSoccer(new File(folder, "progress"));
        game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3);
        try {
            game.saveGame(folder);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_SAVE_FAILED, e.getMessage());
        }
    }

    @Test
    public void shouldNotOpenAFileThatIsNotAGame() throws IOException {
        File garbage = new File(folder, "basura.dat");
        writeGarbage(garbage);
        File progress = new File(folder, "progress");
        writeProgress(progress, 100); // a progress file is not a saved game either

        for (File file : new File[]{garbage, progress}) {
            try {
                DOPOSoccer.openGame(file);
                fail("Debió lanzar DOPOSoccerException");
            } catch (DOPOSoccerException e) {
                assertEquals(DOPOSoccerException.GAME_OPEN_FAILED, e.getMessage());
            }
        }
    }

    @Test
    public void shouldOpenAGameWithTheNewestProfiles() throws IOException, DOPOSoccerException {
        File progress = new File(folder, "progress");
        writeProgress(progress, 3000);
        File saved = new File(folder, "partida.dat");

        DOPOSoccer first = new DOPOSoccer(progress);
        first.loadProgress();
        first.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3);
        first.saveGame(saved);
        // after saving the game, player 1 keeps buying: the progress file has the newest profiles
        first.buyCharacter("LEFT", "España");
        first.setMusicVolume(30);
        first.saveProgress();

        DOPOSoccer second = DOPOSoccer.openGame(saved);
        assertEquals(first.getPoints("LEFT"), second.getPoints("LEFT"));
        assertTrue(second.isCharacterUnlocked("LEFT", "España")); // not the profiles of the saved game
        assertEquals(Settings.DEFAULT_MUSIC_VOLUME, second.getMusicVolume()); // the settings are not saved
        assertNotSame(first, second);
    }
}
