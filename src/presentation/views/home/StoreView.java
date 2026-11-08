package presentation.views.home;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Store of the game, with three tabs: characters and balls (to buy) and upgrades (stats of each player).
 * It only paints: prices, locks, points and stats come from the domain through the GUI.
 *
 * @author Elian Ibarra
 * @author Oscar Poveda
 * @version 1.0
 */
public class StoreView extends HomeView {
    // Colors of the cards (the same of the selection screens)
    private static final Color CARD_BG = new Color(20, 82, 12);
    private static final Color CARD_BORDER = new Color(32, 105, 26);

    // Other colors
    private static final Color POWER_BG = new Color(167, 139, 250);
    private static final Color POWER_TEXT = new Color(40, 20, 90);
    private static final Color STAT_EMPTY = new Color(9, 45, 4);
    private static final Color SHADOW = new Color(0, 0, 0, 140);

    // Images
    private static final String COIN_IMAGE = "assets/img/coin.png";
    private static final String LOCK_IMAGE = "assets/img/lock.png";

    private static final Dimension CARD_SIZE = new Dimension(108, 150);
    private static final int MAX_STAT = 10;

    // Upgrades: text in the screen and stat name in the domain (action command of each "MEJORAR" button)
    private static final String[] STATS_NAMES = {"VELOCIDAD", "SALTO", "PATADA", "VIDA"};
    private static final String[] STATS = {"SPEED", "JUMP", "KICK", "SURVIVAL"};

    // Characters: name, image (assets/img/players/<image>.png), power and flag (assets/img/flags/<flag>.png)
    private static final String[][] CHARACTERS = {
        {"COLOMBIA", "colombia", "ILUSIÓN", "Colombia"},
        {"ARGENTINA", "argentina", "RAYO", "Argentina"},
        {"COREA DEL SUR", "corea-sur", "AURA AZUL", "Corea del Sur"},
        {"CAMERÚN", "cameroon", "RAYO", "Camerún"},
        {"NIGERIA", "nigeria", "TORNADO", "Nigeria"},
        {"ESTADOS UNIDOS", "usa", "ILUSIÓN", "Estados Unidos"},
        {"ESPAÑA", "spain", "AURA AZUL", "España"},
        {"JAPÓN", "japan", "TORNADO", "Japón"}
    };

    // Balls: name and image (assets/img/balls/<image>.png)
    private static final String[][] BALLS = {
        {"CLÁSICO", "classic"},
        {"TRICOLOR", "tricolor"},
        {"DORADO", "golden"},
        {"FUEGO", "fire"},
        {"HIELO", "ice"},
        {"GALAXIA", "galaxy"}
    };

    private static final String CHARACTER_NOTE = "SOLO CAMBIA EL PERSONAJE Y SU TIRO. TUS ESTADÍSTICAS SON TUYAS Y SE MEJORAN EN «MEJORAS».";
    private static final String BALL_NOTE = "DESPUÉS DE COMPRARLO LO ELIGES ANTES DE CADA PARTIDO Y SE USA EN TODOS TUS PARTIDOS.";

    // Buttons the GUI listens to
    public JButton buyCharacterBtn;
    public JButton buyBallBtn;
    public JButton[][] upgradeBtns = new JButton[2][STATS.length]; // [player 0/1][stat]

    // Prices and locks given by the domain (until then: everything locked, price 0)
    private final int[] characterPrices = new int[CHARACTERS.length];
    private final boolean[] characterUnlocked = new boolean[CHARACTERS.length];
    private final int[] ballPrices = new int[BALLS.length];
    private final boolean[] ballUnlocked = new boolean[BALLS.length];

    // Selected card of each tab
    private int selectedCharacter = 0;
    private int selectedBall = 0;

    // Header
    private JLabel pointsLabel;
    private JButton[] tabs;
    private JPanel tabsContent;
    private CardLayout tabsLayout;

    // Characters tab
    private JPanel charactersGrid;
    private JLabel characterFace;
    private JLabel characterName;
    private JLabel characterPower;
    private JLabel characterPrice;

    // Balls tab
    private JPanel ballsGrid;
    private JLabel ballImage;
    private JLabel ballName;
    private JLabel ballPrice;

    // Upgrades tab: [player][stat]
    private JLabel[] playerPoints = new JLabel[2];
    private JLabel[][] statBars = new JLabel[2][STATS.length];
    private JLabel[][] statTexts = new JLabel[2][STATS.length];

