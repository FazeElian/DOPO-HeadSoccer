package presentation;

import domain.enums.Action;
import domain.enums.MatchEvent;
import domain.enums.MatchKind;
import domain.enums.Modality;
import domain.enums.ModeType;
import domain.enums.Resolution;
import domain.enums.Side;
import domain.enums.StatType;
import javax.swing.*;

// Home Module Views classes
import presentation.views.home.MainMenuView;
import presentation.views.home.SelectModeView;
import presentation.views.home.SettingsView;
import presentation.views.home.TutorialView;
import presentation.views.home.StoreView;

// Arcade Module Views classes
import presentation.views.arcade.SelectArcadeModeView;
import presentation.views.arcade.SelectArcadeResolutionView;
import presentation.views.arcade.SelectArcadeMatchTypeView;
import presentation.views.arcade.SelectArcadeCharacterView;
import presentation.views.arcade.SelectStadiumView;
import presentation.views.arcade.SelectArcadeBallView;

// Match Views classes
import presentation.views.match.MatchView;
import presentation.views.match.ArcadeMatchView;
import presentation.views.match.SurvivalMatchView;

// Tournament Views classes
import presentation.views.tournament.NewTournamentView;
import presentation.views.tournament.TournamentBallView;
import presentation.views.tournament.TournamentBracketView;
import presentation.views.tournament.TournamentRoundView;
import presentation.views.tournament.TournamentMatchView;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import domain.*;

public class DOPOSoccerGUI extends JFrame {
    // Game
    private DOPOSoccer game;

    // Views components
    private MainMenuView mainMenuView;
    private TutorialView tutorialView;
    private SettingsView settingsView;
    private StoreView storeView;
    private SelectModeView selectModeView;

    // Arcade Views components
    private SelectArcadeModeView selectArcadeModeView;
    private SelectArcadeResolutionView selectArcadeResolutionView;
    private SelectArcadeMatchTypeView selectArcadeMatchTypeView;
    private SelectArcadeCharacterView selectArcadeCharacterView;
    private SelectArcadeCharacterView selectArcadeRivalView;
    private SelectArcadeBallView selectArcadeBallView;
    private SelectStadiumView selectStadiumView;

    // Tournament Views components
    private NewTournamentView newTournamentView;
    private TournamentBallView tournamentBallView;
    private TournamentBracketView tournamentBracketView;
    private TournamentRoundView tournamentRoundView;
    private TournamentMatchView tournamentMatchView;

    // Match Views components
    private ArcadeMatchView arcadeMatchView;
    private SurvivalMatchView survivalMatchView;
    private MatchView matchView; // the match view being played (arcade, survival or tournament)
    private String tournamentTypeText = ""; // "POR TIEMPO · 60 S", "POR GOLES · META 3"

    // Fonts
    private Font luckiest;

    // Color Constants
    public static final Color DARK_GREEN = new Color(14, 67, 4);
    public static final Color NORMAL_GREEN = new Color(22, 228, 67);
    public static final Color MEDIUM_GREEN = new Color(9, 125, 34);
    public static final Color LIGHT_GREEN = new Color(145, 226, 163);
    public static final Color BLUE = new Color(24, 32, 186);
    public static final Color LIGHT_BLUE = new Color(24, 132, 186);
    public static final Color RED = new Color(229, 40, 40);
    public static final Color YELLOW = new Color(252, 209, 22);
    public static final Color ORANGE = new Color(229, 112, 40);
    public static final Color WHITE = Color.WHITE;
    public static final Color BLACK = new Color(25, 25, 30);

    // Player labels used by several screens
    public static final String PLAYER1_TAG = "J1";
    public static final String PLAYER2_TAG = "J2";
    public static final String MACHINE_TAG = "IA";
    public static final String PLAYER1_NAME = "JUGADOR 1";
    public static final String PLAYER2_NAME = "JUGADOR 2";

    // Balls of each player at the start of a survival series (domain)
    private int maxBalls = 0;
    // Balls of each player at the start of a survival series (to draw an opened game)
    private static final int SURVIVAL_BALLS = 3;

    // Sound player (music, sound effects, etc)
    private SoundPlayer sounds = new SoundPlayer();

    public DOPOSoccerGUI() {
        game = new DOPOSoccer();
        loadSavedProgress(); // before the volumes are given to the sound player
        // Fixed window: the inside is exactly 1280 x 900 (the proportion of the stadium image), so nothing stretches
        getContentPane().setPreferredSize(new Dimension(1280, 900));
        pack();
        setResizable(false);
        setLocationRelativeTo(null); // centered on the screen
        setTitle("DOPOSoccer | Inicio");
        prepareView();
        prepareController();
    }

    private void prepareView() {
        loadFonts();

        // Home Views
        mainMenuView = new MainMenuView(luckiest);
        tutorialView = new TutorialView(luckiest);
        settingsView = new SettingsView(luckiest);
        storeView = new StoreView(luckiest);
        selectModeView = new SelectModeView(luckiest);

        // Arcade Views
        selectArcadeModeView = new SelectArcadeModeView(luckiest);
        selectArcadeResolutionView = new SelectArcadeResolutionView(luckiest);
        selectArcadeMatchTypeView = new SelectArcadeMatchTypeView(luckiest);
        selectArcadeCharacterView = new SelectArcadeCharacterView(luckiest, false);
        selectArcadeRivalView = new SelectArcadeCharacterView(luckiest, true);
        selectStadiumView = new SelectStadiumView(luckiest);
        selectArcadeBallView = new SelectArcadeBallView(luckiest);

        // Tournament Views
        newTournamentView = new NewTournamentView(luckiest);
        tournamentBallView = new TournamentBallView(luckiest);
        tournamentBracketView = new TournamentBracketView(luckiest);
        tournamentRoundView = new TournamentRoundView(luckiest);
        tournamentMatchView = new TournamentMatchView(luckiest, game);

        // Match Views
        arcadeMatchView = new ArcadeMatchView(luckiest, game);
        survivalMatchView = new SurvivalMatchView(luckiest, game);
        matchView = arcadeMatchView;
		add(mainMenuView, BorderLayout.CENTER);
    }

