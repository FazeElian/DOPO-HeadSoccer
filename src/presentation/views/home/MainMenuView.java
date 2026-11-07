package presentation.views.home;

import javax.swing.*;
import java.awt.*;

// Color constants
import static presentation.DOPOSoccerGUI.*;

public class MainMenuView extends HomeView {
    public JButton settingsBtn;
    public JButton tutorialBtn;
    public JButton storeBtn;
    public JButton playBtn;
    public JButton openGameBtn;
    public JButton exitBtn;

    public MainMenuView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    @Override
    protected void prepareView() {
        JPanel topBar = createBar(
            settingsBtn = createButton("Ajustes", DARK_GREEN, WHITE, "assets/img/icons/home/settings.png"),
            storeBtn = createButton("Tienda", YELLOW, BLACK, "assets/img/icons/home/store.png")
        );

        JLabel credits = createRoundedLabel("HECHO POR: E. IBARRA & O. POVEDA", 15);
        credits.setFont(luckiest.deriveFont(22f));
        credits.setBackground(NORMAL_GREEN);
        credits.setForeground(DARK_GREEN);
        credits.setBorder(BorderFactory.createEmptyBorder(14, 24, 8, 24));

        JPanel bottomBar = createBar(
            credits,
            tutorialBtn = createButton("¿Cómo jugar?", DARK_GREEN, WHITE, "assets/img/icons/home/help.png")
        );

        // Center block: game title, play & exit button
        JPanel center = createCenterBlock(190);

        // Game Title
        JLabel gameTitle = createRoundedLabel("POOBSOCCER", 30);
        gameTitle.setBackground(DARK_GREEN);
        gameTitle.setFont(luckiest.deriveFont(72f));
        gameTitle.setForeground(WHITE);
        gameTitle.setBorder(BorderFactory.createEmptyBorder(34, 50, 18, 50));

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 10));
        buttons.setOpaque(false);

        playBtn = createButton("JUGAR", BLUE, WHITE, null);
        playBtn.setFont(luckiest.deriveFont(35f));
        playBtn.setIcon(loadIcon("assets/img/icons/home/play.png", 30));
        playBtn.setIconTextGap(10);
        playBtn.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));

        openGameBtn = createButton("ABRIR PARTIDA", LIGHT_BLUE, WHITE, null);
        openGameBtn.setFont(luckiest.deriveFont(35f));
        openGameBtn.setIcon(loadIcon("assets/img/icons/home/open.png", 30));
        openGameBtn.setIconTextGap(10);
        openGameBtn.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));

        exitBtn = createButton("SALIR", RED, WHITE, null);
        exitBtn.setFont(luckiest.deriveFont(35f));
        exitBtn.setBorder(BorderFactory.createEmptyBorder(18, 24, 10, 24));

        buttons.add(playBtn);
        buttons.add(openGameBtn);
        buttons.add(exitBtn);

        // BorderLayout (not GridLayout) so the title keeps its own height instead of matching the buttons
        JPanel block = new JPanel(new BorderLayout(0, 15));
        block.setOpaque(false);
        block.add(gameTitle, BorderLayout.NORTH);
        block.add(buttons, BorderLayout.CENTER);

        center.add(block);

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }
}
