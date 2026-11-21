package presentation.views.tournament;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * "LLAVE DEL TORNEO": quarter-finals (8 teams), semi-finals (4) and final (2), joined by lines.
 * The team of the player has a blue border, the eliminated teams are faded and the places not played yet
 * say "POR DEFINIR". The teams of each round, the match type and the prize are given by the GUI (domain data).
 */
public class TournamentBracketView extends TournamentView {
    private static final Color SLOT_BG = new Color(16, 70, 10);
    private static final Color FADED_TEXT = new Color(120, 160, 120);

    private JLabel typeLabel;
    private BracketPanel bracket;

    // Teams of each round, in bracket order (null = not defined yet)
    private List<String> quarters = new ArrayList<>();
    private List<String> semis = new ArrayList<>();
    private List<String> finalists = new ArrayList<>();
    private String playerTeam = "";
    private int prize = 0;

    public TournamentBracketView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    @Override
    protected void prepareView() {
        JPanel topBar = createBackBar();
        JPanel block = createCenterBlock(6);

        JPanel card = createCard(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 40, 24, 40));

        typeLabel = createPill(" ", YELLOW, DARK_GREEN, 15f);
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        header.add(createTitleRow("LLAVE DEL TORNEO"), BorderLayout.NORTH);
        header.add(centered(typeLabel), BorderLayout.CENTER);

        bracket = new BracketPanel();

        JPanel continueRow = createContinueRow(" ");
        continueBtn.setFont(luckiest.deriveFont(22f));
        continueBtn.setBorder(BorderFactory.createEmptyBorder(16, 40, 10, 40));

