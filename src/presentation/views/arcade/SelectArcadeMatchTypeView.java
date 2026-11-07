package presentation.views.arcade;

import javax.swing.*;
import java.awt.*;

// Color constants
import static presentation.DOPOSoccerGUI.*;

public class SelectArcadeMatchTypeView extends ArcadeView {
    // Match settings
    public static final int MIN_MINUTES = 1;
    public static final int MAX_MINUTES = 10;
    public static final int[] GOAL_TARGETS = {3, 5, 7};

    private int minutes = 3;
    private int goalTarget = 5;

    private Option byTimeOption;
    private Option byGoalsOption;
    private Option survivalOption;

    // Controls components
    private JLabel minutesLabel;
    public JButton minutesUpBtn;
    public JButton minutesDownBtn;
    public JButton[] goalBtns = new JButton[GOAL_TARGETS.length];

    public SelectArcadeMatchTypeView(Font luckiest) {
        super(luckiest);
        prepareView();
        prepareController();
    }

    @Override
    protected void prepareView() {
        byTimeOption = createMatchOption(
            "POR TIEMPO",
            "assets/img/arcade/clock.png",
            "LOS MINUTOS QUE ELIJAS. GANA QUIEN TENGA MÁS GOLES. SI HAY EMPATE: ¡MUERTE SÚBITA!"
        );
        addBottomControl(byTimeOption, createMinutesControl());

        byGoalsOption = createMatchOption(
            "POR GOLES",
            "assets/img/arcade/goals.png",
            "GANA EL PRIMERO EN LLEGAR A LA META. SIN LÍMITE DE TIEMPO."
        );
        addBottomControl(byGoalsOption, createGoalsControl());

        survivalOption = createMatchOption(
            "SUPERVIVENCIA",
            "assets/img/arcade/supervivence.png",
            "SERIE DE PARTIDOS CONTRA LA IA. CADA GOL LE QUITA UN BALÓN AL RIVAL."
        );
        addBottomControl(survivalOption, createSurvivalInfo());

        selectOption(byGoalsOption); // recommended type (default value)
        prepareSelectionCard("TIPO DE PARTIDO");
    }

    private void prepareController() {
        minutesUpBtn.addActionListener(e -> changeMinutes(1));
        minutesDownBtn.addActionListener(e -> changeMinutes(-1));
        for (int i = 0; i < GOAL_TARGETS.length; i++) {
            int goals = GOAL_TARGETS[i];
            goalBtns[i].addActionListener(e -> selectGoalTarget(goals));
        }
    }

    public boolean isByTime() {
        return byTimeOption.isSelected();
    }

    public boolean isByGoals() {
        return byGoalsOption.isSelected();
    }

    public boolean isSurvival() {
        return survivalOption.isSelected();
    }

    public int getMinutes() {
        return minutes;
    }

    public int getGoalTarget() {
        return goalTarget;
    }

    // Survival is only against the machine: in PvsP it's locked (and if it was chosen, goes back to "POR GOLES")
    public void setVsMachine(boolean vsMachine) {
        survivalOption.setLocked(vsMachine ? null : "SOLO DISPONIBLE EN\nJUGADOR VS MÁQUINA");
        if (!vsMachine && survivalOption.isSelected()) selectOption(byGoalsOption);
    }

    // Changing the minutes means playing by time
    private void changeMinutes(int change) {
        minutes = Math.max(MIN_MINUTES, Math.min(MAX_MINUTES, minutes + change));
        minutesLabel.setText(minutes + " MIN");
        selectOption(byTimeOption);
    }

    // Choosing a goal target means playing by goals
    private void selectGoalTarget(int goals) {
        goalTarget = goals;
        updateGoalButtons();
        selectOption(byGoalsOption);
    }

