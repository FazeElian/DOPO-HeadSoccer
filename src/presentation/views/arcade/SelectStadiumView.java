package presentation.views.arcade;

import java.awt.*;
import javax.swing.*;

/**
 * "ELIGE EL ESTADIO": the 9 stadiums in a grid of 3 columns, each one a selectable card with its picture.
 * Only the picture changes (weather, time and crowd): the field is the same in all of them.
 * The GUI gives the chosen one to the domain (selectStadium) before an Arcade match starts.
 */
public class SelectStadiumView extends ArcadeView {
    // Pictures of the stadiums: assets/img/stadiums/stadium-<name>.png
    private static final String BASE_IMG_URL = "assets/img/stadiums/stadium-";

    // Names of the stadiums, in the order of the grid (the same names as GameMode.STADIUMS)
    public static final String[] STADIUMS = {
        "default", "day", "sunset", "rain", "snow", "fog", "storm", "albiceleste", "empty"
    };

    public SelectStadiumView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    // Picture of a stadium: "rain" -> "assets/img/stadiums/stadium-rain.png"
    public static String imageOf(String key) {
        return BASE_IMG_URL + key + ".png";
    }

    @Override
    protected void prepareView() {
        // Top bar
        JPanel topBar = createBackBar();

        // Block that will contain the green card
        JPanel block = createCenterBlock(10);

        // Green card
        JPanel card = createCard(new BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(24, 35, 28, 35));

        // Title
        JPanel titleRow = createTitleRow("ELIGE EL ESTADIO");

        // Stadiums: 3 columns, as many rows as needed; each one a selectable card with its picture
        // (createOption selects the first one, "default")
        JPanel grid = new JPanel(new GridLayout(0, 3, 18, 16));
        grid.setOpaque(false);
        for (String key : STADIUMS) {
            Option option = createOption();
            option.setPreferredSize(new Dimension(250, 176)); // smaller than the arcade options
            option.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
            option.add(createModeImage(imageOf(key), 250, 146));
            grid.add(option);
        }

        // The grid centered in the card, with its own size
        JPanel gridRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gridRow.setOpaque(false);
        gridRow.add(grid);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(gridRow, BorderLayout.CENTER);
        card.add(createContinueRow("CONTINUAR"), BorderLayout.SOUTH);

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    // Name of the chosen stadium ("rain"), for the domain
    public String getSelectedStadium() {
        return STADIUMS[getSelectedIndex()];
    }
}
