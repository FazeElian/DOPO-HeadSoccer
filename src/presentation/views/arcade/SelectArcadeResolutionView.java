package presentation.views.arcade;

import javax.swing.*;
import java.awt.*;

// Color constants
import static presentation.DOPOSoccerGUI.*;

public class SelectArcadeResolutionView extends ArcadeView {
    // Available resolutions (width x height)
    public static final Dimension SMALL = new Dimension(960, 540);
    public static final Dimension MEDIUM = new Dimension(1280, 720);
    public static final Dimension LARGE = new Dimension(1600, 900);
    private static final Dimension[] RESOLUTIONS = {SMALL, MEDIUM, LARGE};

    private static final Color PREVIEW_BG = new Color(36, 120, 48);

    public SelectArcadeResolutionView(Font luckiest) {
        super(luckiest);
        prepareView();
    }

    @Override
    protected void prepareView() {
        createResolutionOption(SMALL, "PEQUEÑA", "960 × 540 PX. CANCHA COMPACTA,\nPARTIDOS MÁS RÁPIDOS.");
        Option medium = createResolutionOption(MEDIUM, "MEDIANA", "1280 × 720 PX. EL TAMAÑO\nRECOMENDADO.");
        createResolutionOption(LARGE, "GRANDE", "1600 × 900 PX. MÁS ESPACIO PARA\nCORRER.");
        selectOption(medium); // recommended size

        prepareSelectionCard("RESOLUCIÓN");
    }

    public Dimension getSelectedResolution() {
        return RESOLUTIONS[getSelectedIndex()];
    }

    private Option createResolutionOption(Dimension resolution, String title, String description) {
        Option option = createOption();
        option.add(createScreenPreview(resolution));
        option.add(Box.createVerticalStrut(12));
        option.add(createOptionText(title, OPTION_TITLE_SIZE, WHITE));
        option.add(Box.createVerticalStrut(14));
        option.add(createOptionText(description, OPTION_TEXT_SIZE, LIGHT_GREEN));
        return option;
    }

    // Screen drawing proportional to the resolution
    private JComponent createScreenPreview(Dimension resolution) {
        JComponent preview = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = resolution.width / 10;
                int h = resolution.height / 10;
                int x = (getWidth() - w) / 2;
                int y = (getHeight() - h) / 2;

                g2.setColor(PREVIEW_BG);
                g2.fillRoundRect(x, y, w, h, 6, 6);
                g2.setColor(WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x, y, w, h, 6, 6);

                g2.dispose();
            }
        };
        Dimension size = new Dimension(LARGE.width / 10 + 10, 130); // room for the biggest screen
        preview.setPreferredSize(size);
        preview.setMaximumSize(size); // so BoxLayout doesn't stretch it
        preview.setAlignmentX(Component.CENTER_ALIGNMENT);
        return preview;
    }
}
