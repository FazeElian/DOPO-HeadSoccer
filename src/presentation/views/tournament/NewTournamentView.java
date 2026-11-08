package presentation.views.tournament;

import javax.swing.*;
import java.awt.*;

import presentation.views.arcade.SelectArcadeMatchTypeView;

// Color constants
import static presentation.DOPOSoccerGUI.*;

/**
 * "NUEVO TORNEO": the team of the player (face, flag, power and stats) on the left and the match type
 * of the whole tournament on the right (by time or by goals), with the prize and "EMPEZAR TORNEO".
 * The team, its stats and the prize are given by the GUI (domain data).
 */
public class NewTournamentView extends TournamentView {
    private static final String[] STATS_NAMES = {"VELOCIDAD", "SALTO", "PATADA", "VIDA"};
    private static final Dimension TYPE_OPTION_SIZE = new Dimension(220, 200);

    // Match type of the tournament
    private int minutes = 1;
    private int goalTarget = SelectArcadeMatchTypeView.GOAL_TARGETS[0];
    private Option byTimeOption;
    private Option byGoalsOption;
    private JLabel timeText;
    public JButton[] goalBtns = new JButton[SelectArcadeMatchTypeView.GOAL_TARGETS.length];

    // Team panel components
    private JLabel teamFace;
    private JLabel teamFlag;
    private JLabel teamName;
    private JLabel teamPower;
    private JLabel[] teamStats = new JLabel[STATS_NAMES.length];

    private JLabel prizeLabel;

    public NewTournamentView(Font luckiest) {
        super(luckiest);
        prepareView();
        prepareController();
    }

