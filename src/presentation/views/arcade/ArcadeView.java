package presentation.views.arcade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import presentation.views.View;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * Generic view for the arcade screens (select arcade mode, select resolution...).
 * Every arcade screen is a big card with a title, a row of selectable options and a "CONTINUAR" button.
 */
public abstract class ArcadeView extends View {
    public static final String ARCADE_BACKGROUND = "assets/img/homeBackground.png";

    // Option cards colors
    protected static final Color OPTION_BG = new Color(20, 82, 12);
    protected static final Color OPTION_BORDER = new Color(32, 105, 26);
    private static final Dimension OPTION_SIZE = new Dimension(256, 460);
    private static final int TEXT_MAX_WIDTH = 200; // max width of a text line inside an option

    // Font sizes of the options texts
    protected static final float OPTION_TITLE_SIZE = 25f;
    protected static final float OPTION_TEXT_SIZE = 16f;

    public JButton continueBtn;
    private ArrayList<Option> options = new ArrayList<>();
    private Option selectedOption;

    public ArcadeView(Font luckiest) {
        super(luckiest, ARCADE_BACKGROUND);
    }

    // Builds the screen: back bar + green card with the title, the options in a row and "CONTINUAR"
    protected void prepareSelectionCard(String title) {
        // Top bar
        JPanel topBar = createBackBar();

        // Block that will contain the green card
        JPanel block = createCenterBlock(30);

        // Green card
        JPanel card = createCard(new BorderLayout(0, 25));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 35, 30));

        // Title
        JPanel titleRow = createTitleRow(title);

        // Options (all in one row, same size, centered)
        JPanel optionsGrid = new JPanel(new GridLayout(1, 0, 24, 0));
        optionsGrid.setOpaque(false);
        for (Option option : options) {
            optionsGrid.add(option);
        }

        JPanel optionsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        optionsRow.setOpaque(false);
        optionsRow.add(optionsGrid);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(optionsRow, BorderLayout.CENTER);
        card.add(createContinueRow("CONTINUAR"), BorderLayout.SOUTH);

        // Fixed width, but the height grows with the content
        card.setPreferredSize(new Dimension(880, card.getPreferredSize().height));

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    // Light green button to go to the next screen (continueBtn), centered in a row
    protected JPanel createContinueRow(String text) {
        continueBtn = createButton(text, NORMAL_GREEN, DARK_GREEN, null);
        continueBtn.setFont(luckiest.deriveFont(26f));
        continueBtn.setBorder(BorderFactory.createEmptyBorder(18, 60, 12, 60));

        JPanel continueRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        continueRow.setOpaque(false);
        continueRow.add(continueBtn);
        return continueRow;
    }

    // New selectable option (add its content and then call prepareSelectionCard)
    protected Option createOption() {
        Option option = new Option();
        options.add(option);
        if (selectedOption == null) selectOption(option); // first option selected by default
        return option;
    }

    protected void selectOption(Option option) {
        selectedOption = option;
        for (Option o : options) o.updateBadge();
        repaint();
    }

    // Index of the selected option (in creation order)
    protected int getSelectedIndex() {
        return options.indexOf(selectedOption);
    }

    // Centered text for the options, one label per line (use \n for line breaks)
    protected JPanel createOptionText(String text, float size, Color color) {
        JPanel lines = new JPanel();
        lines.setLayout(new BoxLayout(lines, BoxLayout.Y_AXIS));
        lines.setOpaque(false);
        lines.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Long lines go down automatically when they don't fit in the option
        Font font = luckiest.deriveFont(size);
        FontMetrics metrics = getFontMetrics(font);
        for (String paragraph : text.split("\n")) {
            String line = "";
            for (String word : paragraph.split(" ")) {
                String withWord = line.isEmpty() ? word : line + " " + word;
                if (!line.isEmpty() && metrics.stringWidth(withWord) > TEXT_MAX_WIDTH) {
                    lines.add(createTextLine(line, font, color));
                    line = word;
                } else {
                    line = withWord;
                }
            }
            lines.add(createTextLine(line, font, color));
        }
        lines.setMaximumSize(lines.getPreferredSize()); // so BoxLayout doesn't stretch it
        return lines;
    }

    private JLabel createTextLine(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(Math.round(font.getSize2D() / 3), 0, 0, 0)); // room for accents (Ñ, Á...)
        return label;
    }

    // Image of each option, as big as possible inside width x height but keeping its aspect ratio
    // (leaves the space empty if the image doesn't exist yet)
    protected JComponent createModeImage(String path, int width, int height) {
        Dimension size = new Dimension(width, height);
        JComponent image;
        if (new File(path).exists()) {
            Image original = new ImageIcon(path).getImage();
            double scale = Math.min(
                (double) width / original.getWidth(null),
                (double) height / original.getHeight(null)
            );
            int scaledWidth = (int) Math.round(original.getWidth(null) * scale);
            int scaledHeight = (int) Math.round(original.getHeight(null) * scale);
            Image img = original.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            image = new JLabel(new ImageIcon(img)); // centered inside its space
        } else {
            image = new JPanel();
            image.setOpaque(false);
        }
        image.setPreferredSize(size);
        image.setMaximumSize(size); // so BoxLayout doesn't stretch it
        image.setAlignmentX(Component.CENTER_ALIGNMENT);
        return image;
    }

    /**
     * Rounded option card: yellow border when selected, click to select it.
     * Its content goes from top to bottom (BoxLayout).
     */
    protected class Option extends JPanel {
        private JLabel badge;

        public Option() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setPreferredSize(OPTION_SIZE);
            setBorder(BorderFactory.createEmptyBorder(22, 15, 22, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectOption(Option.this);
                }
            });
        }

        public boolean isSelected() {
            return selectedOption == this;
        }

        // Small rounded label on top (yellow when the option is selected)
        public void addBadge(String text) {
            badge = createRoundedLabel(text, 25);
            badge.setFont(luckiest.deriveFont(20f));
            badge.setBorder(BorderFactory.createEmptyBorder(10, 26, 4, 26));
            badge.setAlignmentX(Component.CENTER_ALIGNMENT);
            updateBadge();
            add(badge);
        }

        private void updateBadge() {
            if (badge == null) return;
            badge.setBackground(isSelected() ? YELLOW : MEDIUM_GREEN);
            badge.setForeground(isSelected() ? BLACK : WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(OPTION_BG);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

            g2.setColor(isSelected() ? YELLOW : OPTION_BORDER);
            g2.setStroke(new BasicStroke(isSelected() ? 4 : 2));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 25, 25);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
