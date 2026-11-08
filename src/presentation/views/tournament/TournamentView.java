package presentation.views.tournament;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import presentation.views.arcade.ArcadeView;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Generic view for the tournament screens (new tournament, bracket, round...).
 * Same field background and cards as the arcade screens, plus the images every tournament screen uses:
 * faces and flags of the characters (by their domain country), the trophy and the check.
 * The views only paint what the GUI gives them: the data comes from the domain.
 */
public abstract class TournamentView extends ArcadeView {
    protected static final String TROPHY_IMAGE = "assets/img/icons/tournament/trophy.png";
    protected static final String CHECK_IMAGE = "assets/img/icons/tournament/check.png";

    // Colors of the stat bars and the power pill (same as the character selection)
    protected static final Color POWER_BG = new Color(167, 139, 250);
    protected static final Color POWER_TEXT = new Color(40, 20, 90);
    protected static final Color STAT_EMPTY = new Color(9, 45, 4);
    protected static final int MAX_STAT = 10;

    public TournamentView(Font luckiest) {
        super(luckiest);
    }

    // Country of the domain -> name of the image file
    protected String imageOf(String country) {
        switch (country) {
            case "Corea del Sur": return "corea-sur";
            case "Camerún": return "cameroon";
            case "España": return "spain";
            case "Japón": return "japan";
            case "Estados Unidos": return "usa";
            default: return country.toLowerCase();   // "Colombia" -> "colombia", "Argentina", "Nigeria"
        }
    }

    // Face of a character; flipped = looking to the left (the rival)
    protected ImageIcon faceIcon(String country, int size, boolean flipped) {
        Image img = new ImageIcon("assets/img/players/" + imageOf(country) + ".png").getImage();
        BufferedImage face = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = face.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (flipped) {
            g2.drawImage(img, size, 0, -size, size, null);
        } else {
            g2.drawImage(img, 0, 0, size, size, null);
        }
        g2.dispose();
        return new ImageIcon(face);
    }

    // Flag of a character (assets/img/flags/<country>.png)
    protected ImageIcon flagIcon(String country, int width, int height) {
        Image img = new ImageIcon("assets/img/flags/" + country + ".png").getImage();
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    // Image keeping its aspect ratio inside a square
    protected ImageIcon scaledIcon(String path, int maxSize) {
        Image img = new ImageIcon(path).getImage();
        double scale = Math.min((double) maxSize / img.getWidth(null), (double) maxSize / img.getHeight(null));
        int w = (int) Math.round(img.getWidth(null) * scale);
        int h = (int) Math.round(img.getHeight(null) * scale);
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    // Rounded label with a background color ("TU EQUIPO", "RIVAL: IA", "TIPO: POR GOLES"...)
    protected JLabel createPill(String text, Color bgColor, Color txtColor, float size) {
        JLabel pill = createRoundedLabel(text, 22);
        pill.setFont(luckiest.deriveFont(size));
        pill.setBackground(bgColor);
        pill.setForeground(txtColor);
        pill.setHorizontalAlignment(SwingConstants.CENTER);
        pill.setBorder(BorderFactory.createEmptyBorder(8, 20, 3, 20));
        return pill;
    }

    // Coin + text in yellow ("+500", "PREMIO AL CAMPEÓN: +500")
    protected JLabel createCoinLabel(String text, float size) {
        JLabel label = new JLabel(text, loadIcon(COIN_IMAGE, Math.round(size * 1.2f)), SwingConstants.LEFT);
        label.setFont(luckiest.deriveFont(size));
        label.setForeground(YELLOW);
        label.setIconTextGap(8);
        return label;
    }

    // Label with a text in the font
    protected JLabel createText(String text, float size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(luckiest.deriveFont(size));
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(Math.round(size / 3), 0, 0, 0)); // room for accents
        return label;
    }

    // Panel with a centered component (FlowLayout keeps its preferred size)
    protected JPanel centered(JComponent c) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);
        panel.add(c);
        return panel;
    }

    // Stat bar: yellow segments for the value, dark segments for the rest
    protected Icon createStatIcon(int value) {
        int segmentWidth = 9;
        int segmentHeight = 11;
        int gap = 3;
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

    // Dark green box with a rounded border of a color (null = no border)
    protected JPanel createBox(LayoutManager layout, Color bgColor, Color borderColor, int arc) {
        JPanel box = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
                if (borderColor != null) {
                    g2.setColor(borderColor);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        box.setOpaque(false);
        return box;
    }
}
