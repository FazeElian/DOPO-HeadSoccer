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

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DOPOSoccerGUI extends JFrame {
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

    public DOPOSoccerGUI() {
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
    }

    private void showView(JPanel view, String title) {
		getContentPane().removeAll();
        setTitle("DOPOSoccer | " + title);
		add(view, BorderLayout.CENTER);
		revalidate();
		repaint();
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
