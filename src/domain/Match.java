package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A match between two players. It creates everything on the field and coordinates each tick:
 * the players act, the physics library moves everything and the goals are checked.
 * When it ends is decided by its {@link MatchType}.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Match implements Serializable {

    /** Version of the serialized form. */
    private static final long serialVersionUID = 1L;

    /** Radius of the ball, in pixels. */
    public static final double BALL_RADIUS = 22;

    /** Player that defends the left goal. */
    private final Player leftPlayer;
    /** Player that defends the right goal. */
    private final Player rightPlayer;
    /** The real ball. */
    private final Ball ball;
    /** Fake balls of the Illusion shot. */
    private final ArrayList<FakeBall> fakeBalls;
    /** The field. */
    private final Stadium stadium;
    /** Goals of each side. */
    private final ScoreBoard scoreBoard;
    /** Adapter of the physics library. */
    private final PhysicsEngine physics;
    /** Rule that ends the match. */
    private final MatchType type;
    /** Seconds played. */
    private double elapsed;
    /** Whether the match was ended by hand. */
    private boolean ended;

    /**
     * Creates a match ready to start: 0 - 0, empty power bars, full life and the ball over the center.
     *
     * @param leftCharacter   character of the left player
     * @param leftStats       stats of the left player
     * @param leftController  who controls the left player
     * @param rightCharacter  character of the right player
     * @param rightStats      stats of the right player
     * @param rightController who controls the right player
     * @param type            rule to end the match
     * @param resolution      "SMALL", "MEDIUM" or "LARGE"
     * @param skin            ball chosen to play
     * @throws DOPOSoccerException UNKNOWN_RESOLUTION if the resolution is not valid
     */
    public Match(GameCharacter leftCharacter, Stats leftStats, PlayerController leftController,
                 GameCharacter rightCharacter, Stats rightStats, PlayerController rightController,
                 MatchType type, String resolution, BallSkin skin) throws DOPOSoccerException {
        this.stadium = new Stadium(resolution);
        this.ball = new Ball(0, 0, BALL_RADIUS, skin);
        this.fakeBalls = new ArrayList<>();
        this.leftPlayer = new Player("LEFT", leftCharacter, leftStats, leftController, 0, 0);
        this.rightPlayer = new Player("RIGHT", rightCharacter, rightStats, rightController, 0, 0);
        this.scoreBoard = new ScoreBoard();
        this.physics = new PhysicsEngine(stadium);
        this.type = type;
        this.elapsed = 0;
        this.ended = false;
        placeForKickOff();
    }

    /**
     * Puts the players at their places and drops the ball from the center.
     */
    public void kickOff() {
        placeForKickOff();
    }

        /**
     * Advances the match one tick: players act and update, fake balls count down,
     * the physics library moves everything and the goals are checked.
     * It does nothing once the match is finished.
     *
     * @param dt elapsed time, in seconds
     */
    public void update(double dt) {
        if (isFinished()) {
            return;
        }
        elapsed += dt;
        leftPlayer.act(this);
        rightPlayer.act(this);
        leftPlayer.update(dt);
        rightPlayer.update(dt);
        for (int i = fakeBalls.size() - 1; i >= 0; i--) {
            FakeBall fake = fakeBalls.get(i);
            fake.update(dt);
            if (fake.isExpired()) {
                fakeBalls.remove(i);
            }
        }
        if (ball.isPowerShot() && ball.getSpeed() < Ball.MIN_SHOT_SPEED) {
            ball.clearCharge();
        }
        physics.step(this, dt);
        checkGoals();
    }

    /**
     * A player touched the ball. The physics calls it, and also a kick with the aura on.
     * If the player has the aura, its power shot comes out. If the ball carries a power shot of the rival,
     * the player takes its damage (base damage times the shooter's kick multiplier) and its effect.
     *
     * @param p the player that touched the ball
     */
    public void onBallTouch(Player p) {
        if (p.hasAura()) {
            p.useAura();
            ball.clearCharge();
            p.getCharacter().getPowerShot().launch(this, p);
            return;
        }
        if (ball.isPowerShot() && !p.getSide().equals(ball.getShooter())) {
            PowerShot shot = ball.getCharge();
            double multiplier = getPlayer(ball.getShooter()).getStats().getKickMultiplier();
            p.takeDamage((int) Math.round(shot.getBaseDamage() * multiplier));
            p.applyEffect(shot.createEffect());
            ball.clearCharge();
        }
    }

    /**
     * The ball hit a wall, the ceiling or a crossbar: a power shot that was on its way is over.
     * The physics calls it.
     */
    public void onBallHitsBorder() {
        ball.clearCharge();
    }

    /**
     * Creates fake balls next to the real one, each going a bit differently.
     *
     * @param count    how many
     * @param duration seconds they last
     */
    public void spawnFakeBalls(int count, double duration) {
        for (int i = 0; i < count; i++) {
            FakeBall fake = new FakeBall(ball.getX(), ball.getY(), ball.getRadius(), duration);
            double spread = i - (count - 1) / 2.0;
            fake.setVelocity(ball.getVx() * (0.75 + 0.08 * i), ball.getVy() + 110 * spread);
            fakeBalls.add(fake);
        }
    }

    /**
     * Ends the match now, without a winner.
     */
    public void end() {
        ended = true;
    }

    /**
     * Tells whether the match is over.
     *
     * @return true if it was ended or its rule says it is over
     */
    public boolean isFinished() {
        return ended || type.isMatchOver(this);
    }

    /**
     * Returns the winner.
     *
     * @return "LEFT", "RIGHT", or null if it is not over or was ended by hand
     */
    public String getWinner() {
        if (ended || !type.isMatchOver(this)) {
            return null;
        }
        return type.getWinner(this);
    }

    /**
     * Tells whether the match was ended by hand.
     *
     * @return true if {@link #end()} was called
     */
    public boolean wasEnded() {
        return ended;
    }

    /**
     * Returns the player of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return its player, or null for an unknown side
     */
    public Player getPlayer(String side) {
        if ("LEFT".equals(side)) {
            return leftPlayer;
        }
        return "RIGHT".equals(side) ? rightPlayer : null;
    }

    /**
     * Returns the rival of a player.
     *
     * @param p one of the players
     * @return the other player
     */
    public Player getOpponent(Player p) {
        return p == leftPlayer ? rightPlayer : leftPlayer;
    }

    /**
     * Returns the real ball.
     *
     * @return the ball
     */
    public Ball getBall() {
        return ball;
    }

    /**
     * Returns the fake balls on the field.
     *
     * @return a copy of the list of fake balls
     */
    public List<FakeBall> getFakeBalls() {
        return new ArrayList<>(fakeBalls);
    }

    /**
     * Returns the field.
     *
     * @return the stadium
     */
    public Stadium getStadium() {
        return stadium;
    }

    /**
     * Returns the scoreboard.
     *
     * @return the scoreboard
     */
    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    /**
     * Returns the rule to end the match.
     *
     * @return the match type
     */
    public MatchType getType() {
        return type;
    }

    /**
     * Returns the seconds played.
     *
     * @return seconds played
     */
    public double getElapsed() {
        return elapsed;
    }

    /**
     * Checks whether the ball entered a goal: adds the goal, gives the quarter of bar to the side that
     * received it, tells the rule and kicks off again.
     */
    private void checkGoals() {
        for (String defender : new String[]{"LEFT", "RIGHT"}) {
            if (stadium.getGoal(defender).contains(ball)) {
                String scorer = "LEFT".equals(defender) ? "RIGHT" : "LEFT";
                scoreBoard.addGoal(scorer);
                getPlayer(defender).onGoalAgainst();
                type.onGoal(scorer, this);
                fakeBalls.clear();
                placeForKickOff();
                return;
            }
        }
    }

    /**
     * Places the players on the ground at a fifth of the field from each side,
     * and the ball still over the center.
     */
    private void placeForKickOff() {
        double width = stadium.getWidth();
        double feet = stadium.getGroundY() - Player.HEIGHT;
        leftPlayer.setPosition(width * 0.22 - Player.WIDTH / 2, feet);
        rightPlayer.setPosition(width * 0.78 - Player.WIDTH / 2, feet);
        leftPlayer.setVelocity(0, 0);
        rightPlayer.setVelocity(0, 0);
        ball.reset(width / 2 - ball.getRadius(), stadium.getHeight() * 0.15);
        fakeBalls.clear();
    }
}
