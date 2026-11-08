package presentation;

import javax.swing.*;

// Home Module Views classes
import presentation.views.home.MainMenuView;
import presentation.views.home.SelectModeView;
import presentation.views.home.SettingsView;
import presentation.views.home.TutorialView;

// Arcade Module Views classes
import presentation.views.arcade.SelectArcadeModeView;
import presentation.views.arcade.SelectArcadeResolutionView;
import presentation.views.arcade.SelectArcadeMatchTypeView;
import presentation.views.arcade.SelectArcadeCharacterView;
import presentation.views.arcade.SelectArcadeBallView;

// Match Views classes
import presentation.views.match.ArcadeMatchView;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import domain.*;

public class DOPOSoccerGUI extends JFrame {
    // Game
    DOPOSoccer game;

    // Views components
    MainMenuView mainMenuView;
    TutorialView tutorialView;
    SettingsView settingsView;
    SelectModeView selectModeView;

    // Arcade Views components
    SelectArcadeModeView selectArcadeModeView;
    SelectArcadeResolutionView selectArcadeResolutionView;
    SelectArcadeMatchTypeView selectArcadeMatchTypeView;
    SelectArcadeCharacterView selectArcadeCharacterView;
    SelectArcadeCharacterView selectArcadeRivalView;
    SelectArcadeBallView selectArcadeBallView;

    // Match Views components
    ArcadeMatchView arcadePVPMatchView;

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

    // Game Constants
    int goalTarget = 0;

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
        selectModeView = new SelectModeView(luckiest);

        // Arcade Views
        selectArcadeModeView = new SelectArcadeModeView(luckiest);
        selectArcadeResolutionView = new SelectArcadeResolutionView(luckiest);
        selectArcadeMatchTypeView = new SelectArcadeMatchTypeView(luckiest);
        selectArcadeCharacterView = new SelectArcadeCharacterView(luckiest, false);
        selectArcadeRivalView = new SelectArcadeCharacterView(luckiest, true);
        selectArcadeBallView = new SelectArcadeBallView(luckiest);

