package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the facade {@link DOPOSoccer}: a new game in Arcade (player vs player and vs the machine), the match,
 * the catalogs, the stats, the settings, the tournament, survival and the store.
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
        try {
            game.newGame("XYZ", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_MODALITY, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptAnUnknownMode() {
        try {
            game.newGame("PVP", null, "MEDIUM", "XYZ", "Colombia", "Argentina", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_MODE, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptAnUnknownCharacter() {
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "Brasil", "Argentina", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_CHARACTER, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptAnUnknownMatchType() {
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "XYZ", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_MATCH_TYPE, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptAnUnknownResolution() {
        try {
            game.newGame("PVP", null, "HUGE", "ARCADE", "Colombia", "Argentina", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_RESOLUTION, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptZeroMinutes() {
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "TIME", 0);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_MINUTES, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptZeroGoals() {
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 0);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_GOAL_TARGET, e.getMessage());
        }
    }

    @Test
    public void shouldNotStartAGameWhenTheChoiceFails() {
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "Brasil", "Argentina", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_CHARACTER, e.getMessage());
        }
        try {
            game.getGoals("LEFT");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_NOT_STARTED, e.getMessage());
        }
    }

    // without a game

    @Test
    public void shouldNotGiveTheStateWithoutAGame() {
        try {
            game.isMatchOver();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_NOT_STARTED, e.getMessage());
        }
        try {
            game.getLife("LEFT");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_NOT_STARTED, e.getMessage());
        }
        try {
            game.pause();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_NOT_STARTED, e.getMessage());
        }
        try {
            game.press("LEFT", "JUMP");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_NOT_STARTED, e.getMessage());
        }
    }

    @Test
    public void shouldDoNothingOnUpdateWithoutAGame() {
        game.update(1 / 60.0);
    }

    @Test
    public void shouldNotAcceptAnUnknownSide() throws DOPOSoccerException {
        newGoalsGame(3);
        try {
            game.getGoals("CENTER");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_SIDE, e.getMessage());
        }
        try {
            game.press("UP", "JUMP");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_SIDE, e.getMessage());
        }
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
        try {
            game.endGame();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_OVER, e.getMessage());
        }
        try {
            game.pause();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_OVER, e.getMessage());
        }
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
        try {
            game.getCharacterPrice("Brasil");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_CHARACTER, e.getMessage());
        }
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
        try {
            game.getBallPrice("Playa");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_BALL, e.getMessage());
        }
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
        try {
            game.getStat("LEFT", "MAGIC");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_STAT, e.getMessage());
        }
    }

    @Test
    public void shouldChangeTheVolumes() throws DOPOSoccerException {
        game.setMusicVolume(30);
        game.setEffectsVolume(70);
        assertEquals(30, game.getMusicVolume());
        assertEquals(70, game.getEffectsVolume());
        try {
            game.setMusicVolume(150);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_VOLUME, e.getMessage());
        }
        game.resetSettings();
        assertEquals(Settings.DEFAULT_MUSIC_VOLUME, game.getMusicVolume());
    }

    @Test
    public void shouldGiveThePrizeOfTheTournament() {
        assertEquals(TournamentMode.PRIZE, game.getTournamentPrize());
    }

    // saving a game

    @Test
    public void shouldNotSaveAGameWithoutAGame() {
        try {
            game.saveGame(new File("partida.dat"));
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_NOT_STARTED, e.getMessage());
        }
        assertFalse(new File("partida.dat").exists()); // nothing was written
    }

    @Test
    public void shouldPlaySurvivalOnlyAgainstTheMachine() {
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Argentina", "SURVIVAL", 0);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.SURVIVAL_ONLY_PVM, e.getMessage());
        }
    }

    // tournament

    @Test
    public void shouldStartATournamentWithEightTeams() throws DOPOSoccerException {
        game.newGame("PVM", "CLUMSY", "MEDIUM", "TOURNAMENT", "Colombia", "Colombia", "GOALS", 3);
        List<String> teams = game.getTournamentTeams();
        assertEquals(8, teams.size());
        assertEquals(8, new HashSet<String>(teams).size()); // no team is repeated
        assertEquals("Colombia", teams.get(0));
        assertEquals("CUARTOS", game.getTournamentRound());
        assertEquals("PVM", game.getModality());
        assertEquals(game.getTournamentRival(), game.getCharacter("RIGHT"));
    }

    @Test
    public void shouldNotPlayATournamentInSurvival() {
        try {
            game.newGame("PVM", "CLUMSY", "MEDIUM", "TOURNAMENT", "Colombia", "Colombia", "SURVIVAL", 0);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_TOURNAMENT_TYPE, e.getMessage());
        }
    }

    @Test
    public void shouldNotPlayATournamentWithALockedCharacter() {
        try {
            game.newGame("PVM", "CLUMSY", "MEDIUM", "TOURNAMENT", "España", "España", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.CHARACTER_LOCKED, e.getMessage());
        }
    }

    @Test
    public void shouldNotGoToTheNextMatchInArcade() throws DOPOSoccerException {
        try {
            game.nextMatch();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_NOT_STARTED, e.getMessage());
        }
        newGoalsGame(3);
        game.endGame();
        try {
            game.nextMatch();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_STATE, e.getMessage());
        }
    }

    @Test
    public void shouldNotAskForTheTournamentInArcade() throws DOPOSoccerException {
        newGoalsGame(3);
        try {
            game.getTournamentTeams();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_STATE, e.getMessage()); // a game, but not a tournament
        }
    }

    private void newTournament() throws DOPOSoccerException {
        game.newGame("PVM", "CLUMSY", "MEDIUM", "TOURNAMENT", "Colombia", "Colombia", "GOALS", 3);
    }

    @Test
    public void shouldStartTheTournamentWithoutSemifinalNorFinal() throws DOPOSoccerException {
        newTournament();
        assertTrue(game.getTournamentSemifinalists().isEmpty());
        assertTrue(game.getTournamentFinalists().isEmpty());
        assertEquals(List.of("CUARTOS", "SEMIFINAL", "FINAL"), game.getTournamentRounds());
        assertEquals(1, game.getRound());
        assertEquals(0, game.getTournamentWins());
        assertFalse(game.isModeOver());
    }

    @Test
    public void shouldNotGoToTheNextRoundBeforeTheMatchEnds() throws DOPOSoccerException {
        newTournament();
        try {
            game.nextMatch();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_STATE, e.getMessage());
        }
    }

    @Test
    public void shouldBeEliminatedIfTheTournamentMatchIsEndedByHand() throws DOPOSoccerException {
        newTournament();
        game.endGame(); // no winner: it counts as lost
        assertTrue(game.isModeOver());
        assertFalse(game.isTournamentChampion());
        assertEquals(0, game.getPointsEarned("LEFT"));
        try {
            game.nextMatch();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_OVER, e.getMessage());
        }
    }

    @Test
    public void shouldForgetTheTournamentWhenPlayingArcade() throws DOPOSoccerException {
        newTournament();
        newGoalsGame(3);
        assertEquals("ARCADE", game.getMode());
        try {
            game.getTournamentTeams();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_STATE, e.getMessage()); // a game, but not a tournament
        }
    }

    // stadium

    @Test
    public void shouldPlayInTheDefaultStadium() throws DOPOSoccerException {
        newGoalsGame(3);
        assertEquals(DOPOSoccer.DEFAULT_STADIUM, game.getStadium());
    }

    @Test
    public void shouldPlayInTheChosenStadium() throws DOPOSoccerException {
        game.selectStadium("rain");
        newGoalsGame(3);
        assertEquals("rain", game.getStadium());
    }

    @Test
    public void shouldPlayEachTournamentMatchInARandomStadium() throws DOPOSoccerException {
        game.selectStadium("rain");
        newTournament();
        assertTrue(game.getStadiums().contains(game.getStadium())); // drawn, not the chosen one
    }

    @Test
    public void shouldNotChooseAnUnknownStadium() {
        try {
            game.selectStadium("moon");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_STADIUM, e.getMessage());
        }
        assertEquals(9, game.getStadiums().size());
    }

    // player vs machine

    @Test
    public void shouldStartAGameAgainstTheMachine() throws DOPOSoccerException {
        game.newGame("PVM", "EXPERT", "MEDIUM", "ARCADE", "Colombia", "Japón", "GOALS", 3);
        assertEquals("PVM", game.getModality());
        assertEquals("Japón", game.getCharacter("RIGHT")); // the machine plays with any character
    }

    @Test
    public void shouldNotAcceptAnUnknownMachineProfile() {
        try {
            game.newGame("PVM", "GENIUS", "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_AI_PROFILE, e.getMessage());
        }
    }

    @Test
    public void shouldMoveTheMachineByItself() throws DOPOSoccerException {
        game.newGame("PVM", "OFFENSIVE", "MEDIUM", "ARCADE", "Colombia", "Argentina", "GOALS", 3);
        double before = game.getPlayerX("RIGHT");
        for (int i = 0; i < 60; i++) {
            game.update(1 / 60.0);
        }
        assertTrue(game.getPlayerX("RIGHT") < before); // it goes for the ball, to its left
    }

    // store

    @Test
    public void shouldStartWithoutPoints() throws DOPOSoccerException {
        assertEquals(0, game.getPoints("LEFT"));
        assertEquals(0, game.getPoints("RIGHT"));
    }

    @Test
    public void shouldNotBuyWithoutPoints() {
        try {
            game.buyCharacter("LEFT", "España");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
        try {
            game.buyBall("LEFT", "Dorado");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
        try {
            game.upgrade("LEFT", "SPEED");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
    }

    @Test
    public void shouldNotBuyWhatIsAlreadyOwned() {
        try {
            game.buyCharacter("LEFT", "Colombia");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.ALREADY_OWNED, e.getMessage());
        }
        try {
            game.buyBall("LEFT", "Tricolor");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.ALREADY_OWNED, e.getMessage());
        }
    }

    @Test
    public void shouldNotPlayWithALockedCharacter() {
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "España", "Argentina", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.CHARACTER_LOCKED, e.getMessage());
        }
        try {
            game.newGame("PVP", null, "MEDIUM", "ARCADE", "Colombia", "Japón", "GOALS", 3);
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.CHARACTER_LOCKED, e.getMessage());
        }
    }

    @Test
    public void shouldNotSelectALockedBall() throws DOPOSoccerException {
        assertFalse(game.isBallUnlocked("LEFT", "Dorado"));
        assertTrue(game.isBallUnlocked("LEFT", "Tricolor"));
        try {
            game.selectBall("LEFT", "Dorado");
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.BALL_LOCKED, e.getMessage());
        }
    }
}
