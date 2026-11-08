package domain;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Facade of the domain: the only class the interface talks to (Facade pattern).
 * In this mock-up only player vs player in Arcade works, by time or by goals.
 * Every other option of the design throws a {@link DOPOSoccerException} with
 * {@link DOPOSoccerException#UNDER_CONSTRUCTION}, which the interface catches and shows.
 * The public methods keep the signatures of the full version.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class DOPOSoccer {

    /** Points for the winner of a match. */
    public static final int WIN_POINTS = 100;
    /** Points for the loser of a match. */
    public static final int LOSE_POINTS = 25;
    /** Name of the ball used until another one is chosen. */
    public static final String DEFAULT_BALL = "Clásico";

    /** The 8 teams, by country. */
    private final LinkedHashMap<String, GameCharacter> characters;
    /** The balls of the catalog, by name. */
    private final LinkedHashMap<String, BallSkin> balls;
    /** Ball used in the matches (the last one chosen). */
    private BallSkin ball;
    /** Game being played, or null. */
    private GameMode mode;
    /** Whether the game is paused. */
    private boolean paused;
    /** Whether the points of the current match were already computed. */
    private boolean pointsGiven;
    /** Points earned by each side in the last match. */
    private final int[] lastPoints;
    /** Profiles of the left and right players: their stats are used in every match. */
    private final PlayerProfile[] profiles;
    /** Settings of the game. */
    private final Settings settings;

    /**
     * Creates the game with the 8 teams (the 4 characters of Annex 1 and 4 more that share their power shots;
     * España and Japón are locked) and the 6 balls of the catalog (Dorado, Fuego, Hielo and Galaxia are locked).
     */
    public DOPOSoccer() {
        characters = new LinkedHashMap<>();
        characters.put("Corea del Sur", new GameCharacter("Corea del Sur", new BlueAuraShot()));
        characters.put("Camerún", new GameCharacter("Camerún", new LightningShot()));
        characters.put("Nigeria", new GameCharacter("Nigeria", new TornadoShot()));
        characters.put("Estados Unidos", new GameCharacter("Estados Unidos", new IllusionShot()));
        characters.put("Colombia", new GameCharacter("Colombia", new IllusionShot()));
        characters.put("Argentina", new GameCharacter("Argentina", new LightningShot()));
        characters.put("España", new GameCharacter("España", new BlueAuraShot(), 800));
        characters.put("Japón", new GameCharacter("Japón", new TornadoShot(), 1200));
        balls = new LinkedHashMap<>();
        balls.put("Clásico", new BallSkin("Clásico", 0));
        balls.put("Tricolor", new BallSkin("Tricolor", 0));
        balls.put("Dorado", new BallSkin("Dorado", 500));
        balls.put("Fuego", new BallSkin("Fuego", 700));
        balls.put("Hielo", new BallSkin("Hielo", 700));
        balls.put("Galaxia", new BallSkin("Galaxia", 1000));
        ball = balls.get(DEFAULT_BALL);
        lastPoints = new int[2];
        profiles = new PlayerProfile[]{new PlayerProfile("J1", ball), new PlayerProfile("J2", ball)};
        settings = new Settings();
    }

    // game

    /**
     * Validates the choices and prepares the game. Only "PVP", "ARCADE", "TIME" and "GOALS" are built;
     * player vs machine, the tournament, survival and the locked characters are under construction.
     *
     * @param modality       "PVP" ("PVM" is under construction)
     * @param aiProfile      machine profile; not used in player vs player
     * @param resolution     "SMALL", "MEDIUM" or "LARGE"
     * @param modeName       "ARCADE" ("TOURNAMENT" is under construction)
     * @param leftCharacter  country of the left player
     * @param rightCharacter country of the right player
     * @param matchType      "TIME" or "GOALS" ("SURVIVAL" is under construction)
     * @param amount         minutes of the match or goals to win
     * @throws DOPOSoccerException UNDER_CONSTRUCTION, UNKNOWN_MODALITY, UNKNOWN_MODE, UNKNOWN_CHARACTER,
     *                             UNKNOWN_MATCH_TYPE, UNKNOWN_RESOLUTION, INVALID_MINUTES or INVALID_GOAL_TARGET
     */
    public void newGame(String modality, String aiProfile, String resolution, String modeName,
                        String leftCharacter, String rightCharacter, String matchType, int amount)
            throws DOPOSoccerException {
        if ("PVM".equals(modality)) {
            throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
        }
        else if (!"PVP".equals(modality)) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MODALITY);
        }

        if ("TOURNAMENT".equals(modeName)) {
            throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
        }
        else if (!"ARCADE".equals(modeName)) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MODE);
        }
        GameCharacter left = character(leftCharacter);
        GameCharacter right = character(rightCharacter);
        if (!left.isFree() || !right.isFree()) {
            throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
        }
        MatchType type = createType(matchType, amount);
        GameMode newMode = new ArcadeMode(left, profiles[0].getStats(), new HumanController(),
                right, profiles[1].getStats(), new HumanController(), type, resolution, ball);
        newMode.start();
        mode = newMode;
        paused = false;
        pointsGiven = false;
        lastPoints[0] = 0;
        lastPoints[1] = 0;
    }

    /**
     * Advances the game one tick if it is not paused. When the match ends, it computes the points.
     * Without a game it does nothing.
     *
     * @param dt elapsed time, in seconds
     */
    public void update(double dt) {
        if (mode == null || paused) {
            return;
        }
        mode.update(dt);
        if (mode.getMatch().isFinished() && !pointsGiven) {
            givePoints();
        }
    }

    /**
     * An action key of a side was pressed.
     *
     * @param side   "LEFT" or "RIGHT"
     * @param action "MOVE_LEFT", "MOVE_RIGHT", "JUMP", "KICK" or "POWER"
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public void press(String side, String action) throws DOPOSoccerException {
        player(side).getController().press(action);
    }

    /**
     * An action key of a side was released.
     *
     * @param side   "LEFT" or "RIGHT"
     * @param action the released action
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public void release(String side, String action) throws DOPOSoccerException {
        player(side).getController().release(action);
    }


    /**
     * Pauses the game.
     *
     * @throws DOPOSoccerException GAME_NOT_STARTED, or GAME_OVER if the match ended
     */
    public void pause() throws DOPOSoccerException {
        if (match().isFinished()) {
            throw new DOPOSoccerException(DOPOSoccerException.GAME_OVER);
        }
        paused = true;
    }

    /**
     * Resumes the game.
     *
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public void resume() throws DOPOSoccerException {
        match();
        paused = false;
    }

    /**
     * Tells whether the game is paused.
     *
     * @return true if it is paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Ends the game at any moment. No points are given.
     *
     * @throws DOPOSoccerException GAME_NOT_STARTED, or GAME_OVER if the match already ended
     */
    public void endGame() throws DOPOSoccerException {
        Match match = match();
        if (match.isFinished()) {
            throw new DOPOSoccerException(DOPOSoccerException.GAME_OVER);
        }
        match.end();
        pointsGiven = true;
        paused = false;
    }

    /**
     * Goes to the next match of the mode. Under construction (Arcade has only one match).
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void nextMatch() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Tells whether the current match ended.
     *
     * @return true if it ended
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public boolean isMatchOver() throws DOPOSoccerException {
        return match().isFinished();
    }

    /**
     * Tells whether the mode ended. In Arcade it is the same as the match.
     *
     * @return true if it ended
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public boolean isModeOver() throws DOPOSoccerException {
        return match().isFinished();
    }

    /**
     * Returns the winner of the current match.
     *
     * @return "LEFT", "RIGHT", or null if it is not over or was ended by hand
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getWinner() throws DOPOSoccerException {
        return match().getWinner();
    }

    /**
     * Returns the points a side earned in the last match, to show them in the result message.
     *
     * @param side "LEFT" or "RIGHT"
     * @return 100, 25 or 0
     * @throws DOPOSoccerException UNKNOWN_SIDE
     */
    public int getPointsEarned(String side) throws DOPOSoccerException {
        return lastPoints[index(side)];
    }

    /**
     * Returns the name of the mode being played.
     *
     * @return "ARCADE"
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getMode() throws DOPOSoccerException {
        match();
        return mode.getName();
    }

    /**
     * Returns the current round of the mode.
     *
     * @return 1 in Arcade
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public int getRound() throws DOPOSoccerException {
        match();
        return mode.getRound();
    }

    /**
     * Returns the modality being played.
     *
     * @return "PVP"
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getModality() throws DOPOSoccerException {
        match();
        return "PVP";
    }

    /**
     * Returns the name of the match type being played.
     *
     * @return "TIME" or "GOALS"
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getMatchType() throws DOPOSoccerException {
        return match().getType().getName();
    }

    // state for drawing

    /**
     * Returns the goals of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return its goals
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public int getGoals(String side) throws DOPOSoccerException {
        index(side);
        return match().getScoreBoard().getGoals(side);
    }

    /**
     * Returns the seconds left of a match by time.
     *
     * @return seconds left, or -1 in a match by goals
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public int getTimeLeft() throws DOPOSoccerException {
        Match match = match();
        return match.getType().getTimeLeft(match);
    }

    /**
     * Tells whether the match is in sudden death.
     *
     * @return true in sudden death
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public boolean isSuddenDeath() throws DOPOSoccerException {
        return match().getType().isSuddenDeath();
    }

    /**
     * Returns what happened since the last call, for the sounds ("KICK", "POST", "GOAL").
     *
     * @return the events, in the order they happened
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public List<String> pollEvents() throws DOPOSoccerException {
        return match().pollEvents();
    }

    /**
     * Returns the current life of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return its life
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public int getLife(String side) throws DOPOSoccerException {
        return player(side).getLife();
    }

    /**
     * Returns the maximum life of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return its maximum life
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public int getMaxLife(String side) throws DOPOSoccerException {
        return player(side).getMaxLife();
    }

    /**
     * Returns the power bar of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return a value from 0 to 1
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public double getPowerCharge(String side) throws DOPOSoccerException {
        return player(side).getPowerCharge();
    }

    /**
     * Tells whether a side has its power active (aura).
     *
     * @param side "LEFT" or "RIGHT"
     * @return true if it has the aura
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public boolean hasAura(String side) throws DOPOSoccerException {
        return player(side).hasAura();
    }

    /**
     * Returns the names of the effects on a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return names, empty if it is normal
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public List<String> getStates(String side) throws DOPOSoccerException {
        return player(side).getStates();
    }

    /**
     * Tells whether a side is kicking, to draw its leg.
     *
     * @param side "LEFT" or "RIGHT"
     * @return true during a kick
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public boolean isKicking(String side) throws DOPOSoccerException {
        return player(side).isKicking();
    }

    /**
     * Returns the country of the character of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return its country
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public String getCharacter(String side) throws DOPOSoccerException {
        return player(side).getCharacter().getCountry();
    }

    /**
     * Returns the name of the power shot of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return name of its shot
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public String getPowerName(String side) throws DOPOSoccerException {
        return player(side).getCharacter().getPowerName();
    }

    /**
     * Returns the left edge of a player.
     *
     * @param side "LEFT" or "RIGHT"
     * @return x, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public double getPlayerX(String side) throws DOPOSoccerException {
        return player(side).getX();
    }

    /**
     * Returns the top edge of a player.
     *
     * @param side "LEFT" or "RIGHT"
     * @return y, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public double getPlayerY(String side) throws DOPOSoccerException {
        return player(side).getY();
    }

    /**
     * Returns the width of the players' box.
     *
     * @return width, in pixels
     */
    public double getPlayerWidth() {
        return Player.WIDTH;
    }

    /**
     * Returns the height of the players' box.
     *
     * @return height, in pixels
     */
    public double getPlayerHeight() {
        return Player.HEIGHT;
    }

    /**
     * Returns the left edge of the real ball.
     *
     * @return x, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getBallX() throws DOPOSoccerException {
        return match().getBall().getX();
    }

    /**
     * Returns the top edge of the real ball.
     *
     * @return y, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getBallY() throws DOPOSoccerException {
        return match().getBall().getY();
    }

    /**
     * Returns the radius of the balls.
     *
     * @return radius, in pixels
     */
    public double getBallRadius() {
        return Match.BALL_RADIUS;
    }

    /**
     * Returns the positions of the fake balls of the Illusion shot.
     *
     * @return one {x, y} (left and top edge, in pixels) per fake ball
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public List<double[]> getFakeBalls() throws DOPOSoccerException {
        List<double[]> positions = new ArrayList<>();
        for (FakeBall fake : match().getFakeBalls()) {
            positions.add(new double[]{fake.getX(), fake.getY()});
        }
        return positions;
    }

    /**
     * Returns the name of the ball of the match, to draw its picture.
     *
     * @return name of the ball
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getBallName() throws DOPOSoccerException {
        return match().getBall().getSkin().getName();
    }

    /**
     * Returns the width of the field.
     *
     * @return width, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getFieldWidth() throws DOPOSoccerException {
        return match().getStadium().getWidth();
    }

    /**
     * Returns the height of the field.
     *
     * @return height, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getFieldHeight() throws DOPOSoccerException {
        return match().getStadium().getHeight();
    }

    /**
     * Returns the height of the ground.
     *
     * @return y of the ground, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getGroundY() throws DOPOSoccerException {
        return match().getStadium().getGroundY();
    }

    /**
     * Returns the area of the goal a side defends.
     *
     * @param side "LEFT" or "RIGHT"
     * @return {x, y, width, height}, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public double[] getGoal(String side) throws DOPOSoccerException {
        index(side);
        Goal goal = match().getStadium().getGoal(side);
        return new double[]{goal.getX(), goal.getY(), goal.getWidth(), goal.getHeight()};
    }

    //  options for the screens

    /**
     * Returns the countries of the characters.
     *
     * @return the 8 countries, in order
     */
    public List<String> getCharacters() {
        return new ArrayList<>(characters.keySet());
    }

    /**
     * Returns the power shot of a character, for the selection cards.
     *
     * @param country country of the character
     * @return name of its shot
     * @throws DOPOSoccerException UNKNOWN_CHARACTER
     */
    public String getPowerNameOf(String country) throws DOPOSoccerException {
        return character(country).getPowerName();
    }

    /**
     * Returns the price of a character, to draw the padlock of the locked ones.
     *
     * @param country country of the character
     * @return price in points; 0 if it is free
     * @throws DOPOSoccerException UNKNOWN_CHARACTER
     */
    public int getCharacterPrice(String country) throws DOPOSoccerException {
        return character(country).getPrice();
    }

    /**
     * Tells whether a player can use a character. In the mock-up only the free ones.
     *
     * @param side    "LEFT" or "RIGHT"
     * @param country country of the character
     * @return true if it is free
     * @throws DOPOSoccerException UNKNOWN_SIDE or UNKNOWN_CHARACTER
     */
    public boolean isCharacterUnlocked(String side, String country) throws DOPOSoccerException {
        index(side);
        return character(country).isFree();
    }

    /**
     * Returns the resolutions of the field.
     *
     * @return "SMALL", "MEDIUM" and "LARGE"
     */
    public List<String> getResolutions() {
        return List.of("SMALL", "MEDIUM", "LARGE");
    }

    /**
     * Returns the profiles of the machine, to fill the combo box.
     *
     * @return "CLUMSY", "OFFENSIVE", "DEFENSIVE" and "EXPERT"
     */
    public List<String> getAIProfiles() {
        return List.of("CLUMSY", "OFFENSIVE", "DEFENSIVE", "EXPERT");
    }

    // under construction

    /**
     * Returns the balls left in Survival. Under construction.
     *
     * @param side "LEFT" or "RIGHT"
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getBalls(String side) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the teams of the tournament bracket. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public List<String> getTournamentTeams() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the current tournament round. Under construction.
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public String getTournamentRound() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Returns the points of a profile. Under construction (store).
     *
     * @param side "LEFT" or "RIGHT"
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public int getPoints(String side) throws DOPOSoccerException {
        return profiles[index(side)].getPoints();
    }

    /**
     * Returns the level of a stat of a player, to draw its bar.
     *
     * @param side "LEFT" or "RIGHT"
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @return level, from 1 to 10
     * @throws DOPOSoccerException UNKNOWN_SIDE or UNKNOWN_STAT
     */
    public int getStat(String side, String stat) throws DOPOSoccerException {
        return profiles[index(side)].getStats().get(stat);
    }

    /**
     * Returns the price of the next point of a stat.
     *
     * @param side "LEFT" or "RIGHT"
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @return price in points
     * @throws DOPOSoccerException UNKNOWN_SIDE or UNKNOWN_STAT
     */
    public int getUpgradeCost(String side, String stat) throws DOPOSoccerException {
        return profiles[index(side)].getStats().getUpgradeCost(stat);
    }

    /**
     * Buys one point of a stat. Under construction (store).
     *
     * @param side "LEFT" or "RIGHT"
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void upgrade(String side, String stat) throws DOPOSoccerException {
        profiles[index(side)].upgrade(stat);
    }

    /**
     * Buys a locked character. Under construction (store).
     *
     * @param side    "LEFT" or "RIGHT"
     * @param country country of the character
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void buyCharacter(String side, String country) throws DOPOSoccerException {
        profiles[index(side)].unlockCharacter(character(country));
    }

    /**
     * Returns the names of the balls of the catalog.
     *
     * @return the 6 names, in order
     */
    public List<String> getBallNames() {
        return new ArrayList<>(balls.keySet());
    }

    /**
     * Returns the price of a ball, to draw the padlock of the locked ones.
     *
     * @param name name of the ball
     * @return price in points; 0 if it is free
     * @throws DOPOSoccerException UNKNOWN_BALL
     */
    public int getBallPrice(String name) throws DOPOSoccerException {
        return ballSkin(name).getPrice();
    }

    /**
     * Returns the power shot the real ball carries, to draw it glowing while the shot flies.
     *
     * @return name of the shot, or null if the ball is normal
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getBallPower() throws DOPOSoccerException {
        Ball ball = match().getBall();
        if (ball.isPowerShot()) {
            return ball.getCharge().getName();
        }
        return null;
    }

    /**
     * Tells whether a profile bought a ball. Under construction (store).
     *
     * @param side "LEFT" or "RIGHT"
     * @param name name of the ball
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public boolean isBallUnlocked(String side, String name) throws DOPOSoccerException {
        return profiles[index(side)].isBallUnlocked(ballSkin(name).getName());
    }

    /**
     * Buys a ball. Under construction (store).
     *
     * @param side "LEFT" or "RIGHT"
     * @param name name of the ball
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void buyBall(String side, String name) throws DOPOSoccerException {
        profiles[index(side)].unlockBall(ballSkin(name));
    }

    /**
     * Chooses the ball of the next matches. In this version there is one ball for the game,
     * so the last one chosen is used. Only the free balls: the locked ones are bought in the store.
     *
     * @param side "LEFT" or "RIGHT"
     * @param name name of the ball
     * @throws DOPOSoccerException UNKNOWN_SIDE, UNKNOWN_BALL, or UNDER_CONSTRUCTION for a locked ball
     */
    public void selectBall(String side, String name) throws DOPOSoccerException {
        index(side);
        BallSkin skin = ballSkin(name);
        if (skin.getPrice() > 0) {
            throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
        }
        ball = skin;
    }

    /**
     * Returns the ball chosen for the next matches.
     *
     * @param side "LEFT" or "RIGHT"
     * @return name of the ball
     * @throws DOPOSoccerException UNKNOWN_SIDE
     */
    public String getSelectedBall(String side) throws DOPOSoccerException {
        index(side);
        return ball.getName();
    }

    // settings

    /**
     * Returns the volume of the music.
     *
     * @return the volume, from 0 to 100
     */
    public int getMusicVolume() {
        return settings.getMusicVolume();
    }

    /**
     * Changes the volume of the music.
     *
     * @param volume new volume, from 0 to 100
     * @throws DOPOSoccerException INVALID_VOLUME if it is out of 0..100
     */
    public void setMusicVolume(int volume) throws DOPOSoccerException {
        settings.setMusicVolume(volume);
    }

    /**
     * Returns the volume of the sound effects.
     *
     * @return the volume, from 0 to 100
     */
    public int getEffectsVolume() {
        return settings.getEffectsVolume();
    }

    /**
     * Changes the volume of the sound effects.
     *
     * @param volume new volume, from 0 to 100
     * @throws DOPOSoccerException INVALID_VOLUME if it is out of 0..100
     */
    public void setEffectsVolume(int volume) throws DOPOSoccerException {
        settings.setEffectsVolume(volume);
    }

    /**
     * Puts both volumes back to their default values.
     */
    public void resetSettings() {
        settings.reset();
    }

    /**
     * Tells whether there is saved progress. Under construction (persistence).
     *
     * @return never returns
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public boolean hasSavedProgress() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Saves the progress. Under construction (persistence).
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void saveProgress() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Loads the progress. Under construction (persistence).
     *
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void loadProgress() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Saves the game being played. Under construction (persistence, version two).
     *
     * @param file file chosen by the user
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void saveGame(File file) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    /**
     * Opens a saved game. Under construction (persistence, version two).
     *
     * @param file file chosen by the user
     * @throws DOPOSoccerException UNDER_CONSTRUCTION always
     */
    public void openGame(File file) throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.UNDER_CONSTRUCTION);
    }

    //  helpers

    /**
     * Computes 100 points for the winner and 25 for the loser, to show them in the result message.
     * They are not kept yet: the store is under construction.
     */
    private void givePoints() {
        pointsGiven = true;
        String winner = mode.getMatch().getWinner();
        if (winner == null) {
            return;
        }
        int w = "LEFT".equals(winner) ? 0 : 1;
        lastPoints[w] = WIN_POINTS;
        lastPoints[1 - w] = LOSE_POINTS;
    }

    /**
     * Creates the rule of the match.
     *
     * @param matchType "TIME" or "GOALS" ("SURVIVAL" is under construction)
     * @param amount    minutes or goals
     * @return the rule
     * @throws DOPOSoccerException UNDER_CONSTRUCTION, UNKNOWN_MATCH_TYPE, INVALID_MINUTES or INVALID_GOAL_TARGET
     */
    private static MatchType createType(String matchType, int amount) throws DOPOSoccerException {
        if ("TIME".equals(matchType)) {
            return new TimeMatch(amount);
        }
        if ("GOALS".equals(matchType)) {
            return new GoalsMatch(amount);
        }
        if ("SURVIVAL".equals(matchType)) {
            return new SurvivalMatch(); // under construction: it throws
        }
        throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MATCH_TYPE);
    }

    /**
     * Finds a character.
     *
     * @param country its country
     * @return the character
     * @throws DOPOSoccerException UNKNOWN_CHARACTER
     */
    private GameCharacter character(String country) throws DOPOSoccerException {
        GameCharacter c = characters.get(country);
        if (c == null) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_CHARACTER);
        }
        return c;
    }

    /**
     * Finds a ball of the catalog.
     *
     * @param name its name
     * @return the ball
     * @throws DOPOSoccerException UNKNOWN_BALL
     */
    private BallSkin ballSkin(String name) throws DOPOSoccerException {
        BallSkin skin = balls.get(name);
        if (skin == null) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_BALL);
        }
        return skin;
    }

    /**
     * Returns the current match.
     *
     * @return the match
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    private Match match() throws DOPOSoccerException {
        if (mode == null || mode.getMatch() == null) {
            throw new DOPOSoccerException(DOPOSoccerException.GAME_NOT_STARTED);
        }
        return mode.getMatch();
    }

    /**
     * Returns the player of a side in the current match.
     *
     * @param side "LEFT" or "RIGHT"
     * @return the player
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    private Player player(String side) throws DOPOSoccerException {
        index(side);
        return match().getPlayer(side);
    }

    /**
     * Turns a side into an index.
     *
     * @param side "LEFT" or "RIGHT"
     * @return 0 for left, 1 for right
     * @throws DOPOSoccerException UNKNOWN_SIDE
     */
    private static int index(String side) throws DOPOSoccerException {
        if ("LEFT".equals(side)) {
            return 0;
        }
        if ("RIGHT".equals(side)) {
            return 1;
        }
        throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_SIDE);
    }
}
