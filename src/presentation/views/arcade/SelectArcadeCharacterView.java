package presentation.views.arcade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Character selection. The same class is used twice:
 * rival = false: "ELIGE TU PERSONAJE" (player 1, blue panel on the left)
 * rival = true: "ELIGE TU RIVAL" (player 2 or the AI, red panel on the right)
 */
public class SelectArcadeCharacterView extends ArcadeView {
    // Flags colors
    private static final Color FLAG_YELLOW = new Color(252, 209, 22);
    private static final Color FLAG_BLUE = new Color(0, 56, 147);
    private static final Color FLAG_RED = new Color(206, 17, 38);
    private static final Color FLAG_SKY = new Color(116, 172, 223);
    private static final Color FLAG_GREEN = new Color(0, 135, 81);

    // Other colors
    private static final Color POWER_BG = new Color(167, 139, 250);
    private static final Color POWER_TEXT = new Color(40, 20, 90);
    private static final Color STAT_EMPTY = new Color(9, 45, 4);

    private static final Dimension CARD_SIZE = new Dimension(140, 180);

    // Images of the blocked characters
    private static final String COIN_IMAGE = "assets/img/coin.png";
    private static final String LOCK_IMAGE = "assets/img/lock.png";
    private static final int LOCK_SIZE = 44;
    private final Image lockImage = new ImageIcon(LOCK_IMAGE).getImage();
    private static final int MAX_STAT = 6;
    private static final String[] STATS_NAMES = {"VELOCIDAD", "SALTO", "PATADA", "VIDA"};

    // Characters: name, image, power, stats (velocidad, salto, patada, vida from 0 to 6), price (0 = unlocked) and flag
    private static final CharacterInfo[] CHARACTERS = {
        new CharacterInfo("COLOMBIA", "colombia", "ILUSIÓN", new int[]{4, 3, 4, 3}, 0,
            new Flag(false, FLAG_YELLOW, FLAG_YELLOW, FLAG_BLUE, FLAG_RED)),
        new CharacterInfo("ARGENTINA", "argentina", "TIRO DE FUEGO", new int[]{3, 3, 5, 3}, 0,
            new Flag(false, FLAG_SKY, WHITE, FLAG_SKY).circle(0.3f, FLAG_YELLOW)),
        new CharacterInfo("COREA DEL SUR", "corea-sur", "ZIGZAG", new int[]{5, 3, 3, 3}, 0,
            new Flag(false, WHITE).circle(0.55f, FLAG_RED, FLAG_BLUE)),
        new CharacterInfo("CAMERÚN", "cameroon", "MURALLA", new int[]{3, 4, 3, 5}, 0,
            new Flag(true, FLAG_GREEN, FLAG_RED, FLAG_YELLOW)),
        new CharacterInfo("NIGERIA", "nigeria", "SALTO ALTO", new int[]{4, 5, 3, 3}, 0,
            new Flag(true, FLAG_GREEN, WHITE, FLAG_GREEN)),
        new CharacterInfo("PERÚ", "peru", "CHALACA", new int[]{3, 4, 4, 3}, 0,
            new Flag(true, FLAG_RED, WHITE, FLAG_RED)),
        new CharacterInfo("ESPAÑA", "spain", "TIKI-TAKA", new int[]{4, 3, 5, 4}, 800,
            new Flag(false, FLAG_RED, FLAG_YELLOW, FLAG_YELLOW, FLAG_RED)),
        new CharacterInfo("JAPÓN", "japan", "TORNADO", new int[]{5, 4, 4, 4}, 1200,
            new Flag(false, WHITE).circle(0.55f, FLAG_RED))
    };

    private final boolean rival;
    private final Color pickColor; // blue for player 1, red for the rival
    private int selected;
    private int playerPick = -1; // only for the rival view: character chosen by player 1
    private String rivalTag = "IA";

