package presentation;

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

    // Tournament Views components
    private NewTournamentView newTournamentView;
    private TournamentBallView tournamentBallView;
    private TournamentBracketView tournamentBracketView;
    private TournamentRoundView tournamentRoundView;
    private TournamentMatchView tournamentMatchView;

    // Match Views components
    private ArcadeMatchView arcadeMatchView;
    private SurvivalMatchView survivalMatchView;
    private MatchView matchView; // the match view being played (arcade or survival)

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

    // Balls of each player at the start of a survival series (domain)
    private int maxBalls = 0;

    // Sound player (music, sound effects, etc)
    private SoundPlayer sounds = new SoundPlayer();

    public DOPOSoccerGUI() {
        game = new DOPOSoccer();
        setSize(1280, 900); // Small Laptop size
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
        prepareSelectArcadeBallController();
        prepareLockedController();

        // Tournament Views
        prepareNewTournamentController();
        prepareTournamentBallController();
        prepareTournamentBracketController();

        // Match Views
        prepareMatchController(arcadeMatchView);
        prepareMatchController(survivalMatchView);

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

        // Store: the points of player 1 (the store is under construction in the domain)
        mainMenuView.storeBtn.addActionListener(e -> {
            loadStore();
            showView(storeView, "Tienda");
        });

        // Open a saved game (under construction in the domain)
        mainMenuView.openGameBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    game.openGame(chooser.getSelectedFile());
                }
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
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

    private void prepareSelectModeController()  {
        selectModeView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
        selectModeView.arcadeBtn.addActionListener(e -> showView(selectArcadeModeView, "Modalidad (Arcade)"));

        // Tournament: the teams of the bracket (under construction in the domain)
        // Tournament: "NUEVO TORNEO" with the team and the stats of player 1 (domain)
        selectModeView.tournamentBtn.addActionListener(e -> {
            try {
                String team = toDomainCountry(selectArcadeCharacterView.getSelectedCharacter());
                int[] stats = new int[STATS.length];
                for (int i = 0; i < STATS.length; i++) {
                    stats[i] = game.getStat("LEFT", STATS[i]);
                }
                newTournamentView.setTeam(team, game.getPowerNameOf(team), stats);
                newTournamentView.setPrize(game.getTournamentPrize());
                showView(newTournamentView, "Nuevo Torneo");
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
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
            if (loadStats(selectArcadeCharacterView, "LEFT")) {
                loadCharacterAvailability(selectArcadeCharacterView, "LEFT");
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
            if (loadStats(selectArcadeRivalView, "RIGHT")) {
                loadCharacterAvailability(selectArcadeRivalView, "RIGHT");
                showView(selectArcadeRivalView, "Rival (Arcade)");
            }
        });
    }

    // Stats of a player, in the order of the bars
    private static final String[] STATS = {"SPEED", "JUMP", "KICK", "SURVIVAL"};
    private static final String[] SIDES = {"LEFT", "RIGHT"};

    // Asks the domain the stats of a side and gives them to a character view; false if the domain failed
    private boolean loadStats(SelectArcadeCharacterView view, String side) {
        String[] names = {"SPEED", "JUMP", "KICK", "SURVIVAL"}; // same order as the bars
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

    // Locked character or ball: the domain buys it for that player (the store is under construction)
    private void prepareLockedController() {
        selectArcadeCharacterView.setLockedListener(e -> buy(() -> game.buyCharacter("LEFT", toDomainCountry(e.getActionCommand()))));
        selectArcadeRivalView.setLockedListener(e -> buy(() -> game.buyCharacter("RIGHT", toDomainCountry(e.getActionCommand()))));
        selectArcadeBallView.setLockedListener(e -> buy(() -> game.buyBall("LEFT", toDomainBall(e.getActionCommand()))));
    }

    // A purchase in the domain; its error (e.g. "Opción en construcción.") in a message
    private void buy(Purchase purchase) {
        try {
            purchase.run();
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Store: buy characters and balls (player 1) and upgrade the stats of both players
    private void prepareStoreController() {
        storeView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
        storeView.buyCharacterBtn.addActionListener(e -> {
            buy(() -> game.buyCharacter("LEFT", toDomainCountry(storeView.getSelectedCharacter())));
            loadStore();
        });
        storeView.buyBallBtn.addActionListener(e -> {
            buy(() -> game.buyBall("LEFT", toDomainBall(storeView.getSelectedBall())));
            loadStore();
        });
        for (int s = 0; s < SIDES.length; s++) {
            String side = SIDES[s];
            for (JButton upgradeBtn : storeView.upgradeBtns[s]) {
                upgradeBtn.addActionListener(e -> {
                    buy(() -> game.upgrade(side, e.getActionCommand()));
                    loadStore();
                });
            }
        }
    }

    // Asks the domain for everything the store shows
    private void loadStore() {
        storeView.setPoints(getPointsOrNone("LEFT"));

        // Characters of player 1
        String[] characters = storeView.getCharacterNames();
        int[] characterPrices = new int[characters.length];
        boolean[] characterUnlocked = new boolean[characters.length];
        for (int i = 0; i < characters.length; i++) {
            String country = toDomainCountry(characters[i]);
            try {
                characterPrices[i] = game.getCharacterPrice(country);
                characterUnlocked[i] = game.isCharacterUnlocked("LEFT", country);
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
                ballUnlocked[i] = game.isBallUnlocked("LEFT", ball);
            } catch (DOPOSoccerException ex) {
                ballUnlocked[i] = ballPrices[i] == 0; // store under construction: only the free balls
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

    // Points of a player, or -1 while the wallet is under construction
    private int getPointsOrNone(String side) {
        try {
            return game.getPoints(side);
        } catch (DOPOSoccerException ex) {
            return -1;
        }
    }

    // An action of the domain that can fail
    private interface Purchase {
        void run() throws DOPOSoccerException;
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
        tournamentBallView.setLockedListener(e -> buy(() -> game.buyBall("LEFT", toDomainBall(e.getActionCommand()))));
        tournamentBallView.continueBtn.addActionListener(e -> {
            try {
                String team = toDomainCountry(selectArcadeCharacterView.getSelectedCharacter());
                boolean byTime = newTournamentView.isByTime();
                game.selectBall("LEFT", toDomainBall(tournamentBallView.getSelectedBall()));
                game.newGame("PVM", toDomainProfile(selectArcadeModeView.getAIProfile()),
                    toDomainResolution(selectArcadeResolutionView.getSelectedResolution()), "TOURNAMENT",
                    team, team, byTime ? "TIME" : "GOALS",
                    byTime ? newTournamentView.getMinutes() : newTournamentView.getGoalTarget());

                tournamentBracketView.setTeams(game.getTournamentTeams(), java.util.List.of(), java.util.List.of(), team);
                tournamentBracketView.setMatchType(byTime
                    ? "POR TIEMPO · " + newTournamentView.getMinutes() * 60 + " S"
                    : "POR GOLES · META " + newTournamentView.getGoalTarget());
                tournamentBracketView.setPrize(game.getTournamentPrize());
                tournamentBracketView.setNextRound(game.getTournamentRound());
                showView(tournamentBracketView, "Llave del Torneo");
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage()); // "Opción en construcción."
            }
        });
    }

    private void prepareTournamentBracketController() {
        tournamentBracketView.backBtn.addActionListener(e -> showView(tournamentBallView, "Balón (Torneo)"));
    }
    
    private void prepareSelectArcadeRivalController() {
        selectArcadeRivalView.backBtn.addActionListener(e -> showView(selectArcadeCharacterView, "Personaje (Arcade)"));
        selectArcadeRivalView.continueBtn.addActionListener(e -> {
            loadBallAvailability(selectArcadeBallView);
            showView(selectArcadeBallView, "Balón (Arcade)");
        });
    }

    private void prepareSelectArcadeBallController() {
        selectArcadeBallView.backBtn.addActionListener(e -> showView(selectArcadeRivalView, "Rival (Arcade)"));
        selectArcadeBallView.continueBtn.addActionListener(e -> startGame());
    }

    // Pause menu and result card of a match view (arcade or survival)
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

        // Under construction in the domain: it shows "Opción en construcción."
        view.saveGameBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    game.saveGame(chooser.getSelectedFile());
                }
            } catch (DOPOSoccerException ex) {
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

        // Result card: "REVANCHA" / "OTRA SERIE" plays again with the same choices, "MENÚ" goes to the menu
        view.againBtn.addActionListener(e -> {
            getGlassPane().setVisible(false);
            startGame();
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
            String modality = selectArcadeModeView.isVsMachine() ? "PVM" : "PVP";
            String matchType = selectArcadeMatchTypeView.isByTime()
                ? "TIME"
                : selectArcadeMatchTypeView.isByGoals() ? "GOALS" : "SURVIVAL";
            int amount = selectArcadeMatchTypeView.isByTime()
                ? selectArcadeMatchTypeView.getMinutes()
                : selectArcadeMatchTypeView.getGoalTarget();

            // The ball chosen in "Elige tu balón" (before newGame: the match is created with it)
            game.selectBall("LEFT", toDomainBall(selectArcadeBallView.getSelectedBall()));
            game.newGame(modality, AIProfile, resolution, "ARCADE", j1Country, j2Country, matchType, amount);

            // Only if the domain accepted it: the view of the match type, with the players data of the domain
            boolean survival = "SURVIVAL".equals(game.getMatchType());
            matchView = survival ? survivalMatchView : arcadeMatchView;
            boolean vsMachine = "PVM".equals(game.getModality());
            matchView.setPlayer1(
                "JUGADOR 1",
                game.getCharacter("LEFT").toUpperCase(),
                game.getPowerName("LEFT").toUpperCase(),
                selectArcadeCharacterView.getSelectedCode()
            );
            matchView.setPlayer2(
                vsMachine ? "IA" : "J2",
                vsMachine ? "IA" : "JUGADOR 2",
                game.getCharacter("RIGHT").toUpperCase(),
                game.getPowerName("RIGHT").toUpperCase(),
                selectArcadeRivalView.getSelectedCode()
            );
            matchView.setSuddenDeath(false);
            if (survival) {
                maxBalls = game.getBalls("LEFT"); // the balls at the start of the series
            }

            showView(matchView, survival ? "Supervivencia" : "Partido (Arcade)");
            refreshMatchView();  // 0 - 0, full time, full life... before the first tick
            lastTime = System.nanoTime(); // so the first dt isn't huge
            sounds.startMusic("match");
            gameLoop.start();
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage()); // e.g. "Opción en construcción."
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
                unlocked[i] = game.isCharacterUnlocked(side, country);
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
                unlocked[i] = game.isBallUnlocked("LEFT", ball);
            } catch (DOPOSoccerException ex) {
                unlocked[i] = prices[i] == 0; // store under construction: only the free balls
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
        if (resolution.equals(SelectArcadeResolutionView.SMALL)) return "SMALL";
        if (resolution.equals(SelectArcadeResolutionView.MEDIUM)) return "MEDIUM";
        return "LARGE";
    }

    // Game loop: every 16 ms (~60 times per second) the domain advances and the match view is refreshed
    private long lastTime;
    private Timer gameLoop = new Timer(16, e -> {
        long now = System.nanoTime();
        game.update((now - lastTime) / 1_000_000_000.0);
        try {
            for (String event : game.pollEvents()) {
                switch (event) {
                    case "KICK": sounds.play("kick"); break;
                    case "POST": sounds.play("post"); break;
                    case "GOAL": sounds.play("goal"); break;
                }
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
            matchView.setPlayer1Hp(game.getLife("LEFT") / (double) game.getMaxLife("LEFT"));
            matchView.setPlayer2Hp(game.getLife("RIGHT") / (double) game.getMaxLife("RIGHT"));
            matchView.setPlayer1Power(game.getPowerCharge("LEFT"));
            matchView.setPlayer2Power(game.getPowerCharge("RIGHT"));
            matchView.setAura(game.hasAura("LEFT"), game.hasAura("RIGHT"));

            if (matchView == survivalMatchView) {
                refreshSurvival();
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
        arcadeMatchView.setScore(game.getGoals("LEFT"), game.getGoals("RIGHT"));

        boolean suddenDeath = game.isSuddenDeath();
        arcadeMatchView.setSuddenDeath(suddenDeath);
        int timeLeft = game.getTimeLeft(); // -1 in a goals match
        if (suddenDeath) {
            arcadeMatchView.setTimeText("MUERTE SÚBITA");
        } else if (timeLeft >= 0) {
            arcadeMatchView.setTime(timeLeft);
        } else {
            arcadeMatchView.setTimeText("A " + selectArcadeMatchTypeView.getGoalTarget() + " GOLES");
        }

        if (game.isMatchOver()) {
            gameLoop.stop();
            String winner = game.getWinner(); // "LEFT", "RIGHT" or null
            boolean vsMachine = "PVM".equals(game.getModality());
            String title;
            Color color;
            if (winner == null) {
                title = "PARTIDO TERMINADO";
                color = WHITE;
            } else if ("LEFT".equals(winner)) {
                title = vsMachine ? "¡VICTORIA!" : "¡GANA J1!";
                color = YELLOW;
            } else {
                title = vsMachine ? "DERROTA" : "¡GANA J2!";
                color = vsMachine ? RED : YELLOW;
            }
            // Against the AI the points of player 1; between 2 players the points of the winner
            String pointsSide = vsMachine || winner == null ? "LEFT" : winner;
            arcadeMatchView.showResult(
                title, color,
                game.getGoals("LEFT"), game.getGoals("RIGHT"),
                winner == null ? null : game.getCharacter(winner),
                toMatchTypeText(game.getMatchType()),
                game.getPointsEarned(pointsSide)
            );
            showResultCard();
        }
    }

    // Survival: balls left and match number; a finished match goes to the next one, until the series ends
    private void refreshSurvival() throws DOPOSoccerException {
        survivalMatchView.setRound(game.getRound());
        survivalMatchView.setBalls(game.getBalls("LEFT"), game.getBalls("RIGHT"), maxBalls,
            survivalMatchView.getBallImage(game.getBallName()));

        if (game.isModeOver()) {
            gameLoop.stop();
            int round = game.getRound();
            int wins = "LEFT".equals(game.getWinner()) ? round : round - 1; // the last match was lost (or won)
            survivalMatchView.showSeriesResult(round, wins, game.getCharacter("LEFT"), game.getPointsEarned("LEFT"));
            showResultCard();
        } else if (game.isMatchOver()) {
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
        switch (matchType) {
            case "TIME": return "POR TIEMPO";
            case "GOALS": return "POR GOLES";
            default: return "SUPERVIVENCIA";
        }
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
            case KeyEvent.VK_A:     return new String[]{"LEFT", "MOVE_LEFT"};
            case KeyEvent.VK_D:     return new String[]{"LEFT", "MOVE_RIGHT"};
            case KeyEvent.VK_W:     return new String[]{"LEFT", "JUMP"};
            case KeyEvent.VK_S:     return new String[]{"LEFT", "KICK"};
            case KeyEvent.VK_Q:     return new String[]{"LEFT", "POWER"};
            case KeyEvent.VK_LEFT:  return new String[]{"RIGHT", "MOVE_LEFT"};
            case KeyEvent.VK_RIGHT: return new String[]{"RIGHT", "MOVE_RIGHT"};
            case KeyEvent.VK_UP:    return new String[]{"RIGHT", "JUMP"};
            case KeyEvent.VK_DOWN:  return new String[]{"RIGHT", "KICK"};
            case KeyEvent.VK_CONTROL:
                if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) return new String[]{"RIGHT", "POWER"};
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
