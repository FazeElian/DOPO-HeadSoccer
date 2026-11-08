package presentation.views.tournament;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * "TORNEO": the round being played. On top the rounds (the played ones with a check, the current one in yellow),
 * in the middle the match of the player against the AI (faces, round, match type and teams), at the bottom
 * the prize and "¡JUGAR!". The points of the player are on the top right.
 * The rounds, teams, type, prize and points are given by the GUI (domain data).
 */
public class TournamentRoundView extends TournamentView {
    private static final int FACE_SIZE = 110;
    private static final int COINS_WIDTH = 130;
    private static final Color FUTURE_TEXT = new Color(90, 140, 90);

    private JLabel pointsLabel;
    private JPanel tabsRow;
    private JLabel playerFace;
    private JLabel rivalFace;
    private JLabel roundLabel;
    private JLabel typeLabel;
    private JLabel teamsLabel;
    private JLabel prizeLabel;

    public TournamentRoundView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    @Override
    protected void prepareView() {
        JPanel topBar = createBackBar();
        JPanel block = createCenterBlock(40);

        JPanel card = createCard(new BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(24, 30, 30, 30));

        tabsRow = new JPanel(new GridLayout(1, 0, 12, 0));
        tabsRow.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);
        top.add(createHeader(), BorderLayout.NORTH);
        top.add(tabsRow, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(createMatchBox(), BorderLayout.CENTER);
        card.add(createBottomRow(), BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(760, card.getPreferredSize().height));

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    /**
     * Round being played and the match of the player (from the domain).
     *
     * @param roundNames names of the rounds ("CUARTOS", "SEMIFINAL", "FINAL")
     * @param current    index of the current round
     * @param player     team of the player
     * @param rival      team of the AI
     * @param matchType  "POR TIEMPO · 60 S", "POR GOLES · META 3"
     */
    public void setRound(List<String> roundNames, int current, String player, String rival, String matchType) {
        tabsRow.removeAll();
        for (int i = 0; i < roundNames.size(); i++) {
            tabsRow.add(createTab(roundNames.get(i), i < current, i == current));
        }
        tabsRow.revalidate();
        tabsRow.repaint();

        playerFace.setIcon(faceIcon(player, FACE_SIZE, false));
        rivalFace.setIcon(faceIcon(rival, FACE_SIZE, true));
        roundLabel.setText(roundNames.get(current));
        typeLabel.setText("TIPO: " + matchType);
        teamsLabel.setText(player.toUpperCase() + " VS " + rival.toUpperCase());
        continueBtn.setText("¡JUGAR!");
    }

    // Points of the player (domain)
    public void setPoints(int points) {
        pointsLabel.setText(String.format("%,d", points).replace(',', '.'));
    }

    // Points for the champion (domain)
    public void setPrize(int prize) {
        prizeLabel.setText("+" + prize);
    }

    // "TORNEO" in the middle and the points of the player on the right
    private JPanel createHeader() {
        pointsLabel = new JLabel(" ", loadIcon(COIN_IMAGE, 20), SwingConstants.CENTER);
        pointsLabel.setFont(luckiest.deriveFont(17f));
        pointsLabel.setForeground(YELLOW);
        pointsLabel.setIconTextGap(8);
        pointsLabel.setBorder(BorderFactory.createEmptyBorder(9, 12, 4, 12));

        JPanel coins = createBox(new BorderLayout(), DARK_GREEN, YELLOW, 18);
        coins.add(pointsLabel, BorderLayout.CENTER);
        JPanel coinsColumn = new JPanel(new BorderLayout());
        coinsColumn.setOpaque(false);
        coinsColumn.setPreferredSize(new Dimension(COINS_WIDTH, 40));
        coinsColumn.add(coins, BorderLayout.NORTH);

        // Empty space on the left as wide as the points, so the title stays centered
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(COINS_WIDTH, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(spacer, BorderLayout.WEST);
        header.add(createTitleRow("TORNEO"), BorderLayout.CENTER);
        header.add(coinsColumn, BorderLayout.EAST);
        return header;
    }

    // Tab of a round: played (check), current (yellow) or still to come (faded)
    private JPanel createTab(String name, boolean played, boolean current) {
        JLabel label = createText(name, 15f, current ? DARK_GREEN : played ? WHITE : FUTURE_TEXT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (played) {
            label.setIcon(loadIcon(CHECK_IMAGE, 16));
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            label.setIconTextGap(8);
        }
        JPanel tab = createBox(new BorderLayout(), current ? YELLOW : DARK_GREEN, current ? YELLOW : OPTION_BORDER, 14);
        tab.setBorder(BorderFactory.createEmptyBorder(4, 10, 6, 10));
        tab.add(label, BorderLayout.CENTER);
        return tab;
    }

    // Blue box: player 1 on the left, the round and "VS" in the middle, the AI on the right
    private JPanel createMatchBox() {
        JPanel box = createBox(new BorderLayout(20, 0), OPTION_BG, BLUE, 22);
        box.setBorder(BorderFactory.createEmptyBorder(16, 26, 18, 26));

        playerFace = new JLabel("", SwingConstants.CENTER);
        rivalFace = new JLabel("", SwingConstants.CENTER);

        roundLabel = createText(" ", 22f, YELLOW);
        JLabel vs = createText("VS", 54f, WHITE);
        typeLabel = createText(" ", 14f, WHITE);
        teamsLabel = createText(" ", 12f, LIGHT_GREEN);

        JPanel info = new JPanel(new GridLayout(4, 1, 0, 0));
        info.setOpaque(false);
        for (JLabel label : new JLabel[]{roundLabel, vs, typeLabel, teamsLabel}) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
            info.add(label);
        }

        box.add(createSideColumn("JUGADOR 1", BLUE, playerFace), BorderLayout.WEST);
        box.add(info, BorderLayout.CENTER);
        box.add(createSideColumn("RIVAL: IA", RED, rivalFace), BorderLayout.EAST);
        return box;
    }

    // Pill on top and the face under it
    private JPanel createSideColumn(String tag, Color color, JLabel face) {
        face.setPreferredSize(new Dimension(FACE_SIZE + 20, FACE_SIZE));
        JPanel column = new JPanel(new BorderLayout(0, 10));
        column.setOpaque(false);
        column.add(centered(createPill(tag, color, WHITE, 14f)), BorderLayout.NORTH);
        column.add(face, BorderLayout.CENTER);
        return column;
    }

    // Prize of the tournament on the left and "¡JUGAR!" on the right
    private JPanel createBottomRow() {
        JLabel trophy = new JLabel(scaledIcon(TROPHY_IMAGE, 34));
        prizeLabel = createCoinLabel(" ", 15f);

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 2));
        texts.setOpaque(false);
        texts.add(createText("PREMIO DEL TORNEO", 12f, WHITE));
        texts.add(prizeLabel);

        JPanel prize = createBox(new BorderLayout(12, 0), DARK_GREEN, OPTION_BORDER, 16);
        prize.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 22));
        prize.add(trophy, BorderLayout.WEST);
        prize.add(texts, BorderLayout.CENTER);

        JPanel continueRow = createContinueRow("¡JUGAR!");
        continueBtn.setBorder(BorderFactory.createEmptyBorder(18, 60, 12, 60));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(centered(prize), BorderLayout.WEST);
        row.add(continueRow, BorderLayout.EAST);
        return row;
    }
}
