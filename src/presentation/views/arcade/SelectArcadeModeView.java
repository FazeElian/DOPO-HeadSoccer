package presentation.views.arcade;

import javax.swing.*;
import java.awt.*;

// Color constants
import static presentation.DOPOSoccerGUI.*;

public class SelectArcadeModeView extends ArcadeView {
    // AI profiles shown in the "PERFIL" menu
    public static final String[] AI_PROFILES = {"TORPE", "OFENSIVA", "DEFENSIVA", "EXPERTA"};

    private String aiProfile = AI_PROFILES[2];
    private Option vsMachineOption;
    private JPopupMenu aiProfilesMenu;
    public JButton aiProfileBtn;

    public SelectArcadeModeView(Font luckiest) {
        super(luckiest);
        prepareView();
        prepareController();
    }

    @Override
    protected void prepareView() {
        // Player vs Player
        Option vsPlayerOption = createOption();
        vsPlayerOption.add(Box.createVerticalStrut(8));
        vsPlayerOption.addBadge("PVSP");
        vsPlayerOption.add(Box.createVerticalStrut(18));
        vsPlayerOption.add(createModeImage("assets/img/arcade/player-vs-player.png", 225, 161)); // 254x176 image
        vsPlayerOption.add(Box.createVerticalStrut(18)); // margin between image and text
        vsPlayerOption.add(createOptionText("JUGADOR VS\nJUGADOR", OPTION_TITLE_SIZE, WHITE));
        vsPlayerOption.add(Box.createVerticalStrut(18));
        vsPlayerOption.add(createOptionText("DOS JUGADORES, MISMO TECLADO", OPTION_TEXT_SIZE, LIGHT_GREEN));

        // Player vs Machine
        vsMachineOption = createOption();
        vsMachineOption.add(Box.createVerticalStrut(8));
        vsMachineOption.addBadge("PVSM");
        vsMachineOption.add(Box.createVerticalStrut(18));
        vsMachineOption.add(createModeImage("assets/img/arcade/player-vs-ia.png", 225, 161)); // 254x176 image
        vsMachineOption.add(Box.createVerticalStrut(12)); // margin between image and text
        vsMachineOption.add(createOptionText("JUGADOR VS\nMÁQUINA", OPTION_TITLE_SIZE, WHITE));
        vsMachineOption.add(Box.createVerticalStrut(10));
        vsMachineOption.add(createOptionText("TÚ CONTRA LA IA", OPTION_TEXT_SIZE, LIGHT_GREEN));
        vsMachineOption.add(Box.createVerticalStrut(4));
        vsMachineOption.add(createOptionText("PERFIL DE LA IA:", OPTION_TEXT_SIZE, LIGHT_GREEN));
        vsMachineOption.add(Box.createVerticalStrut(10));
        vsMachineOption.add(createAIProfileButton());

        prepareSelectionCard("MODALIDAD");
    }

    private void prepareController() {
        aiProfileBtn.addActionListener(e -> aiProfilesMenu.show(aiProfileBtn, 0, aiProfileBtn.getHeight()));
    }

    public boolean isVsMachine() {
        return vsMachineOption.isSelected();
    }

    public String getAIProfile() {
        return aiProfile;
    }

    private void selectAIProfile(String profile) {
        aiProfile = profile;
        aiProfileBtn.setText("PERFIL: " + profile);
        selectOption(vsMachineOption); // choosing a profile means playing against the machine
    }

    // Yellow button with a menu to choose the AI profile
    private JButton createAIProfileButton() {
        aiProfileBtn = createButton("PERFIL: " + aiProfile, YELLOW, BLACK, null);
        aiProfileBtn.setFont(luckiest.deriveFont(15f));
        aiProfileBtn.setBorder(BorderFactory.createEmptyBorder(12, 16, 7, 16));
        aiProfileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiProfileBtn.setHorizontalTextPosition(SwingConstants.LEFT); // arrow on the right
        aiProfileBtn.setIconTextGap(8);
        aiProfileBtn.setIcon(createArrowIcon(10, 6, false, BLACK)); // small down arrow

        aiProfilesMenu = new JPopupMenu();
        for (String profile : AI_PROFILES) {
            JMenuItem item = new JMenuItem(profile);
            item.setFont(luckiest.deriveFont(16f));
            item.addActionListener(e -> selectAIProfile(profile));
            aiProfilesMenu.add(item);
        }
        return aiProfileBtn;
    }
}
