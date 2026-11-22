package tests;

import domain.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests of the machine: {@link AIController} and its profiles.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class AIControllerTest {

    // A match where the machine is the right player
    private static Match newMatch(PlayerController machine) throws DOPOSoccerException {
        return new Match(new GameCharacter("Colombia", new IllusionShot()), new Stats(), new HumanController(),
                new GameCharacter("Argentina", new LightningShot()), new Stats(), machine,
                new GoalsMatch(3), "MEDIUM", new BallSkin("Clásico", 0), "default");
    }

    @Test
    public void shouldGiveTheNameOfEachProfile() {
        assertEquals("CLUMSY", new ClumsyAI().getProfile());
        assertEquals("OFFENSIVE", new OffensiveAI().getProfile());
        assertEquals("DEFENSIVE", new DefensiveAI().getProfile());
        assertEquals("EXPERT", new ExpertAI().getProfile());
    }

    @Test
    public void shouldGoForTheBallWhenOffensive() throws DOPOSoccerException {
        OffensiveAI machine = new OffensiveAI();
        Match match = newMatch(machine);
        Set<String> actions = machine.nextActions(match.getPlayer("RIGHT"), match);
        assertTrue(actions.contains("MOVE_LEFT")); // the ball is in the center, to its left
    }

    @Test
    public void shouldStayAtItsGoalWhenDefensiveAndTheBallIsFar() throws DOPOSoccerException {
        DefensiveAI machine = new DefensiveAI();
        Match match = newMatch(machine);
        match.getBall().setPosition(100, 300); // in the half of the rival
        Set<String> actions = machine.nextActions(match.getPlayer("RIGHT"), match);
        assertFalse(actions.contains("MOVE_LEFT"));
        assertFalse(actions.contains("KICK"));
    }

    @Test
    public void shouldDefendWhenTheBallIsInItsHalf() throws DOPOSoccerException {
        DefensiveAI machine = new DefensiveAI();
        Match match = newMatch(machine);
        Player self = match.getPlayer("RIGHT");
        match.getBall().setPosition(self.getX() - 30, self.getY()); // next to it
        assertTrue(machine.nextActions(self, match).contains("KICK"));
    }

    @Test
    public void shouldGoBackToItsGoalWhenExpertAndTheRivalHasTheBall() throws DOPOSoccerException {
        ExpertAI machine = new ExpertAI();
        Match match = newMatch(machine);
        Player self = match.getPlayer("RIGHT");
        Player rival = match.getPlayer("LEFT");
        self.setPosition(500, self.getY());          // in the center of the field
        rival.setPosition(150, rival.getY());
        match.getBall().setPosition(180, 300);       // in the rival half, next to the rival
        Set<String> actions = machine.nextActions(self, match);
        assertTrue(actions.contains("MOVE_RIGHT"));  // its goal is on the right
        assertFalse(actions.contains("KICK"));
    }

    @Test
    public void shouldAttackWhenExpertAndCloserToTheBall() throws DOPOSoccerException {
        ExpertAI machine = new ExpertAI();
        Match match = newMatch(machine);
        Player self = match.getPlayer("RIGHT");
        Player rival = match.getPlayer("LEFT");
        rival.setPosition(20, rival.getY());         // far from the ball
        self.setPosition(450, self.getY());
        match.getBall().setPosition(350, 300);       // in the rival half, closer to the machine
        assertTrue(machine.nextActions(self, match).contains("MOVE_LEFT"));
    }

    @Test
    public void shouldKeepItsDecisionDuringItsReactionTime() throws DOPOSoccerException {
        OffensiveAI machine = new OffensiveAI();
        Match match = newMatch(machine);
        Player self = match.getPlayer("RIGHT");
        Set<String> first = machine.nextActions(self, match);
        match.getBall().setPosition(self.getX() + 400, self.getY()); // now the ball is on the other side
        assertEquals(first, machine.nextActions(self, match));      // no time passed: same decision
    }

    @Test
    public void shouldPlayAWholeMatchWithoutErrors() throws DOPOSoccerException {
        for (PlayerController machine : new PlayerController[]{
                new ClumsyAI(), new OffensiveAI(), new DefensiveAI(), new ExpertAI()}) {
            Match match = newMatch(machine);
            // if an update throws, the test fails
            for (int i = 0; i < 60 * 20; i++) {
                match.update(1 / 60.0);
            }
        }
    }
}
