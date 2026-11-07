package presentation.views.home;

import java.awt.*;

import presentation.views.View;

/**
 * Generic view for the home screens (main menu, tutorial, settings, select mode...).
 * The shared components builders are in View.
 */
public abstract class HomeView extends View {
    public static final String HOME_BACKGROUND = "assets/img/homeBackground.png";

    public HomeView(Font luckiest) {
        super(luckiest, HOME_BACKGROUND);
    }
}