        JPanel bottom = new JPanel(new BorderLayout(20, 0));
        bottom.setOpaque(false);
        bottom.add(createText("LOS DEMÁS PARTIDOS LOS SIMULA LA MÁQUINA", 12f, LIGHT_GREEN), BorderLayout.WEST);
        bottom.add(continueRow, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(bracket, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    /**
     * Teams of the bracket (from the domain), in order: each 2 consecutive teams play each other.
     *
     * @param quarters  the 8 teams of the quarter-finals
     * @param semis     the 4 teams of the semi-finals (null where it is not defined yet)
     * @param finalists the 2 teams of the final (null where it is not defined yet)
     * @param player    team of the player
     */
    public void setTeams(List<String> quarters, List<String> semis, List<String> finalists, String player) {
        this.quarters = new ArrayList<>(quarters);
        this.semis = new ArrayList<>(semis);
        this.finalists = new ArrayList<>(finalists);
        this.playerTeam = player;
        bracket.repaint();
    }

    // "TIPO: POR GOLES · META 3", "TIPO: POR TIEMPO · 60 S"
    public void setMatchType(String text) {
        typeLabel.setText("TIPO: " + text);
    }

    // Points for the champion (domain)
    public void setPrize(int prize) {
        this.prize = prize;
        bracket.repaint();
    }

    // Next round the player plays: "JUGAR SEMIFINAL"
    public void setNextRound(String roundName) {
        continueBtn.setText("JUGAR " + roundName);
    }

    // Team in a place of a round, or null
    private static String at(List<String> teams, int index) {
        return index < teams.size() ? teams.get(index) : null;
    }

    /**
     * The bracket, painted: 3 columns (quarters, semi-final, final) where each match is a pair of team slots
     * one over the other, and the lines that join two matches with the match they lead to.
     */
    private class BracketPanel extends JComponent {
        private static final int WIDTH = 1080;
        private static final int SLOT_W = 250;
        private static final int SLOT_H = 38;
        private static final int SLOT_GAP = 4;      // between the 2 teams of a match
        private static final int TOP = 40;          // under the column titles
        private static final int MATCH_STEP = 112;  // from one quarter-final match to the next
        private static final int QUARTERS_X = 2;    // 2 px of margin, so the borders are not cut
        private static final int SEMIS_X = (WIDTH - SLOT_W) / 2;
        private static final int FINAL_X = WIDTH - SLOT_W - 2;

        private final Image trophy = new ImageIcon(TROPHY_IMAGE).getImage();
        private final Image coin = new ImageIcon(COIN_IMAGE).getImage();

        public BracketPanel() {
            setPreferredSize(new Dimension(WIDTH, TOP + MATCH_STEP * 4));
        }

        // Center (y) of a quarter-final match (between its 2 teams)
        private int quarterMatchY(int match) {
            return TOP + match * MATCH_STEP + SLOT_H + SLOT_GAP / 2;
        }

        // A semi-final match sits between the 2 quarter-final matches it comes from
        private int semiMatchY(int match) {
            return (quarterMatchY(match * 2) + quarterMatchY(match * 2 + 1)) / 2;
        }

        // The final sits between the 2 semi-final matches
        private int finalMatchY() {
            return (semiMatchY(0) + semiMatchY(1)) / 2;
        }

        // Center (y) of a team slot: the first team of a match over its center, the second one under it
        private int teamY(int matchY, int team) {
            int half = SLOT_H / 2 + SLOT_GAP / 2;
            return team % 2 == 0 ? matchY - half : matchY + half;
        }

        private int quarterY(int i) {
            return teamY(quarterMatchY(i / 2), i);
        }

        private int semiY(int i) {
            return teamY(semiMatchY(i / 2), i);
        }

        private int finalY(int i) {
            return teamY(finalMatchY(), i);
        }

        // Two matches of a round joined with the match they lead to
        private void drawJoin(Graphics2D g2, int fromX, int toX, int topY, int bottomY) {
            int midX = (fromX + toX) / 2;
            g2.drawLine(fromX, topY, midX, topY);
            g2.drawLine(fromX, bottomY, midX, bottomY);
            g2.drawLine(midX, topY, midX, bottomY);
            g2.drawLine(midX, (topY + bottomY) / 2, toX, (topY + bottomY) / 2);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Column titles
            g2.setFont(luckiest.deriveFont(18f));
            g2.setColor(LIGHT_GREEN);
            drawCentered(g2, "CUARTOS", QUARTERS_X + SLOT_W / 2, 20);
            drawCentered(g2, "SEMIFINAL", SEMIS_X + SLOT_W / 2, 20);
            drawCentered(g2, "FINAL", FINAL_X + SLOT_W / 2, 20);

            // Lines: 2 quarter-final matches -> 1 semi-final match, 2 semi-final matches -> the final
            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < 2; i++) {
                drawJoin(g2, QUARTERS_X + SLOT_W, SEMIS_X, quarterMatchY(i * 2), quarterMatchY(i * 2 + 1));
            }
            drawJoin(g2, SEMIS_X + SLOT_W, FINAL_X, semiMatchY(0), semiMatchY(1));

            // Slots (a team is eliminated if the next round of its match is defined and it isn't there)
            for (int i = 0; i < 8; i++) {
                drawSlot(g2, at(quarters, i), QUARTERS_X, quarterY(i), isOut(at(quarters, i), at(semis, i / 2)));
            }
            for (int i = 0; i < 4; i++) {
                drawSlot(g2, at(semis, i), SEMIS_X, semiY(i), isOut(at(semis, i), at(finalists, i / 2)));
            }
            for (int i = 0; i < 2; i++) {
                drawSlot(g2, at(finalists, i), FINAL_X, finalY(i), false);
            }

            // Trophy over the final and the prize under it
            int trophySize = 84;
            g2.drawImage(trophy, FINAL_X + SLOT_W / 2 - trophySize / 2, finalY(0) - SLOT_H / 2 - trophySize - 24,
                trophySize, trophySize * 45 / 48, null);
            if (prize > 0) {
                g2.setFont(luckiest.deriveFont(17f));
                String text = "+" + prize + " AL CAMPEÓN";
                int w = g2.getFontMetrics().stringWidth(text) + 30;
                int x = FINAL_X + (SLOT_W - w) / 2;
                int y = finalY(1) + SLOT_H / 2 + 44;
                g2.drawImage(coin, x, y - 18, 22, 22, null);
                g2.setColor(YELLOW);
                g2.drawString(text, x + 30, y);
            }
            g2.dispose();
        }

        private boolean isOut(String team, String winner) {
            return team != null && winner != null && !team.equals(winner);
        }

        // Rounded slot: flag + name; blue border for the player, faded if eliminated, "POR DEFINIR" if empty
        private void drawSlot(Graphics2D g2, String team, int x, int centerY, boolean out) {
            int y = centerY - SLOT_H / 2;
            boolean player = team != null && team.equals(playerTeam);
            g2.setColor(SLOT_BG);
            g2.fillRoundRect(x, y, SLOT_W, SLOT_H, 12, 12);
            g2.setColor(player ? BLUE : OPTION_BORDER);
            g2.setStroke(new BasicStroke(player ? 3 : 2));
            g2.drawRoundRect(x, y, SLOT_W, SLOT_H, 12, 12);

            g2.setFont(luckiest.deriveFont(16f));
            int textY = centerY + g2.getFontMetrics().getAscent() / 2 - 1;
            if (team == null) {
                g2.setColor(FADED_TEXT);
                g2.drawString("POR DEFINIR", x + 14, textY);
                return;
            }
            Graphics2D flag = (Graphics2D) g2.create();
            if (out) flag.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            flag.drawImage(flagIcon(team, 32, 20).getImage(), x + 12, centerY - 10, null);
            flag.dispose();
            g2.setColor(out ? FADED_TEXT : WHITE);
            g2.drawString(team.toUpperCase(), x + 56, textY);
        }

        private void drawCentered(Graphics2D g2, String text, int centerX, int baseline) {
            g2.drawString(text, centerX - g2.getFontMetrics().stringWidth(text) / 2, baseline);
        }
    }
}
