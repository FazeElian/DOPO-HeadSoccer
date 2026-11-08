package presentation.views.match;

import javax.swing.*;
import java.awt.*;
import domain.DOPOSoccer;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Survival match (player vs the AI): series of matches without a time limit.
 * Under the top bar it shows the balls left of each player and the rule; the scoreboard says "VS"
 * and the pill the number of the match. At the end, the "FIN DE LA SERIE" card.
 * Every value (balls, match, wins, points) comes from the domain.
 */
public class SurvivalMatchView extends MatchView {
    private static final int BALL_ICON_SIZE = 30;
    private static final int FACE_SIZE = 120;

    // Balls left of each player
    private JLabel p1BallsLabel;
    private JLabel p2BallsLabel;

    // "Fin de la serie" card components
    private JLabel seriesSubtitle;
    private JLabel seriesFace;
    private JLabel winsValue;
    private JLabel coinsLabel;

    public SurvivalMatchView(Font luckiest, DOPOSoccer game) {
        super(luckiest, game);
        prepareView();
    }

    @Override
    protected void prepareView() {
        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(createTopBar(), BorderLayout.NORTH);
        north.add(createBallsRow(), BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        scoreLabel.setText("VS");
        resultOverlay = createOverlay(createSeriesCard());
    }

    // Number of the match of the series, in the time pill
    public void setRound(int round) {
        setTimeText("PARTIDO " + round);
    }

    /**
     * Balls left of each player (domain): full balls for the ones left, faded for the lost ones.
     *
     * @param p1Balls   balls left of player 1
     * @param p2Balls   balls left of the rival
     * @param maxBalls  balls at the start of the series
     * @param ballImage image of the ball of the match
     */
    public void setBalls(int p1Balls, int p2Balls, int maxBalls, Image ballImage) {
        p1BallsLabel.setIcon(createBallsIcon(p1Balls, maxBalls, ballImage));
        p2BallsLabel.setIcon(createBallsIcon(p2Balls, maxBalls, ballImage));
    }

    // Image of a ball of the domain, for setBalls
    public Image getBallImage(String ballName) {
        return ballImage(ballName);
    }

    /**
     * Fills the "Fin de la serie" card with the data of the domain.
     *
     * @param round   match where the player ran out of balls
     * @param wins    matches won
     * @param country character of player 1
     * @param points  points earned
     */
    public void showSeriesResult(int round, int wins, String country, int points) {
        seriesSubtitle.setText("TE QUEDASTE SIN BALONES EN EL PARTIDO " + round);
        seriesFace.setIcon(faceIcon(country, FACE_SIZE));
        winsValue.setText(String.valueOf(wins));
        coinsLabel.setText("+" + points);
    }

    // Row under the top bar: balls of player 1, the rule in the middle and balls of the rival
    private JPanel createBallsRow() {
        p1BallsLabel = createBallsBox(BLUE);
        p2BallsLabel = createBallsBox(RED);

        JLabel rule = createRoundedLabel("SIN LÍMITE DE TIEMPO · QUIEN SE QUEDE SIN BALONES PIERDE", 30);
        rule.setFont(luckiest.deriveFont(16f));
        rule.setBackground(YELLOW);
        rule.setForeground(DARK_GREEN);
        rule.setBorder(BorderFactory.createEmptyBorder(10, 24, 5, 24));

        JPanel ruleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        ruleRow.setOpaque(false);
        ruleRow.add(rule);

        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        row.add(wrap(p1BallsLabel), BorderLayout.WEST);
        row.add(ruleRow, BorderLayout.CENTER);
        row.add(wrap(p2BallsLabel), BorderLayout.EAST);
        return row;
    }

    // "BALONES" and the balls, in a dark green box with the color of the player
    private JLabel createBallsBox(Color color) {
        JLabel label = new JLabel("BALONES") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK_GREEN);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(luckiest.deriveFont(17f));
        label.setForeground(WHITE);
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        label.setIconTextGap(18);
        label.setBorder(BorderFactory.createEmptyBorder(8, 14, 6, 14));
        return label;
    }

    // The box keeps its size inside the BorderLayout
    private JPanel wrap(JComponent c) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);
        panel.add(c);
        return panel;
    }

    // maxBalls balls in a row: the first "left" full, the rest faded
    private Icon createBallsIcon(int left, int maxBalls, Image ballImage) {
        int gap = 6;
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                for (int i = 0; i < maxBalls; i++) {
                    float alpha = i < left ? 1f : 0.25f;
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2.drawImage(ballImage, x + i * (BALL_ICON_SIZE + gap), y, BALL_ICON_SIZE, BALL_ICON_SIZE, null);
                }
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return maxBalls * BALL_ICON_SIZE + Math.max(0, maxBalls - 1) * gap;
            }

            @Override
            public int getIconHeight() {
                return BALL_ICON_SIZE;
            }
        };
    }

    // "FIN DE LA SERIE": subtitle, face of player 1 on the left, matches won and points on the right
    private JPanel createSeriesCard() {
        JLabel title = createResultLabel(48f, SwingConstants.CENTER);
        title.setText("FIN DE LA SERIE");
        title.setForeground(RED);

        seriesSubtitle = createResultLabel(17f, SwingConstants.CENTER);
        seriesFace = new JLabel("", SwingConstants.CENTER);
        seriesFace.setPreferredSize(new Dimension(FACE_SIZE, FACE_SIZE));

        JLabel winsTitle = createResultLabel(15f, SwingConstants.LEFT);
        winsTitle.setText("PARTIDOS GANADOS");
        winsValue = createResultLabel(15f, SwingConstants.RIGHT);

        JPanel wins = new JPanel(new BorderLayout(10, 0));
        wins.setOpaque(false);
        wins.add(winsTitle, BorderLayout.WEST);
        wins.add(winsValue, BorderLayout.EAST);

        coinsLabel = createCoinsLabel();

        JPanel data = new JPanel(new GridLayout(2, 1, 0, 16));
        data.setOpaque(false);
        data.add(wins);
        data.add(coinsLabel);
        JPanel dataColumn = new JPanel(new BorderLayout());
        dataColumn.setOpaque(false);
        dataColumn.add(data, BorderLayout.NORTH);

        JPanel columns = new JPanel(new GridLayout(1, 2, 30, 0));
        columns.setOpaque(false);
        columns.add(seriesFace);
        columns.add(dataColumn);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(seriesSubtitle, BorderLayout.NORTH);
        content.add(columns, BorderLayout.CENTER);

        return createResultCard(RED, title, content, "OTRA SERIE");
    }
}
