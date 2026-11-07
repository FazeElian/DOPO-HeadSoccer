package presentation.views.match;

import javax.swing.*;
import domain.DOPOSoccer;
import domain.DOPOSoccerException;

import java.awt.*;
import presentation.views.View;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Generic view for the matches (arcade, survival...).
 * It has the parts every match uses: players HUD, scoreboard, time (countdown or stopwatch) and pause button.
 * Each match view builds its screen in prepareView with these parts (createTopBar or the single blocks).
 * Layouts: only BorderLayout, GridLayout and FlowLayout (padding with empty borders).
 */
public abstract class GameView extends View {
    private DOPOSoccer game;
    public static final String STADIUM_BACKGROUND = "assets/img/stadiumBackground.png";

    protected static final int HUD_WIDTH = 320;

    // Pause button image (square) and its size inside the button
    protected static final String PAUSE_IMAGE = "assets/img/icons/match/pause.png";
    protected static final int PAUSE_ICON_SIZE = 30;

    // Match time: countdown, 1 minute by default (javax.swing.Timer: runs every second in the Swing thread)
    // (by goals it counts up: secondsLeft is then the time played)
    public static final int DEFAULT_MATCH_SECONDS = 60;

    // Player 1 HUD components
    protected StatBar p1HpBar;
    protected StatBar p1PowerBar;
    protected JLabel p1NameLabel;
    protected JLabel p1PowerLabel;

    // Rival (player 2 or AI) HUD components
    protected StatBar p2HpBar;
    protected StatBar p2PowerBar;
    protected JLabel p2TagLabel;
    protected JLabel p2NameLabel;
    protected JLabel p2PowerLabel;

    // Scoreboard components
    protected JLabel p1TeamLabel;
    protected JLabel p2TeamLabel;
    protected JLabel scoreLabel;
    protected JLabel timeLabel;
    public JButton pauseBtn;

    // Pause menu: card over the match (shown with the window glass pane) and its buttons
    public JPanel pauseOverlay;
    public JButton resumeBtn;
    public JButton saveGameBtn;
    public JButton endGameBtn;
    private static final Color PAUSE_SHADOW = new Color(0, 0, 0, 150); // black, alpha 150 of 255

    // Scoreboard data
    private String p1Code = "CO";
    private String p2Code = "AR";
    private String iaCode = "IA";

    // Time data
    private boolean countDown = true; // true: time match (goes down), false: goals match (goes up)
    private int secondsLeft = DEFAULT_MATCH_SECONDS;
    private Timer matchTimer = new Timer(1000, e -> tic());

    // Goals to win in a goals match (0 = no goal target, it's a time match)
    private int goalTarget = 0;

    public GameView(Font luckiest, DOPOSoccer game) {
        super(luckiest, STADIUM_BACKGROUND); // super must be the first line of a constructor
        this.game = game;
        pauseOverlay = createPauseOverlay();
    }
    
