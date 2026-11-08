package presentation.views.match;

import javax.swing.*;
import domain.DOPOSoccer;
import domain.DOPOSoccerException;

import java.awt.*;
import java.util.ArrayList;
import presentation.views.View;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Generic view for the matches (arcade, survival...).
 * It has the parts every match uses: players HUD, scoreboard, time and pause button, the pause card
 * and the result card. Everything it shows comes from the domain (the GUI gives it the values).
 * Layouts: only BorderLayout, GridLayout and FlowLayout (padding with empty borders).
 */
public abstract class GameView extends View {
    private DOPOSoccer game;
    public static final String STADIUM_BACKGROUND = "assets/img/stadiumBackground.png";

    protected static final int HUD_WIDTH = 320;

    // Pause button image (square) and its size inside the button
    protected static final String PAUSE_IMAGE = "assets/img/icons/match/pause.png";
    protected static final int PAUSE_ICON_SIZE = 30;
    protected static final String COIN_IMAGE = "assets/img/coin.png";

    // Light of the aura (ball with a power shot and player with the power active): the same for every character
    private static final Color AURA_LIGHT = new Color(170, 230, 255, 200);
    private static final Color AURA_PILL = new Color(200, 240, 255);
    private static final int TRAIL_LENGTH = 6; // previous positions of the ball drawn behind it
    private static final int SUDDEN_DEATH_BANNER_MS = 3000; // time the "¡MUERTE SÚBITA!" banner is shown

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

    // Result card (end of the match or of the series), also over the match, and its buttons
    public JPanel resultOverlay;
    public JButton againBtn;
    public JButton menuBtn;

    // Scoreboard data (given by the GUI)
    private String p1Code = "";
    private String p2Code = "";
    private String rivalTag = "";

    // Power names of the players (domain), for the "PODER:" / "AURA ACTIVA" texts
    private String p1Power = "";
    private String p2Power = "";

    // Sudden death: when it started, to show the banner a few seconds
    private boolean suddenDeath = false;
    private long suddenDeathSince = 0;

    // Last positions of the ball while it carries a power shot (only for drawing the trail)
    private final ArrayList<Point> ballTrail = new ArrayList<>();

    public GameView(Font luckiest, DOPOSoccer game) {
        super(luckiest, STADIUM_BACKGROUND); // super must be the first line of a constructor
        this.game = game;
        pauseOverlay = createOverlay(createPauseCard());
    }

