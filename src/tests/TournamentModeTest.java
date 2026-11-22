package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link TournamentMode} and {@link BracketMatch}: the draw of the quarterfinals, going through
 * the rounds, losing, winning the final and the simulated matches.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class TournamentModeTest {

    private static final GameCharacter PLAYER = new GameCharacter("Colombia", new IllusionShot());

    private static List<GameCharacter> rivals(int count) {
        List<GameCharacter> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new GameCharacter("Equipo " + i, new LightningShot()));
        }
        return list;
    }

    // A fixed seed, so the draw and the simulated matches are always the same
    private static TournamentMode newTournament() throws DOPOSoccerException {
        TournamentMode t = new TournamentMode(PLAYER, new Stats(), new HumanController(), new Stats(),
                new HumanController(), new GoalsMatch(1), "MEDIUM", new BallSkin("Clásico", 0), rivals(7),
                new Random(42));
        t.start();
        return t;
    }

    // With a target of 1 goal, one goal ends the match
    private static void win(TournamentMode t) {
        t.getMatch().getScoreBoard().addGoal("LEFT");
    }

    private static void lose(TournamentMode t) {
        t.getMatch().getScoreBoard().addGoal("RIGHT");
    }

    @Test
    public void shouldNeedSevenRivals() {
        try {
            new TournamentMode(PLAYER, new Stats(), new HumanController(), new Stats(), new HumanController(), new GoalsMatch(1), "MEDIUM", new BallSkin("Clásico", 0), rivals(6), new Random(42));
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_STATE, e.getMessage());
        }
    }

    @Test
    public void shouldDrawEightDifferentTeamsInTheQuarterfinals() throws DOPOSoccerException {
        TournamentMode t = newTournament();
        List<GameCharacter> teams = t.getTeams(1);
        assertEquals(8, teams.size());
        assertEquals(8, new HashSet<>(teams).size());
        assertSame(PLAYER, teams.get(0));
        assertSame(teams.get(1), t.getRival());
        assertEquals(1, t.getRound());
        assertEquals("CUARTOS", t.getRoundName());
        assertTrue(t.getTeams(2).isEmpty());
        assertFalse(t.isOver());
    }

    @Test
    public void shouldNotGoOnBeforeTheMatchEnds() throws DOPOSoccerException {
        TournamentMode t = newTournament();
        try {
            t.nextMatch();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_STATE, e.getMessage());
        }
    }

    @Test
    public void shouldGoToTheSemifinalAfterWinning() throws DOPOSoccerException {
        TournamentMode t = newTournament();
        win(t);
        assertTrue(t.playerWonMatch());
        assertFalse(t.isOver());
        t.nextMatch();

        assertEquals(2, t.getRound());
        assertEquals("SEMIFINAL", t.getRoundName());
        List<GameCharacter> semis = t.getTeams(2);
        assertEquals(4, semis.size());
        assertSame(PLAYER, semis.get(0)); // always on the left
        assertFalse(semis.contains(null));
        assertSame(semis.get(1), t.getRival());
        assertEquals(1, t.getWins());
        assertFalse(t.getMatch().isFinished()); // a new match
    }

    @Test
    public void shouldPlayEachMatchInARandomStadium() throws DOPOSoccerException {
        HashSet<String> stadiums = new HashSet<>();
        for (int seed = 0; seed < 20; seed++) {
            TournamentMode t = new TournamentMode(PLAYER, new Stats(), new HumanController(), new Stats(),
                    new HumanController(), new GoalsMatch(1), "MEDIUM", new BallSkin("Clásico", 0), rivals(7),
                    new Random(seed));
            t.start();
            for (int round = 1; round <= 3; round++) {
                String stadium = t.getMatch().getStadiumName();
                assertTrue(GameMode.STADIUMS.contains(stadium));
                stadiums.add(stadium);
                if (round < 3) {
                    win(t);
                    t.nextMatch();
                }
            }
        }
        assertTrue(stadiums.size() > 1); // not always the same one
    }

    @Test
    public void shouldEndWhenThePlayerLoses() throws DOPOSoccerException {
        TournamentMode t = newTournament();
        lose(t);
        assertTrue(t.isOver());
        assertFalse(t.isChampion());
        assertEquals(0, t.getPrize());
        assertEquals(0, t.getWins());
        try {
            t.nextMatch();
            fail("Debió lanzar DOPOSoccerException");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.GAME_OVER, e.getMessage());
        }
    }

    @Test
    public void shouldBeChampionAfterWinningTheFinal() throws DOPOSoccerException {
        TournamentMode t = newTournament();
        win(t);
        t.nextMatch();
        win(t);
        t.nextMatch();

        assertEquals(3, t.getRound());
        assertEquals("FINAL", t.getRoundName());
        assertEquals(2, t.getTeams(3).size());
        assertFalse(t.isOver());

        win(t);
        assertTrue(t.isOver());
        assertTrue(t.isChampion());
        assertEquals(TournamentMode.PRIZE, t.getPrize());
        assertEquals(3, t.getWins());
    }

    @Test
    public void shouldNeverTieASimulatedMatch() {
        Random random = new Random(7);
        for (int i = 0; i < 100; i++) {
            BracketMatch b = new BracketMatch(1, PLAYER, new GameCharacter("Rival", new TornadoShot()));
            assertNull(b.getWinner());
            b.simulate(random);
            assertTrue(b.isPlayed());
            assertNotNull(b.getWinner());
        }
    }

    @Test
    public void shouldKnowWhoWonABracketMatch() {
        GameCharacter rival = new GameCharacter("Rival", new TornadoShot());
        BracketMatch b = new BracketMatch(3, PLAYER, rival);
        b.setResult(1, 2);
        assertSame(rival, b.getWinner());
        assertTrue(b.includes(PLAYER));
        assertFalse(b.includes(new GameCharacter("Otro", new TornadoShot())));
        assertEquals("FINAL", b.getDisplayRound());
    }
}
