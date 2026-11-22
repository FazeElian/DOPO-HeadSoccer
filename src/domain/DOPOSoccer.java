package domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * Facade of the domain: the only class the interface talks to (Facade pattern).
 * Arcade works player vs player and player vs machine, by time, by goals or in survival (only
 * against the machine); the tournament (8 teams against the machine) works by time or by goals;
 * and the store (points, characters, balls and stats). The profiles of both players are saved in a
 * fixed progress file every time they change, and loaded when the game opens. A game being played is
 * saved whole in a file chosen by the user ({@link #saveGame}) and opened again as a new object
 * ({@link #openGame}); the profiles and the settings are not part of it.
 * The public methods keep the signatures of the full version.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class DOPOSoccer implements Serializable {

    /** Version of the saved data, so a saved game of this version can be opened again. */
    private static final long serialVersionUID = 1L;

    /** Points for the winner of a match. */
    public static final int WIN_POINTS = 100;
    /** Points for the loser of a match. */
    public static final int LOSE_POINTS = 25;
    /** Name of the ball used until another one is chosen. */
    public static final String DEFAULT_BALL = "Clásico";
    /** Name of the file where the progress is saved, next to the project. */
    public static final String PROGRESS_FILE = "DOPOSoccer.progress";
    /** Extension of the files of the saved games ("ds", of DOPO Soccer), without the dot. */
    public static final String GAME_EXTENSION = "ds";

    /** The 8 teams, by country. */
    private final LinkedHashMap<String, GameCharacter> characters;
    /** The balls of the catalog, by name. */
    private final LinkedHashMap<String, BallSkin> balls;
    /** Ball used in the matches (the last one chosen). */
    private BallSkin ball;
    /** Modality of the current game: "PVP" or "PVM". */
    private String modality;
    /** Game being played, or null. */
    private GameMode mode;
    /** Whether the game is paused. */
    private boolean paused;
    /** Whether the points of the current match were already computed. */
    private boolean pointsGiven;
    /** Points earned by each side in the last match. */
    private final int[] lastPoints;
    /**
     * Profiles of the left and right players: their stats are used in every match. They are not saved
     * with a game (transient): they live in the progress file, so opening a game never takes them back.
     */
    private transient PlayerProfile[] profiles;
    /** Settings of the game. They are not saved with a game (transient). */
    private transient Settings settings;
    /** Tournament being played, or null in Arcade. */
    private TournamentMode tournament;
    /** Stadium used until another one is chosen. */
    public static final String DEFAULT_STADIUM = "default";
    /** Stadium of the next matches (the last one chosen). */
    private String stadium = DEFAULT_STADIUM;
    /** Fixed file where the profiles of both players are saved and loaded. */
    private final File progressFile;

    /**
     * Creates the game that saves its progress in {@link #PROGRESS_FILE}.
     */
    public DOPOSoccer() {
        this(new File(PROGRESS_FILE));
    }

    /**
     * Creates the game with the 8 teams (the 4 characters of Annex 1 and 4 more that share their power shots;
     * España and Japón are locked) and the 6 balls of the catalog (Dorado, Fuego, Hielo and Galaxia are locked).
     * The progress is not loaded here: {@link #loadProgress()} does it.
     *
     * @param progressFile file where the progress is saved and loaded
     */
    public DOPOSoccer(File progressFile) {
        this.progressFile = progressFile;
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
     * Validates the choices and prepares the game. Arcade is played player vs player or against
     * the machine; the tournament is played against the machine, with 7 rivals drawn at random.
     * Both are played by time or by goals; survival is played only in Arcade against the machine.
     *
     * @param modality       "PVP" or "PVM"
     * @param aiProfile      "CLUMSY", "OFFENSIVE", "DEFENSIVE" or "EXPERT"; not used in player vs player
     * @param resolution     "SMALL", "MEDIUM" or "LARGE"
     * @param modeName       "ARCADE" or "TOURNAMENT"
     * @param leftCharacter  country of the left player
     * @param rightCharacter country of the right player (not used in the tournament)
     * @param matchType      "TIME", "GOALS" or "SURVIVAL"
     * @param amount         minutes of the match or goals to win (not used in survival)
     * @throws DOPOSoccerException UNKNOWN_MODALITY, UNKNOWN_MODE, UNKNOWN_CHARACTER, CHARACTER_LOCKED,
     *                             UNKNOWN_AI_PROFILE, UNKNOWN_MATCH_TYPE, UNKNOWN_RESOLUTION, INVALID_MINUTES,
     *                             INVALID_GOAL_TARGET, INVALID_TOURNAMENT_TYPE or SURVIVAL_ONLY_PVM
     */
    public void newGame(String modality, String aiProfile, String resolution, String modeName,
                        String leftCharacter, String rightCharacter, String matchType, int amount)
            throws DOPOSoccerException {
        boolean vsMachine = "PVM".equals(modality);
        if (!vsMachine && !"PVP".equals(modality)) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MODALITY);
        }
        boolean isTournament = "TOURNAMENT".equals(modeName);
        if (!isTournament && !"ARCADE".equals(modeName)) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MODE);
        }
        if (isTournament && "SURVIVAL".equals(matchType)) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_TOURNAMENT_TYPE);
        }

        GameCharacter left = character(leftCharacter);
        GameCharacter right = character(rightCharacter);
        
        // Each person plays only with its own characters; the machine plays with any
        if (!profiles[0].isCharacterUnlocked(left)
                || (!isTournament && !vsMachine && !profiles[1].isCharacterUnlocked(right))) {
            throw new DOPOSoccerException(DOPOSoccerException.CHARACTER_LOCKED);
        }

        if ("SURVIVAL".equals(matchType) && !vsMachine) {
            throw new DOPOSoccerException(DOPOSoccerException.SURVIVAL_ONLY_PVM);
        }
        MatchType type = createType(matchType, amount);

        GameMode newMode;
        TournamentMode newTournament = null;
        if (isTournament) {
            // Always against the machine: the 7 rivals are the other teams
            List<GameCharacter> others = new ArrayList<>(characters.values());
            others.remove(left);
            newTournament = new TournamentMode(left, profiles[0].getStats(), new HumanController(),
                    new Stats(), createAI(aiProfile), type, resolution, ball, others, new Random());
            newMode = newTournament;
            this.modality = "PVM";
        } else {
            PlayerController rival = vsMachine ? createAI(aiProfile) : new HumanController();
            Stats rivalStats = vsMachine ? new Stats() : profiles[1].getStats();
            newMode = new ArcadeMode(left, profiles[0].getStats(), new HumanController(),
                    right, rivalStats, rival, type, resolution, ball, stadium);
            this.modality = modality;
        }
        newMode.start();
        mode = newMode;
        tournament = newTournament;
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
        if (mode.isMatchFinished() && !pointsGiven) {
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
        player(side).press(action);
    }

    /**
     * An action key of a side was released.
     *
     * @param side   "LEFT" or "RIGHT"
     * @param action the released action
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public void release(String side, String action) throws DOPOSoccerException {
        player(side).release(action);
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
     * Goes to the next match of the mode: in the tournament, the next round; in survival, the next
     * match of the series. The new match gives points again.
     *
     * @throws DOPOSoccerException GAME_NOT_STARTED; INVALID_STATE in a single Arcade match (by time or by goals)
     *                             or if the current match is not over; GAME_OVER if the mode already ended
     */
    public void nextMatch() throws DOPOSoccerException {
        match();
        mode.nextMatch();
        paused = false;
        pointsGiven = false;   // the new match gives points again
        lastPoints[0] = 0;
        lastPoints[1] = 0;
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
     * Tells whether the mode ended. In a single Arcade match it is the same as the match; a survival
     * series ends when the left player loses a match; the tournament ends when the player loses a
     * match or wins the final.
     *
     * @return true if it ended
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public boolean isModeOver() throws DOPOSoccerException {
        match();
        return mode.isOver();
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
     * @return "ARCADE" or "TOURNAMENT"
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getMode() throws DOPOSoccerException {
        match();
        return mode.getName();
    }

    /**
     * Returns the current round of the mode.
     *
     * @return 1 in a single Arcade match; 1, 2, 3... in a survival series; 1 to 3 in the tournament
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public int getRound() throws DOPOSoccerException {
        match();
        return mode.getRound();
    }

    /**
     * Returns the modality being played.
     *
     * @return "PVP" or "PVM" (the tournament is always "PVM")
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getModality() throws DOPOSoccerException {
        match();
        return modality;
    }

    /**
     * Returns the name of the match type being played.
     *
     * @return "TIME", "GOALS" or "SURVIVAL"
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getMatchType() throws DOPOSoccerException {
        return match().getTypeName();
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
        return match().getGoals(side);
    }

    /**
     * Returns the seconds left of a match by time.
     *
     * @return seconds left, or -1 in a match by goals
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public int getTimeLeft() throws DOPOSoccerException {
        return match().getTimeLeft();
    }

    /**
     * Returns the goals needed to win a match by goals.
     *
     * @return the goal target, or -1 in a match by time or in survival
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public int getGoalTarget() throws DOPOSoccerException {
        return match().getGoalTarget();
    }

    /**
     * Tells whether the match is in sudden death.
     *
     * @return true in sudden death
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public boolean isSuddenDeath() throws DOPOSoccerException {
        return match().isSuddenDeath();
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
        return player(side).getCountry();
    }

    /**
     * Returns the name of the power shot of a side.
     *
     * @param side "LEFT" or "RIGHT"
     * @return name of its shot
     * @throws DOPOSoccerException GAME_NOT_STARTED or UNKNOWN_SIDE
     */
    public String getPowerName(String side) throws DOPOSoccerException {
        return player(side).getPowerName();
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
        return match().getBallX();
    }

    /**
     * Returns the top edge of the real ball.
     *
     * @return y, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getBallY() throws DOPOSoccerException {
        return match().getBallY();
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
        return match().getBallName();
    }

    /**
     * Returns the width of the field.
     *
     * @return width, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getFieldWidth() throws DOPOSoccerException {
        return match().getFieldWidth();
    }

    /**
     * Returns the height of the field.
     *
     * @return height, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getFieldHeight() throws DOPOSoccerException {
        return match().getFieldHeight();
    }

    /**
     * Returns the height of the ground.
     *
     * @return y of the ground, in pixels
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public double getGroundY() throws DOPOSoccerException {
        return match().getGroundY();
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
        return match().getGoalBounds(side);
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
     * Tells whether a player can use a character: the free ones and the ones it bought.
     *
     * @param side    "LEFT" or "RIGHT"
     * @param country country of the character
     * @return true if it is free or the player bought it
     * @throws DOPOSoccerException UNKNOWN_SIDE or UNKNOWN_CHARACTER
     */
    public boolean isCharacterUnlocked(String side, String country) throws DOPOSoccerException {
        return profiles[index(side)].isCharacterUnlocked(character(country));
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

    // survival and tournament

    /**
     * Returns the balls left of a player in survival. The left player keeps its balls for the
     * whole series; the rival starts every match with 3.
     *
     * @param side "LEFT" or "RIGHT"
     * @return the balls left, from 0 to 3
     * @throws DOPOSoccerException UNKNOWN_SIDE, GAME_NOT_STARTED, or INVALID_STATE if the match is not survival
     */
    public int getBalls(String side) throws DOPOSoccerException {
        index(side);
        return match().getBalls(side);
    }

    /**
     * Returns the tournament being played.
     *
     * @return the tournament
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    private TournamentMode tournament() throws DOPOSoccerException {
        match();
        if (tournament == null) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_STATE); // a game, but not a tournament
        }
        return tournament;
    }

    /**
     * Turns a list of characters into their countries, so the interface only gets texts.
     *
     * @param list the characters
     * @return their countries, in the same order
     */
    private static List<String> countries(List<GameCharacter> list) {
        List<String> names = new ArrayList<>();
        for (GameCharacter c : list) {
            names.add(c.getCountry());
        }
        return names;
    }

    /**
     * Returns the 8 teams of the quarterfinals, in bracket order: each 2 consecutive teams play each other.
     *
     * @return the countries of the teams
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    public List<String> getTournamentTeams() throws DOPOSoccerException {
        return countries(tournament().getTeams(1));
    }

    /**
     * Returns the 4 teams of the semifinal, in bracket order.
     *
     * @return the countries of the teams, empty if the semifinal was not reached yet
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    public List<String> getTournamentSemifinalists() throws DOPOSoccerException {
        return countries(tournament().getTeams(2));
    }

    /**
     * Returns the 2 teams of the final.
     *
     * @return the countries of the teams, empty if the final was not reached yet
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    public List<String> getTournamentFinalists() throws DOPOSoccerException {
        return countries(tournament().getTeams(3));
    }

    /**
     * Returns the name of the current round of the tournament.
     *
     * @return "CUARTOS", "SEMIFINAL" or "FINAL"
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    public String getTournamentRound() throws DOPOSoccerException {
        return tournament().getRoundName();
    }

    /**
     * Returns the names of the rounds of the tournament, in order.
     *
     * @return "CUARTOS", "SEMIFINAL" and "FINAL"
     */
    public List<String> getTournamentRounds() {
        return List.of(TournamentMode.ROUND_NAMES);
    }

    /**
     * Returns the rival of the player in the current round.
     *
     * @return the country of the rival
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    public String getTournamentRival() throws DOPOSoccerException {
        return tournament().getRival().getCountry();
    }

    /**
     * Tells whether the player won the tournament.
     *
     * @return true if the player won the final
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    public boolean isTournamentChampion() throws DOPOSoccerException {
        return tournament().isChampion();
    }

    /**
     * Returns how many matches of the tournament the player won.
     *
     * @return wins of the player, from 0 to 3
     * @throws DOPOSoccerException GAME_NOT_STARTED if there is no game; INVALID_STATE if the game is not a tournament
     */
    public int getTournamentWins() throws DOPOSoccerException {
        return tournament().getWins();
    }

    /**
     * Returns the points of a profile, to show them in the store.
     *
     * @param side "LEFT" or "RIGHT"
     * @return points of the player
     * @throws DOPOSoccerException UNKNOWN_SIDE
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
        return profiles[index(side)].getStat(stat);
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
        return profiles[index(side)].getUpgradeCost(stat);
    }

    /**
     * Buys one point of a stat with the points of the player.
     *
     * @param side "LEFT" or "RIGHT"
     * @param stat "SPEED", "JUMP", "KICK" or "SURVIVAL"
     * @throws DOPOSoccerException UNKNOWN_SIDE, UNKNOWN_STAT, STAT_AT_MAX or NOT_ENOUGH_POINTS
     */
    public void upgrade(String side, String stat) throws DOPOSoccerException {
        profiles[index(side)].upgrade(stat);
    }

    /**
     * Buys a locked character with the points of the player.
     *
     * @param side    "LEFT" or "RIGHT"
     * @param country country of the character
     * @throws DOPOSoccerException UNKNOWN_SIDE, UNKNOWN_CHARACTER, ALREADY_OWNED or NOT_ENOUGH_POINTS
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
        return match().getBallPower();
    }

    /**
     * Tells whether a player can use a ball: the free ones and the ones it bought.
     *
     * @param side "LEFT" or "RIGHT"
     * @param name name of the ball
     * @return true if it is free or the player bought it
     * @throws DOPOSoccerException UNKNOWN_SIDE or UNKNOWN_BALL
     */
    public boolean isBallUnlocked(String side, String name) throws DOPOSoccerException {
        return profiles[index(side)].isBallUnlocked(ballSkin(name));
    }

    /**
     * Buys a locked ball with the points of the player.
     *
     * @param side "LEFT" or "RIGHT"
     * @param name name of the ball
     * @throws DOPOSoccerException UNKNOWN_SIDE, UNKNOWN_BALL, ALREADY_OWNED or NOT_ENOUGH_POINTS
     */
    public void buyBall(String side, String name) throws DOPOSoccerException {
        profiles[index(side)].unlockBall(ballSkin(name));
    }

    /**
     * Chooses the ball of the next matches. In this version there is one ball for the game,
     * so the last one chosen is used. Only the free balls and the ones the player bought.
     *
     * @param side "LEFT" or "RIGHT"
     * @param name name of the ball
     * @throws DOPOSoccerException UNKNOWN_SIDE, UNKNOWN_BALL or BALL_LOCKED
     */
    public void selectBall(String side, String name) throws DOPOSoccerException {
        BallSkin skin = ballSkin(name);
        profiles[index(side)].selectBall(skin);
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
     * Returns the points the champion of a tournament wins, to show them before and during the tournament.
     *
     * @return {@link TournamentMode#PRIZE}
     */
    public int getTournamentPrize() {
        return TournamentMode.PRIZE;
    }

    /**
     * Tells whether there is saved progress.
     *
     * @return true if the progress file exists
     */
    public boolean hasSavedProgress() {
        return progressFile.exists();
    }

    /**
     * Saves the progress in the progress file, which is always the same (the player doesn't choose it):
     * the profiles of both players (points, purchases, upgrades and selected ball). The file is
     * written again every time it is called.
     *
     * @throws DOPOSoccerException SAVE_FAILED if the file cannot be written
     */
    public void saveProgress() throws DOPOSoccerException {
        // The file is its own resource, so it is closed even if the object stream cannot be created
        try (FileOutputStream file = new FileOutputStream(progressFile);
             ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(profiles);
        } catch (IOException e) {
            throw new DOPOSoccerException(DOPOSoccerException.SAVE_FAILED, e);
        }
    }

    /**
     * Loads the profiles saved in the progress file. If the file cannot be read, the current
     * profiles are kept as they were.
     *
     * @throws DOPOSoccerException LOAD_FAILED if the file cannot be read or is damaged
     */
    public void loadProgress() throws DOPOSoccerException {
        // The file is its own resource, so it is closed even if it is damaged and the object stream fails
        try (FileInputStream file = new FileInputStream(progressFile);
             ObjectInputStream in = new ObjectInputStream(file)) {
            PlayerProfile[] loadedProfiles = (PlayerProfile[]) in.readObject();
            profiles = loadedProfiles;
            ball = profiles[0].getSelectedBall();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new DOPOSoccerException(DOPOSoccerException.LOAD_FAILED, e);
        }
    }

    /**
     * Saves the whole game in a file chosen by the user: the catalogs, the mode with its match (players,
     * ball, score, time, effects, stadium), the tournament, the modality and the points of the last match.
     * Not saved (transient): the physics world of the library, rebuilt from the entities on the first step;
     * the profiles, which are in the progress file; and the settings.
     *
     * @param file file chosen by the user
     * @throws DOPOSoccerException GAME_NOT_STARTED, GAME_OVER if the mode already ended,
     *                             or GAME_SAVE_FAILED if the file cannot be written
     */
    public void saveGame(File file) throws DOPOSoccerException {
        match();
        if (mode.isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.GAME_OVER);
        }
        // The file is its own resource, so it is closed even if the object stream cannot be created
        try (FileOutputStream stream = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(stream)) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new DOPOSoccerException(DOPOSoccerException.GAME_SAVE_FAILED, e);
        }
    }

    /**
     * Opens a game saved with {@link #saveGame(File)} as a new object, paused, so the player resumes it.
     * Its profiles are loaded from its progress file (the newest ones, not the ones of the moment it was
     * saved) and its settings are the default ones. The interface replaces its game with the returned one.
     *
     * @param file file chosen by the user
     * @return the opened game
     * @throws DOPOSoccerException GAME_OPEN_FAILED if the file is not a saved game or is damaged;
     *                             LOAD_FAILED if the progress file is damaged
     */
    public static DOPOSoccer openGame(File file) throws DOPOSoccerException {
        DOPOSoccer opened;
        // The file is its own resource, so it is closed even if it is damaged and the object stream fails
        try (FileInputStream stream = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(stream)) {
            opened = (DOPOSoccer) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new DOPOSoccerException(DOPOSoccerException.GAME_OPEN_FAILED, e);
        }
        // What was not saved (transient): settings by default, profiles from the progress file
        opened.settings = new Settings();
        opened.ball = opened.balls.get(DEFAULT_BALL);
        opened.profiles = new PlayerProfile[]{new PlayerProfile("J1", opened.ball), new PlayerProfile("J2", opened.ball)};
        if (opened.hasSavedProgress()) {
            opened.loadProgress();
        }
        opened.paused = true;
        return opened;
    }

    //  helpers

    /**
     * Gives 100 points to the winner and 25 to the loser and keeps them in their profiles, to spend
     * them in the store. Against the machine only player 1 gets points. The champion of a tournament also
     * wins the prize. They last while the game is open.
     */
    private void givePoints() {
        pointsGiven = true;
        String winner = mode.getMatchWinner();
        if (winner == null) {
            return;
        }
        int w = "LEFT".equals(winner) ? 0 : 1;
        lastPoints[w] = WIN_POINTS;
        lastPoints[1 - w] = LOSE_POINTS;
        profiles[0].addPoints(lastPoints[0]);
        if ("PVP".equals(modality)) {
            profiles[1].addPoints(lastPoints[1]); // the machine has no wallet
        }
        if (tournament != null) {
            int prize = tournament.getPrize();
            lastPoints[0] += prize;
            profiles[0].addPoints(prize);
        }
    }

    /**
     * Creates the controller of the machine.
     *
     * @param profile "CLUMSY", "OFFENSIVE", "DEFENSIVE" or "EXPERT"
     * @return the controller of that profile
     * @throws DOPOSoccerException UNKNOWN_AI_PROFILE
     */
    private static PlayerController createAI(String profile) throws DOPOSoccerException {
        if ("CLUMSY".equals(profile)) {
            return new ClumsyAI();
        }
        if ("OFFENSIVE".equals(profile)) {
            return new OffensiveAI();
        }
        if ("DEFENSIVE".equals(profile)) {
            return new DefensiveAI();
        }
        if ("EXPERT".equals(profile)) {
            return new ExpertAI();
        }
        throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_AI_PROFILE);
    }

    /**
     * Creates the rule of the match.
     *
     * @param matchType "TIME", "GOALS" or "SURVIVAL"
     * @param amount    minutes or goals (not used in survival)
     * @return the rule
     * @throws DOPOSoccerException UNKNOWN_MATCH_TYPE, INVALID_MINUTES or INVALID_GOAL_TARGET
     */
    private static MatchType createType(String matchType, int amount) throws DOPOSoccerException {
        if ("TIME".equals(matchType)) {
            return new TimeMatch(amount);
        }
        if ("GOALS".equals(matchType)) {
            return new GoalsMatch(amount);
        }
        if ("SURVIVAL".equals(matchType)) {
            return new SurvivalMatch(); // 3 balls for each player
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

    /**
     * Chooses the stadium of the next Arcade matches (each tournament match draws its own).
     * Like the ball, choose it before {@link #newGame}.
     *
     * @param stadium name of the stadium ("default", "rain", "snow"...)
     * @throws DOPOSoccerException UNKNOWN_STADIUM if it is not one of {@link #getStadiums()}
     */
    public void selectStadium(String stadium) throws DOPOSoccerException {
        if (!GameMode.STADIUMS.contains(stadium)) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_STADIUM);
        }
        this.stadium = stadium;
    }

    /**
     * Returns the names of the stadiums, in order.
     *
     * @return "default", "day", "sunset"...
     */
    public List<String> getStadiums() {
        return GameMode.STADIUMS;
    }

    /**
     * Returns the stadium of the current match, so the interface draws its picture.
     * In the tournament it changes in every match.
     *
     * @return name of the stadium ("default", "rain"...)
     * @throws DOPOSoccerException GAME_NOT_STARTED
     */
    public String getStadium() throws DOPOSoccerException {
        return match().getStadiumName();
    }
}