    private void prepareController() {
        // Volumes saved in the domain, then the music
        sounds.setMusicVolume(game.getMusicVolume());
        sounds.setEffectsVolume(game.getEffectsVolume());
        sounds.startMusic("music");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

        // Home Views
        prepareMainMenuController();
        prepareTutorialController();
        prepareSettingsController();
        prepareStoreController();

        // Arcade Views
        prepareSelectModeController();
        prepareSelectArcadeModeController();
        prepareSelectArcadeResolutionController();
        prepareSelectArcadeMatchTypeController();
        prepareSelectArcadeCharacterController();
        prepareSelectArcadeRivalController();
        prepareSelectStadiumController();
        prepareSelectArcadeBallController();
        prepareLockedController();

        // Tournament Views
        prepareNewTournamentController();
        prepareTournamentBallController();
        prepareTournamentBracketController();
        prepareTournamentRoundController();

        // Match Views ("REVANCHA" / "OTRA SERIE" play again; "NUEVO TORNEO" goes to a new tournament)
        prepareMatchController(arcadeMatchView);
        prepareMatchController(survivalMatchView);
        prepareMatchController(tournamentMatchView);

        // Key movement
        prepareKeysController();
    }

    private void prepareMainMenuController() {
        mainMenuView.exitBtn.addActionListener(e -> exit());
        mainMenuView.tutorialBtn.addActionListener(e -> showView(tutorialView, "¿Cómo Jugar?"));
        mainMenuView.settingsBtn.addActionListener(e -> {
            settingsView.setVolumes(game.getMusicVolume(), game.getEffectsVolume());
            showView(settingsView, "Ajustes");
        });
        mainMenuView.playBtn.addActionListener(e -> showView(selectModeView, "Modo de Juego"));

        // Store: points, characters, balls and stats of both players
        mainMenuView.storeBtn.addActionListener(e -> {
            loadStore();
            showView(storeView, "Tienda");
        });

        // Open a game saved from the pause menu: it opens paused, with the pause card over the match
        mainMenuView.openGameBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = createGameChooser();
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    DOPOSoccer opened = DOPOSoccer.openGame(chooser.getSelectedFile());
                    // The volumes are not part of a saved game: the ones of the player are kept
                    opened.setMusicVolume(game.getMusicVolume());
                    opened.setEffectsVolume(game.getEffectsVolume());
                    useGame(opened);
                    showOpenedGame();
                }
            } catch (DOPOSoccerException ex) {
                Log.record(ex);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    // File chooser of the saved games: it only shows the .ds files
    private JFileChooser createGameChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Partidas de DOPOSoccer (*." + DOPOSoccer.GAME_EXTENSION + ")", DOPOSoccer.GAME_EXTENSION));
        return chooser;
    }

    // The file with the .ds extension: "partida" -> "partida.ds" ("partida.ds" stays the same)
    private File withGameExtension(File file) {
        String extension = "." + DOPOSoccer.GAME_EXTENSION;
        if (file.getName().toLowerCase().endsWith(extension)) {
            return file;
        }
        return new File(file.getParentFile(), file.getName() + extension);
    }

    // From now on the GUI and the match views work with this game (an opened game is a new object)
    private void useGame(DOPOSoccer newGame) {
        game = newGame;
        arcadeMatchView.setGame(newGame);
        survivalMatchView.setGame(newGame);
        tournamentMatchView.setGame(newGame);
    }

    // Shows the game just opened: the view of its mode and match type, with the data of the domain
    // (not of the selection screens, which the player didn't go through)
    private void showOpenedGame() throws DOPOSoccerException {
        boolean tournamentGame = ModeType.TOURNAMENT.getName().equals(game.getMode());
        boolean survival = MatchKind.SURVIVAL.getName().equals(game.getMatchType());
        boolean vsMachine = Modality.PVM.getName().equals(game.getModality());
        matchView = tournamentGame ? tournamentMatchView : survival ? survivalMatchView : arcadeMatchView;
        matchView.setBackgroundImage(SelectStadiumView.imageOf(game.getStadium())); // the stadium of the saved match

        String me = game.getCharacter(Side.LEFT.getName());
        String rival = game.getCharacter(Side.RIGHT.getName());
        matchView.setPlayer1(PLAYER1_NAME, me.toUpperCase(), game.getPowerName(Side.LEFT.getName()).toUpperCase(),
            selectArcadeCharacterView.getCodeOf(me));
        matchView.setPlayer2(vsMachine ? MACHINE_TAG : PLAYER2_TAG, vsMachine ? MACHINE_TAG : PLAYER2_NAME, rival.toUpperCase(),
            game.getPowerName(Side.RIGHT.getName()).toUpperCase(), selectArcadeCharacterView.getCodeOf(rival));
        matchView.setSuddenDeath(game.isSuddenDeath());
        if (survival) {
            maxBalls = SURVIVAL_BALLS;
        }
        if (tournamentGame) {
            int target = game.getGoalTarget();
            tournamentTypeText = target < 0 ? "POR TIEMPO" : "POR GOLES · META " + target;
        }

        String title = tournamentGame ? "Partido (Torneo · " + game.getTournamentRound() + ")"
            : survival ? "Supervivencia" : "Partido (Arcade)";
        showView(matchView, title);
        refreshMatchView();
        sounds.startMusic("match");
        // Paused: "REANUDAR" starts the game loop
        setGlassPane(matchView.pauseOverlay);
        getGlassPane().setVisible(true);
    }

    private void prepareTutorialController() {
        tutorialView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
    }

    private void prepareSettingsController() {
        settingsView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));

        // Each slider saves its volume in the domain and the sound player uses it
        settingsView.setMusicListener(e -> {
            try {
                game.setMusicVolume(settingsView.getMusicVolume());
                sounds.setMusicVolume(game.getMusicVolume());
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        settingsView.setEffectsListener(e -> {
            try {
                game.setEffectsVolume(settingsView.getEffectsVolume());
                sounds.setEffectsVolume(game.getEffectsVolume());
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        // "RESTABLECER": the domain puts back its default volumes
        settingsView.resetBtn.addActionListener(e -> {
            game.resetSettings();
            settingsView.setVolumes(game.getMusicVolume(), game.getEffectsVolume());
        });
    }

    // Opens the profiles saved in the progress file; if there is no file, it creates a new one
    // with the profiles from zero. If the file is damaged, the game starts from zero.
    private void loadSavedProgress() {
        if (game.hasSavedProgress()) {
            try {
                game.loadProgress();
            } catch (DOPOSoccerException ex) {
                Log.record(ex);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        } else {
            saveProgress();
        }
    }

    // Saves the progress in the file; if it fails, the player is told and the game goes on
    private void saveProgress() {
        try {
            game.saveProgress();
        } catch (DOPOSoccerException ex) {
            Log.record(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void prepareSelectModeController()  {
        selectModeView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
        selectModeView.arcadeBtn.addActionListener(e -> showView(selectArcadeModeView, "Modalidad (Arcade)"));

        // Tournament: "NUEVO TORNEO" with the team and the stats of player 1 (domain)
        selectModeView.tournamentBtn.addActionListener(e -> showNewTournament());
    }

    // "NUEVO TORNEO": the team of player 1, its power and stats, and the prize (domain)
    private void showNewTournament() {
        try {
            String team = toDomainCountry(selectArcadeCharacterView.getSelectedCharacter());
            int[] stats = new int[STATS.length];
            for (int i = 0; i < STATS.length; i++) {
                stats[i] = game.getStat(Side.LEFT.getName(), STATS[i]);
            }
            newTournamentView.setTeam(team, game.getPowerNameOf(team), stats);
            newTournamentView.setPrize(game.getTournamentPrize());
            showView(newTournamentView, "Nuevo Torneo");
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void prepareSelectArcadeModeController() {
        selectArcadeModeView.backBtn.addActionListener(e -> showView(selectModeView, "Modo de Juego"));
        selectArcadeModeView.continueBtn.addActionListener(e -> showView(selectArcadeResolutionView, "Resolución (Arcade)"));
    }

    private void prepareSelectArcadeResolutionController() {
        selectArcadeResolutionView.backBtn.addActionListener(e -> showView(selectArcadeModeView, "Modalidad (Arcade)"));
        selectArcadeResolutionView.continueBtn.addActionListener(e -> {
            // Survival is locked when playing player vs player
            selectArcadeMatchTypeView.setVsMachine(selectArcadeModeView.isVsMachine());
            showView(selectArcadeMatchTypeView, "Tipo de Partido (Arcade)");
        });
    }

    private void prepareSelectArcadeMatchTypeController() {
        selectArcadeMatchTypeView.backBtn.addActionListener(e -> showView(selectArcadeResolutionView, "Resolución (Arcade)"));
        selectArcadeMatchTypeView.continueBtn.addActionListener(e -> {
            // The bars show the stats of player 1 (domain)
            if (loadStats(selectArcadeCharacterView, Side.LEFT.getName())) {
                loadCharacterAvailability(selectArcadeCharacterView, Side.LEFT.getName());
                showView(selectArcadeCharacterView, "Personaje (Arcade)");
            }
        });
    }

    private void prepareSelectArcadeCharacterController() {
        selectArcadeCharacterView.backBtn.addActionListener(e -> showView(selectArcadeMatchTypeView, "Tipo de Partido (Arcade)"));
        selectArcadeCharacterView.continueBtn.addActionListener(e -> {
            // The rival view shows the character of player 1 and if the rival is the AI or player 2
            selectArcadeRivalView.setPlayerCharacter(selectArcadeCharacterView.getSelectedCharacter());
            selectArcadeRivalView.setVsMachine(selectArcadeModeView.isVsMachine());
            // The bars show the stats of the rival (domain)
            if (loadStats(selectArcadeRivalView, Side.RIGHT.getName())) {
                // The machine plays with any character; player 2 only with its own
                loadCharacterAvailability(selectArcadeRivalView,
                    selectArcadeModeView.isVsMachine() ? null : Side.RIGHT.getName());
                showView(selectArcadeRivalView, "Rival (Arcade)");
            }
        });
    }

    // Stats of a player, in the order of the bars
    private static final String[] STATS = {StatType.SPEED.getName(), StatType.JUMP.getName(), StatType.KICK.getName(), StatType.SURVIVAL.getName()};
    private static final String[] SIDES = {Side.LEFT.getName(), Side.RIGHT.getName()};

    // Asks the domain the stats of a side and gives them to a character view; false if the domain failed
    private boolean loadStats(SelectArcadeCharacterView view, String side) {
        String[] names = {StatType.SPEED.getName(), StatType.JUMP.getName(), StatType.KICK.getName(), StatType.SURVIVAL.getName()}; // same order as the bars
        int[] values = new int[names.length];
        try {
            for (int i = 0; i < names.length; i++) {
                values[i] = game.getStat(side, names[i]);
            }
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return false;
        }
        view.setPlayerStats(values);
        return true;
    }

    // Locked character or ball: the domain buys it for that player with its points
    private void prepareLockedController() {
        selectArcadeCharacterView.setLockedListener(e -> {
            buyCharacter(Side.LEFT.getName(), e.getActionCommand());
            loadCharacterAvailability(selectArcadeCharacterView, Side.LEFT.getName());
        });
        selectArcadeRivalView.setLockedListener(e -> {
            buyCharacter(Side.RIGHT.getName(), e.getActionCommand());
            loadCharacterAvailability(selectArcadeRivalView, Side.RIGHT.getName());
        });
        selectArcadeBallView.setLockedListener(e -> {
            buyBall(Side.LEFT.getName(), e.getActionCommand());
            loadBallAvailability(selectArcadeBallView);
        });
    }

    // Buys a character for a player (item = name shown on the screen, "ESPAÑA")
    private void buyCharacter(String side, String item) {
        String country = toDomainCountry(item);
        int price = 0;
        try {
            price = game.getCharacterPrice(country);
            game.buyCharacter(side, country);
            saveProgress();
        } catch (DOPOSoccerException ex) {
            showPurchaseError(ex, item, price, side);
        }
    }

    // Buys a ball for a player (item = name shown on the screen, "DORADO")
    private void buyBall(String side, String item) {
        String ball = toDomainBall(item);
        int price = 0;
        try {
            price = game.getBallPrice(ball);
            game.buyBall(side, ball);
            saveProgress();
        } catch (DOPOSoccerException ex) {
            showPurchaseError(ex, "BALÓN " + item, price, side);
        }
    }

    // Upgrades a stat of a player one level
    private void upgradeStat(String side, String stat) {
        int price = 0;
        try {
            price = game.getUpgradeCost(side, stat);
            game.upgrade(side, stat);
            saveProgress();
        } catch (DOPOSoccerException ex) {
            showPurchaseError(ex, "LA MEJORA", price, side);
        }
    }

    // Error of a purchase: in the store, without points, the "puntos insuficientes" dialog;
    // any other error (or outside the store) in a message
    private void showPurchaseError(DOPOSoccerException ex, String item, int price, String side) {
        if (DOPOSoccerException.NOT_ENOUGH_POINTS.equals(ex.getMessage()) && storeView.isShowing()) {
            storeView.showNotEnoughPoints(item, price, getPointsOrNone(side));
        } else {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Store: buy characters and balls (player 1) and upgrade the stats of both players
    private void prepareStoreController() {
        storeView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
        storeView.buyCharacterBtn.addActionListener(e -> {
            buyCharacter(Side.LEFT.getName(), storeView.getSelectedCharacter());
            loadStore();
        });
        storeView.buyBallBtn.addActionListener(e -> {
            buyBall(Side.LEFT.getName(), storeView.getSelectedBall());
            loadStore();
        });
        for (int s = 0; s < SIDES.length; s++) {
            String side = SIDES[s];
            for (JButton upgradeBtn : storeView.upgradeBtns[s]) {
                upgradeBtn.addActionListener(e -> {
                    upgradeStat(side, e.getActionCommand());
                    loadStore();
                });
            }
        }
    }

    // Asks the domain for everything the store shows
    private void loadStore() {
        storeView.setPoints(getPointsOrNone(Side.LEFT.getName()));

        // Characters of player 1
        String[] characters = storeView.getCharacterNames();
        int[] characterPrices = new int[characters.length];
        boolean[] characterUnlocked = new boolean[characters.length];
        for (int i = 0; i < characters.length; i++) {
            String country = toDomainCountry(characters[i]);
            try {
                characterPrices[i] = game.getCharacterPrice(country);
                characterUnlocked[i] = game.isCharacterUnlocked(Side.LEFT.getName(), country);
            } catch (DOPOSoccerException ex) {
                characterUnlocked[i] = false;
            }
        }
        storeView.setCharacters(characterPrices, characterUnlocked);

        // Balls of player 1
        String[] balls = storeView.getBallNames();
        int[] ballPrices = new int[balls.length];
        boolean[] ballUnlocked = new boolean[balls.length];
        for (int i = 0; i < balls.length; i++) {
            String ball = toDomainBall(balls[i]);
            try {
                ballPrices[i] = game.getBallPrice(ball);
            } catch (DOPOSoccerException ex) {
                continue;
            }
            try {
                ballUnlocked[i] = game.isBallUnlocked(Side.LEFT.getName(), ball);
            } catch (DOPOSoccerException ex) {
                ballUnlocked[i] = ballPrices[i] == 0; // if the domain fails: only the free balls
            }
        }
        storeView.setBalls(ballPrices, ballUnlocked);

        // Stats of both players
        for (int s = 0; s < SIDES.length; s++) {
            int[] levels = new int[STATS.length];
            int[] costs = new int[STATS.length];
            for (int i = 0; i < STATS.length; i++) {
                try {
                    levels[i] = game.getStat(SIDES[s], STATS[i]);
                    costs[i] = game.getUpgradeCost(SIDES[s], STATS[i]);
                } catch (DOPOSoccerException ex) {
                    costs[i] = -1; // STAT_AT_MAX: no next upgrade
                }
            }
            storeView.setPlayerStats(s, getPointsOrNone(SIDES[s]), levels, costs);
        }
    }

    // Points of a player, or -1 if the domain can't give them
    private int getPointsOrNone(String side) {
        try {
            return game.getPoints(side);
        } catch (DOPOSoccerException ex) {
            return -1;
        }
    }

    private void prepareNewTournamentController() {
        newTournamentView.backBtn.addActionListener(e -> showView(selectModeView, "Modo de Juego"));
                newTournamentView.continueBtn.addActionListener(e -> {
            loadBallAvailability(tournamentBallView);
            showView(tournamentBallView, "Balón (Torneo)");
        });
    }

    private void prepareTournamentBallController() {
        tournamentBallView.backBtn.addActionListener(e -> showView(newTournamentView, "Nuevo Torneo"));
        tournamentBallView.setLockedListener(e -> {
            buyBall(Side.LEFT.getName(), e.getActionCommand());
            loadBallAvailability(tournamentBallView);
        });
        tournamentBallView.continueBtn.addActionListener(e -> {
            try {
                String team = toDomainCountry(selectArcadeCharacterView.getSelectedCharacter());
                boolean byTime = newTournamentView.isByTime();
                game.selectBall(Side.LEFT.getName(), toDomainBall(tournamentBallView.getSelectedBall()));
                game.selectStadium(selectStadiumView.getSelectedStadium());
                game.newGame(Modality.PVM.getName(), toDomainProfile(SelectArcadeModeView.EXPERT_PROFILE),
                    toDomainResolution(selectArcadeResolutionView.getSelectedResolution()), ModeType.TOURNAMENT.getName(),
                    team, team, byTime ? MatchKind.TIME.getName() : MatchKind.GOALS.getName(),
                    byTime ? newTournamentView.getMinutes() : newTournamentView.getGoalTarget());

                tournamentTypeText = byTime
                    ? "POR TIEMPO · " + newTournamentView.getMinutes() * 60 + " S"
                    : "POR GOLES · META " + newTournamentView.getGoalTarget();
                showBracket();
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    // Bracket: back to the ball (a new tournament), or to the screen of the next round
    private void prepareTournamentBracketController() {
        tournamentBracketView.backBtn.addActionListener(e -> showView(tournamentBallView, "Balón (Torneo)"));
        tournamentBracketView.continueBtn.addActionListener(e -> showTournamentRound());
    }

    // Round: back to the bracket, or play the match of the player
    private void prepareTournamentRoundController() {
        tournamentRoundView.backBtn.addActionListener(e -> showBracket());
        tournamentRoundView.continueBtn.addActionListener(e -> startTournamentMatch());
    }

    // The bracket with the teams of each round reached so far (domain)
    private void showBracket() {
        try {
            tournamentBracketView.setTeams(
                game.getTournamentTeams(),
                game.getTournamentSemifinalists(),
                game.getTournamentFinalists(),
                game.getCharacter(Side.LEFT.getName())
            );
            tournamentBracketView.setMatchType(tournamentTypeText);
            tournamentBracketView.setPrize(game.getTournamentPrize());
            tournamentBracketView.setNextRound(game.getTournamentRound());
            showView(tournamentBracketView, "Llave del Torneo");
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // The round being played: the rounds, the player against its rival, the type and the points (domain)
    private void showTournamentRound() {
        try {
            tournamentRoundView.setRound(
                game.getTournamentRounds(),
                game.getRound() - 1,
                game.getCharacter(Side.LEFT.getName()),
                game.getTournamentRival(),
                tournamentTypeText
            );
            tournamentRoundView.setPoints(game.getPoints(Side.LEFT.getName()));
            tournamentRoundView.setPrize(game.getTournamentPrize());
            showView(tournamentRoundView, "Ronda del Torneo");
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // The match of the player in the current round, against the machine
    private void startTournamentMatch() {
        try {
            matchView = tournamentMatchView;
            matchView.setBackgroundImage(SelectStadiumView.imageOf(game.getStadium()));
            String me = game.getCharacter(Side.LEFT.getName());
            String rival = game.getCharacter(Side.RIGHT.getName());
            matchView.setPlayer1(PLAYER1_NAME, me.toUpperCase(), game.getPowerName(Side.LEFT.getName()).toUpperCase(),
                selectArcadeCharacterView.getCodeOf(me));
            matchView.setPlayer2(MACHINE_TAG, MACHINE_TAG, rival.toUpperCase(), game.getPowerName(Side.RIGHT.getName()).toUpperCase(),
                selectArcadeCharacterView.getCodeOf(rival));
            matchView.setSuddenDeath(false);

            showView(matchView, "Partido (Torneo · " + game.getTournamentRound() + ")");
            refreshMatchView();
            lastTime = System.nanoTime(); // so the first dt isn't huge
            sounds.startMusic("match");
            gameLoop.start();
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void prepareSelectArcadeRivalController() {
        selectArcadeRivalView.backBtn.addActionListener(e -> showView(selectArcadeCharacterView, "Personaje (Arcade)"));
        selectArcadeRivalView.continueBtn.addActionListener(e -> showView(selectStadiumView, "Estadio (Arcade)"));
    }

    // Stadium: back to the rival, or go on to the ball (the stadium is used when the match starts)
    private void prepareSelectStadiumController() {
        selectStadiumView.backBtn.addActionListener(e -> showView(selectArcadeRivalView, "Rival (Arcade)"));
        selectStadiumView.continueBtn.addActionListener(e -> {
            loadBallAvailability(selectArcadeBallView);
            showView(selectArcadeBallView, "Balón (Arcade)");
        });
    }

    private void prepareSelectArcadeBallController() {
        selectArcadeBallView.backBtn.addActionListener(e -> showView(selectStadiumView, "Estadio (Arcade)"));
        selectArcadeBallView.continueBtn.addActionListener(e -> startGame());
    }

    // Pause menu and result card of a match view (arcade, survival or tournament)
    private void prepareMatchController(MatchView view) {
        // Pause: stops the game loop and the domain, and shows the pause card over the match (window glass pane)
        view.pauseBtn.addActionListener(e -> {
            try {
                game.pause();
                gameLoop.stop();
                setGlassPane(view.pauseOverlay);
                getGlassPane().setVisible(true);
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        view.resumeBtn.addActionListener(e -> {
            try {
                game.resume();
                getGlassPane().setVisible(false);
                lastTime = System.nanoTime(); // the paused time doesn't count
                gameLoop.start();
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        // Save the game being played (it is paused) in a file chosen by the player; then the pause card stays
        view.saveGameBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = createGameChooser();
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    game.saveGame(withGameExtension(chooser.getSelectedFile()));
                    JOptionPane.showMessageDialog(this, "Partida guardada.");
                }
            } catch (DOPOSoccerException ex) {
                Log.record(ex);
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        view.endGameBtn.addActionListener(e -> {
            try {
                game.endGame();
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            gameLoop.stop();
            getGlassPane().setVisible(false);
            sounds.startMusic("music");
            showView(mainMenuView, "Inicio");
        });

        // Result card: "REVANCHA" / "OTRA SERIE" / "NUEVO TORNEO", and "MENÚ" goes to the menu
        view.againBtn.addActionListener(e -> {
            getGlassPane().setVisible(false);
            if (view == tournamentMatchView) {
                sounds.startMusic("music");
                showNewTournament();
            } else {
                startGame();
            }
        });

        view.menuBtn.addActionListener(e -> {
            getGlassPane().setVisible(false);
            sounds.startMusic("music");
            showView(mainMenuView, "Inicio");
        });
    }

    private void showView(JPanel view, String title) {
		getContentPane().removeAll();
        setTitle("DOPOSoccer | " + title);
		add(view, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

    // Starts a game in the domain with the choices of every screen (also for "REVANCHA" / "OTRA SERIE")
    private void startGame() {
        newGame(
            toDomainProfile(selectArcadeModeView.getAIProfile()),
            toDomainResolution(selectArcadeResolutionView.getSelectedResolution()),
            toDomainCountry(selectArcadeCharacterView.getSelectedCharacter()),
            toDomainCountry(selectArcadeRivalView.getSelectedCharacter())
        );
    }

    private void newGame(
        String AIProfile,
        String resolution,
        String j1Country,
        String j2Country
    ) {
        try {
            String modality = selectArcadeModeView.isVsMachine() ? Modality.PVM.getName() : Modality.PVP.getName();
            String matchType = selectArcadeMatchTypeView.isByTime()
                ? MatchKind.TIME.getName()
                : selectArcadeMatchTypeView.isByGoals() ? MatchKind.GOALS.getName() : MatchKind.SURVIVAL.getName();
            int amount = selectArcadeMatchTypeView.isByTime()
                ? selectArcadeMatchTypeView.getMinutes()
                : selectArcadeMatchTypeView.getGoalTarget();

            // The ball and the stadium chosen (before newGame: the match is created with them)
            game.selectBall(Side.LEFT.getName(), toDomainBall(selectArcadeBallView.getSelectedBall()));
            game.selectStadium(selectStadiumView.getSelectedStadium());
            game.newGame(modality, AIProfile, resolution, ModeType.ARCADE.getName(), j1Country, j2Country, matchType, amount);

            // Only if the domain accepted it: the view of the match type, with the players data of the domain
            boolean survival = MatchKind.SURVIVAL.getName().equals(game.getMatchType());
            matchView = survival ? survivalMatchView : arcadeMatchView;
            // The stadium of the match (domain) is the background
            matchView.setBackgroundImage(SelectStadiumView.imageOf(game.getStadium()));
            boolean vsMachine = Modality.PVM.getName().equals(game.getModality());
            matchView.setPlayer1(
                PLAYER1_NAME,
                game.getCharacter(Side.LEFT.getName()).toUpperCase(),
                game.getPowerName(Side.LEFT.getName()).toUpperCase(),
                selectArcadeCharacterView.getSelectedCode()
            );
            matchView.setPlayer2(
                vsMachine ? MACHINE_TAG : PLAYER2_TAG,
                vsMachine ? MACHINE_TAG : PLAYER2_NAME,
                game.getCharacter(Side.RIGHT.getName()).toUpperCase(),
                game.getPowerName(Side.RIGHT.getName()).toUpperCase(),
                selectArcadeRivalView.getSelectedCode()
            );
            matchView.setSuddenDeath(false);
            if (survival) {
                maxBalls = game.getBalls(Side.LEFT.getName()); // the balls at the start of the series
            }

            showView(matchView, survival ? "Supervivencia" : "Partido (Arcade)");
            refreshMatchView();  // 0 - 0, full time, full life... before the first tick
            lastTime = System.nanoTime(); // so the first dt isn't huge
            sounds.startMusic("match");
            gameLoop.start();
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Price and lock of each character for a side, asked to the domain
    private void loadCharacterAvailability(SelectArcadeCharacterView view, String side) {
        String[] names = view.getCharacterNames();
        int[] prices = new int[names.length];
        boolean[] unlocked = new boolean[names.length];
        for (int i = 0; i < names.length; i++) {
            String country = toDomainCountry(names[i]);
            try {
                prices[i] = game.getCharacterPrice(country);
                unlocked[i] = side == null || game.isCharacterUnlocked(side, country); // null: the machine
            } catch (DOPOSoccerException ex) {
                unlocked[i] = false; // a character the domain doesn't know can't be chosen
            }
        }
        view.setAvailability(prices, unlocked);
    }

    // Price and lock of each ball for player 1, asked to the domain
    private void loadBallAvailability(SelectArcadeBallView view) {
        String[] names = view.getNames();
        int[] prices = new int[names.length];
        boolean[] unlocked = new boolean[names.length];
        for (int i = 0; i < names.length; i++) {
            String ball = toDomainBall(names[i]);
            try {
                prices[i] = game.getBallPrice(ball);
            } catch (DOPOSoccerException ex) {
                unlocked[i] = false; // a ball the domain doesn't know can't be chosen
                continue;
            }
            try {
                unlocked[i] = game.isBallUnlocked(Side.LEFT.getName(), ball);
            } catch (DOPOSoccerException ex) {
                unlocked[i] = prices[i] == 0; // if the domain fails: only the free balls
            }
        }
        view.setAvailability(prices, unlocked);
    }
    // "COLOMBIA" (GUI) -> "Colombia" (domain). If it doesn't exist (e.g. "PERÚ"), the domain says it
    private String toDomainCountry(String guiName) {
        for (String country : game.getCharacters()) {
            if (country.equalsIgnoreCase(guiName)) return country;
        }
        return guiName;
    }

    // "CLÁSICO" (GUI) -> "Clásico" (domain). If it doesn't exist, the domain says it
    private String toDomainBall(String guiName) {
        for (String ball : game.getBallNames()) {
            if (ball.equalsIgnoreCase(guiName)) return ball;
        }
        return guiName;
    }

    // "TORPE" -> "CLUMSY"... (same order in the GUI and in the domain)
    private String toDomainProfile(String guiProfile) {
        int index = java.util.Arrays.asList(SelectArcadeModeView.AI_PROFILES).indexOf(guiProfile);
        return game.getAIProfiles().get(index);
    }

    // 960x540 -> "SMALL", 1280x720 -> "MEDIUM", 1600x900 -> "LARGE"
    private String toDomainResolution(Dimension resolution) {
        if (resolution.equals(SelectArcadeResolutionView.SMALL)) return Resolution.SMALL.getName();
        if (resolution.equals(SelectArcadeResolutionView.MEDIUM)) return Resolution.MEDIUM.getName();
        return Resolution.LARGE.getName();
    }

    // Game loop: every 16 ms (~60 times per second) the domain advances and the match view is refreshed
    private long lastTime;
    private Timer gameLoop = new Timer(16, e -> {
        long now = System.nanoTime();
        game.update((now - lastTime) / 1_000_000_000.0);
        try {
            for (String event : game.pollEvents()) {
                if (MatchEvent.KICK.getName().equals(event)) sounds.play("kick");
                else if (MatchEvent.POST.getName().equals(event)) sounds.play("post");
                else if (MatchEvent.GOAL.getName().equals(event)) sounds.play("goal");
            }
        } catch (DOPOSoccerException ex) {
            // no match: no sounds
        }
        lastTime = now;
        refreshMatchView();
    });

    private void refreshMatchView() {
        try {
            // Players HUD (both match types)
            matchView.setPlayer1Hp(game.getLife(Side.LEFT.getName()) / (double) game.getMaxLife(Side.LEFT.getName()));
            matchView.setPlayer2Hp(game.getLife(Side.RIGHT.getName()) / (double) game.getMaxLife(Side.RIGHT.getName()));
            matchView.setPlayer1Power(game.getPowerCharge(Side.LEFT.getName()));
            matchView.setPlayer2Power(game.getPowerCharge(Side.RIGHT.getName()));
            matchView.setAura(game.hasAura(Side.LEFT.getName()), game.hasAura(Side.RIGHT.getName()));

            if (matchView == survivalMatchView) {
                refreshSurvival();
            } else if (matchView == tournamentMatchView) {
                refreshTournament();
            } else {
                refreshArcade();
            }
            matchView.repaint(); // players and ball in their new places
        } catch (DOPOSoccerException ex) {
            gameLoop.stop();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Arcade: score, time (or sudden death) and, at the end, the result card
    private void refreshArcade() throws DOPOSoccerException {
        arcadeMatchView.setScore(game.getGoals(Side.LEFT.getName()), game.getGoals(Side.RIGHT.getName()));

        boolean suddenDeath = game.isSuddenDeath();
        arcadeMatchView.setSuddenDeath(suddenDeath);
        int timeLeft = game.getTimeLeft(); // -1 in a goals match
        if (suddenDeath) {
            arcadeMatchView.setTimeText("MUERTE SÚBITA");
        } else if (timeLeft >= 0) {
            arcadeMatchView.setTime(timeLeft);
        } else {
            arcadeMatchView.setTimeText("A " + game.getGoalTarget() + " GOLES");
        }

        if (game.isMatchOver()) {
            gameLoop.stop();
            saveProgress(); // the points of the match
            String winner = game.getWinner(); // "LEFT", "RIGHT" or null
            boolean vsMachine = Modality.PVM.getName().equals(game.getModality());
            String title;
            Color color;
            if (winner == null) {
                title = "PARTIDO TERMINADO";
                color = WHITE;
            } else if (Side.LEFT.getName().equals(winner)) {
                title = vsMachine ? "¡VICTORIA!" : "¡GANA J1!";
                color = YELLOW;
            } else {
                title = vsMachine ? "DERROTA" : "¡GANA J2!";
                color = vsMachine ? RED : YELLOW;
            }
            // Against the AI the points of player 1; between 2 players the points of the winner
            String pointsSide = vsMachine || winner == null ? Side.LEFT.getName() : winner;
            arcadeMatchView.showResult(
                title, color,
                game.getGoals(Side.LEFT.getName()), game.getGoals(Side.RIGHT.getName()),
                winner == null ? null : game.getCharacter(winner),
                toMatchTypeText(game.getMatchType()),
                game.getPointsEarned(pointsSide)
            );
            showResultCard();
        }
    }

    // Tournament: score and time like Arcade. When the match ends: the result card if the tournament
    // is over (champion or eliminated), or the next round in the bracket if the player won
    private void refreshTournament() throws DOPOSoccerException {
        tournamentMatchView.setScore(game.getGoals(Side.LEFT.getName()), game.getGoals(Side.RIGHT.getName()));

        boolean suddenDeath = game.isSuddenDeath();
        tournamentMatchView.setSuddenDeath(suddenDeath);
        int timeLeft = game.getTimeLeft(); // -1 in a goals match
        if (suddenDeath) {
            tournamentMatchView.setTimeText("MUERTE SÚBITA");
        } else if (timeLeft >= 0) {
            tournamentMatchView.setTime(timeLeft);
        } else {
            tournamentMatchView.setTimeText("A " + game.getGoalTarget() + " GOLES");
        }

        if (!game.isMatchOver()) {
            return;
        }
        gameLoop.stop();
        saveProgress(); // the points of the match (and the prize, if it was the final)
        String me = game.getCharacter(Side.LEFT.getName());
        String rival = game.getCharacter(Side.RIGHT.getName());
        int goals = game.getGoals(Side.LEFT.getName());
        int rivalGoals = game.getGoals(Side.RIGHT.getName());

        if (game.isModeOver()) {
            if (game.isTournamentChampion()) {
                tournamentMatchView.showChampion(me, rival, goals, rivalGoals, game.getPointsEarned(Side.LEFT.getName()));
            } else {
                tournamentMatchView.showEliminated(me, rival, goals, rivalGoals,
                    game.getTournamentRound(), game.getTournamentWins());
            }
            showResultCard();
        } else {
            // Won the round: the domain simulates the other matches and draws the next round
            game.nextMatch();
            sounds.startMusic("music");
            showBracket();
        }
    }

    // Survival: balls left and match number; a finished match goes to the next one, until the series ends
    private void refreshSurvival() throws DOPOSoccerException {
        survivalMatchView.setRound(game.getRound());
        survivalMatchView.setBalls(game.getBalls(Side.LEFT.getName()), game.getBalls(Side.RIGHT.getName()), maxBalls,
            survivalMatchView.getBallImage(game.getBallName()));

        if (game.isModeOver()) {
            gameLoop.stop();
            saveProgress(); // the points of the last match of the series
            int round = game.getRound();
            int wins = Side.LEFT.getName().equals(game.getWinner()) ? round : round - 1; // the last match was lost (or won)
            survivalMatchView.showSeriesResult(round, wins, game.getCharacter(Side.LEFT.getName()), game.getPointsEarned(Side.LEFT.getName()));
            showResultCard();
        } else if (game.isMatchOver()) {
            saveProgress(); // the points of this match, before the next one starts
            game.nextMatch();
        }
    }

    // The result card of the match view over the match (window glass pane)
    private void showResultCard() {
        setGlassPane(matchView.resultOverlay);
        getGlassPane().setVisible(true);
    }

    // "TIME" (domain) -> "POR TIEMPO", "GOALS" -> "POR GOLES"
    private String toMatchTypeText(String matchType) {
        if (MatchKind.TIME.getName().equals(matchType)) return "POR TIEMPO";
        if (MatchKind.GOALS.getName().equals(matchType)) return "POR GOLES";
        return "SUPERVIVENCIA";
    }

    // Keys of the match: they only tell the domain which action is pressed or released
    private void prepareKeysController() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            boolean inMatch = getContentPane().getComponentCount() > 0
                && getContentPane().getComponent(0) == matchView;
            if (!inMatch) return false; // in the menus the keys work as always

            // Esc: pause, or resume if it's already paused (same as clicking the buttons)
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (getGlassPane() == matchView.pauseOverlay && getGlassPane().isVisible()) {
                        matchView.resumeBtn.doClick();
                    } else if (gameLoop.isRunning()) {
                        matchView.pauseBtn.doClick();
                    }
                }
                return true;
            }

            String[] key = toAction(e);  // {side, action}, or null if it isn't a game key
            if (key == null) return false;
            try {
                // Presses only while playing; releases always (a key released during the pause
                // would stay pressed in the domain and the player would keep running after resuming)
                if (e.getID() == KeyEvent.KEY_PRESSED && gameLoop.isRunning()) {
                    game.press(key[0], key[1]);
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    game.release(key[0], key[1]);
                }
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            return true;  // the key was used by the game
        });
    }

    // Key -> {side, action}, like the tutorial:
    // J1: A left, D right, W jump, S kick, Q power / J2: arrows (left, right, up jump, down kick), right Ctrl power
    private String[] toAction(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:     return new String[]{Side.LEFT.getName(), Action.MOVE_LEFT.getName()};
            case KeyEvent.VK_D:     return new String[]{Side.LEFT.getName(), Action.MOVE_RIGHT.getName()};
            case KeyEvent.VK_W:     return new String[]{Side.LEFT.getName(), Action.JUMP.getName()};
            case KeyEvent.VK_S:     return new String[]{Side.LEFT.getName(), Action.KICK.getName()};
            case KeyEvent.VK_Q:     return new String[]{Side.LEFT.getName(), Action.POWER.getName()};
            case KeyEvent.VK_LEFT:  return new String[]{Side.RIGHT.getName(), Action.MOVE_LEFT.getName()};
            case KeyEvent.VK_RIGHT: return new String[]{Side.RIGHT.getName(), Action.MOVE_RIGHT.getName()};
            case KeyEvent.VK_UP:    return new String[]{Side.RIGHT.getName(), Action.JUMP.getName()};
            case KeyEvent.VK_DOWN:  return new String[]{Side.RIGHT.getName(), Action.KICK.getName()};
            case KeyEvent.VK_CONTROL:
                if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) return new String[]{Side.RIGHT.getName(), Action.POWER.getName()};
        }
        return null;
    }

    private void exit() {
		int option = JOptionPane.showConfirmDialog(
			this,
			"¿Estás seguro de que deseas salir?",
			"Confirmar",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE
		);
		if (option == JOptionPane.YES_OPTION) {
			dispose();
			System.exit(0);
		}
	}

    private void loadFonts() {
        try {
            luckiest = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("assets/font/LuckiestGuy-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(luckiest);
        } catch (Exception e) {
            Log.record(e);
            luckiest = new Font(Font.SANS_SERIF, Font.BOLD, 12); // respaldo
        }
    }

    public static void main(String[] args) {
        // Unexpected errors: to DOPOSoccer.log, and a short message to the user
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            Log.record(e);
            JOptionPane.showMessageDialog(null,
                "Ocurrió un error inesperado. Se guardó en DOPOSoccer.log.",
                "Error", JOptionPane.ERROR_MESSAGE);
        });
        DOPOSoccerGUI ui = new DOPOSoccerGUI();
        ui.setVisible(true);
    }
}
