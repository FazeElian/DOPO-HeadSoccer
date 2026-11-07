package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.World;
import org.dyn4j.world.listener.ContactListenerAdapter;

/**
 * Adapter of the physics library dyn4j (Adapter pattern). It is the only class that imports it:
 * the rest of the domain works in pixels with {@link Entity}, and this class copies the entities
 * to bodies of the library, advances the world and copies the result back.
 * Each entity asks for its own kind of body ({@link Entity#addTo}), so no type checks are needed.
 * The ground, the walls, the ceiling and the crossbars are static bodies.
 * The library is not serializable, so its world is rebuilt after opening a saved game.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class PhysicsEngine implements Serializable {

    /** Pixels in one meter of the library. */
    public static final double PIXELS_PER_METER = 100;
    /** Gravity, in meters per second squared (y grows downwards, as on the screen). */
    public static final double GRAVITY = 9.8;

    /** Collision category of the field. */
    private static final long STATIC = 1;
    /** Collision category of the players. */
    private static final long PLAYER = 2;
    /** Collision category of the real ball. */
    private static final long BALL = 4;
    /** Collision category of the fake balls. */
    private static final long FAKE = 8;
    /** Collision category of the goal areas: they stop the players, but the balls go in. */
    private static final long GOAL_AREA = 16;
    /** Thickness of the walls, the ground and the ceiling, in pixels. */
    private static final double WALL = 200;

    /** Field whose limits become static bodies. */
    private final Stadium stadium;
    /** World of the library; rebuilt after opening a game. */
    private World<Body> world;
    /** Body of each entity on the field. */
    private Map<Entity, Body> bodies;
    /** Player of each player body, to report touches and the ground. */
    private Map<Body, Player> players;
    /** Body of the real ball. */
    private Body ballBody;
    /** Bodies of the walls, the ceiling and the crossbars: they end a power shot. */
    private Set<Body> borders;
    /** Pairs of bodies that started touching in the last step. */
    private LinkedHashSet<List<Body>> contacts;

    /**
     * Creates the adapter for a field. The world of the library is built on the first step.
     *
     * @param stadium the field of the match
     */
    public PhysicsEngine(Stadium stadium) {
        this.stadium = stadium;
    }

    /**
     * Advances the physics dt seconds: sends the entities to the library, steps its world,
     * copies positions and velocities back and reports the touches to the match.
     *
     * @param match the match being played
     * @param dt    elapsed time, in seconds
     */
    public void step(Match match, double dt) {
        if (world == null) {
            createWorld();
        }
        syncToBodies(match);
        contacts.clear();
        world.step(1, dt);
        syncFromBodies();
        for (List<Body> pair : new ArrayList<>(contacts)) {
            onContact(pair.get(0), pair.get(1), match);
        }
    }

    /**
     * Creates the body of a player: a box that does not rotate and collides with the field,
     * the other player and the real ball. {@link Player#addTo} calls it.
     *
     * @param p the player
     */
    void addPlayer(Player p) {
        Body body = new Body();
        BodyFixture fixture = body.addFixture(
                Geometry.createRectangle(p.getWidth() / PIXELS_PER_METER, p.getHeight() / PIXELS_PER_METER), 1.0, 0.4, 0.0);
        fixture.setFilter(new CategoryFilter(PLAYER, STATIC | PLAYER | BALL | GOAL_AREA));
        body.setMass(MassType.FIXED_ANGULAR_VELOCITY);
        register(p, body);
        players.put(body, p);
    }

    /**
     * Creates the body of the real ball: a bouncing circle that collides with the field and the players.
     * {@link Ball#addTo} calls it.
     *
     * @param b the real ball
     */
    void addBall(Ball b) {
        ballBody = circle(b, BALL, STATIC | PLAYER);
    }

    /**
     * Creates the body of a fake ball: a bouncing circle that only collides with the field,
     * so it goes through the players. {@link FakeBall#addTo} calls it.
     *
     * @param f the fake ball
     */
    void addFakeBall(FakeBall f) {
        circle(f, FAKE, STATIC);
    }

    /**
     * Creates the world, its gravity, the static bodies of the field and the contact listener.
     */
    private void createWorld() {
        world = new World<>();
        world.setGravity(0, GRAVITY);
        world.getSettings().setAtRestDetectionEnabled(false);
        bodies = new HashMap<>();
        players = new HashMap<>();
        borders = new HashSet<>();
        contacts = new LinkedHashSet<>();
        ballBody = null;
        double w = stadium.getWidth();
        double h = stadium.getHeight();
        addStatic(-WALL, stadium.getGroundY(), w + 2 * WALL, WALL);
        borders.add(addStatic(-WALL, -WALL, WALL, h + 2 * WALL));
        borders.add(addStatic(w, -WALL, WALL, h + 2 * WALL));
        borders.add(addStatic(-WALL, -WALL, w + 2 * WALL, WALL));
        for (String side : new String[]{"LEFT", "RIGHT"}) {
            Goal goal = stadium.getGoal(side);
            borders.add(addStatic(goal.getX(), goal.getY() - 10, goal.getWidth(), 10));
            addGoalArea(goal); // the players can't walk into the goal
        }
        world.addContactListener(new ContactListenerAdapter<Body>() {
            @Override
            public void begin(ContactCollisionData<Body> collision, Contact contact) {
                contacts.add(List.of(collision.getBody1(), collision.getBody2()));
            }
        });
    }

    /**
     * Adds a still rectangle to the world.
     *
     * @param x      left edge, in pixels
     * @param y      top edge, in pixels
     * @param width  width, in pixels
     * @param height height, in pixels
     * @return the new body
     */
    private Body addStatic(double x, double y, double width, double height) {
        Body body = new Body();
        BodyFixture fixture = body.addFixture(Geometry.createRectangle(width / PIXELS_PER_METER, height / PIXELS_PER_METER));
        fixture.setFriction(0.6);
        fixture.setFilter(new CategoryFilter(STATIC, PLAYER | BALL | FAKE));
        body.translate((x + width / 2) / PIXELS_PER_METER, (y + height / 2) / PIXELS_PER_METER);
        body.setMass(MassType.INFINITE);
        world.addBody(body);
        return body;
    }

    /**
     * Adds a still box over a goal area that only the players hit, so they can't walk into the goal;
     * the balls go through it and can score. It is not a border: it doesn't end a power shot.
     *
     * @param goal the goal
     */
    private void addGoalArea(Goal goal) {
        Body body = new Body();
        BodyFixture fixture = body.addFixture(Geometry.createRectangle(
                goal.getWidth() / PIXELS_PER_METER, goal.getHeight() / PIXELS_PER_METER));
        fixture.setFriction(0); // the player slides down its front instead of sticking to it
        fixture.setFilter(new CategoryFilter(GOAL_AREA, PLAYER));
        body.translate((goal.getX() + goal.getWidth() / 2) / PIXELS_PER_METER,
                (goal.getY() + goal.getHeight() / 2) / PIXELS_PER_METER);
        body.setMass(MassType.INFINITE);
        world.addBody(body);
    }

    /**
     * Creates and registers a bouncing circle for a ball.
     *
     * @param e        the ball, real or fake
     * @param category its collision category
     * @param mask     the categories it collides with
     * @return the new body
     */
    private Body circle(Entity e, long category, long mask) {
        Body body = new Body();
        BodyFixture fixture = body.addFixture(Geometry.createCircle(e.getWidth() / 2 / PIXELS_PER_METER), 0.5, 0.3, Ball.RESTITUTION);
        fixture.setFilter(new CategoryFilter(category, mask));
        body.setMass(MassType.NORMAL);
        body.setBullet(true);
        body.setLinearDamping(0.05);
        register(e, body);
        return body;
    }

    /**
     * Keeps the body of an entity and adds it to the world.
     *
     * @param e    the entity
     * @param body its body
     */
    private void register(Entity e, Body body) {
        bodies.put(e, body);
        world.addBody(body);
    }

    /**
     * Sends the entities to the library: creates the bodies that are missing, removes the ones whose entity
     * left the field (fake balls) and copies positions, velocities and gravity, which the domain may have
     * changed (running, jumping, kicks, kick-off, straight power shots).
     *
     * @param match the match being played
     */
    private void syncToBodies(Match match) {
        List<Entity> entities = entitiesOf(match);
        Iterator<Map.Entry<Entity, Body>> it = bodies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Entity, Body> entry = it.next();
            if (!entities.contains(entry.getKey())) {
                world.removeBody(entry.getValue());
                it.remove();
            }
        }
        for (Entity e : entities) {
            if (!bodies.containsKey(e)) {
                e.addTo(this);
            }
            Body body = bodies.get(e);
            body.getTransform().setTranslation(centerX(e) / PIXELS_PER_METER, centerY(e) / PIXELS_PER_METER);
            body.setLinearVelocity(e.getVx() / PIXELS_PER_METER, e.getVy() / PIXELS_PER_METER);
            body.setGravityScale(e.getGravityScale());
        }
    }

    /**
     * Copies positions and velocities from the library to the entities and tells the players
     * whether they stand on the ground.
     */
    private void syncFromBodies() {
        for (Map.Entry<Entity, Body> entry : bodies.entrySet()) {
            Entity e = entry.getKey();
            Vector2 center = entry.getValue().getWorldCenter();
            Vector2 velocity = entry.getValue().getLinearVelocity();
            e.setPosition(center.x * PIXELS_PER_METER - e.getWidth() / 2, center.y * PIXELS_PER_METER - e.getHeight() / 2);
            e.setVelocity(velocity.x * PIXELS_PER_METER, velocity.y * PIXELS_PER_METER);
        }
        for (Player p : players.values()) {
            p.setOnGround(p.getY() + p.getHeight() >= stadium.getGroundY() - 2);
        }
    }

    /**
     * Reports a touch found by the library: when the real ball touches a player, the match decides
     * whether a power shot comes out or hits; when it hits a wall, the ceiling or a crossbar, the shot is over.
     * Other touches (players on the ground, fake balls) do not matter to the rules.
     *
     * @param a     one body of the touch
     * @param b     the other body
     * @param match the match being played
     */
    private void onContact(Body a, Body b, Match match) {
        Body other = a == ballBody ? b : b == ballBody ? a : null;
        if (other == null) {
            return;
        }
        Player p = players.get(other);
        if (p != null) {
            match.onBallTouch(p);
        } else if (borders.contains(other)) {
            match.onBallHitsBorder();
        }
    }

    /**
     * Returns the entities that must have a body: the two players, the ball and the fake balls.
     *
     * @param match the match being played
     * @return the entities on the field
     */
    private static List<Entity> entitiesOf(Match match) {
        List<Entity> entities = new ArrayList<>();
        entities.add(match.getPlayer("LEFT"));
        entities.add(match.getPlayer("RIGHT"));
        entities.add(match.getBall());
        entities.addAll(match.getFakeBalls());
        return entities;
    }

    /**
     * Returns the horizontal center of an entity.
     *
     * @param e the entity
     * @return x of its center, in pixels
     */
    private static double centerX(Entity e) {
        return e.getX() + e.getWidth() / 2;
    }

    /**
     * Returns the vertical center of an entity.
     *
     * @param e the entity
     * @return y of its center, in pixels
     */
    private static double centerY(Entity e) {
        return e.getY() + e.getHeight() / 2;
    }
}