    // Draws the match over the stadium: players, fake balls and ball, in the places the domain says
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   // View's version: the stadium background
        if (game == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            drawPlayer(g2, "LEFT");
            drawPlayer(g2, "RIGHT");
            drawBalls(g2);
        } catch (DOPOSoccerException e) {
            // no match started yet: only the background
        }
        drawSuddenDeathBanner(g2);
        g2.dispose();
    }

    // Domain pixels -> screen pixels (the field fills the whole view, like the background)
    private int sx(double x) throws DOPOSoccerException {
        return (int) (x * getWidth() / game.getFieldWidth());
    }

    private int sy(double y) throws DOPOSoccerException {
        return (int) (y * getHeight() / game.getFieldHeight());
    }

    // Player image (normal or kicking); the right one looks to the left. With the power active, the aura behind
    private void drawPlayer(Graphics2D g2, String side) throws DOPOSoccerException {
        // Under an effect (electrocuted, stunned...) its image; if not, kicking or normal
        java.util.List<String> states = game.getStates(side);
        String suffix = states.isEmpty()
            ? (game.isKicking(side) ? "-kick" : "")
            : stateSuffix(states.get(states.size() - 1)); // the stun goes last and wins over the effect
        String file = "assets/img/players/" + imageOf(game.getCharacter(side)) + suffix + ".png";
        Image img = new ImageIcon(file).getImage();
        int x = sx(game.getPlayerX(side));
        int y = sy(game.getPlayerY(side));
        int w = sx(game.getPlayerWidth());
        int h = sy(game.getPlayerHeight());
        if (game.hasAura(side)) {
            drawGlow(g2, x + w / 2, y + h / 2, Math.max(w, h) * 0.75f);
        }
        if ("LEFT".equals(side)) {
            g2.drawImage(img, x, y, w, h, null);
        } else {
            g2.drawImage(img, x + w, y, -w, h, null);   // negative width = flipped
        }
        // Effects on the player (electrocuted, stunned...), as the domain names them
        if (!states.isEmpty()) {
            drawPill(g2, String.join(" · ", states).toUpperCase(), x + w / 2, y - 6, RED, WHITE);
        }
    }

    // Fake balls (illusion) and the real ball; if the ball carries a power shot: trail, aura and its name
    private void drawBalls(Graphics2D g2) throws DOPOSoccerException {
        Image ball = ballImage(game.getBallName());
        int d = sx(game.getBallRadius() * 2);
        for (double[] fake : game.getFakeBalls()) {
            g2.drawImage(ball, sx(fake[0]), sy(fake[1]), d, d, null);
        }

        int x = sx(game.getBallX());
        int y = sy(game.getBallY());
        String power = game.getBallPower(); // null if it is a normal ball
        if (power != null) {
            drawTrail(g2, ball, d);
            drawGlow(g2, x + d / 2, y + d / 2, d * 0.95f);
            drawPill(g2, "TIRO " + power.toUpperCase(), x + d / 2, y - 14, AURA_PILL, LIGHT_BLUE);
            ballTrail.add(0, new Point(x, y));
            if (ballTrail.size() > TRAIL_LENGTH) ballTrail.remove(ballTrail.size() - 1);
        } else {
            ballTrail.clear();
        }
        g2.drawImage(ball, x, y, d, d, null);
    }

    // The previous positions of the ball, more transparent the older they are
    private void drawTrail(Graphics2D g2, Image ball, int d) {
        for (int i = ballTrail.size() - 1; i >= 0; i--) {
            float alpha = 0.35f * (TRAIL_LENGTH - i) / TRAIL_LENGTH;
            Graphics2D t = (Graphics2D) g2.create();
            t.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            Point p = ballTrail.get(i);
            t.drawImage(ball, p.x, p.y, d, d, null);
            t.dispose();
        }
    }

    // Light-blue light that fades out, centered in (cx, cy)
    private void drawGlow(Graphics2D g2, int cx, int cy, float radius) {
        g2.setPaint(new RadialGradientPaint(
            cx, cy, radius,
            new float[]{0.45f, 1f},
            new Color[]{AURA_LIGHT, new Color(AURA_LIGHT.getRed(), AURA_LIGHT.getGreen(), AURA_LIGHT.getBlue(), 0)}
        ));
        g2.fillOval(Math.round(cx - radius), Math.round(cy - radius), Math.round(radius * 2), Math.round(radius * 2));
    }

    // Pill with a text (power shot, effects), centered over (cx, bottom)
    private void drawPill(Graphics2D g2, String text, int cx, int bottom, Color bg, Color fg) {
        g2.setFont(luckiest.deriveFont(16f));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(text) + 32;
        int h = fm.getAscent() + 12;
        int x = cx - w / 2;
        int y = bottom - h;
        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, h, h);
        g2.setColor(fg);
        g2.drawString(text, x + 16, y + fm.getAscent() + 4);
    }

    // "¡MUERTE SÚBITA!" banner in the middle of the field, the first seconds of the sudden death
    private void drawSuddenDeathBanner(Graphics2D g2) {
        if (!suddenDeath || System.currentTimeMillis() - suddenDeathSince > SUDDEN_DEATH_BANNER_MS) return;
        String title = "¡MUERTE SÚBITA!";
        String subtitle = "EMPATE AL TERMINAR EL TIEMPO · EL PRÓXIMO GOL GANA";
        Font titleFont = luckiest.deriveFont(56f);
        Font subtitleFont = luckiest.deriveFont(18f);
        FontMetrics tm = g2.getFontMetrics(titleFont);
        FontMetrics sm = g2.getFontMetrics(subtitleFont);

        int w = Math.max(tm.stringWidth(title), sm.stringWidth(subtitle)) + 80;
        int h = tm.getAscent() + sm.getAscent() + 60;
        int x = (getWidth() - w) / 2;
        int y = (getHeight() - h) / 2;

        g2.setColor(DARK_GREEN);
        g2.fillRoundRect(x, y, w, h, 30, 30);
        g2.setColor(YELLOW);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x, y, w, h, 30, 30);

        g2.setFont(titleFont);
        g2.drawString(title, x + (w - tm.stringWidth(title)) / 2, y + 22 + tm.getAscent());
        g2.setFont(subtitleFont);
        g2.setColor(WHITE);
        g2.drawString(subtitle, x + (w - sm.stringWidth(subtitle)) / 2, y + h - 22);
    }

    // Image of a ball of the domain ("Clásico" -> assets/img/balls/classic.png)
    protected Image ballImage(String ballName) {
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

    // State of the domain -> end of the image file ("Electrocutado" -> colombia-electrocuted.png)
    private String stateSuffix(String state) {
        switch (state) {
            case "Electrocutado": return "-electrocuted";
            case "Aturdido": return "-stunned";
            case "Confusión": return "-confused";
            case "Empuje": return "-pushed";
            case "Inconsciente": return "-unconscious";
            default: return ""; // a new effect without image: the normal one
        }
    }

    // Country of the domain -> name of the image file
    protected String imageOf(String country) {
        switch (country) {
            case "Corea del Sur": return "corea-sur";
            case "Camerún": return "cameroon";
            case "España": return "spain";
            case "Japón": return "japan";
            case "Estados Unidos": return "usa";
            default: return country.toLowerCase();   // "Colombia" -> "colombia", "Argentina", "Nigeria"
        }
    }

    // Player 1 data (from the domain): info box and the code in the scoreboard
    public void setPlayer1(String name, String character, String power, String countryCode) {
        p1NameLabel.setText(name + " · " + character);
        p1Power = power;
        p1PowerLabel.setText(powerText(power, false));
        p1Code = countryCode;
        updateTeamLabels();
    }

    // Rival data (from the domain): tag ("IA" or "J2"), info box and the code in the scoreboard
    public void setPlayer2(String tag, String name, String character, String power, String countryCode) {
        rivalTag = tag;
        p2TagLabel.setText(tag);
        p2NameLabel.setText(name + " · " + character);
        p2Power = power;
        p2PowerLabel.setText(powerText(power, false));
        p2Code = countryCode;
        updateTeamLabels();
    }

    // Whether each player has its power active (aura), as the domain says
    public void setAura(boolean p1Aura, boolean p2Aura) {
        p1PowerLabel.setText(powerText(p1Power, p1Aura));
        p2PowerLabel.setText(powerText(p2Power, p2Aura));
    }

    private String powerText(String power, boolean aura) {
        return aura ? "AURA ACTIVA · TIRO: " + power : "PODER: " + power;
    }

    public String getRivalTag() {
        return rivalTag;
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

    public void setScore(int p1Goals, int p2Goals) {
        scoreLabel.setText(p1Goals + " - " + p2Goals);
    }

    // Time in seconds, shown as m:ss
    public void setTime(int seconds) {
        timeLabel.setText(formatTime(seconds));
    }

    // Any text in the time pill ("MUERTE SÚBITA", "PARTIDO 3"...)
    public void setTimeText(String text) {
        timeLabel.setText(text);
    }

    // Sudden death (domain): the banner is shown the first seconds after it starts
    public void setSuddenDeath(boolean value) {
        if (value && !suddenDeath) {
            suddenDeathSince = System.currentTimeMillis();
        }
        suddenDeath = value;
    }

    protected String formatTime(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private void updateTeamLabels() {
        p1TeamLabel.setText("(" + p1Code + ") J1");
        p2TeamLabel.setText("(" + p2Code + ") " + rivalTag);
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
        p1NameLabel = new JLabel(" ");
        p1PowerLabel = new JLabel(" ");
        return createBlock(BLUE, new JLabel("J1"), p1HpBar, p1PowerBar, p1NameLabel, p1PowerLabel, false);
    }

    protected JPanel createPlayer2Block() {
        p2HpBar = new StatBar(NORMAL_GREEN, 1.0);
        p2PowerBar = new StatBar(YELLOW, 0);
        p2TagLabel = new JLabel(" ");
        p2NameLabel = new JLabel(" ");
        p2PowerLabel = new JLabel(" ");
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
        timeLabel = createOutlinedLabel(formatTime(0), DARK_GREEN, YELLOW, 20f);

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

    // Transparent layer with a card centered (it goes in the window glass pane, over the match)
    protected JPanel createOverlay(JPanel card) {
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

        resumeBtn = createMenuButton("REANUDAR", LIGHT_BLUE, WHITE);
        saveGameBtn = createMenuButton("GUARDAR PARTIDA", ORANGE, WHITE);
        endGameBtn = createMenuButton("TERMINAR JUEGO", RED, WHITE);

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

    protected JButton createMenuButton(String text, Color color, Color textColor) {
        JButton btn = createButton(text, color, textColor, null);
        btn.setFont(luckiest.deriveFont(22f));
        btn.setBorder(BorderFactory.createEmptyBorder(14, 20, 8, 20));
        btn.setFocusable(false); // without focus, Space doesn't press it
        return btn;
    }

    // Result card: dark green with a colored border, title on top and the 2 buttons at the bottom
    // (againBtn: "REVANCHA" / "OTRA SERIE", menuBtn: "MENÚ"); the content goes in the center
    protected JPanel createResultCard(Color borderColor, JLabel title, JComponent content, String againText) {
        JPanel card = createRoundedPanel(new BorderLayout(0, 24), DARK_GREEN, borderColor, 30);
        card.setBorder(BorderFactory.createEmptyBorder(26, 44, 30, 44));

        title.setFont(luckiest.deriveFont(48f));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // room for accents

        againBtn = createMenuButton(againText, NORMAL_GREEN, DARK_GREEN);
        menuBtn = createMenuButton("MENÚ", YELLOW, DARK_GREEN);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 30, 0));
        buttons.setOpaque(false);
        buttons.add(againBtn);
        buttons.add(menuBtn);
        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonsRow.setOpaque(false);
        buttonsRow.add(buttons);
        // The buttons row gives the card its width (the height follows the content)
        buttonsRow.setPreferredSize(new Dimension(560, buttons.getPreferredSize().height));

        card.add(title, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        card.add(buttonsRow, BorderLayout.SOUTH);
        return card;
    }

    // White label of the result card
    protected JLabel createResultLabel(float size, int align) {
        JLabel label = new JLabel(" ", align);
        label.setFont(luckiest.deriveFont(size));
        label.setForeground(WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); // room for accents
        return label;
    }

    // Coin and "+points" in yellow
    protected JLabel createCoinsLabel() {
        JLabel label = new JLabel(" ", loadIcon(COIN_IMAGE, 24), SwingConstants.CENTER);
        label.setFont(luckiest.deriveFont(24f));
        label.setForeground(YELLOW);
        label.setIconTextGap(10);
        return label;
    }

    // Face of a character of the domain
    protected ImageIcon faceIcon(String country, int size) {
        return loadIcon("assets/img/players/" + imageOf(country) + ".png", size);
    }

    // Flag of a character of the domain (assets/img/flags/<country>.png)
    protected ImageIcon flagIcon(String country, int width, int height) {
        Image img = new ImageIcon("assets/img/flags/" + country + ".png").getImage();
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
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
