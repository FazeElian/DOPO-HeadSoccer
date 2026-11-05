package presentation;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class POOBSoccerGUI extends JFrame {
    // Views components
    JPanel homeView;
    JPanel tutorialView;
    JPanel settingsView;
    JPanel selectModeView;

    // Buttons with events associated
    // For Home View:
    JButton settingsBtn;
    JButton tutorialBtn;
    JButton storeBtn;
    JButton playBtn;
    JButton exitBtn;

    JButton tutorialBackBtn;
    JButton settingsBackBtn;
    JButton selectModeBackBtn;

    // Settings constants
    int musicVolPercentage = 70;
    int soundEffectsVolPercentage = 70;

    // Settings components
    JSlider musicSlider;
    JSlider soundEffectsSlider;
    JLabel musicPercentage;
    JLabel soundEffectsPercentage;

    // Select mode components
    JButton arcadeBtn;
    JButton tournamentBtn;

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

    public POOBSoccerGUI() {
        setSize(1280, 900); // Small Laptop size
        setTitle("POOBSOCCER | Inicio");
        prepareView();
        prepareController();
    }

    private void prepareView() {
        loadFonts();
        prepareHomeView();
        prepareTutorialView();
        prepareSettingsView();
        prepareSelectModeView();
    }

    private void prepareHomeView() {
        homeView = createBackgroundPanel(new BorderLayout(), "assets/img/homeBackground.png");
        JPanel topBar = createHomeBar(
            settingsBtn = createHomeButton("Ajustes", DARK_GREEN, WHITE, "assets/img/icons/homeView/settings.png"),
            storeBtn = createHomeButton("Tienda", YELLOW, BLACK, "assets/img/icons/homeView/store.png")
        );

        JLabel credits = new JLabel("HECHO POR: E. IBARRA & O. POVEDA") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        credits.setOpaque(false);
        credits.setFont(luckiest.deriveFont(22f));
        credits.setBackground(NORMAL_GREEN);
        credits.setForeground(DARK_GREEN);
        credits.setBorder(BorderFactory.createEmptyBorder(14, 24, 8, 24));

        JPanel bottomBar = createHomeBar(
            credits,
            tutorialBtn = createHomeButton("¿Cómo jugar?", DARK_GREEN, WHITE, "assets/img/icons/homeView/help.png")
        );
        
        // Center block: game title, play & exit button
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(220, 0, 0, 0));

        // Game Title
        JLabel gameTitle = new JLabel("POOBSOCCER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        gameTitle.setBackground(DARK_GREEN);
        gameTitle.setFont(luckiest.deriveFont(72f));
        gameTitle.setOpaque(false);
        gameTitle.setForeground(WHITE);
        gameTitle.setBorder(BorderFactory.createEmptyBorder(26, 50, 10, 50));

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 10));
        buttons.setOpaque(false);

        playBtn = createHomeButton("JUGAR", BLUE, WHITE, null);
        playBtn.setFont(luckiest.deriveFont(35f));
        playBtn.setIcon(loadIcon("assets/img/icons/homeView/play.png", 30));
        playBtn.setIconTextGap(10);
        playBtn.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));

        exitBtn = createHomeButton("SALIR", RED, WHITE, null);
        exitBtn.setFont(luckiest.deriveFont(35f));
        exitBtn.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));

        buttons.add(playBtn);
        buttons.add(exitBtn);

        JPanel block = new JPanel(new GridLayout(2, 1, 0, 15));
        block.setOpaque(false);
        block.add(gameTitle);
        block.add(buttons);

        center.add(block);

        homeView.add(topBar, BorderLayout.NORTH);
        homeView.add(center, BorderLayout.CENTER);
        homeView.add(bottomBar, BorderLayout.SOUTH);
		add(homeView, BorderLayout.CENTER);
    }

    private void prepareTutorialView() {
        tutorialView = createBackgroundPanel(new BorderLayout(), "assets/img/homeBackground.png");

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        tutorialBackBtn = createHomeButton("Volver", DARK_GREEN, WHITE, "assets/img/icons/back.png");
        topBar.add(tutorialBackBtn, BorderLayout.WEST);

        // Block that will contain the green card
        JPanel block = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        block.setOpaque(false);

        // Green card
        JPanel card = new JPanel(new BorderLayout(0, 30)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBackground(DARK_GREEN);
        card.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        // Title
        JLabel howToPlayTxt = new JLabel("¿CÓMO JUGAR?");
        howToPlayTxt.setFont(luckiest.deriveFont(40f));
        howToPlayTxt.setForeground(WHITE);
        howToPlayTxt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, WHITE),
            BorderFactory.createEmptyBorder(8, 6, 6, 6)
        ));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(howToPlayTxt);

        // Block for both players controls
        JPanel controls = new JPanel(new GridLayout(1, 2, 50, 0)); // 1 row, 2 columns, 50px between them
        controls.setOpaque(false);

        // Player 1 block
        JPanel player1Controls = new JPanel(new BorderLayout(0, 20)); // 20px of vertical gap
        player1Controls.setOpaque(false);

        JButton p1Title = createHomeButton("JUGADOR 1", BLUE, WHITE, null);
        p1Title.setFont(luckiest.deriveFont(19f));
        p1Title.setBorder(BorderFactory.createEmptyBorder(14, 0, 12, 0));
        p1Title.setCursor(Cursor.getDefaultCursor()); // it's only a title, not clickable
        p1Title.setFocusable(false);

        Image p1Img = new ImageIcon("assets/img/controls-player-1.png").getImage().getScaledInstance(250, 231, Image.SCALE_SMOOTH);
        JLabel p1ImgLabel = new JLabel(new ImageIcon(p1Img));

        player1Controls.add(p1Title, BorderLayout.NORTH);
        player1Controls.add(p1ImgLabel, BorderLayout.CENTER);

        // Player 2 block
        JPanel player2Controls = new JPanel(new BorderLayout(0, 20));
        player2Controls.setOpaque(false);

        JButton p2Title = createHomeButton("JUGADOR 2", RED, WHITE, null);
        p2Title.setFont(luckiest.deriveFont(19f));
        p2Title.setBorder(BorderFactory.createEmptyBorder(14, 0, 12, 0));
        p2Title.setCursor(Cursor.getDefaultCursor());
        p2Title.setFocusable(false);

        Image p2Img = new ImageIcon("assets/img/controls-player-2.png").getImage().getScaledInstance(250, 231, Image.SCALE_SMOOTH);
        JLabel p2ImgLabel = new JLabel(new ImageIcon(p2Img));

        player2Controls.add(p2Title, BorderLayout.NORTH);
        player2Controls.add(p2ImgLabel, BorderLayout.CENTER);

        // Add both blocks to controls block
        controls.add(player1Controls);
        controls.add(player2Controls);

        // Rules block
        JPanel rules = new JPanel(new GridLayout(5, 1)) { // 5 rows, 1 column
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.setColor(YELLOW);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);

                g2.dispose();
            }
        };
        rules.setOpaque(false);
        rules.setBackground(MEDIUM_GREEN);
        rules.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel rulesTitle = new JLabel("REGLAS CLAVE", SwingConstants.CENTER);
        rulesTitle.setForeground(YELLOW);
        rulesTitle.setFont(luckiest.deriveFont(20f));
        rulesTitle.setBorder(BorderFactory.createEmptyBorder(7, 15, 7, 15));
        rules.add(rulesTitle);

        String[] items = {
            "• LA BARRA DE PODER SE LLENA CON EL TIEMPO. LLENA: PRESIONA PODER Y GOLPEA EL BALÓN.",
            "• SUPERTIRO: SI TE LLEGA, PATEA JUSTO A TIEMPO PARA DEVOLVERLO.",
            "• PATADAS Y PODERES DEL RIVAL TE ATURDEN 3 SEGUNDOS.",
            "• SI HAY EMPATE AL TERMINAR EL TIEMPO: ¡GOL DE ORO!"
        };
        for (String rule : items) {
            JLabel newItem = new JLabel(rule);
            newItem.setForeground(WHITE);
            newItem.setFont(luckiest.deriveFont(15f));
            rules.add(newItem);
        }

        card.add(titleRow, BorderLayout.NORTH);
        card.add(controls, BorderLayout.CENTER);
        card.add(rules, BorderLayout.SOUTH);

        block.add(card);
        tutorialView.add(topBar, BorderLayout.NORTH);
        tutorialView.add(block, BorderLayout.CENTER);
    }

    private void prepareSettingsView() {
        settingsView = createBackgroundPanel(new BorderLayout(), "assets/img/homeBackground.png");
        
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        settingsBackBtn = createHomeButton("Volver", DARK_GREEN, WHITE, "assets/img/icons/back.png");
        topBar.add(settingsBackBtn, BorderLayout.WEST);
        
        // Block that will contain the green card
        JPanel block = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        block.setOpaque(false);
        block.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));

        // Green card
        JPanel card = new JPanel(new BorderLayout(0, 00)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBackground(DARK_GREEN);
        card.setBorder(BorderFactory.createEmptyBorder(35, 45, 40, 45));
        card.setPreferredSize(new Dimension(550, 330));

        // Title
        JLabel settingsTitle = new JLabel("AJUSTES");
        settingsTitle.setFont(luckiest.deriveFont(40f));
        settingsTitle.setForeground(WHITE);
        settingsTitle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, WHITE),
            BorderFactory.createEmptyBorder(8, 6, 6, 6)
        ));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(settingsTitle);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Options block
        JPanel options = new JPanel(new GridLayout(2, 1, 20, 0));
        options.setOpaque(false);

        // music
        JPanel music = new JPanel(new GridLayout(1, 2));
        music.setOpaque(false);
        music.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MEDIUM_GREEN),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));

        JLabel musicLabel = new JLabel("MÚSICA");
        musicLabel.setOpaque(false);
        musicLabel.setForeground(WHITE);
        musicLabel.setFont(luckiest.deriveFont(20f));

        // slider block
        JPanel musicSliderBlock = new JPanel(new BorderLayout(0, 15));
        musicSliderBlock.setOpaque(false);
        
        // slider
        musicSlider = new JSlider(0, 100, 70); // max value of 100, min of 0 and a default value of 70
        musicSlider.setOpaque(false);
        musicSlider.setFocusable(false);
        musicSlider.setPreferredSize(new Dimension(115, 20));
        musicSlider.setUI(new BasicSliderUI(musicSlider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int h = 10; // track height
                int y = trackRect.y + (trackRect.height - h) / 2;

                // Empty part
                g2.setColor(new Color(35, 95, 30));
                g2.fillRoundRect(trackRect.x, y, trackRect.width, h, h, h);

                // Filled part (up to the thumb center)
                int filled = thumbRect.x + thumbRect.width / 2 - trackRect.x;
                g2.setColor(NORMAL_GREEN);
                g2.fillRoundRect(trackRect.x, y, filled, h, h, h);

                g2.dispose();
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillOval(thumbRect.x, thumbRect.y + (thumbRect.height - 16) / 2, 16, 16);
                g2.dispose();
            }

            @Override
            protected Dimension getThumbSize() {
                return new Dimension(16, 16);
            }

            @Override
            public void paintFocus(Graphics g) {
                // no focus rectangle
            }
        });

        // percentage label
        musicPercentage = new JLabel(musicVolPercentage + "%");
        musicPercentage.setOpaque(false);
        musicPercentage.setForeground(WHITE);
        musicPercentage.setFont(luckiest.deriveFont(20f));
        musicPercentage.setPreferredSize(new Dimension(50, 20));

        musicSliderBlock.add(musicSlider, BorderLayout.CENTER);
        musicSliderBlock.add(musicPercentage, BorderLayout.EAST);

        music.add(musicLabel, BorderLayout.WEST);
        music.add(musicSliderBlock, BorderLayout.EAST);

        // Sound Effects
        JPanel soundEffects = new JPanel(new GridLayout(1, 2));
        soundEffects.setOpaque(false);
        soundEffects.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MEDIUM_GREEN),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));

        JLabel soundEffectsLabel = new JLabel("EFECTOS DE SONIDO");
        soundEffectsLabel.setOpaque(false);
        soundEffectsLabel.setForeground(WHITE);
        soundEffectsLabel.setFont(luckiest.deriveFont(20f));

        // slider block
        JPanel soundEffectsSliderBlock = new JPanel(new BorderLayout());
        soundEffectsSliderBlock.setOpaque(false);
        
        // slider
        soundEffectsSlider = new JSlider(0, 100, 70); // max value of 100, min of 0 and a default value of 70
        soundEffectsSlider.setOpaque(false);
        soundEffectsSlider.setFocusable(false);
        soundEffectsSlider.setPreferredSize(new Dimension(115, 20));
        soundEffectsSlider.setUI(new BasicSliderUI(soundEffectsSlider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int h = 10; // track height
                int y = trackRect.y + (trackRect.height - h) / 2;

                // Empty part
                g2.setColor(new Color(35, 95, 30));
                g2.fillRoundRect(trackRect.x, y, trackRect.width, h, h, h);

                // Filled part (up to the thumb center)
                int filled = thumbRect.x + thumbRect.width / 2 - trackRect.x;
                g2.setColor(NORMAL_GREEN);
                g2.fillRoundRect(trackRect.x, y, filled, h, h, h);

                g2.dispose();
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillOval(thumbRect.x, thumbRect.y + (thumbRect.height - 16) / 2, 16, 16);
                g2.dispose();
            }

            @Override
            protected Dimension getThumbSize() {
                return new Dimension(16, 16);
            }

            @Override
            public void paintFocus(Graphics g) {
                // no focus rectangle
            }
        });

        // percentage label
        soundEffectsPercentage = new JLabel(soundEffectsVolPercentage + "%");
        soundEffectsPercentage.setOpaque(false);
        soundEffectsPercentage.setForeground(WHITE);
        soundEffectsPercentage.setFont(luckiest.deriveFont(20f));
        soundEffectsPercentage.setPreferredSize(new Dimension(50, 20));

        soundEffectsSliderBlock.add(soundEffectsSlider, BorderLayout.CENTER);
        soundEffectsSliderBlock.add(soundEffectsPercentage, BorderLayout.EAST);

        soundEffects.add(soundEffectsLabel, BorderLayout.WEST);
        soundEffects.add(soundEffectsSliderBlock, BorderLayout.EAST);

        options.add(music);
        options.add(soundEffects);

        // Reset button
        JButton resetBtn = createHomeButton("RESTABLECER", RED, WHITE, null);
        resetBtn.setFont(luckiest.deriveFont(18f));

        JPanel resetRow = new JPanel(new BorderLayout());
        resetRow.setOpaque(false);
        resetRow.add(resetBtn);
        resetRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        card.add(titleRow, BorderLayout.NORTH);
        card.add(options, BorderLayout.CENTER);
        card.add(resetRow, BorderLayout.SOUTH);

        block.add(card);
        settingsView.add(topBar, BorderLayout.NORTH);
        settingsView.add(block, BorderLayout.CENTER);
    }

    private void prepareSelectModeView() {
        selectModeView = createBackgroundPanel(new BorderLayout(), "assets/img/homeBackground.png");
        
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        selectModeBackBtn = createHomeButton("Volver", DARK_GREEN, WHITE, "assets/img/icons/back.png");
        topBar.add(selectModeBackBtn, BorderLayout.WEST);
        
        // Block that will contain the green card
        JPanel block = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        block.setOpaque(false);
        block.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));

        // Green card
        JPanel card = new JPanel(new BorderLayout(0, 00)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBackground(DARK_GREEN);
        card.setBorder(BorderFactory.createEmptyBorder(35, 45, 40, 45));
        card.setPreferredSize(new Dimension(550, 330));

        // Title
        JLabel selectModeTitle = new JLabel("SELECCIONAR MODO");
        selectModeTitle.setFont(luckiest.deriveFont(40f));
        selectModeTitle.setForeground(WHITE);
        selectModeTitle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, WHITE),
            BorderFactory.createEmptyBorder(8, 6, 6, 6)
        ));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(selectModeTitle);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        card.add(titleRow, BorderLayout.NORTH);

        block.add(card);
        selectModeView.add(topBar, BorderLayout.NORTH);
        selectModeView.add(block, BorderLayout.CENTER);
    }

    private void prepareController() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
        prepareHomeController();
        prepareTutorialController();
        prepareSettingsController();
        prepareSelectModeController();
    }

    private void prepareTutorialController() {
        tutorialBackBtn.addActionListener(e -> showView(homeView, "Inicio"));
    }

    private void prepareSettingsController() {
        settingsBackBtn.addActionListener(e -> showView(homeView, "Inicio"));
        musicSlider.addChangeListener(e -> {
            musicVolPercentage = musicSlider.getValue();
            musicPercentage.setText(musicVolPercentage + "%");
        });

        soundEffectsSlider.addChangeListener(e -> {
            soundEffectsVolPercentage = soundEffectsSlider.getValue();
            soundEffectsPercentage.setText(soundEffectsVolPercentage + "%");
        });
    }

    private void prepareSelectModeController()  {
        selectModeBackBtn.addActionListener(e -> showView(homeView, "Inicio"));
    }

    private void showView(JPanel view, String title) {
		getContentPane().removeAll();
        setTitle("POOBSoccer | " + title);
		add(view, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

    private void prepareHomeController() {
        exitBtn.addActionListener(e -> exit());
        tutorialBtn.addActionListener(e -> showView(tutorialView, "¿Cómo Jugar?"));
        settingsBtn.addActionListener(e -> showView(settingsView, "Ajustes"));
        playBtn.addActionListener(e -> showView(selectModeView, "Seleccionar modo de juego"));
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

    // Home View helper methods
    private JPanel createHomeBar(JComponent left, JComponent right) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false); // Delete background (transparent)
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton createHomeButton(
        String title,
        Color bgColor,
        Color txtColor,
        String iconPath
    ) {
        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getBackground();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, 18));
            btn.setIconTextGap(5); // gap between icon & text
        }

        btn.setBackground(bgColor);
        btn.setForeground(txtColor);
        btn.setFont(luckiest.deriveFont(22f));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.TOP);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(14, 24, 8, 24));

        return btn;
    }
    
    private ImageIcon loadIcon(String path, int size) {
        Image img = new ImageIcon(path).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
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

    private JPanel createBackgroundPanel(LayoutManager layout, String imageUrl) {
		Image background = new ImageIcon(imageUrl).getImage();
		JPanel panel = new JPanel(layout) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
			}
		};
		panel.setOpaque(false);
		return panel;
	}

    public static void main(String[] args) {
        POOBSoccerGUI ui = new POOBSoccerGUI();
        ui.setVisible(true);
    }
}