        // Match Views
        arcadePVPMatchView = new ArcadeMatchView(luckiest, game);
		add(mainMenuView, BorderLayout.CENTER);
    }

    private void prepareController() {
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

        // Arcade Views
        prepareSelectModeController();
        prepareSelectArcadeModeController();
        prepareSelectArcadeResolutionController();
        prepareSelectArcadeMatchTypeController();
        prepareSelectArcadeCharacterController();
        prepareSelectArcadeRivalController();
        prepareSelectArcadeBallController();

        // Match Views
        prepareArcadePVPMatchController();

        // Key movement
        prepareKeysController();
    }

    private void prepareMainMenuController() {
        mainMenuView.exitBtn.addActionListener(e -> exit());
        mainMenuView.tutorialBtn.addActionListener(e -> showView(tutorialView, "¿Cómo Jugar?"));
        mainMenuView.settingsBtn.addActionListener(e -> showView(settingsView, "Ajustes"));
        mainMenuView.playBtn.addActionListener(e -> showView(selectModeView, "Modo de Juego"));
    }

    private void prepareTutorialController() {
        tutorialView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
    }

    private void prepareSettingsController() {
        settingsView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
    }

    private void prepareSelectModeController()  {
        selectModeView.backBtn.addActionListener(e -> showView(mainMenuView, "Inicio"));
        selectModeView.arcadeBtn.addActionListener(e -> showView(selectArcadeModeView, "Modalidad (Arcade)"));
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
        selectArcadeMatchTypeView.continueBtn.addActionListener(e -> showView(selectArcadeCharacterView, "Personaje (Arcade)"));
    }

    private void prepareSelectArcadeCharacterController() {
        selectArcadeCharacterView.backBtn.addActionListener(e -> showView(selectArcadeMatchTypeView, "Tipo de Partido (Arcade)"));
        selectArcadeCharacterView.continueBtn.addActionListener(e -> {
            // The rival view shows the character of player 1 and if the rival is the AI or player 2
            selectArcadeRivalView.setPlayerCharacter(selectArcadeCharacterView.getSelectedCharacter());
            selectArcadeRivalView.setVsMachine(selectArcadeModeView.isVsMachine());
            showView(selectArcadeRivalView, "Rival (Arcade)");
        });
    }

    private void prepareSelectArcadeRivalController() {
        selectArcadeRivalView.backBtn.addActionListener(e -> showView(selectArcadeCharacterView, "Personaje (Arcade)"));
        selectArcadeRivalView.continueBtn.addActionListener(e -> showView(selectArcadeBallView, "Balón (Arcade)"));
    }

    private void prepareSelectArcadeBallController() {
        selectArcadeBallView.backBtn.addActionListener(e -> showView(selectArcadeRivalView, "Rival (Arcade)"));
        // ¡JUGAR!: starts the game in the domain with the choices of every screen (translated to the domain names)
        selectArcadeBallView.continueBtn.addActionListener(e -> newGame(
            toDomainProfile(selectArcadeModeView.getAIProfile()),
            toDomainResolution(selectArcadeResolutionView.getSelectedResolution()),
            toDomainCountry(selectArcadeCharacterView.getSelectedCharacter()),
            toDomainCountry(selectArcadeRivalView.getSelectedCharacter())
        ));
    }

    private void prepareArcadePVPMatchController() {
        // Pause: stops the game loop and the domain, and shows the pause card over the match (window glass pane)
        arcadePVPMatchView.pauseBtn.addActionListener(e -> {
            try {
                game.pause();
                gameLoop.stop();
                setGlassPane(arcadePVPMatchView.pauseOverlay);
                getGlassPane().setVisible(true);
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        arcadePVPMatchView.resumeBtn.addActionListener(e -> {
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
        arcadePVPMatchView.saveGameBtn.addActionListener(e -> {
            try {
                game.saveGame(null);
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        arcadePVPMatchView.endGameBtn.addActionListener(e -> {
            try {
                game.endGame();
            } catch (DOPOSoccerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            gameLoop.stop();
            getGlassPane().setVisible(false);
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

            // Only if the domain accepted it: players data in the HUD and scoreboard
            boolean vsMachine = selectArcadeModeView.isVsMachine();
            arcadePVPMatchView.setPlayer1(
                "TÚ",
                selectArcadeCharacterView.getSelectedCharacter(),
                selectArcadeCharacterView.getSelectedPower(),
                selectArcadeCharacterView.getSelectedCode()
            );
            arcadePVPMatchView.setPlayer2(
                vsMachine ? "IA" : "J2",
                selectArcadeRivalView.getSelectedCharacter(),
                selectArcadeRivalView.getSelectedPower(),
                selectArcadeRivalView.getSelectedCode()
            );

            arcadePVPMatchView.setVsMachine(vsMachine);
            if (vsMachine) { // against the AI, also its profile (style)
                arcadePVPMatchView.setAILabel(selectArcadeModeView.getAIProfile(), selectArcadeRivalView.getSelectedCharacter());
            }

            showView(arcadePVPMatchView, "Partido (Arcade)");
            refreshMatchView();  // 0 - 0, full time, full life... before the first tick
            lastTime = System.nanoTime(); // so the first dt isn't huge
            gameLoop.start();
        } catch (DOPOSoccerException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage()); // e.g. "Opción en construcción."
        }
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
        lastTime = now;
        refreshMatchView();
    });

    private void refreshMatchView() {
        try {
            arcadePVPMatchView.setScore(game.getGoals("LEFT"), game.getGoals("RIGHT"));

            int timeLeft = game.getTimeLeft();// -1 in a goals match
            if (timeLeft >= 0) arcadePVPMatchView.setTime(timeLeft);

            arcadePVPMatchView.setPlayer1Hp(game.getLife("LEFT") / (double) game.getMaxLife("LEFT"));
            arcadePVPMatchView.setPlayer2Hp(game.getLife("RIGHT") / (double) game.getMaxLife("RIGHT"));
            arcadePVPMatchView.setPlayer1Power(game.getPowerCharge("LEFT"));
            arcadePVPMatchView.setPlayer2Power(game.getPowerCharge("RIGHT"));

            // End of the match
            if (game.isMatchOver()) {
                gameLoop.stop();
                String winner = game.getWinner();  // "LEFT", "RIGHT" or null
                String text = winner == null ? "Partido terminado."
                    : ("LEFT".equals(winner) ? "¡Ganó J1!" : "¡Ganó el rival!")
                    + " Puntos: " + game.getPointsEarned(winner);
                JOptionPane.showMessageDialog(this, text);
                showView(mainMenuView, "Inicio");
            }

            // Enables movement
            arcadePVPMatchView.repaint();
        } catch (DOPOSoccerException ex) {
            gameLoop.stop();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Keys of the match: they only tell the domain which action is pressed or released
    private void prepareKeysController() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            boolean inMatch = getContentPane().getComponentCount() > 0
                && getContentPane().getComponent(0) == arcadePVPMatchView;
            if (!inMatch) return false; // in the menus the keys work as always

            // Esc: pause, or resume if it's already paused (same as clicking the buttons)
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (getGlassPane().isVisible()) {
                        arcadePVPMatchView.resumeBtn.doClick();
                    } else if (gameLoop.isRunning()) {
                        arcadePVPMatchView.pauseBtn.doClick();
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
            System.out.println("No se pudo cargar la fuente: " + e.getMessage());
            luckiest = new Font(Font.SANS_SERIF, Font.BOLD, 12); // respaldo
        }
    }

    public static void main(String[] args) {
        DOPOSoccerGUI ui = new DOPOSoccerGUI();
        ui.setVisible(true);
    }
}