    // Details panel components
    private JLabel badge;
    private JLabel detailFace;
    private JLabel detailFlag;
    private JLabel detailName;
    private JLabel detailPower;
    private JLabel[] detailStats = new JLabel[STATS_NAMES.length];
    private JPanel cardsGrid;

    public SelectArcadeCharacterView(Font luckiest, boolean rival) {
        super(luckiest);
        this.rival = rival;
        this.pickColor = rival ? RED : BLUE;
        this.selected = rival ? 1 : 0; // Argentina for the rival, Colombia for player 1
        prepareView();
    }

    @Override
    protected void prepareView() {
        // Top bar
        JPanel topBar = createBackBar();

        // Block that will contain the green card
        JPanel block = createCenterBlock(30);

        // Green card
        JPanel card = createCard(new BorderLayout(0, 20));
        card.setBorder(BorderFactory.createEmptyBorder(30, 35, 35, 35));

        // Body: details panel + characters column (details on the side of who is choosing)
        JPanel body = new JPanel(new BorderLayout(30, 0));
        body.setOpaque(false);
        body.add(createDetailsPanel(), rival ? BorderLayout.EAST : BorderLayout.WEST);
        body.add(createCharactersColumn(), BorderLayout.CENTER);

        card.add(createTitleRow(rival ? "ELIGE TU RIVAL" : "ELIGE TU PERSONAJE"), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);

        updateDetails();
    }

    public String getSelectedCharacter() {
        return CHARACTERS[selected].name;
    }

    // Rival view: marks the character chosen by player 1 (J1)
    public void setPlayerCharacter(String name) {
        for (int i = 0; i < CHARACTERS.length; i++) {
            if (CHARACTERS[i].name.equals(name)) playerPick = i;
        }
        cardsGrid.repaint();
    }

    // Rival view: the rival is the AI (PvsM) or player 2 (PvsP)
    public void setVsMachine(boolean vsMachine) {
        rivalTag = vsMachine ? "IA" : "J2";
        badge.setText(vsMachine ? "RIVAL · IA" : "JUGADOR 2");
        repaint();
    }

    private void selectCharacter(int index) {
        selected = index;
        updateDetails();
        cardsGrid.repaint();
    }

    // Grid with the characters, info text and the continue button
    private JPanel createCharactersColumn() {
        cardsGrid = new JPanel(new GridLayout(2, 4, 12, 12));
        cardsGrid.setOpaque(false);
        for (int i = 0; i < CHARACTERS.length; i++) {
            cardsGrid.add(new CharacterCard(i));
        }

        JLabel info = new JLabel("LOS BLOQUEADOS NO SE PUEDEN ELEGIR: SE COMPRAN EN LA TIENDA");
        info.setFont(luckiest.deriveFont(12f));
        info.setForeground(LIGHT_GREEN);
        info.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); // room for accents

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setOpaque(false);
        for (JComponent c : new JComponent[]{cardsGrid, info}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        JPanel continueRow = createContinueRow(rival ? "¡JUGAR!" : "LISTO");
        continueRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        column.add(cardsGrid);
        column.add(Box.createVerticalStrut(14));
        column.add(info);
        column.add(Box.createVerticalStrut(18));
        column.add(continueRow);
        return column;
    }

    // Panel with the selected character: face, flag, name, power and stats
    private JPanel createDetailsPanel() {
        badge = createRoundedLabel(rival ? "RIVAL · IA" : "JUGADOR 1", 22);
        badge.setFont(luckiest.deriveFont(15f));
        badge.setBackground(pickColor);
        badge.setForeground(WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(8, 20, 3, 20));

        // The border starts in the middle of the badge, so the badge is on top of it
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int top = badge.getHeight() / 2;
                g2.setColor(pickColor);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, top, getWidth() - 5, getHeight() - top - 3, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(330, 500));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        detailFace = new JLabel();
        detailFlag = new JLabel();
        detailName = new JLabel();
        detailName.setFont(luckiest.deriveFont(30f));
        detailName.setForeground(WHITE);
        detailName.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // room for accents