    // Draws the match over the stadium: players and ball, in the places the domain says
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   // View's version: the stadium background
        if (game == null) return;
        try {
            drawPlayer(g, "LEFT");
            drawPlayer(g, "RIGHT");

            int d = sx(game.getBallRadius() * 2);
            g.drawImage(ballImage(game.getBallName()), sx(game.getBallX()), sy(game.getBallY()), d, d, null);
        } catch (DOPOSoccerException e) {
            // no match started yet: only the background
        }
    }

    // Domain pixels -> screen pixels (the field fills the whole view, like the background)
    private int sx(double x) throws DOPOSoccerException {
        return (int) (x * getWidth() / game.getFieldWidth());
    }

    private int sy(double y) throws DOPOSoccerException {
        return (int) (y * getHeight() / game.getFieldHeight());
    }

    // Player image (normal or kicking); the right one looks to the left
    private void drawPlayer(Graphics g, String side) throws DOPOSoccerException {
        String file = "assets/img/players/" + imageOf(game.getCharacter(side)) + (game.isKicking(side) ? "-kick" : "") + ".png";
        Image img = new ImageIcon(file).getImage();
        int x = sx(game.getPlayerX(side));
        int y = sy(game.getPlayerY(side));
        int w = sx(game.getPlayerWidth());
        int h = sy(game.getPlayerHeight());
        if ("LEFT".equals(side)) {
            g.drawImage(img, x, y, w, h, null);
        } else {
            g.drawImage(img, x + w, y, -w, h, null);   // negative width = flipped
        }
    }

    // Image of the ball the domain uses ("Clásico" -> assets/img/balls/classic.png)
    private Image ballImage(String ballName) {
        String file;
        switch (ballName) {
            case "Tricolor": file = "tricolor"; break;
            case "Dorado": file = "golden"; break;
            case "Fuego": file = "fire"; break;
            case "Hielo": file = "ice"; break;
            case "Galaxia": file = "galaxy"; break;
            default: file = "classic"; // "Clásico"
        }
        return new ImageIcon("assets/img/balls/" + file + ".png").getImage();
    }

    // Country of the domain -> name of the image file
    private String imageOf(String country) {
        switch (country) {
            case "Corea del Sur": return "corea-sur";
            case "Camerún": return "cameroon";
            case "España": return "spain";
            case "Japón": return "japan";
            default: return country.toLowerCase();   // "Colombia" -> "colombia", "Argentina", "Nigeria"
        }
    }
    


    // Player 1 data: info box and the code in the scoreboard
    public void setPlayer1(String name, String character, String power, String countryCode) {
        p1NameLabel.setText(name + " · " + character);
        p1PowerLabel.setText("PODER: " + power);
        p1Code = countryCode;
        updateTeamLabels();
    }

    // Rival data: info box and the code in the scoreboard
    public void setPlayer2(String name, String character, String power, String countryCode) {
        p2NameLabel.setText(name + " · " + character);
        p2PowerLabel.setText("PODER: " + power);
        p2Code = countryCode;
        updateTeamLabels();
    }

    // The rival is the AI (IA) or player 2 (J2)
    public void setVsMachine(boolean vsMachine) {
        iaCode = vsMachine ? "IA" : "J2";
        p2TagLabel.setText(iaCode);
        updateTeamLabels();
    }

    // Bars values from 0 (empty) to 1 (full)
    public void setPlayer1Hp(double value) {
        p1HpBar.setValue(value);
    }

    public void setPlayer1Power(double value) {
        p1PowerBar.setValue(value);
    }

    public void setPlayer2Hp(double value) {
        p2HpBar.setValue(value);
    }

    public void setPlayer2Power(double value) {
        p2PowerBar.setValue(value);
    }

    public void setAILabel(String profile, String character) {
        p2NameLabel.setText("IA · " + character + " (" + profile + ")");
    }

    // In a goals match, the match ends when someone gets to the goal target
    public void setScore(int p1Goals, int p2Goals) {
        scoreLabel.setText(p1Goals + " - " + p2Goals);
        if (goalTarget > 0 && (p1Goals >= goalTarget || p2Goals >= goalTarget)) {
            matchTimer.stop();
            onGoalTargetReached();
        }
    }

    // Goals to win (only for goals matches)
    public void setGoalTarget(int goals) {
        goalTarget = goals;
    }

    // Time in seconds, shown as m:ss
    public void setTime(int seconds) {
        timeLabel.setText(formatTime(seconds));
    }

    // Starts the countdown from the given seconds (DEFAULT_MATCH_SECONDS = 1 minute)
    public void startTimer(int seconds) {
        countDown = true;
        goalTarget = 0; // a time match has no goal target
        secondsLeft = seconds;
        setTime(secondsLeft);
        matchTimer.restart();
    }

    // Starts the clock from 0:00 counting up (goals match: no time limit)
    public void startStopwatch() {
        countDown = false;
        secondsLeft = 0;
        setTime(secondsLeft);
        matchTimer.restart();
    }

    // Stops the clock without losing the time (for the pause)
    public void pauseTimer() {
        matchTimer.stop();
    }

    // Continues the clock after a pause
    public void resumeTimer() {
        if (!countDown || secondsLeft > 0) matchTimer.start(); // the countdown doesn't continue from 0:00
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    // What happens when the countdown gets to 0:00 (each match view can change it)
    protected void onTimeUp() {
        JOptionPane.showMessageDialog(this, "El tiempo ha terminado.");
    }

    // What happens when someone gets to the goal target (each match view can change it)
    protected void onGoalTargetReached() {
        JOptionPane.showMessageDialog(this, "¡Alguien llegó a " + goalTarget + " goles!");
    }

    // One second less (countdown) or one more (stopwatch)
    private void tic() {
        if (countDown) {
            secondsLeft--;
            setTime(secondsLeft);
            if (secondsLeft <= 0) {
                matchTimer.stop();
                onTimeUp();
            }
        } else {
            secondsLeft++; // By goals, only shows the time played
            setTime(secondsLeft);
        }
    }

    protected String formatTime(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private void updateTeamLabels() {
        p1TeamLabel.setText("(" + p1Code + ") J1");
        p2TeamLabel.setText("(" + p2Code + ") " + iaCode);
    }

    // Default top bar: player 1 HUD, scoreboard + time + pause and rival HUD, together and centered
    protected JPanel createTopBar() {
        // The 3 blocks together (16px between them) instead of spread over the whole width
        JPanel blocks = new JPanel(new BorderLayout(16, 0));
        blocks.setOpaque(false);
        blocks.add(createPlayer1Block(), BorderLayout.WEST);
        blocks.add(createCenterBlock(), BorderLayout.CENTER);
        blocks.add(createPlayer2Block(), BorderLayout.EAST);

        // FlowLayout centers the blocks group in the top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        topBar.add(blocks);
        return topBar;
    }

    protected JPanel createPlayer1Block() {
        p1HpBar = new StatBar(NORMAL_GREEN, 1.0);
        p1PowerBar = new StatBar(YELLOW, 0);
        p1NameLabel = new JLabel("TÚ · COLOMBIA");
        p1PowerLabel = new JLabel("PODER: ILUSIÓN");
        return createBlock(BLUE, new JLabel("J1"), p1HpBar, p1PowerBar, p1NameLabel, p1PowerLabel, false);
    }

    protected JPanel createPlayer2Block() {
        p2HpBar = new StatBar(NORMAL_GREEN, 1.0);
        p2PowerBar = new StatBar(YELLOW, 0);
        p2TagLabel = new JLabel(iaCode);
        p2NameLabel = new JLabel("");
        p2PowerLabel = new JLabel("PODER: RAYO");
        return createBlock(RED, p2TagLabel, p2HpBar, p2PowerBar, p2NameLabel, p2PowerLabel, true);
    }

    // HUD: HP bar and power bar on top, info box under them (texts on the right for the rival)
    protected JPanel createBlock(
        Color color,
        JLabel tagLabel,
        StatBar hpBar,
        StatBar powerBar,
        JLabel nameLabel,
        JLabel powerLabel,
        boolean alignRight
    ) {
        JPanel bars = new JPanel(new GridLayout(2, 1, 0, 6));
        bars.setOpaque(false);
        bars.add(createBarRow(color, new JLabel("HP"), hpBar));
        bars.add(createBarRow(color, tagLabel, powerBar));

        JPanel hud = new JPanel(new BorderLayout(0, 6));
        hud.setOpaque(false);
        hud.add(bars, BorderLayout.NORTH);
        hud.add(createInfoRow(color, nameLabel, powerLabel, alignRight), BorderLayout.CENTER);
        hud.setPreferredSize(new Dimension(HUD_WIDTH, hud.getPreferredSize().height));
        return hud;
    }

    // Rounded row: label on the left ("HP", "J1", "IA") and the bar
    protected JPanel createBarRow(
        Color color,
        JLabel label,
        StatBar bar
    ) {
        JPanel row = createRoundedPanel(new BorderLayout(10, 0), color, null, 16);
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        label.setFont(luckiest.deriveFont(20f));
        label.setForeground(WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); // the font sits high

        row.add(label, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    // Rounded row with a dark green box inside: name · character and the power
    protected JPanel createInfoRow(Color color, JLabel nameLabel, JLabel powerLabel, boolean alignRight) {
        JPanel row = createRoundedPanel(new BorderLayout(), color, null, 16);
        row.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel box = createRoundedPanel(new GridLayout(2, 1, 0, 2), DARK_GREEN, null, 12);
        box.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        int align = alignRight ? SwingConstants.RIGHT : SwingConstants.LEFT;
        nameLabel.setFont(luckiest.deriveFont(17f));
        nameLabel.setForeground(WHITE);
        nameLabel.setHorizontalAlignment(align);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0)); // room for accents

        powerLabel.setFont(luckiest.deriveFont(14f));
        powerLabel.setForeground(YELLOW);
        powerLabel.setHorizontalAlignment(align);
        powerLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0)); // room for accents

        box.add(nameLabel);
        box.add(powerLabel);
        row.add(box, BorderLayout.CENTER);
        return row;
    }

    // Scoreboard with the time under it, and the pause button on its right (all centered)
    protected JPanel createCenterBlock() {
        JPanel scoreboard = createScoreboard();

        JPanel scoreColumn = new JPanel(new BorderLayout(0, 6));
        scoreColumn.setOpaque(false);
        scoreColumn.add(scoreboard, BorderLayout.NORTH);
        scoreColumn.add(createTimeRow(), BorderLayout.CENTER);

        // The pause button in NORTH so it's aligned with the top of the scoreboard (and as tall as it)
        JPanel pauseColumn = new JPanel(new BorderLayout());
        pauseColumn.setOpaque(false);
        pauseColumn.add(createPauseButton(scoreboard.getPreferredSize().height), BorderLayout.NORTH);

        JPanel group = new JPanel(new BorderLayout(12, 0));
        group.setOpaque(false);
        group.add(scoreColumn, BorderLayout.CENTER);
        group.add(pauseColumn, BorderLayout.EAST);

        JPanel block = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        block.setOpaque(false);
        block.add(group);
        return block;
    }

    // Green box: "(CO) J1"   0 - 0   "(AR) IA"
    protected JPanel createScoreboard() {
        JPanel scoreboard = createRoundedPanel(new BorderLayout(16, 0), MEDIUM_GREEN, WHITE, 18);
        scoreboard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        p1TeamLabel = createOutlinedLabel("", WHITE, BLUE, 17f);
        p2TeamLabel = createOutlinedLabel("", WHITE, RED, 17f);
        updateTeamLabels();

        scoreLabel = new JLabel("0 - 0", SwingConstants.CENTER);
        scoreLabel.setFont(luckiest.deriveFont(30f));
        scoreLabel.setForeground(WHITE);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); // the font sits high

        scoreboard.add(p1TeamLabel, BorderLayout.WEST);
        scoreboard.add(scoreLabel, BorderLayout.CENTER);
        scoreboard.add(p2TeamLabel, BorderLayout.EAST);
        return scoreboard;
    }

    // Time in a yellow pill, centered under the scoreboard
    protected JPanel createTimeRow() {
        timeLabel = createOutlinedLabel(formatTime(secondsLeft), DARK_GREEN, YELLOW, 20f);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row.setOpaque(false);
        row.add(timeLabel);
        return row;
    }

    // Pause button: square green box with a white border (like the scoreboard) and the pause image in the middle
    protected JButton createPauseButton(int size) {
        pauseBtn = new JButton(loadIcon(PAUSE_IMAGE, PAUSE_ICON_SIZE)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MEDIUM_GREEN);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                g2.setColor(WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                g2.dispose();
                super.paintComponent(g); // the image on top
            }
        };
        pauseBtn.setPreferredSize(new Dimension(size, size));
        pauseBtn.setFocusable(false); // without focus, Space doesn't press it (pause is Esc)
        pauseBtn.setBorder(BorderFactory.createEmptyBorder());
        pauseBtn.setFocusPainted(false);
        pauseBtn.setBorderPainted(false);
        pauseBtn.setContentAreaFilled(false);
        pauseBtn.setOpaque(false);
        pauseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return pauseBtn;
    }

    // Transparent layer with the pause card centered (it goes in the window glass pane, over the match)
    protected JPanel createPauseOverlay() {
        JPanel card = createPauseCard();

        JPanel overlay = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            // Translucent black over the whole match, so the card stands out
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(PAUSE_SHADOW);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }

            // FlowLayout only centers horizontally: the top gap centers the card vertically
            @Override
            public void doLayout() {
                int gap = Math.max(0, (getHeight() - card.getPreferredSize().height) / 2);
                ((FlowLayout) getLayout()).setVgap(gap);
                super.doLayout();
            }
        };
        overlay.setOpaque(false);
        overlay.addMouseListener(new java.awt.event.MouseAdapter() {}); // blocks the clicks on the match below
        overlay.add(card);
        return overlay;
    }

    // Green card with a white border: "PAUSA" and the 3 buttons
    protected JPanel createPauseCard() {
        JPanel card = createRoundedPanel(new BorderLayout(0, 22), MEDIUM_GREEN, WHITE, 26);
        card.setBorder(BorderFactory.createEmptyBorder(26, 40, 34, 40));

        resumeBtn = createPauseMenuButton("REANUDAR", LIGHT_BLUE);
        saveGameBtn = createPauseMenuButton("GUARDAR PARTIDA", ORANGE);
        endGameBtn = createPauseMenuButton("TERMINAR JUEGO", RED);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 12));
        buttons.setOpaque(false);
        buttons.add(resumeBtn);
        buttons.add(saveGameBtn);
        buttons.add(endGameBtn);

        card.add(createTitleRow("PAUSA"), BorderLayout.NORTH);
        card.add(buttons, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(460, card.getPreferredSize().height));
        return card;
    }

    private JButton createPauseMenuButton(String text, Color color) {
        JButton btn = createButton(text, color, WHITE, null);
        btn.setFont(luckiest.deriveFont(22f));
        btn.setBorder(BorderFactory.createEmptyBorder(14, 20, 8, 20));
        btn.setFocusable(false); // without focus, Space doesn't press it
        return btn;
    }

    // Panel with a rounded background and an optional border (null = no border)
    protected JPanel createRoundedPanel(LayoutManager layout, Color bgColor, Color borderColor, int arc) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
                if (borderColor != null) {
                    g2.setColor(borderColor);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    // Pill label with a colored border; the text has the border color
    protected JLabel createOutlinedLabel(String text, Color bgColor, Color color, float size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(luckiest.deriveFont(size));
        label.setForeground(color);
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(11, 16, 6, 16));
        return label;
    }

    /**
     * Rounded bar: white track and a colored part for the value (0 = empty, 1 = full).
     */
    protected static class StatBar extends JComponent {
        private final Color fillColor;
        private double value;

        public StatBar(Color fillColor, double value) {
            this.fillColor = fillColor;
            this.value = value;
            setPreferredSize(new Dimension(200, 26));
        }

        public void setValue(double value) {
            this.value = Math.max(0, Math.min(1, value));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = getHeight();

            // Track
            g2.setColor(WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // Value (inside the track, with a small margin)
            int margin = 4;
            int fillWidth = (int) Math.round((getWidth() - margin * 2) * value);
            if (fillWidth > 0) {
                int innerHeight = getHeight() - margin * 2;
                g2.setColor(fillColor);
                g2.fillRoundRect(margin, margin, Math.max(fillWidth, innerHeight), innerHeight, innerHeight, innerHeight);
            }
            g2.dispose();
        }
    }
}
