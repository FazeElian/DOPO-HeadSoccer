package presentation.views.home;

import javax.swing.*;
import java.awt.*;

// Color constants
import static presentation.DOPOSoccerGUI.*;

public class TutorialView extends HomeView {

    public TutorialView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    @Override
    protected void prepareView() {
        // Top bar
        JPanel topBar = createBackBar();

        // Block that will contain the green card
        JPanel block = createCenterBlock(0);

        // Green card
        JPanel card = createCard(new BorderLayout(0, 30));
        card.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        // Title
        JPanel titleRow = createTitleRow("¿CÓMO JUGAR?");

        // Block for both players controls
        JPanel controls = new JPanel(new GridLayout(1, 2, 50, 0)); // 1 row, 2 columns, 50px between them
        controls.setOpaque(false);
        controls.add(createPlayerControls("JUGADOR 1", BLUE, "assets/img/controls-player-1.png"));
        controls.add(createPlayerControls("JUGADOR 2", RED, "assets/img/controls-player-2.png"));

        card.add(titleRow, BorderLayout.NORTH);
        card.add(controls, BorderLayout.CENTER);
        card.add(createRules(), BorderLayout.SOUTH);

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    // Player block: colored title + controls image
    private JPanel createPlayerControls(String player, Color color, String imagePath) {
        JPanel playerControls = new JPanel(new BorderLayout(0, 20)); // 20px of vertical gap
        playerControls.setOpaque(false);

        JButton playerTitle = createButton(player, color, WHITE, null);
        playerTitle.setFont(luckiest.deriveFont(19f));
        playerTitle.setBorder(BorderFactory.createEmptyBorder(14, 0, 12, 0));
        playerTitle.setCursor(Cursor.getDefaultCursor()); // it's only a title, not clickable
        playerTitle.setFocusable(false);

        Image img = new ImageIcon(imagePath).getImage().getScaledInstance(250, 231, Image.SCALE_SMOOTH);
        JLabel imgLabel = new JLabel(new ImageIcon(img));

        playerControls.add(playerTitle, BorderLayout.NORTH);
        playerControls.add(imgLabel, BorderLayout.CENTER);
        return playerControls;
    }

    // Rules block
    private JPanel createRules() {
        JPanel rules = new JPanel(new GridLayout(5, 1)) { // 5 rows, 1 column
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.setColor(YELLOW);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);

                g2.dispose();
            }
        };
        rules.setOpaque(false);
        rules.setBackground(MEDIUM_GREEN);
        rules.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel rulesTitle = new JLabel("REGLAS CLAVE", SwingConstants.CENTER);
        rulesTitle.setForeground(YELLOW);
        rulesTitle.setFont(luckiest.deriveFont(20f));
        rulesTitle.setBorder(BorderFactory.createEmptyBorder(7, 15, 7, 15));
        rules.add(rulesTitle);

        String[] items = {
            "• LA BARRA DE PODER SE LLENA CON EL TIEMPO. LLENA: PRESIONA PODER Y GOLPEA EL BALÓN.",
            "• SUPERTIRO: SI TE LLEGA, PATEA JUSTO A TIEMPO PARA DEVOLVERLO.",
            "• PATADAS Y PODERES DEL RIVAL TE ATURDEN 3 SEGUNDOS.",
            "• SI HAY EMPATE AL TERMINAR EL TIEMPO: ¡GOL DE ORO!"
        };
        for (String rule : items) {
            JLabel newItem = new JLabel(rule);
            newItem.setForeground(WHITE);
            newItem.setFont(luckiest.deriveFont(15f));
            rules.add(newItem);
        }
        return rules;
    }
}
