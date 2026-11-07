package presentation.views;

import javax.swing.*;
import java.awt.*;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * General view for every screen of the game
 * Paints the background and provides the shared components builders.
 */
public abstract class View extends JPanel {
    protected Font luckiest;
    public JButton backBtn;
    private Image background;

    public View(Font luckiest, String imageUrl) {
        super(new BorderLayout());
        this.luckiest = luckiest;
        this.background = new ImageIcon(imageUrl).getImage();
        setOpaque(false);
    }

    // Each view builds its own components (call it from the subclass constructor)
    protected abstract void prepareView();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }

    // Replaces this view with another one in the same window
    public void showView(View view, String title) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setTitle("DOPOSoccer | " + title);
        frame.add(view, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // Helper methods
    protected JPanel createBar(JComponent left, JComponent right) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false); // Delete background (transparent)
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        if (left != null) bar.add(left, BorderLayout.WEST);
        if (right != null) bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // Top bar with the "Volver" button (backBtn)
    protected JPanel createBackBar() {
        backBtn = createButton("Volver", DARK_GREEN, WHITE, "assets/img/icons/back.png");
        return createBar(backBtn, null);
    }

    // Transparent block that centers its content horizontally
    protected JPanel createCenterBlock(int topPadding) {
        JPanel block = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        block.setOpaque(false);
        block.setBorder(BorderFactory.createEmptyBorder(topPadding, 0, 0, 0));
        return block;
    }

    // Rounded dark green card with a medium green border (same width as the arcade options border)
    protected JPanel createCard(LayoutManager layout) {
        JPanel card = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.setColor(MEDIUM_GREEN);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBackground(DARK_GREEN);
        return card;
    }

    // Underlined white title centered in a row
    protected JPanel createTitleRow(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(luckiest.deriveFont(42f));
        titleLabel.setForeground(WHITE);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, WHITE),
            BorderFactory.createEmptyBorder(8, 6, 6, 6)
        ));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setOpaque(false);
        titleRow.add(titleLabel);
        return titleRow;
    }

    protected JButton createButton(
        String title,
        Color bgColor,
        Color txtColor,
        String iconPath
    ) {
        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getBackground();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        if (iconPath != null) {
            btn.setIcon(loadIcon(iconPath, 18));
            btn.setIconTextGap(5); // gap between icon & text
        }

        btn.setBackground(bgColor);
        btn.setForeground(txtColor);
        btn.setFont(luckiest.deriveFont(22f));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.TOP);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(14, 24, 8, 24));

        return btn;
    }

    // Label with a rounded background (uses the label background color)
    protected JLabel createRoundedLabel(String text, int arc) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        label.setOpaque(false);
        return label;
    }

    // Small triangle icon (arrows like ▲ ▼ are not in the font)
    protected Icon createArrowIcon(int width, int height, boolean up, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                if (up) {
                    g2.fillPolygon(new int[]{x, x + width, x + width / 2}, new int[]{y + height, y + height, y}, 3);
                } else {
                    g2.fillPolygon(new int[]{x, x + width, x + width / 2}, new int[]{y, y, y + height}, 3);
                }
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

    protected ImageIcon loadIcon(String path, int size) {
        Image img = new ImageIcon(path).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