        detailPower = createRoundedLabel("", 25);
        detailPower.setFont(luckiest.deriveFont(15f));
        detailPower.setBackground(POWER_BG);
        detailPower.setForeground(POWER_TEXT);
        detailPower.setBorder(BorderFactory.createEmptyBorder(10, 30, 4, 30));

        // Stats: name + bar
        JPanel stats = new JPanel(new GridLayout(STATS_NAMES.length, 2, 10, 6));
        stats.setOpaque(false);
        for (int i = 0; i < STATS_NAMES.length; i++) {
            JLabel name = new JLabel(STATS_NAMES[i]);
            name.setFont(luckiest.deriveFont(14f));
            name.setForeground(WHITE);
            detailStats[i] = new JLabel();
            stats.add(name);
            stats.add(detailStats[i]);
        }

        for (JComponent c : new JComponent[]{badge, detailFace, detailFlag, detailName, detailPower, stats}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        panel.add(badge);
        panel.add(Box.createVerticalStrut(15));
        panel.add(detailFace);
        panel.add(Box.createVerticalStrut(10));
        panel.add(detailFlag);
        panel.add(detailName);
        panel.add(Box.createVerticalStrut(14));
        panel.add(detailPower);
        panel.add(Box.createVerticalStrut(20));
        panel.add(stats);
        return panel;
    }

    private void updateDetails() {
        CharacterInfo character = CHARACTERS[selected];
        detailFace.setIcon(loadFace(character, 140));
        detailFlag.setIcon(createFlagIcon(character.flag, 80, 48));
        detailName.setText(character.name);
        detailPower.setText("PODER: " + character.power);
        for (int i = 0; i < detailStats.length; i++) {
            detailStats[i].setIcon(createStatIcon(character.stats[i]));
        }
    }

    private ImageIcon loadFace(CharacterInfo character, int size) {
        Image img = new ImageIcon("assets/img/players/" + character.image + ".png").getImage();
        return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    // Flag drawn with stripes (and an optional circle in the middle)
    private Icon createFlagIcon(Flag flag, int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int n = flag.stripes.length;
                for (int i = 0; i < n; i++) {
                    g2.setColor(flag.stripes[i]);
                    if (flag.vertical) {
                        int x0 = x + i * width / n;
                        g2.fillRect(x0, y, x + (i + 1) * width / n - x0, height);
                    } else {
                        int y0 = y + i * height / n;
                        g2.fillRect(x, y0, width, y + (i + 1) * height / n - y0);
                    }
                }
                if (flag.circle.length > 0) {
                    int d = Math.round(height * flag.circleSize);
                    int cx = x + (width - d) / 2;
                    int cy = y + (height - d) / 2;
                    g2.setColor(flag.circle[0]);
                    g2.fillArc(cx, cy, d, d, 0, 180); // top half
                    g2.setColor(flag.circle[flag.circle.length - 1]);
                    g2.fillArc(cx, cy, d, d, 180, 180); // bottom half
                }
                g2.setColor(new Color(0, 0, 0, 60));
                g2.drawRect(x, y, width - 1, height - 1);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }

    // Stat bar: yellow segments for the value, dark segments for the rest
    private Icon createStatIcon(int value) {
        int segmentWidth = 18;
        int segmentHeight = 11;
        int gap = 4;
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 0; i < MAX_STAT; i++) {
                    g2.setColor(i < value ? YELLOW : STAT_EMPTY);
                    g2.fillRoundRect(x + i * (segmentWidth + gap), y, segmentWidth, segmentHeight, 5, 5);
                }
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return MAX_STAT * (segmentWidth + gap) - gap;
            }

