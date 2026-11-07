package presentation.views.home;

import javax.swing.*;

import static presentation.DOPOSoccerGUI.BLACK;
import static presentation.DOPOSoccerGUI.LIGHT_GREEN;
import static presentation.DOPOSoccerGUI.MEDIUM_GREEN;

import java.awt.*;

public class SelectModeView extends HomeView {
    // Select mode components
    public JButton arcadeBtn;
    public JButton tournamentBtn;

    public SelectModeView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    @Override
    protected void prepareView() {
        // Top bar
        JPanel topBar = createBackBar();

        // Block that will contain the green card
        JPanel block = createCenterBlock(200);

        // Green card
        JPanel card = createCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(35, 45, 40, 45));

        // Title
        JPanel titleRow = createTitleRow("SELECCIONAR MODO");
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 15, 0)); // one row per button
        buttons.setOpaque(false);

        arcadeBtn = createModeButton(
            "ARCADE",
            "UN PARTIDO A TU GUSTO",
            LIGHT_GREEN,
            Color.WHITE,
            BLACK
        );
        tournamentBtn = createModeButton(
            "TORNEO",
            "LLAVE DE 8 EQUIPOS",
            MEDIUM_GREEN,
            Color.WHITE,
            Color.WHITE
        );

        buttons.add(arcadeBtn);
        buttons.add(tournamentBtn);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(buttons, BorderLayout.CENTER);

        // Fixed width, but the height grows with the content (so the buttons are never cut)
        // card.setPreferredSize(new Dimension(6, card.getPreferredSize().height));

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    private JButton createModeButton(
        String title,
        String subtitle,
        Color bgColor,
        Color titleColor,
        Color subtitleColor
    ) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setLayout(new BoxLayout(btn, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(luckiest.deriveFont(35f));
        titleLbl.setForeground(titleColor);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLbl = new JLabel(subtitle);
        subtitleLbl.setFont(luckiest.deriveFont(20f));
        subtitleLbl.setForeground(subtitleColor);
        subtitleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.add(titleLbl);
        btn.add(Box.createVerticalStrut(4));   // espacio entre los dos textos
        btn.add(subtitleLbl);

        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(35, 35, 27, 35));
        return btn;
    }
}
