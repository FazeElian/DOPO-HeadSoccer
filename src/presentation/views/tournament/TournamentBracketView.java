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
        JPanel block = createCenterBlock(20);

        JPanel card = createCard(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createEmptyBorder(24, 35, 30, 35));

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
     * The bracket, painted: 3 columns of team slots and the lines that join each match with the next round.
     */
    private class BracketPanel extends JComponent {
        private static final int SLOT_W = 200;
        private static final int SLOT_H = 30;
        private static final int TOP = 36;          // under the column titles
        private static final int PAIR_STEP = 78;    // from one quarter-final match to the next
        private static final int QUARTERS_X = 0;
        private static final int SEMIS_X = 300;
        private static final int FINAL_X = 590;

        private final Image trophy = new ImageIcon(TROPHY_IMAGE).getImage();
        private final Image coin = new ImageIcon(COIN_IMAGE).getImage();

        public BracketPanel() {
            setPreferredSize(new Dimension(FINAL_X + SLOT_W, TOP + PAIR_STEP * 4));
        }

        // Center (y) of the slot of a quarter-final team
        private int quarterY(int i) {
            return TOP + (i / 2) * PAIR_STEP + (i % 2) * (SLOT_H + 4) + SLOT_H / 2;
        }

        // A semi-final team sits at the height of the quarter-final match it comes from
        private int semiY(int i) {
            return (quarterY(i * 2) + quarterY(i * 2 + 1)) / 2;
        }

        // The 2 finalists, together in the middle
        private int finalY(int i) {
            int middle = (semiY(0) + semiY(3)) / 2;
            return middle + (i == 0 ? -(SLOT_H / 2 + 2) : SLOT_H / 2 + 2);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Column titles
            g2.setFont(luckiest.deriveFont(16f));
            g2.setColor(LIGHT_GREEN);
            drawCentered(g2, "CUARTOS", QUARTERS_X + SLOT_W / 2, 18);
            drawCentered(g2, "SEMIFINAL", SEMIS_X + SLOT_W / 2, 18);
            drawCentered(g2, "FINAL", FINAL_X + SLOT_W / 2, 18);

            // Lines: quarter-final match -> semi-final place, semi-final match -> finalist
            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < 4; i++) {
                int x = QUARTERS_X + SLOT_W;
                int midX = (x + SEMIS_X) / 2;
                g2.drawLine(x, quarterY(i * 2), midX, quarterY(i * 2));
                g2.drawLine(x, quarterY(i * 2 + 1), midX, quarterY(i * 2 + 1));
                g2.drawLine(midX, quarterY(i * 2), midX, quarterY(i * 2 + 1));
                g2.drawLine(midX, semiY(i), SEMIS_X, semiY(i));
            }
            for (int i = 0; i < 2; i++) {
                int x = SEMIS_X + SLOT_W;
                int midX = (x + FINAL_X) / 2;
                g2.drawLine(x, semiY(i * 2), midX, semiY(i * 2));
                g2.drawLine(x, semiY(i * 2 + 1), midX, semiY(i * 2 + 1));
                g2.drawLine(midX, semiY(i * 2), midX, semiY(i * 2 + 1));
                int fromY = (semiY(i * 2) + semiY(i * 2 + 1)) / 2;
                g2.drawLine(midX, fromY, midX + 14, fromY);
                g2.drawLine(midX + 14, fromY, midX + 14, finalY(i));
                g2.drawLine(midX + 14, finalY(i), FINAL_X, finalY(i));
            }

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
            int trophySize = 64;
            g2.drawImage(trophy, FINAL_X + SLOT_W / 2 - trophySize / 2, finalY(0) - SLOT_H - trophySize - 6,
                trophySize, trophySize * 45 / 48, null);
            if (prize > 0) {
                g2.setFont(luckiest.deriveFont(15f));
                String text = "+" + prize + " AL CAMPEÓN";
                int w = g2.getFontMetrics().stringWidth(text) + 26;
                int x = FINAL_X + (SLOT_W - w) / 2;
                int y = finalY(1) + SLOT_H + 20;
                g2.drawImage(coin, x, y - 16, 20, 20, null);
                g2.setColor(YELLOW);
                g2.drawString(text, x + 26, y);
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
            g2.fillRoundRect(x, y, SLOT_W, SLOT_H, 10, 10);
            g2.setColor(player ? BLUE : OPTION_BORDER);
            g2.setStroke(new BasicStroke(player ? 3 : 2));
            g2.drawRoundRect(x, y, SLOT_W, SLOT_H, 10, 10);

            g2.setFont(luckiest.deriveFont(13f));
            int textY = centerY + g2.getFontMetrics().getAscent() / 2 - 1;
            if (team == null) {
                g2.setColor(FADED_TEXT);
                g2.drawString("POR DEFINIR", x + 12, textY);
                return;
            }
            Graphics2D flag = (Graphics2D) g2.create();
            if (out) flag.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            flag.drawImage(flagIcon(team, 26, 16).getImage(), x + 10, centerY - 8, null);
            flag.dispose();
            g2.setColor(out ? FADED_TEXT : WHITE);
            g2.drawString(team.toUpperCase(), x + 46, textY);
        }

        private void drawCentered(Graphics2D g2, String text, int centerX, int baseline) {
            g2.drawString(text, centerX - g2.getFontMetrics().stringWidth(text) / 2, baseline);
        }
    }
}
