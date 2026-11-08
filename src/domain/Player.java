package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A player on the field: a character controlled by a person or by the machine, with the stats of its profile.
 * It runs, jumps and kicks; it has life, a power bar that fills in 10 seconds and an aura when its power is active.
 * It can have one effect of a power shot (a new one replaces the old one) and, apart from it, the stun.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class Player extends Entity {

    /** Width of the player's box, in pixels. */
    public static final double WIDTH = 105;
    /** Height of the player's box, in pixels. */
    public static final double HEIGHT = 90;
    /** Running speed with Speed at 1, in pixels per second. */
    public static final double BASE_SPEED = 250;
    /** Jump speed with Jump at 1, in pixels per second. */
    public static final double BASE_JUMP = 450;
    /** Speed of the ball after a normal kick, in pixels per second; Kick does not change it. */
    public static final double KICK_SPEED = 650;
    /** Damage of a kick before the kick multiplier. */
    public static final int KICK_DAMAGE = 10;
    /** Reach of the foot beyond the player's box, in pixels. */
    public static final double KICK_REACH = 40;
    /** Seconds the power bar takes to fill. */
    public static final double POWER_RATE = 10;
    /** Share of the power bar gained when the player receives a goal. */
    public static final double GOAL_AGAINST_BONUS = 0.25;
    /** Seconds the leg stays stretched; a new kick cannot start before. */
    public static final double KICK_WINDOW = 0.25;

    /** Side it defends: "LEFT" or "RIGHT". */
    private final String side;
    /** Character the player uses. */
    private final GameCharacter character;
    /** Stats of its profile. */
    private final Stats stats;
    /** Who decides its actions. */
    private final PlayerController controller;
    /** Maximum life, given by Survival. */
    private final int maxLife;
    /** Effect of a power shot, or null. */
    private Effect effect;
    /** Stun when the life reached 0, or null. */
    private StunEffect stun;
    /** Current life. */
    private int life;
    /** Power bar, from 0 to 1. */
    private double powerCharge;
    /** Whether the power is active, waiting to touch the ball. */
    private boolean aura;
    /** Whether it stands on the ground. */
    private boolean onGround;
    /** Seconds the leg stays stretched. */
    private double kickTimer;

    /**
     * Creates a player with full life, an empty power bar and no effects.
     *
     * @param side       "LEFT" or "RIGHT": the goal it defends
     * @param character  character it plays with
     * @param stats      stats of its profile
     * @param controller who decides its actions
     * @param x          left edge, in pixels
     * @param y          top edge, in pixels
     */
    public Player(String side, GameCharacter character, Stats stats, PlayerController controller, double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.side = side;
        this.character = character;
        this.stats = stats;
        this.controller = controller;
        this.maxLife = stats.getMaxLife();
        this.life = maxLife;
        this.effect = null;
        this.stun = null;
        this.powerCharge = 0;
        this.aura = false;
        this.onGround = false;
        this.kickTimer = 0;
    }

    /**
     * Asks the controller for this tick's actions and executes them.
     * If the player cannot act (stunned, pushed, unconscious) it does nothing and the physics carries it.
     *
     * @param match the match being played
     */
    public void act(Match match) {
        if (!canAct()) {
            return;
        }
        Set<String> actions = controller.nextActions(this, match);
        boolean left = actions.contains("MOVE_LEFT");
        boolean right = actions.contains("MOVE_RIGHT");
        if (left && !right) {
            moveLeft();
        } else if (right && !left) {
            moveRight();
        } else {
            stop();
        }
        if (actions.contains("JUMP")) {
            jump();
        }
        if (actions.contains("POWER")) {
            activatePower();
        }
        if (actions.contains("KICK")) {
            kick(match);
        }
    }

    /**
     * Fills the power bar (also while the aura is on), counts down the kick, the effect and the stun.
     * When the stun is over the life goes back to the maximum.
     *
     * @param dt elapsed time, in seconds
     */
    public void update(double dt) {
        powerCharge = Math.min(1, powerCharge + dt / POWER_RATE);
        kickTimer = Math.max(0, kickTimer - dt);
        if (effect != null) {
            effect.update(dt);
            if (effect.isExpired()) {
                effect = null;
            }
        }
        if (stun != null) {
            stun.update(dt);
            if (stun.isExpired()) {
                stun = null;
                life = maxLife;
            }
        }
    }

    /**
     * Runs left.
     */
    public void moveLeft() {
        vx = -runSpeed();
    }

    /**
     * Runs right.
     */
    public void moveRight() {
        vx = runSpeed();
    }

    /**
     * Stops running; the vertical movement does not change.
     */
    public void stop() {
        vx = 0;
    }

    /**
     * Jumps. It only works on the ground and if the effect allows it.
     */
    public void jump() {
        if (onGround && (effect == null || effect.canJump())) {
            vy = -BASE_JUMP * stats.getJumpMultiplier();
            onGround = false;
        }
    }

    /**
     * Kicks. If the ball is within the reach of the foot it leaves towards the rival goal
     * (or the power shot comes out, if the aura is on). If the rival is within reach it takes
     * {@link #KICK_DAMAGE} times the kick multiplier.
     *
     * @param match the match being played
     */
    public void kick(Match match) {
        if (kickTimer > 0) {
            return;
        }
        kickTimer = KICK_WINDOW;
        Ball ball = match.getBall();
        if (reachesBall(ball)) {
            match.addEvent("KICK");
            if (aura) {
                match.onBallTouch(this);
            } else {
                ball.applyImpulse(getDirection() * KICK_SPEED, -KICK_SPEED * 0.5);
            }
        }
        Player rival = match.getOpponent(this);
        if (reaches(rival)) {
            rival.takeDamage((int) Math.round(KICK_DAMAGE * stats.getKickMultiplier()));
        }
    }

    /**
     * Activates the power: with a full bar and no aura, the bar goes to 0 (and starts filling again)
     * and the aura appears. It has no time limit.
     */
    public void activatePower() {
        if (!aura && isPowerReady()) {
            powerCharge = 0;
            aura = true;
        }
    }

    /**
     * Removes the aura because the power shot was launched.
     */
    public void useAura() {
        aura = false;
    }

    /**
     * Tells whether the power is active, waiting to touch the ball.
     *
     * @return true if the player has the aura
     */
    public boolean hasAura() {
        return aura;
    }

    /**
     * Tells whether the power bar is full.
     *
     * @return true if the power can be activated
     */
    public boolean isPowerReady() {
        return powerCharge >= 1;
    }

    /**
     * Takes damage from a kick or a power shot. If the life reaches 0 the player is stunned.
     * A stunned player takes no more damage.
     *
     * @param amount damage
     */
    public void takeDamage(int amount) {
        if (stun != null || amount <= 0) {
            return;
        }
        life = Math.max(0, life - amount);
        if (life == 0) {
            stun = new StunEffect();
        }
    }

    /**
     * Puts the effect of a power shot on the player. It replaces the current one.
     *
     * @param e the new effect
     */
    public void applyEffect(Effect e) {
        effect = e;
        e.onApply(this);
    }

    /**
     * The player received a goal: its power bar goes up a quarter.
     */
    public void onGoalAgainst() {
        powerCharge = Math.min(1, powerCharge + GOAL_AGAINST_BONUS);
    }

    /**
     * Tells whether the player can move and kick.
     *
     * @return false if it is stunned or its effect blocks it
     */
    public boolean canAct() {
        return stun == null && (effect == null || effect.canAct());
    }

    /**
     * Tells whether the leg is stretched, to draw the kick.
     *
     * @return true during a kick
     */
    public boolean isKicking() {
        return kickTimer > 0;
    }

    /**
     * Returns the names of the active effect and the stun, to show them on screen.
     *
     * @return names, empty if the player is normal
     */
    public List<String> getStates() {
        List<String> names = new ArrayList<>();
        if (effect != null) {
            names.add(effect.getName());
        }
        if (stun != null) {
            names.add(stun.getName());
        }
        return names;
    }

    /**
     * Returns the direction the player attacks.
     *
     * @return 1 for the left player (attacks to the right), -1 for the right one
     */
    public double getDirection() {
        return "LEFT".equals(side) ? 1 : -1;
    }

    /**
     * Returns the current life.
     *
     * @return life, from 0 to the maximum
     */
    public int getLife() {
        return life;
    }

    /**
     * Returns the maximum life, given by Survival.
     *
     * @return maximum life
     */
    public int getMaxLife() {
        return maxLife;
    }

    /**
     * Returns the progress of the power bar.
     *
     * @return a value from 0 to 1
     */
    public double getPowerCharge() {
        return powerCharge;
    }

    /**
     * Tells the player whether it stands on the ground. The physics calls it.
     *
     * @param value true if it touches the ground
     */
    public void setOnGround(boolean value) {
        onGround = value;
    }

    /**
     * Tells whether the player stands on the ground.
     *
     * @return true if it touches the ground
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Returns the side the player defends.
     *
     * @return "LEFT" or "RIGHT"
     */
    public String getSide() {
        return side;
    }

    /**
     * Returns the character of the player.
     *
     * @return the character
     */
    public GameCharacter getCharacter() {
        return character;
    }

    /**
     * Returns the stats of the player.
     *
     * @return the stats of its profile
     */
    public Stats getStats() {
        return stats;
    }

    /**
     * Returns who controls the player.
     *
     * @return the controller
     */
    public PlayerController getController() {
        return controller;
    }

    /**
     * Returns the running speed with the Speed stat and the effect.
     *
     * @return speed, in pixels per second
     */
    private double runSpeed() {
        double factor = effect == null ? 1 : effect.getSpeedFactor();
        return BASE_SPEED * stats.getSpeedMultiplier() * factor;
    }

    /**
     * Tells whether the ball is within the reach of the foot.
     *
     * @param ball the real ball
     * @return true if the foot can touch it
     */
    private boolean reachesBall(Ball ball) {
        double footX = getDirection() > 0 ? x + width : x;
        double footY = y + height * 0.75;
        double dx = ball.getX() + ball.getRadius() - footX;
        double dy = ball.getY() + ball.getRadius() - footY;
        return Math.hypot(dx, dy) <= KICK_REACH + ball.getRadius();
    }

    /**
     * Tells whether the rival is within the reach of the foot, in front of the player.
     *
     * @param rival the other player
     * @return true if the kick hits it
     */
    private boolean reaches(Player rival) {
        double gap = getDirection() > 0 ? rival.x - (x + width) : x - (rival.x + rival.width);
        boolean sameHeight = y < rival.y + rival.height && rival.y < y + height;
        return sameHeight && gap <= KICK_REACH && gap >= -width / 2;
    }

    /**
     * Asks the physics for the body of a player.
     *
     * @param engine the physics adapter of the match
     */
    @Override
    void addTo(PhysicsEngine engine) {
        engine.addPlayer(this);
    }
}