    private Option createMatchOption(String title, String imageUrl, String description) {
        Option option = createOption();
        option.add(Box.createVerticalStrut(25));
        option.add(createModeImage(imageUrl, 120, 120)); // square images (~120x120)
        option.add(Box.createVerticalStrut(12));
        option.add(createOptionText(title, OPTION_TITLE_SIZE, WHITE));
        option.add(Box.createVerticalStrut(14));
        option.add(createOptionText(description, OPTION_TEXT_SIZE, LIGHT_GREEN));
        return option;
    }

    // Puts the control at the bottom of the option (the glue takes the empty space)
    private void addBottomControl(Option option, JComponent control) {
        control.setAlignmentX(Component.CENTER_ALIGNMENT);
        control.setMaximumSize(control.getPreferredSize()); // so BoxLayout doesn't stretch it
        option.add(Box.createVerticalGlue());
        option.add(control);
        option.add(Box.createVerticalStrut(45));
    }

    // "3 MIN ▲▼" rounded control
    private JPanel createMinutesControl() {
        JPanel control = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DARK_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        control.setOpaque(false);
        control.setBorder(BorderFactory.createEmptyBorder(8, 14, 6, 14));

        // Width of the longest text ("10 MIN"), so the arrows don't jump to another line when it changes
        minutesLabel = new JLabel(MAX_MINUTES + " MIN");
        minutesLabel.setFont(luckiest.deriveFont(22f));
        minutesLabel.setForeground(YELLOW);
        minutesLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 4)); // the font sits high
        minutesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        minutesLabel.setPreferredSize(minutesLabel.getPreferredSize());
        minutesLabel.setText(minutes + " MIN");

        minutesUpBtn = createArrowButton(true);
        minutesDownBtn = createArrowButton(false);

        control.add(minutesLabel);
        control.add(minutesUpBtn);
        control.add(minutesDownBtn);
        return control;
    }

    private JButton createArrowButton(boolean up) {
        JButton btn = new JButton(createArrowIcon(16, 13, up, YELLOW));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // "META DE GOLES" + one circle per goal target
    private JPanel createGoalsControl() {
        JPanel control = new JPanel();
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
        control.setOpaque(false);

        JPanel circles = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        circles.setOpaque(false);
        for (int i = 0; i < GOAL_TARGETS.length; i++) {
            goalBtns[i] = createGoalButton(GOAL_TARGETS[i]);
            circles.add(goalBtns[i]);
        }
        circles.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateGoalButtons();

        control.add(createOptionText("META DE GOLES", OPTION_TEXT_SIZE, YELLOW));
        control.add(Box.createVerticalStrut(12));
        control.add(circles);
        return control;
    }

    private JButton createGoalButton(int goals) {
        JButton btn = new JButton(String.valueOf(goals)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(luckiest.deriveFont(24f));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); // the font sits high
        btn.setPreferredSize(new Dimension(52, 52));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Selected goal target in yellow, the others in green
    private void updateGoalButtons() {
        for (int i = 0; i < GOAL_TARGETS.length; i++) {
            boolean selected = GOAL_TARGETS[i] == goalTarget;
            goalBtns[i].setBackground(selected ? YELLOW : DARK_GREEN);
            goalBtns[i].setForeground(selected ? DARK_GREEN : WHITE);
        }
    }

    // "SOLO PVSM" red label + balls info
    private JPanel createSurvivalInfo() {
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel onlyPvsM = createRoundedLabel("SOLO PVSM", 30);
        onlyPvsM.setFont(luckiest.deriveFont(OPTION_TEXT_SIZE));
        onlyPvsM.setBackground(RED);
        onlyPvsM.setForeground(WHITE);
        onlyPvsM.setBorder(BorderFactory.createEmptyBorder(11, 34, 6, 34));
        onlyPvsM.setAlignmentX(Component.CENTER_ALIGNMENT);

        info.add(onlyPvsM);
        info.add(Box.createVerticalStrut(6));
        info.add(createOptionText("3 BALONES POR JUGADOR", OPTION_TEXT_SIZE, WHITE));
        return info;
    }
}
