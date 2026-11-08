package presentation.views.tournament;

import javax.swing.*;
import java.awt.*;
import domain.DOPOSoccer;

import presentation.views.match.MatchView;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Match of the tournament (player vs the AI). The HUD, scoreboard, time and pause come from GameView.
 * At the end of the tournament, one result card: "¡CAMPEÓN!" (won the final) or "ELIMINADO" (lost a match),
 * both with "NUEVO TORNEO" and "MENÚ". Every value is given by the GUI (domain data).
 */
public class TournamentMatchView extends MatchView {
    private static final String TROPHY_IMAGE = "assets/img/icons/tournament/trophy.png";
    private static final int FACE_SIZE = 110;

    private JLabel resultTitle;
    private JPanel resultContent; // champion or eliminated content

    public TournamentMatchView(Font luckiest, DOPOSoccer game) {
        super(luckiest, game);
        prepareView();
    }

    @Override
    protected void prepareView() {
        add(createTopBar(), BorderLayout.NORTH);

        resultTitle = createResultLabel(48f, SwingConstants.CENTER);
        resultContent = new JPanel(new BorderLayout());
        resultContent.setOpaque(false);
        resultOverlay = createOverlay(createResultCard(YELLOW, resultTitle, resultContent, "NUEVO TORNEO"));
    }

    /**
     * "¡CAMPEÓN!": the player won the final.
     *
     * @param country    team of the player
     * @param rival      team it beat in the final
     * @param goals      goals of the player in the final
     * @param rivalGoals goals of the rival in the final
     * @param prize      points earned
     */
    public void showChampion(String country, String rival, int goals, int rivalGoals, int prize) {
        resultTitle.setText("¡CAMPEÓN!");
        resultTitle.setForeground(YELLOW);
        setResultBorderColor(YELLOW);

        JLabel trophy = new JLabel(trophyIcon(70), SwingConstants.CENTER);
        JLabel face = new JLabel(faceIcon(country, FACE_SIZE));
        JLabel flag = new JLabel(flagIcon(country, 76, 46));

        JLabel wins = createResultLabel(20f, SwingConstants.CENTER);
        wins.setText(country.toUpperCase() + " GANA EL TORNEO");
        JLabel finalScore = createResultLabel(13f, SwingConstants.CENTER);
        finalScore.setForeground(LIGHT_GREEN);
        finalScore.setText("FINAL: " + country.toUpperCase() + " " + goals + " - " + rivalGoals + " " + rival.toUpperCase());
        JLabel coins = createCoinsLabel();
        coins.setText("+" + prize);

        JPanel texts = new JPanel(new GridLayout(3, 1, 0, 8));
        texts.setOpaque(false);
        texts.add(wins);
        texts.add(finalScore);
        texts.add(coins);

        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.add(face, BorderLayout.WEST);
        row.add(texts, BorderLayout.CENTER);
        row.add(flag, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        content.add(trophy, BorderLayout.NORTH);
        content.add(row, BorderLayout.CENTER);
        setContent(content);
    }

    /**
     * "ELIMINADO": the player lost a match of the tournament.
     *
     * @param country    team of the player
     * @param rival      team that beat it
     * @param goals      goals of the player
     * @param rivalGoals goals of the rival
     * @param round      round where it lost ("CUARTOS", "SEMIFINAL", "FINAL")
     * @param wins       matches the player won
     */
    public void showEliminated(String country, String rival, int goals, int rivalGoals, String round, int wins) {
        resultTitle.setText("ELIMINADO");
        resultTitle.setForeground(RED);
        setResultBorderColor(RED);

        JLabel subtitle = createResultLabel(17f, SwingConstants.CENTER);
        subtitle.setText(rival.toUpperCase() + " TE GANÓ " + rivalGoals + " - " + goals + " EN " + withArticle(round));

        JLabel face = new JLabel(faceIcon(country, FACE_SIZE), SwingConstants.CENTER);
        JLabel roundUnderFace = createResultLabel(15f, SwingConstants.CENTER);
        roundUnderFace.setForeground(LIGHT_GREEN);
        roundUnderFace.setText(round);

        JPanel faceColumn = new JPanel(new BorderLayout(0, 6));
        faceColumn.setOpaque(false);
        faceColumn.add(face, BorderLayout.CENTER);
        faceColumn.add(roundUnderFace, BorderLayout.SOUTH);

        JPanel data = new JPanel(new GridLayout(2, 2, 10, 10));
        data.setOpaque(false);
        data.add(dataLabel("RONDA ALCANZADA", SwingConstants.LEFT));
        data.add(dataLabel(round, SwingConstants.RIGHT));
        data.add(dataLabel("PARTIDOS GANADOS", SwingConstants.LEFT));
        data.add(dataLabel(String.valueOf(wins), SwingConstants.RIGHT));
        JPanel dataColumn = new JPanel(new BorderLayout());
        dataColumn.setOpaque(false);
        dataColumn.add(data, BorderLayout.NORTH);

        JPanel columns = new JPanel(new GridLayout(1, 2, 30, 0));
        columns.setOpaque(false);
        columns.add(faceColumn);
        columns.add(dataColumn);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(subtitle, BorderLayout.NORTH);
        content.add(columns, BorderLayout.CENTER);
        setContent(content);
    }

    // "EN LOS CUARTOS", "EN LA SEMIFINAL", "EN LA FINAL"
    private String withArticle(String round) {
        return round.endsWith("S") ? "LOS " + round : "LA " + round;
    }

    private JLabel dataLabel(String text, int align) {
        JLabel label = createResultLabel(15f, align);
        label.setText(text);
        return label;
    }

    private ImageIcon trophyIcon(int size) {
        Image img = new ImageIcon(TROPHY_IMAGE).getImage();
        return new ImageIcon(img.getScaledInstance(size, size * img.getHeight(null) / img.getWidth(null), Image.SCALE_SMOOTH));
    }

    // Puts the content of the result in the card
    private void setContent(JComponent content) {
        resultContent.removeAll();
        resultContent.add(content, BorderLayout.CENTER);
        resultOverlay.revalidate();
        resultOverlay.repaint();
    }
}