    public StoreView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    @Override
    protected void prepareView() {
        JPanel topBar = createBackBar();
        JPanel block = createCenterBlock(10);

        JPanel card = createCard(new BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(22, 30, 30, 30));

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.setOpaque(false);
        north.add(createHeader());
        north.add(Box.createVerticalStrut(18));
        north.add(createTabsRow());

        tabsLayout = new CardLayout();
        tabsContent = new JPanel(tabsLayout);
        tabsContent.setOpaque(false);
        tabsContent.add(createCharactersTab(), "0");
        tabsContent.add(createBallsTab(), "1");
        tabsContent.add(createUpgradesTab(), "2");

        card.add(north, BorderLayout.NORTH);
        card.add(tabsContent, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(1040, 640));

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);

        selectTab(0);
        selectCharacter(0);
        selectBall(0);
    }

    // ---------------------------------------------------------------- Public API (used by the GUI)

    /**
     * Shows the points of player 1 in the header.
     * @param points points of the player, or -1 if the domain can't give them yet
     */
    public void setPoints(int points) {
        pointsLabel.setText(points < 0 ? "—" : formatPoints(points));
    }

    /** @return the names of the characters, in the same order as the cards */
    public String[] getCharacterNames() {
        String[] names = new String[CHARACTERS.length];
        for (int i = 0; i < names.length; i++) names[i] = CHARACTERS[i][0];
        return names;
    }

    /** @return the names of the balls, in the same order as the cards */
    public String[] getBallNames() {
        String[] names = new String[BALLS.length];
        for (int i = 0; i < names.length; i++) names[i] = BALLS[i][0];
        return names;
    }

    /**
     * Updates the price and the lock of each character card.
     * @param prices price of each character
     * @param unlocked if the player already has each character
     */
    public void setCharacters(int[] prices, boolean[] unlocked) {
        System.arraycopy(prices, 0, characterPrices, 0, characterPrices.length);
        System.arraycopy(unlocked, 0, characterUnlocked, 0, characterUnlocked.length);
        refreshCards(charactersGrid);
        updateCharacterDetails();
    }

    /**
     * Updates the price and the lock of each ball card.
     * @param prices price of each ball
     * @param unlocked if the player already has each ball
     */
    public void setBalls(int[] prices, boolean[] unlocked) {
        System.arraycopy(prices, 0, ballPrices, 0, ballPrices.length);
        System.arraycopy(unlocked, 0, ballUnlocked, 0, ballUnlocked.length);
        refreshCards(ballsGrid);
        updateBallDetails();
    }

    /** @return the name of the selected character (as it is shown, "ESPAÑA") */
    public String getSelectedCharacter() {
        return CHARACTERS[selectedCharacter][0];
    }

    /** @return the name of the selected ball (as it is shown, "FUEGO") */
    public String getSelectedBall() {
        return BALLS[selectedBall][0];
    }

    /**
     * Shows the points and the stats of one player in the upgrades tab.
     * @param side 0 = player 1, 1 = player 2
     * @param points points of the player, or -1 if the domain can't give them yet
     * @param levels level of each stat (speed, jump, kick, survival)
     * @param nextCosts cost of the next upgrade of each stat, or -1 if it is at the max
     */
    public void setPlayerStats(int side, int points, int[] levels, int[] nextCosts) {
        playerPoints[side].setText("PUNTOS: " + (points < 0 ? "—" : formatPoints(points)));
        for (int i = 0; i < STATS.length; i++) {
            statBars[side][i].setIcon(createStatIcon(levels[i]));
            boolean max = nextCosts[i] < 0;
            statTexts[side][i].setText("NIVEL " + levels[i] + "/" + MAX_STAT + " · "
                + (max ? "MÁXIMO" : "SIGUIENTE: " + formatPoints(nextCosts[i]) + " PTS"));
            upgradeBtns[side][i].setEnabled(!max);
            upgradeBtns[side][i].setText(max ? "MÁX" : "MEJORAR");
            upgradeBtns[side][i].setBackground(max ? MEDIUM_GREEN : YELLOW);
        }
    }

    /**
     * Shows the "PUNTOS INSUFICIENTES" dialog over the store (the screen goes dark behind it).
     * @param item name of what the player wanted to buy ("JAPÓN")
     * @param price price of the item
     * @param points points of the player
     */
    public void showNotEnoughPoints(String item, int price, int points) {
        JRootPane root = SwingUtilities.getRootPane(this);
        Window window = SwingUtilities.getWindowAncestor(this);

        // Dark layer over the whole window (glass pane)
        JPanel shadow = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(SHADOW);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        shadow.setOpaque(false);
        shadow.addMouseListener(new MouseAdapter() { }); // blocks the clicks to the store
        Component oldGlass = root.getGlassPane();
        root.setGlassPane(shadow);
        shadow.setVisible(true);

        JDialog dialog = new JDialog(window, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        JButton okBtn = createButton("ENTENDIDO", YELLOW, BLACK, null);
        okBtn.addActionListener(e -> dialog.dispose());
        dialog.setContentPane(createNotEnoughPointsPanel(item, price, points, okBtn));
        dialog.pack();
        dialog.setLocationRelativeTo(window);
        dialog.setVisible(true); // waits here until "ENTENDIDO"

        shadow.setVisible(false);
        root.setGlassPane(oldGlass);
    }

    // ---------------------------------------------------------------- Header and tabs

    // Title in the middle and the player's points on the right
    private JPanel createHeader() {
        // Black pill with a yellow border: coin + points
        pointsLabel = new JLabel("—") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLACK);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.setColor(YELLOW);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pointsLabel.setOpaque(false);
        pointsLabel.setIcon(loadIcon(COIN_IMAGE, 22));
        pointsLabel.setIconTextGap(8);
        pointsLabel.setFont(luckiest.deriveFont(20f));
        pointsLabel.setForeground(YELLOW);
        pointsLabel.setBorder(BorderFactory.createEmptyBorder(9, 16, 4, 18));

        // Same width on both sides, so the title stays centered
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(160, 10));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(160, 50));
        right.add(pointsLabel);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(left, BorderLayout.WEST);
        header.add(createTitleRow("TIENDA"), BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // "PERSONAJES", "BALONES" and "MEJORAS": the selected one in yellow
    private JPanel createTabsRow() {
        String[] names = {"PERSONAJES", "BALONES", "MEJORAS"};
        tabs = new JButton[names.length];
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        row.setOpaque(false);
        for (int i = 0; i < names.length; i++) {
            int index = i;
            tabs[i] = createButton(names[i], MEDIUM_GREEN, WHITE, null);
            tabs[i].setFont(luckiest.deriveFont(18f));
            tabs[i].setBorder(BorderFactory.createEmptyBorder(12, 30, 6, 30));
            tabs[i].addActionListener(e -> selectTab(index));
            row.add(tabs[i]);
        }
        return row;
    }

    private void selectTab(int index) {
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setBackground(i == index ? YELLOW : MEDIUM_GREEN);
            tabs[i].setForeground(i == index ? BLACK : WHITE);
        }
        tabsLayout.show(tabsContent, String.valueOf(index));
    }

    // ---------------------------------------------------------------- Characters tab

    private JPanel createCharactersTab() {
        characterFace = new JLabel();
        characterName = new JLabel();
        characterPower = createRoundedLabel("", 20);
        characterPrice = new JLabel();
        buyCharacterBtn = createBuyButton();

        JPanel details = createDetailsPanel(characterFace, characterName, characterPower, CHARACTER_NOTE,
            buyCharacterBtn, characterPrice);

        charactersGrid = new JPanel(new GridLayout(1, 0, 10, 0));
        charactersGrid.setOpaque(false);
        for (int i = 0; i < CHARACTERS.length; i++) {
            charactersGrid.add(new StoreCard(i, true));
        }
        return createTab(details, charactersGrid);
    }

    private void selectCharacter(int index) {
        selectedCharacter = index;
        updateCharacterDetails();
        charactersGrid.repaint();
    }

    private void updateCharacterDetails() {
        String[] character = CHARACTERS[selectedCharacter];
        characterFace.setIcon(loadImage("assets/img/players/" + character[1] + ".png", 120, 120));
        characterName.setIcon(loadImage("assets/img/flags/" + character[3] + ".png", 48, 30));
        characterName.setText(character[0]);
        characterPower.setText("PODER: " + character[2]);
        updateBuyButton(buyCharacterBtn, characterPrice,
            characterPrices[selectedCharacter], characterUnlocked[selectedCharacter]);
    }

    // ---------------------------------------------------------------- Balls tab

    private JPanel createBallsTab() {
        ballImage = new JLabel();
        ballName = new JLabel();
        JLabel visual = createRoundedLabel("SOLO VISUAL", 20);
        ballPrice = new JLabel();
        buyBallBtn = createBuyButton();

        JPanel details = createDetailsPanel(ballImage, ballName, visual, BALL_NOTE, buyBallBtn, ballPrice);

        ballsGrid = new JPanel(new GridLayout(1, 0, 10, 0));
        ballsGrid.setOpaque(false);
        for (int i = 0; i < BALLS.length; i++) {
            ballsGrid.add(new StoreCard(i, false));
        }
        return createTab(details, ballsGrid);
    }

    private void selectBall(int index) {
        selectedBall = index;
        updateBallDetails();
        ballsGrid.repaint();
    }

    private void updateBallDetails() {
        String[] ball = BALLS[selectedBall];
        ballImage.setIcon(loadImage("assets/img/balls/" + ball[1] + ".png", 110, 110));
        ballName.setText("BALÓN " + ball[0]);
        updateBuyButton(buyBallBtn, ballPrice, ballPrices[selectedBall], ballUnlocked[selectedBall]);
    }

    // ---------------------------------------------------------------- Shared by characters and balls

    // Details panel on top and the cards in a row below
    private JPanel createTab(JPanel details, JPanel grid) {
        JPanel gridRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gridRow.setOpaque(false);
        gridRow.add(grid);

        JPanel tab = new JPanel(new BorderLayout(0, 20));
        tab.setOpaque(false);
        tab.add(details, BorderLayout.NORTH);
        tab.add(gridRow, BorderLayout.CENTER);
        return tab;
    }

    // Yellow bordered panel: image | name + tag + note | buy button + price
    private JPanel createDetailsPanel(JLabel image, JLabel name, JLabel tag, String note, JButton buyBtn, JLabel price) {
        image.setPreferredSize(new Dimension(130, 130));
        image.setHorizontalAlignment(SwingConstants.CENTER);

        name.setFont(luckiest.deriveFont(34f));
        name.setForeground(WHITE);
        name.setIconTextGap(10);
        name.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // room for accents

        tag.setFont(luckiest.deriveFont(13f));
        tag.setBackground(POWER_BG);
        tag.setForeground(POWER_TEXT);
        tag.setBorder(BorderFactory.createEmptyBorder(7, 18, 3, 18));

        JLabel noteLabel = new JLabel("<html><div style='width:380px'>" + note + "</div></html>");
        noteLabel.setFont(luckiest.deriveFont(12f));
        noteLabel.setForeground(WHITE);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        for (JComponent c : new JComponent[]{name, tag, noteLabel}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        texts.add(name);
        texts.add(Box.createVerticalStrut(8));
        texts.add(tag);
        texts.add(Box.createVerticalStrut(10));
        texts.add(noteLabel);

        JPanel textsColumn = new JPanel(new GridBagLayout()); // centers the texts vertically
        textsColumn.setOpaque(false);
        textsColumn.add(texts);

        price.setFont(luckiest.deriveFont(18f));
        price.setForeground(YELLOW);
        price.setIconTextGap(6);
        price.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buy = new JPanel();
        buy.setLayout(new BoxLayout(buy, BoxLayout.Y_AXIS));
        buy.setOpaque(false);
        buy.add(buyBtn);
        buy.add(Box.createVerticalStrut(8));
        buy.add(price);

        JPanel buyColumn = new JPanel(new GridBagLayout());
        buyColumn.setOpaque(false);
        buyColumn.setPreferredSize(new Dimension(200, 10));
        buyColumn.add(buy);

        JPanel panel = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2.setColor(YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        panel.setPreferredSize(new Dimension(10, 190));
        panel.add(image, BorderLayout.WEST);
        panel.add(textsColumn, BorderLayout.CENTER);
        panel.add(buyColumn, BorderLayout.EAST);
        return panel;
    }

    private JButton createBuyButton() {
        JButton btn = createButton("COMPRAR", YELLOW, BLACK, null);
        btn.setFont(luckiest.deriveFont(24f));
        btn.setBorder(BorderFactory.createEmptyBorder(16, 34, 10, 34));
        return btn;
    }

    // Owned: no button, only "OBTENIDO" in green. Locked: "COMPRAR" and the price under it
    private void updateBuyButton(JButton btn, JLabel price, int cost, boolean owned) {
        btn.setVisible(!owned);
        price.setText(owned ? "OBTENIDO" : formatPoints(cost));
        price.setIcon(owned ? null : loadIcon(COIN_IMAGE, 20));
        price.setForeground(owned ? NORMAL_GREEN : YELLOW);
        price.setFont(luckiest.deriveFont(owned ? 26f : 18f));
    }

    private void refreshCards(JPanel grid) {
        for (Component c : grid.getComponents()) {
            ((StoreCard) c).updateStatus();
        }
        grid.repaint();
    }

    // ---------------------------------------------------------------- Upgrades tab

    private JPanel createUpgradesTab() {
        JPanel tab = new JPanel(new GridLayout(1, 2, 24, 0));
        tab.setOpaque(false);
        tab.add(createPlayerPanel(0));
        tab.add(createPlayerPanel(1));
        return tab;
    }

    // "JUGADOR n" + its points, and a row for each stat
    private JPanel createPlayerPanel(int side) {
        JLabel title = new JLabel("JUGADOR " + (side + 1));
        title.setFont(luckiest.deriveFont(30f));
        title.setForeground(WHITE);

        playerPoints[side] = new JLabel("PUNTOS: —");
        playerPoints[side].setFont(luckiest.deriveFont(16f));
        playerPoints[side].setForeground(YELLOW);

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(title, BorderLayout.WEST);
        head.add(playerPoints[side], BorderLayout.EAST);

        JPanel rows = new JPanel(new GridLayout(STATS.length, 1, 0, 6));
        rows.setOpaque(false);
        for (int i = 0; i < STATS.length; i++) {
            rows.add(createStatRow(side, i));
        }

        JPanel panel = new JPanel(new BorderLayout(0, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2.setColor(YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 20, 24));
        panel.add(head, BorderLayout.NORTH);
        panel.add(rows, BorderLayout.CENTER);
        return panel;
    }

    // Stat name, bar, "NIVEL x/10 · SIGUIENTE: n PTS" and the "MEJORAR" button
    private JPanel createStatRow(int side, int stat) {
        JLabel name = new JLabel(STATS_NAMES[stat]);
        name.setFont(luckiest.deriveFont(16f));
        name.setForeground(WHITE);

        statBars[side][stat] = new JLabel(createStatIcon(1));

        statTexts[side][stat] = new JLabel("NIVEL 1/" + MAX_STAT);
        statTexts[side][stat].setFont(luckiest.deriveFont(11f));
        statTexts[side][stat].setForeground(LIGHT_GREEN);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        for (JComponent c : new JComponent[]{name, statBars[side][stat], statTexts[side][stat]}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        texts.add(name);
        texts.add(Box.createVerticalStrut(4));
        texts.add(statBars[side][stat]);
        texts.add(Box.createVerticalStrut(4));
        texts.add(statTexts[side][stat]);

        JButton upgradeBtn = createButton("MEJORAR", YELLOW, BLACK, null);
        upgradeBtn.setFont(luckiest.deriveFont(15f));
        upgradeBtn.setBorder(BorderFactory.createEmptyBorder(10, 18, 5, 18));
        upgradeBtn.setActionCommand(STATS[stat]); // the GUI reads which stat to upgrade
        upgradeBtns[side][stat] = upgradeBtn;

        JPanel buttonColumn = new JPanel(new GridBagLayout()); // centers the button vertically
        buttonColumn.setOpaque(false);
        buttonColumn.add(upgradeBtn);

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.add(texts, BorderLayout.CENTER);
        row.add(buttonColumn, BorderLayout.EAST);
        return row;
    }

    // Stat bar: yellow segments for the level, dark segments for the rest
    private Icon createStatIcon(int value) {
        int segmentWidth = 16;
        int segmentHeight = 12;
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

    // ---------------------------------------------------------------- "Puntos insuficientes" dialog

    private JPanel createNotEnoughPointsPanel(String item, int price, int points, JButton okBtn) {
        JLabel title = new JLabel("PUNTOS INSUFICIENTES");
        title.setFont(luckiest.deriveFont(32f));
        title.setForeground(YELLOW);
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // room for accents

        JLabel message = new JLabel("<html><div style='width:360px;text-align:center'>"
            + item + " CUESTA " + formatPoints(price) + " Y TIENES " + formatPoints(points)
            + ". JUEGA PARTIDOS PARA CONSEGUIR MÁS.</div></html>");
        message.setFont(luckiest.deriveFont(16f));
        message.setForeground(WHITE);

        JLabel missing = createRoundedLabel("FALTAN " + formatPoints(price - points), 24);
        missing.setIcon(loadIcon(COIN_IMAGE, 18));
        missing.setIconTextGap(6);
        missing.setFont(luckiest.deriveFont(18f));
        missing.setBackground(RED);
        missing.setForeground(WHITE);
        missing.setBorder(BorderFactory.createEmptyBorder(9, 20, 4, 20));

        okBtn.setFont(luckiest.deriveFont(22f));
        okBtn.setBorder(BorderFactory.createEmptyBorder(14, 40, 8, 40));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        for (JComponent c : new JComponent[]{title, message, missing, okBtn}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            content.add(c);
            content.add(Box.createVerticalStrut(16));
        }

        JPanel card = createCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(26, 36, 12, 36));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // ---------------------------------------------------------------- Helpers

    // "1.250" (with a dot every three digits)
    private static String formatPoints(int points) {
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO")).format(points);
    }

    private ImageIcon loadImage(String path, int width, int height) {
        Image img = new ImageIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // ---------------------------------------------------------------- Card of a character or a ball

    private class StoreCard extends JPanel {
        private final int index;
        private final boolean character; // true = character, false = ball
        private final JLabel status;     // "OBTENIDO" or the price

        public StoreCard(int index, boolean character) {
            this.index = index;
            this.character = character;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setPreferredSize(CARD_SIZE);
            setBorder(BorderFactory.createEmptyBorder(14, 4, 10, 4));

            String imagePath = character
                ? "assets/img/players/" + CHARACTERS[index][1] + ".png"
                : "assets/img/balls/" + BALLS[index][1] + ".png";
            JLabel image = new JLabel(loadImage(imagePath, 64, 64));
            image.setPreferredSize(new Dimension(64, 64)); // same space even if the image is missing
            image.setMaximumSize(new Dimension(64, 64));

            JLabel name = new JLabel(character ? CHARACTERS[index][0] : BALLS[index][0]);
            name.setFont(luckiest.deriveFont(11f));
            name.setForeground(WHITE);
            name.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); // room for accents

            status = createRoundedLabel("", 16);
            status.setFont(luckiest.deriveFont(11f));

            for (JComponent c : new JComponent[]{image, name, status}) {
                c.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
            add(image);
            add(name);
            add(Box.createVerticalStrut(6));
            add(status);
            updateStatus();

            // Every card can be selected (also the locked ones: to see them and buy them)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (StoreCard.this.character) {
                        selectCharacter(StoreCard.this.index);
                    } else {
                        selectBall(StoreCard.this.index);
                    }
                }
            });
        }

        private boolean isOwned() {
            return character ? characterUnlocked[index] : ballUnlocked[index];
        }

        private boolean isSelected() {
            return character ? index == selectedCharacter : index == selectedBall;
        }

        // Owned: green "OBTENIDO". Locked: coin + price in yellow
        private void updateStatus() {
            if (isOwned()) {
                status.setText("OBTENIDO");
                status.setIcon(null);
                status.setBackground(NORMAL_GREEN);
                status.setForeground(DARK_GREEN);
                status.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 10));
            } else {
                int price = character ? characterPrices[index] : ballPrices[index];
                status.setText(formatPoints(price));
                status.setIcon(loadIcon(COIN_IMAGE, 13));
                status.setIconTextGap(4);
                status.setBackground(new Color(0, 0, 0, 0));
                status.setForeground(YELLOW);
                status.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

            g2.setColor(isSelected() ? YELLOW : CARD_BORDER);
            g2.setStroke(new BasicStroke(isSelected() ? 4 : 2));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }

        // Small lock on the top right corner of the locked cards
        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            if (isOwned()) return;
            int size = 22;
            g.drawImage(new ImageIcon(LOCK_IMAGE).getImage(), getWidth() - size - 6, 6, size, size, null);
        }
    }
}