            @Override
            public int getIconHeight() {
                return segmentHeight;
            }
        };
    }

    /**
     * Small card of a character in the grid: blue/red border and tag (J1, IA, J2) when chosen,
     * dark with a lock when it's blocked (it has a price).
     */
    private class CharacterCard extends JPanel {
        private final int index;
        private final CharacterInfo character;

        public CharacterCard(int index) {
            this.index = index;
            this.character = CHARACTERS[index];

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setPreferredSize(CARD_SIZE);
            setBorder(BorderFactory.createEmptyBorder(16, 5, 10, 5));

            JLabel face = new JLabel(loadFace(character, 88));

            // Blocked characters show the price instead of the flag
            JLabel flagOrPrice;
            if (isLocked()) {
                flagOrPrice = new JLabel(String.valueOf(character.price), loadIcon(COIN_IMAGE, 16), SwingConstants.CENTER);
                flagOrPrice.setFont(luckiest.deriveFont(15f));
                flagOrPrice.setForeground(YELLOW);
                flagOrPrice.setIconTextGap(4);
            } else {
                flagOrPrice = new JLabel(createFlagIcon(character.flag, 38, 23));
            }

            JLabel name = new JLabel(character.name);
            name.setFont(luckiest.deriveFont(14f));
            name.setForeground(WHITE);
            name.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // room for accents

            for (JComponent c : new JComponent[]{face, flagOrPrice, name}) {
                c.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
            add(face);
            add(Box.createVerticalStrut(6));
            add(flagOrPrice);
            add(Box.createVerticalStrut(4));
            add(name);

            if (!isLocked()) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        selectCharacter(CharacterCard.this.index);
                    }
                });
            }
        }

        private boolean isLocked() {
            return character.price > 0;
        }

        private boolean isPlayerPick() {
            return rival ? index == playerPick : index == selected;
        }

        private boolean isRivalPick() {
            return rival && index == selected;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(OPTION_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

            boolean picked = isPlayerPick() || isRivalPick();
            g2.setColor(isRivalPick() ? RED : isPlayerPick() ? BLUE : OPTION_BORDER);
            g2.setStroke(new BasicStroke(picked ? 4 : 2));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }

        // Tags and lock go on top of the content
        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isLocked()) {
                g2.setColor(new Color(0, 0, 0, 110));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.drawImage(lockImage, getWidth() / 2 - LOCK_SIZE / 2, 60 - LOCK_SIZE / 2, LOCK_SIZE, LOCK_SIZE, null); // over the face
            }
            if (isPlayerPick()) paintTag(g2, "J1", BLUE, false);
            if (isRivalPick()) paintTag(g2, rivalTag, RED, true);

            g2.dispose();
        }

        // Small tag on the top left (player 1) or top right (rival) corner
        private void paintTag(Graphics2D g2, String text, Color color, boolean right) {
            g2.setFont(luckiest.deriveFont(13f));
            FontMetrics metrics = g2.getFontMetrics();
            int width = metrics.stringWidth(text) + 14;
            int height = 22;
            int x = right ? getWidth() - width : 0;

            g2.setColor(color);
            g2.fillRoundRect(x, 0, width, height, 12, 12);
            g2.setColor(WHITE);
            g2.drawString(text, x + 7, height - 5);
        }
    }

    // Data of a character card
    private static class CharacterInfo {
        private final String name;
        private final String image;
        private final String power;
        private final int[] stats;
        private final int price;
        private final Flag flag;

        public CharacterInfo(String name, String image, String power, int[] stats, int price, Flag flag) {
            this.name = name;
            this.image = image;
            this.power = power;
            this.stats = stats;
            this.price = price;
            this.flag = flag;
        }
    }

    // Flag: same size stripes (horizontal or vertical) and an optional circle (1 color, or top/bottom colors)
    private static class Flag {
        private final boolean vertical;
        private final Color[] stripes;
        private float circleSize;
        private Color[] circle = {};

        public Flag(boolean vertical, Color... stripes) {
            this.vertical = vertical;
            this.stripes = stripes;
        }

        public Flag circle(float size, Color... colors) {
            this.circleSize = size;
            this.circle = colors;
            return this;
        }
    }
}
