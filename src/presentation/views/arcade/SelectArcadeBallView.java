package presentation.views.arcade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Ball selection: the ball in use on the left, the balls grid on the right.
 * The ball is only visual (it doesn't change the physics).
 */
public class SelectArcadeBallView extends ArcadeView {
    private static final Dimension CARD_SIZE = new Dimension(190, 185);
    private static final int CARD_BALL_SIZE = 86;
    private static final int DETAIL_BALL_SIZE = 170;

    // Balls: name, image (assets/img/balls/<image>.png) and price (0 = obtained)
    private static final BallInfo[] BALLS = {
        new BallInfo("CLÁSICO", "classic"),
        new BallInfo("TRICOLOR", "tricolor"),
        new BallInfo("DORADO", "golden"),
        new BallInfo("FUEGO", "fire"),
        new BallInfo("HIELO", "ice"),
        new BallInfo("GALAXIA", "galaxy")
    };

    private int selected = 1; // Tricolor by default

    // Details panel components
    private JLabel detailBall;
    private JLabel detailName;
    private JPanel ballsGrid;

    // Who is told when a locked ball is pressed (the GUI, to ask the domain)
    private ActionListener lockedListener;

    
    // Price and lock of each ball for player 1, given by the domain
    private final int[] prices = new int[BALLS.length];
    private final boolean[] unlocked = new boolean[BALLS.length];

    public SelectArcadeBallView(Font luckiest) {
        super(luckiest);
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

        // Body: ball in use + balls column
        ballsGrid = new JPanel(new GridLayout(2, 3, 14, 14));
        ballsGrid.setOpaque(false);
        for (int i = 0; i < BALLS.length; i++) {
            ballsGrid.add(new BallCard(i));
        }

        JPanel body = new JPanel(new BorderLayout(30, 0));
        body.setOpaque(false);
        body.add(createDetailsPanel(), BorderLayout.WEST);
        body.add(createGridColumn(ballsGrid, "LOS BALONES BLOQUEADOS NO SE PUEDEN ELEGIR: SE COMPRAN EN LA TIENDA", "¡JUGAR!"), BorderLayout.CENTER);

        card.add(createTitleRow("ELIGE TU BALÓN"), BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);

        updateDetails();
    }

    // The action command of the event is the name of the locked ball ("DORADO")
    public void setLockedListener(ActionListener listener) {
        lockedListener = listener;
    }

    public String getSelectedBall() {
        return BALLS[selected].name;
    }

        // Names of the balls, in the order of the grid
    public String[] getNames() {
        String[] names = new String[BALLS.length];
        for (int i = 0; i < BALLS.length; i++) names[i] = BALLS[i].name;
        return names;
    }

    // Price and lock of each ball (same order as getNames), from the domain
    public void setAvailability(int[] newPrices, boolean[] newUnlocked) {
        for (int i = 0; i < BALLS.length; i++) {
            prices[i] = newPrices[i];
            unlocked[i] = newUnlocked[i];
        }
        ballsGrid.removeAll();
        for (int i = 0; i < BALLS.length; i++) ballsGrid.add(new BallCard(i));
        ballsGrid.revalidate();
        ballsGrid.repaint();
    }

    private void selectBall(int index) {
        selected = index;
        updateDetails();
        for (Component card : ballsGrid.getComponents()) {
            ((BallCard) card).updateStatus();
        }
        ballsGrid.repaint();
    }

    // Panel with the ball in use: big ball, name and where it is used
    private JPanel createDetailsPanel() {
        JLabel badge = createBadge("BALÓN EN USO", YELLOW, BLACK);
        JPanel panel = createBadgedPanel(badge, YELLOW, new Dimension(330, 470));

        detailBall = new JLabel();
        detailName = new JLabel();
        detailName.setFont(luckiest.deriveFont(32f));
        detailName.setForeground(WHITE);
        detailName.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // room for accents

        JLabel usedIn = createRoundedLabel("SE USA EN TODOS TUS PARTIDOS", 25);
        usedIn.setFont(luckiest.deriveFont(13f));
        usedIn.setBackground(DARK_GREEN);
        usedIn.setForeground(WHITE);
        usedIn.setBorder(BorderFactory.createEmptyBorder(10, 26, 5, 26));

        JLabel modes = new JLabel("ARCADE · SUPERVIVENCIA · TORNEO");
        modes.setFont(luckiest.deriveFont(13f));
        modes.setForeground(LIGHT_GREEN);

        JLabel onlyVisual = new JLabel("(SOLO VISUAL: NO CAMBIA LA FÍSICA)");
        onlyVisual.setFont(luckiest.deriveFont(12f));
        onlyVisual.setForeground(LIGHT_GREEN);
        onlyVisual.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); // room for accents

        for (JComponent c : new JComponent[]{detailBall, detailName, usedIn, modes, onlyVisual}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        panel.add(Box.createVerticalStrut(10));
        panel.add(detailBall);
        panel.add(detailName);
        panel.add(Box.createVerticalStrut(20));
        panel.add(usedIn);
        panel.add(Box.createVerticalStrut(16));
        panel.add(modes);
        panel.add(Box.createVerticalStrut(6));
        panel.add(onlyVisual);
        return panel;
    }

    private void updateDetails() {
        BallInfo ball = BALLS[selected];
        detailBall.setIcon(createBallIcon(ball, DETAIL_BALL_SIZE, true));
        detailName.setText(ball.name);
    }

    // Ball image (assets/img/balls/<image>.png), with a yellow glow behind for the big one
    private Icon createBallIcon(BallInfo ball, int size, boolean glow) {
        Image image = new ImageIcon("assets/img/balls/" + ball.image + ".png").getImage();
        int glowSize = glow ? size / 5 : 0; // space around the ball for the glow

        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (glow) { // yellow light behind the ball
                    float radius = size / 2f + glowSize;
                    g2.setPaint(new RadialGradientPaint(
                        x + radius, y + radius, radius,
                        new float[]{0.6f, 1f},
                        new Color[]{new Color(252, 209, 22, 150), new Color(252, 209, 22, 0)}
                    ));
                    g2.fillOval(x, y, Math.round(radius * 2), Math.round(radius * 2));
                }

                g2.drawImage(image, x + glowSize, y + glowSize, size, size, null);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size + glowSize * 2;
            }

            @Override
            public int getIconHeight() {
                return size + glowSize * 2;
            }
        };
    }

    /**
     * Small card of a ball in the grid: "OBTENIDO", "EN USO" (yellow border) or blocked with a lock and its price.
     */
    private class BallCard extends JPanel {
        private final int index;
        private final BallInfo ball;
        private JLabel status;

        public BallCard(int index) {
            this.index = index;
            this.ball = BALLS[index];

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setPreferredSize(CARD_SIZE);
            setBorder(BorderFactory.createEmptyBorder(14, 5, 12, 5));

            JLabel ballLabel = new JLabel(createBallIcon(ball, CARD_BALL_SIZE, false));

            JLabel name = new JLabel(ball.name);
            name.setFont(luckiest.deriveFont(16f));
            name.setForeground(WHITE);
            name.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); // room for accents

            // Blocked balls show the price, the others "OBTENIDO" / "EN USO"
            JLabel statusOrPrice;
            if (isLocked()) {
                statusOrPrice = createPriceLabel(prices[index]);
            } else {
                status = createRoundedLabel("", 22);
                status.setFont(luckiest.deriveFont(12f));
                status.setForeground(DARK_GREEN);
                status.setBorder(BorderFactory.createEmptyBorder(7, 20, 3, 20));
                statusOrPrice = status;
            }

            for (JComponent c : new JComponent[]{ballLabel, name, statusOrPrice}) {
                c.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
            add(ballLabel);
            add(Box.createVerticalStrut(4));
            add(name);
            add(Box.createVerticalStrut(10));
            add(statusOrPrice);
            updateStatus();

            // Free: it is chosen. Locked: the GUI asks the domain to buy it (the store, under construction)
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!isLocked()) {
                        selectBall(BallCard.this.index);
                    } else if (lockedListener != null) {
                        lockedListener.actionPerformed(new ActionEvent(BallCard.this, ActionEvent.ACTION_PERFORMED, ball.name));
                    }
                }
            });
        }

        private boolean isLocked() {
            return !unlocked[index];
        }

        private boolean isSelected() {
            return index == selected;
        }

        // "EN USO" in yellow for the selected ball, "OBTENIDO" in green for the others
        private void updateStatus() {
            if (status == null) return;
            status.setText(isSelected() ? "EN USO" : "OBTENIDO");
            status.setBackground(isSelected() ? YELLOW : NORMAL_GREEN);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(OPTION_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

            g2.setColor(isSelected() ? YELLOW : OPTION_BORDER);
            g2.setStroke(new BasicStroke(isSelected() ? 4 : 2));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }

        // Lock over the ball for the blocked ones
        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            if (isLocked()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paintLocked(g2, this, 14 + CARD_BALL_SIZE / 2, 20);
                g2.dispose();
            }
        }
    }

    // Data of a ball card
    private static class BallInfo {
        private final String name;
        private final String image;

        public BallInfo(String name, String image) {
            this.name = name;
            this.image = image;
        }
    }
}