    @Override
    protected void prepareView() {
        JPanel topBar = createBackBar();
        JPanel block = createCenterBlock(20);

        JPanel card = createCard(new BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(26, 35, 32, 35));

        // Title and the rules of the tournament under it
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        header.add(createTitleRow("NUEVO TORNEO"), BorderLayout.NORTH);
        header.add(centered(createPill("8 EQUIPOS · CUARTOS, SEMIFINAL Y FINAL · TÚ CONTRA LA IA", OPTION_BG, WHITE, 14f)),
            BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(30, 0));
        body.setOpaque(false);
        body.add(createTeamPanel(), BorderLayout.WEST);
        body.add(createTypeColumn(), BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    private void prepareController() {
        for (int i = 0; i < goalBtns.length; i++) {
            int goals = SelectArcadeMatchTypeView.GOAL_TARGETS[i];
            goalBtns[i].addActionListener(e -> selectGoalTarget(goals));
        }
    }

    /**
     * Team of the player (from the domain).
     *
     * @param country country of the character ("Colombia")
     * @param power   name of its power shot
     * @param stats   speed, jump, kick and survival of the player
     */
    public void setTeam(String country, String power, int[] stats) {
        teamFace.setIcon(faceIcon(country, 130, false));
        teamFlag.setIcon(flagIcon(country, 72, 44));
        teamName.setText(country.toUpperCase());
        teamPower.setText("PODER: " + power.toUpperCase());
        for (int i = 0; i < teamStats.length; i++) {
            teamStats[i].setIcon(createStatIcon(stats[i]));
        }
    }

    // Points for the champion (from the domain)
    public void setPrize(int prize) {
        prizeLabel.setText("PREMIO AL CAMPEÓN: +" + prize);
    }

    public boolean isByTime() {
        return byTimeOption.isSelected();
    }

    public int getMinutes() {
        return minutes;
    }

    public int getGoalTarget() {
        return goalTarget;
    }

    // Choosing a goal target means playing by goals
    private void selectGoalTarget(int goals) {
        goalTarget = goals;
        updateGoalButtons();
        selectOption(byGoalsOption);
    }

    // "TU EQUIPO": face, flag, name, power and the stats bars
    private JPanel createTeamPanel() {
        JLabel badge = createBadge("TU EQUIPO", BLUE, WHITE);
        JPanel panel = createBadgedPanel(badge, BLUE, new Dimension(300, 440));

        teamFace = new JLabel();
        teamFlag = new JLabel();
        teamName = createText(" ", 28f, WHITE);
        teamPower = createPill(" ", POWER_BG, POWER_TEXT, 14f);
        teamPower.setBorder(BorderFactory.createEmptyBorder(10, 30, 4, 30));

        JPanel stats = new JPanel(new GridLayout(STATS_NAMES.length, 2, 10, 6));
        stats.setOpaque(false);
        for (int i = 0; i < STATS_NAMES.length; i++) {
            stats.add(createText(STATS_NAMES[i], 13f, WHITE));
            teamStats[i] = new JLabel();
            stats.add(teamStats[i]);
        }

        for (JComponent c : new JComponent[]{teamFace, teamFlag, teamName, teamPower, stats}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        panel.add(Box.createVerticalStrut(12));
        panel.add(teamFace);
        panel.add(Box.createVerticalStrut(8));
        panel.add(teamFlag);
        panel.add(teamName);
        panel.add(Box.createVerticalStrut(12));
        panel.add(teamPower);
        panel.add(Box.createVerticalStrut(18));
        panel.add(stats);
        return panel;
    }

    // "TIPO DE PARTIDO DEL TORNEO": the 2 options, the info, the prize and "EMPEZAR TORNEO"
    private JPanel createTypeColumn() {
        byTimeOption = createTypeOption("POR TIEMPO", "assets/img/arcade/clock.png");
        timeText = createText(minutes * 60 + " S · MUERTE SÚBITA SI HAY EMPATE", 11f, LIGHT_GREEN);
        timeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        byTimeOption.add(timeText);

        byGoalsOption = createTypeOption("POR GOLES", "assets/img/arcade/goals.png");
        JPanel circles = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        circles.setOpaque(false);
        for (int i = 0; i < goalBtns.length; i++) {
            goalBtns[i] = createGoalButton(SelectArcadeMatchTypeView.GOAL_TARGETS[i]);
            circles.add(goalBtns[i]);
        }
        circles.setAlignmentX(Component.CENTER_ALIGNMENT);
        circles.setMaximumSize(circles.getPreferredSize());
        byGoalsOption.add(circles);
        updateGoalButtons();
        selectOption(byGoalsOption); // by goals by default

        JPanel options = new JPanel(new GridLayout(1, 2, 22, 0));
        options.setOpaque(false);
        options.add(byTimeOption);
        options.add(byGoalsOption);

        prizeLabel = createCoinLabel(" ", 16f);

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 14));
        texts.setOpaque(false);
        texts.add(createText("TODOS LOS PARTIDOS DEL TORNEO SE JUEGAN CON ESTE TIPO", 11f, LIGHT_GREEN));
        texts.add(prizeLabel);

        JPanel continueRow = createContinueRow("EMPEZAR TORNEO");
        ((FlowLayout) continueRow.getLayout()).setAlignment(FlowLayout.RIGHT);
        continueBtn.setFont(luckiest.deriveFont(22f));
        continueBtn.setBorder(BorderFactory.createEmptyBorder(16, 40, 10, 40));

        JPanel bottom = new JPanel(new BorderLayout(0, 16));
        bottom.setOpaque(false);
        bottom.add(texts, BorderLayout.NORTH);
        bottom.add(continueRow, BorderLayout.CENTER);

        JPanel column = new JPanel(new BorderLayout(0, 14));
        column.setOpaque(false);
        column.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        column.add(createText("TIPO DE PARTIDO DEL TORNEO", 17f, WHITE), BorderLayout.NORTH);
        column.add(options, BorderLayout.CENTER);
        column.add(bottom, BorderLayout.SOUTH);
        return column;
    }

    // Small option: image and title (the control goes under them)
    private Option createTypeOption(String title, String imageUrl) {
        Option option = createOption();
        option.setPreferredSize(TYPE_OPTION_SIZE);
        option.setBorder(BorderFactory.createEmptyBorder(16, 10, 16, 10));
        option.add(createModeImage(imageUrl, 64, 64));
        option.add(Box.createVerticalStrut(8));
        option.add(createOptionText(title, 20f, WHITE));
        option.add(Box.createVerticalStrut(12));
        return option;
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
        btn.setFont(luckiest.deriveFont(20f));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // the font sits high
        btn.setPreferredSize(new Dimension(42, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Selected goal target in yellow, the others in green
    private void updateGoalButtons() {
        for (int i = 0; i < goalBtns.length; i++) {
            boolean selected = SelectArcadeMatchTypeView.GOAL_TARGETS[i] == goalTarget;
            goalBtns[i].setBackground(selected ? YELLOW : DARK_GREEN);
            goalBtns[i].setForeground(selected ? DARK_GREEN : WHITE);
        }
    }
}
