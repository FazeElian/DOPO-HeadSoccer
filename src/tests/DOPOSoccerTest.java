package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the facade {@link DOPOSoccer}: a new game in Arcade player vs player, the match,
 * the catalogs, the stats, the settings and the options that are under construction.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class DOPOSoccerTest {

    private DOPOSoccer game;

    @BeforeEach
    public void setUp() {
        game = new DOPOSoccer();
    }

    private void newGoalsGame(int goals) throws DOPOSoccerException {
        game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", goals);
    }

    private static void assertMessage(String message, Executable action) {
        DOPOSoccerException e = assertThrows(DOPOSoccerException.class, action::run);
        assertEquals(message, e.getMessage());
    }

    /** An action of the facade that can throw. */
    private interface Executable {
        void run() throws DOPOSoccerException;
    }

    // new game

    @Test
    public void shouldStartAGameByGoals() throws DOPOSoccerException {
        newGoalsGame(3);
        assertEquals("ARCADE", game.getMode());
        assertEquals("PVP", game.getModality());
        assertEquals("GOALS", game.getMatchType());
        assertEquals(1, game.getRound());
        assertEquals(0, game.getGoals("LEFT"));
        assertEquals(0, game.getGoals("RIGHT"));
        assertEquals(-1, game.getTimeLeft());
        assertFalse(game.isMatchOver());
        assertFalse(game.isPaused());
    }

    @Test
    public void shouldStartAGameByTime() throws DOPOSoccerException {
        game.newGame("PVP", null, "SMALL", "ARCADE", "Nigeria", "Camerún", "TIME", 2);
        assertEquals("TIME", game.getMatchType());
        assertEquals(120, game.getTimeLeft());
        assertFalse(game.isSuddenDeath());
    }

    @Test
    public void shouldPutThePlayersWithFullLife() throws DOPOSoccerException {
        newGoalsGame(3);
        assertEquals(Stats.BASE_LIFE, game.getLife("LEFT"));
        assertEquals(Stats.BASE_LIFE, game.getMaxLife("RIGHT"));
        assertEquals(0, game.getPowerCharge("LEFT"), 1e-9);
        assertFalse(game.hasAura("RIGHT"));
        assertTrue(game.getStates("LEFT").isEmpty());
        assertEquals("Colombia", game.getCharacter("LEFT"));
        assertEquals("Argentina", game.getCharacter("RIGHT"));
    }

    @Test
    public void shouldPutEachPlayerOnItsHalf() throws DOPOSoccerException {
        newGoalsGame(3);
        assertTrue(game.getPlayerX("LEFT") < game.getPlayerX("RIGHT"));
        assertTrue(game.getPlayerX("LEFT") < game.getFieldWidth() / 2);
        assertTrue(game.getPlayerX("RIGHT") > game.getFieldWidth() / 2);
    }

    @Test
    public void shouldNotAcceptAnUnknownModality() {
        assertMessage(DOPOSoccerException.UNKNOWN_MODALITY,
                () -> game.newGame("XYZ", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3));
    }

    @Test
    public void shouldNotAcceptAnUnknownMode() {
        assertMessage(DOPOSoccerException.UNKNOWN_MODE,
                () -> game.newGame("PVP", null, "MEDIUM", "XYZ", "Colombia", "Argentina", "GOALS", 3));
    }

    @Test
    public void shouldNotAcceptAnUnknownCharacter() {
        assertMessage(DOPOSoccerException.UNKNOWN_CHARACTER,
                () -> game.newGame("PVP", null, "MEDIUM", "ARCADE", "Brasil", "Argentina", "GOALS", 3));
    }

    @Test
    public void shouldNotAcceptAnUnknownMatchType() {
        assertMessage(DOPOSoccerException.UNKNOWN_MATCH_TYPE,
                () -> game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "XYZ", 3));
    }

    @Test
    public void shouldNotAcceptAnUnknownResolution() {
        assertMessage(DOPOSoccerException.UNKNOWN_RESOLUTION,
                () -> game.newGame("PVP", null, "HUGE", "ARCADE", "Colombia", "Argentina", "GOALS", 3));
    }

    @Test
    public void shouldNotAcceptZeroMinutes() {
        assertMessage(DOPOSoccerException.INVALID_MINUTES,
                () -> game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "TIME", 0));
    }

    @Test
    public void shouldNotAcceptZeroGoals() {
        assertMessage(DOPOSoccerException.INVALID_GOAL_TARGET,
                () -> game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 0));
    }

    @Test
    public void shouldNotStartAGameWhenTheChoiceFails() {
        assertThrows(DOPOSoccerException.class,
                () -> game.newGame("PVP", null, "MEDIUM", "ARCADE", "Brasil", "Argentina", "GOALS", 3));
        assertMessage(DOPOSoccerException.GAME_NOT_STARTED, () -> game.getGoals("LEFT"));
    }

    // without a game

    @Test
    public void shouldNotGiveTheStateWithoutAGame() {
        assertMessage(DOPOSoccerException.GAME_NOT_STARTED, () -> game.isMatchOver());
        assertMessage(DOPOSoccerException.GAME_NOT_STARTED, () -> game.getLife("LEFT"));
        assertMessage(DOPOSoccerException.GAME_NOT_STARTED, () -> game.pause());
        assertMessage(DOPOSoccerException.GAME_NOT_STARTED, () -> game.press("LEFT", "JUMP"));
    }

    @Test
    public void shouldDoNothingOnUpdateWithoutAGame() {
        assertDoesNotThrow(() -> game.update(1 / 60.0));
    }

    @Test
    public void shouldNotAcceptAnUnknownSide() throws DOPOSoccerException {
        newGoalsGame(3);
        assertMessage(DOPOSoccerException.UNKNOWN_SIDE, () -> game.getGoals("CENTER"));
        assertMessage(DOPOSoccerException.UNKNOWN_SIDE, () -> game.press("UP", "JUMP"));
    }

    // match

    @Test
    public void shouldAdvanceTheTime() throws DOPOSoccerException {
        game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "TIME", 1);
        for (int i = 0; i < 600; i++) {
            game.update(1 / 60.0);
        }
        assertEquals(50, game.getTimeLeft());
    }

    @Test
    public void shouldNotAdvanceWhilePaused() throws DOPOSoccerException {
        game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "TIME", 1);
        game.pause();
        assertTrue(game.isPaused());
        for (int i = 0; i < 600; i++) {
            game.update(1 / 60.0);
        }
        assertEquals(60, game.getTimeLeft());
        game.resume();
        assertFalse(game.isPaused());
    }

    @Test
    public void shouldFillThePowerBarWhilePlaying() throws DOPOSoccerException {
        newGoalsGame(3);
        for (int i = 0; i < 60; i++) {
            game.update(1 / 60.0);
        }
        assertEquals(1 / Player.POWER_RATE, game.getPowerCharge("LEFT"), 1e-6);
    }

    @Test
    public void shouldMoveAPlayerWithItsKeys() throws DOPOSoccerException {
        newGoalsGame(3);
        for (int i = 0; i < 30; i++) {
            game.update(1 / 60.0);
        }
        double before = game.getPlayerX("LEFT");
        game.press("LEFT", "MOVE_RIGHT");
        for (int i = 0; i < 30; i++) {
            game.update(1 / 60.0);
        }
        game.release("LEFT", "MOVE_RIGHT");
        assertTrue(game.getPlayerX("LEFT") > before);
    }

    @Test
    public void shouldEndTheGameWithoutWinnerNorPoints() throws DOPOSoccerException {
        newGoalsGame(3);
        game.endGame();
        assertTrue(game.isMatchOver());
        assertTrue(game.isModeOver());
        assertNull(game.getWinner());
        assertEquals(0, game.getPointsEarned("LEFT"));
        assertEquals(0, game.getPointsEarned("RIGHT"));
    }

    @Test
    public void shouldNotEndOrPauseAFinishedGame() throws DOPOSoccerException {
        newGoalsGame(3);
        game.endGame();
        assertMessage(DOPOSoccerException.GAME_OVER, () -> game.endGame());
        assertMessage(DOPOSoccerException.GAME_OVER, () -> game.pause());
    }

    @Test
    public void shouldGiveNoWinnerBeforeTheEnd() throws DOPOSoccerException {
        newGoalsGame(3);
        assertNull(game.getWinner());
    }

    @Test
    public void shouldGiveTheEventsOnlyOnce() throws DOPOSoccerException {
        newGoalsGame(3);
        game.update(1 / 60.0);
        game.pollEvents();
        assertTrue(game.pollEvents().isEmpty());
    }

    // drawing data

    @Test
    public void shouldGiveTheSizesOfTheElements() throws DOPOSoccerException {
        newGoalsGame(3);
        assertEquals(Player.WIDTH, game.getPlayerWidth(), 1e-9);
        assertEquals(Player.HEIGHT, game.getPlayerHeight(), 1e-9);
        assertEquals(Match.BALL_RADIUS, game.getBallRadius(), 1e-9);
        assertTrue(game.getFieldWidth() > 0);
        assertTrue(game.getGroundY() <= game.getFieldHeight());
        assertEquals(4, game.getGoal("LEFT").length);
        assertTrue(game.getFakeBalls().isEmpty());
        assertNull(game.getBallPower());
        assertEquals(DOPOSoccer.DEFAULT_BALL, game.getBallName());
    }

    // catalogs

    @Test
    public void shouldHaveTheEightTeams() {
        List<String> teams = game.getCharacters();
        assertEquals(8, teams.size());
        assertTrue(teams.contains("Colombia"));
        assertTrue(teams.contains("Japón"));
    }

    @Test
    public void shouldGiveThePricesOfTheTeams() throws DOPOSoccerException {
        assertEquals(0, game.getCharacterPrice("Colombia"));
        assertEquals(800, game.getCharacterPrice("España"));
        assertEquals(1200, game.getCharacterPrice("Japón"));
        assertTrue(game.isCharacterUnlocked("LEFT", "Colombia"));
        assertFalse(game.isCharacterUnlocked("RIGHT", "España"));
        assertMessage(DOPOSoccerException.UNKNOWN_CHARACTER, () -> game.getCharacterPrice("Brasil"));
    }

    @Test
    public void shouldGiveThePowerOfEachTeam() throws DOPOSoccerException {
        assertEquals(game.getPowerNameOf("Colombia"), game.getPowerNameOf("Estados Unidos"));
        assertEquals(game.getPowerNameOf("Argentina"), game.getPowerNameOf("Camerún"));
    }

    @Test
    public void shouldHaveTheSixBalls() throws DOPOSoccerException {
        assertEquals(6, game.getBallNames().size());
        assertEquals(0, game.getBallPrice("Clásico"));
        assertEquals(1000, game.getBallPrice("Galaxia"));
        assertMessage(DOPOSoccerException.UNKNOWN_BALL, () -> game.getBallPrice("Playa"));
    }

    @Test
    public void shouldSelectAFreeBall() throws DOPOSoccerException {
        assertEquals(DOPOSoccer.DEFAULT_BALL, game.getSelectedBall("LEFT"));
        game.selectBall("LEFT", "Tricolor");
        assertEquals("Tricolor", game.getSelectedBall("LEFT"));
        newGoalsGame(3);
        assertEquals("Tricolor", game.getBallName());
    }

    @Test
    public void shouldGiveTheListsOfOptions() {
        assertEquals(List.of("SMALL", "MEDIUM", "LARGE"), game.getResolutions());
        assertEquals(4, game.getAIProfiles().size());
    }

    // stats and settings

    @Test
    public void shouldGiveTheStatsOfEachPlayer() throws DOPOSoccerException {
        assertEquals(Stats.MIN_STAT, game.getStat("LEFT", "SPEED"));
        assertEquals(500, game.getUpgradeCost("RIGHT", "KICK"));
        assertMessage(DOPOSoccerException.UNKNOWN_STAT, () -> game.getStat("LEFT", "MAGIC"));
    }

    @Test
    public void shouldChangeTheVolumes() throws DOPOSoccerException {
        game.setMusicVolume(30);
        game.setEffectsVolume(70);
        assertEquals(30, game.getMusicVolume());
        assertEquals(70, game.getEffectsVolume());
        assertMessage(DOPOSoccerException.INVALID_VOLUME, () -> game.setMusicVolume(150));
        game.resetSettings();
        assertEquals(Settings.DEFAULT_MUSIC_VOLUME, game.getMusicVolume());
    }

    @Test
    public void shouldGiveThePrizeOfTheTournament() {
        assertEquals(TournamentMode.PRIZE, game.getTournamentPrize());
    }

    // under construction

    @Test
    public void shouldSayThatTheUnfinishedOptionsAreUnderConstruction() {
        String building = DOPOSoccerException.UNDER_CONSTRUCTION;
        assertMessage(building,
                () -> game.newGame("PVM", "EXPERT", "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3));
        assertMessage(building,
                () -> game.newGame("PVP", null, "MEDIUM", "TOURNAMENT", "Colombia", "Argentina", "GOALS", 3));
        assertMessage(building,
                () -> game.newGame("PVP", null, "MEDIUM", "ARCADE", "España", "Argentina", "GOALS", 3));
        assertMessage(building,
                () -> game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "SURVIVAL", 0));
        assertMessage(building, () -> game.selectBall("LEFT", "Dorado"));
        assertMessage(building, () -> game.nextMatch());
        assertMessage(building, () -> game.saveProgress());
    }
}
