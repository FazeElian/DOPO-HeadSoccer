package presentation.views.match;

import javax.swing.*;
import java.awt.*;
import domain.DOPOSoccer;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Arcade match (player vs player or vs the AI), by time or by goals.
 * The HUD, scoreboard, time and pause come from GameView; this view builds its screen
 * and the result card ("¡VICTORIA!", score, winner, goals and points), filled with the domain data.
 */
public class ArcadeMatchView extends GameView {
    private static final int FACE_SIZE = 130;

    // Result card components
    private JLabel resultTitle;
    private JLabel resultScore;
    private JLabel winnerFace;
    private JLabel winnerFlag;
    private JLabel winnerName;
    private JLabel matchTypeValue;
    private JLabel p1GoalsTitle;
    private JLabel p1GoalsValue;
    private JLabel p2GoalsTitle;
    private JLabel p2GoalsValue;
    private JLabel coinsLabel;

    public ArcadeMatchView(Font luckiest, DOPOSoccer game) {
        super(luckiest, game);
        prepareView();
    }

    @Override
    protected void prepareView() {
        // Top bar: player 1 HUD, scoreboard + time + pause and rival HUD
        add(createTopBar(), BorderLayout.NORTH);
        resultOverlay = createOverlay(createArcadeResultCard());
    }

    /**
     * Fills the result card with the data of the domain.
     *
     * @param title        "¡VICTORIA!", "DERROTA", "¡GANA J2!"...
     * @param titleColor   color of the title
     * @param p1Goals      goals of player 1
     * @param p2Goals      goals of the rival
     * @param winner       country of the winner, or null if nobody won
     * @param matchType    "POR TIEMPO" or "POR GOLES"
     * @param points       points earned
     */
    public void showResult(String title, Color titleColor, int p1Goals, int p2Goals,
                           String winner, String matchType, int points) {
        resultTitle.setText(title);
        resultTitle.setForeground(titleColor);
        resultScore.setText(p1Goals + " - " + p2Goals);
        winnerFace.setIcon(winner == null ? null : faceIcon(winner, FACE_SIZE));
        winnerFlag.setIcon(winner == null ? null : flagIcon(winner, 72, 44));
        winnerName.setText(winner == null ? "SIN GANADOR" : "GANA " + winner.toUpperCase());
        matchTypeValue.setText(matchType);
        p1GoalsTitle.setText("GOLES J1");
        p1GoalsValue.setText(String.valueOf(p1Goals));
        p2GoalsTitle.setText("GOLES " + getRivalTag());
        p2GoalsValue.setText(String.valueOf(p2Goals));
        coinsLabel.setText("+" + points);
    }

    // "¡VICTORIA!" on top; winner (face, flag, name) on the left; score, data and points on the right
    private JPanel createArcadeResultCard() {
        resultTitle = createResultLabel(48f, SwingConstants.CENTER);

        // Winner column
        winnerFace = new JLabel("", SwingConstants.CENTER);
        winnerFace.setPreferredSize(new Dimension(FACE_SIZE, FACE_SIZE));
        winnerFlag = new JLabel("", SwingConstants.CENTER);
        winnerFlag.setPreferredSize(new Dimension(72, 44));
        winnerName = createResultLabel(20f, SwingConstants.CENTER);

        JPanel flagAndName = new JPanel(new GridLayout(2, 1, 0, 8));
        flagAndName.setOpaque(false);
        flagAndName.add(winnerFlag);
        flagAndName.add(winnerName);

        JPanel winnerColumn = new JPanel(new BorderLayout(0, 8));
        winnerColumn.setOpaque(false);
        winnerColumn.add(winnerFace, BorderLayout.CENTER);
        winnerColumn.add(flagAndName, BorderLayout.SOUTH);

        // Data column
        resultScore = createResultLabel(56f, SwingConstants.CENTER);
        matchTypeValue = createResultLabel(15f, SwingConstants.RIGHT);
        p1GoalsTitle = createResultLabel(15f, SwingConstants.LEFT);
        p1GoalsValue = createResultLabel(15f, SwingConstants.RIGHT);
        p2GoalsTitle = createResultLabel(15f, SwingConstants.LEFT);
        p2GoalsValue = createResultLabel(15f, SwingConstants.RIGHT);
        JLabel matchTypeTitle = createResultLabel(15f, SwingConstants.LEFT);
        matchTypeTitle.setText("TIPO DE PARTIDO");

        JPanel data = new JPanel(new GridLayout(3, 2, 10, 8));
        data.setOpaque(false);
        data.add(matchTypeTitle);
        data.add(matchTypeValue);
        data.add(p1GoalsTitle);
        data.add(p1GoalsValue);
        data.add(p2GoalsTitle);
        data.add(p2GoalsValue);

        coinsLabel = createCoinsLabel();

        JPanel dataColumn = new JPanel(new BorderLayout(0, 16));
        dataColumn.setOpaque(false);
        dataColumn.add(resultScore, BorderLayout.NORTH);
        dataColumn.add(data, BorderLayout.CENTER);
        dataColumn.add(coinsLabel, BorderLayout.SOUTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 30, 0));
        content.setOpaque(false);
        content.add(winnerColumn);
        content.add(dataColumn);

        return createResultCard(YELLOW, resultTitle, content, "REVANCHA");
    }
}
