package presentation.views.match;

import java.awt.*;
import domain.DOPOSoccer;

/**
 * Arcade match (player vs player or vs the AI).
 * The HUD, scoreboard, time and pause come from GameView; this view only builds its screen.
 */
public class ArcadeMatchView extends GameView {

    public ArcadeMatchView(Font luckiest, DOPOSoccer game) {
        super(luckiest, game);
        prepareView();
    }

    @Override
    protected void prepareView() {
        // Top bar: player 1 HUD, scoreboard + time + pause and rival HUD
        add(createTopBar(), BorderLayout.NORTH);
    }
}
