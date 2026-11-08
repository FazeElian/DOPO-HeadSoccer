package presentation.views.tournament;

import java.awt.*;

import presentation.views.arcade.SelectArcadeBallView;

/**
 * "ELIGE TU BALÓN" of the tournament: the same screen as in arcade, but its button goes to the bracket.
 */
public class TournamentBallView extends SelectArcadeBallView {

    public TournamentBallView(Font luckiest) {
        super(luckiest);
        continueBtn.setText("VER LLAVE");
    }
}
