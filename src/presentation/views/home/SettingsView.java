package presentation.views.home;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

// Color constants
import static presentation.DOPOSoccerGUI.*;

public class SettingsView extends HomeView {
    // Settings constants
    private static final int DEFAULT_VOLUME = 70;
    private int musicVolPercentage = DEFAULT_VOLUME;
    private int soundEffectsVolPercentage = DEFAULT_VOLUME;

    // Settings components
    private JSlider musicSlider;
    private JSlider soundEffectsSlider;
    private JLabel musicPercentage;
    private JLabel soundEffectsPercentage;
    public JButton resetBtn;

    public SettingsView(Font luckiest) {
        super(luckiest);
        prepareView();
        prepareController();
    }

    @Override
    protected void prepareView() {
        // Top bar
        JPanel topBar = createBackBar();

        // Block that will contain the green card
        JPanel block = createCenterBlock(200);

        // Green card
        JPanel card = createCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(35, 45, 40, 45));
        card.setPreferredSize(new Dimension(550, 330));

        // Title
        JPanel titleRow = createTitleRow("AJUSTES");
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Options block
        JPanel options = new JPanel(new GridLayout(2, 1, 20, 0));
        options.setOpaque(false);

        // music
        musicSlider = createVolumeSlider(musicVolPercentage);
        musicPercentage = createPercentageLabel(musicVolPercentage);
        options.add(createVolumeOption("MÚSICA", musicSlider, musicPercentage));

        // Sound Effects
        soundEffectsSlider = createVolumeSlider(soundEffectsVolPercentage);
        soundEffectsPercentage = createPercentageLabel(soundEffectsVolPercentage);
        options.add(createVolumeOption("EFECTOS DE SONIDO", soundEffectsSlider, soundEffectsPercentage));

        // Reset button
        resetBtn = createButton("RESTABLECER", RED, WHITE, null);
        resetBtn.setFont(luckiest.deriveFont(18f));

        JPanel resetRow = new JPanel(new BorderLayout());
        resetRow.setOpaque(false);
        resetRow.add(resetBtn);
        resetRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        card.add(titleRow, BorderLayout.NORTH);
        card.add(options, BorderLayout.CENTER);
        card.add(resetRow, BorderLayout.SOUTH);

        block.add(card);
        add(topBar, BorderLayout.NORTH);
        add(block, BorderLayout.CENTER);
    }

    private void prepareController() {
        musicSlider.addChangeListener(e -> updateMusicPercentage());
        soundEffectsSlider.addChangeListener(e -> updateSoundEffectsPercentage());
        resetBtn.addActionListener(e-> reset());
    }

    private void updateMusicPercentage() {
        musicVolPercentage = musicSlider.getValue();
        musicPercentage.setText(musicVolPercentage + "%");
    }

    private void updateSoundEffectsPercentage() {
        soundEffectsVolPercentage = soundEffectsSlider.getValue();
        soundEffectsPercentage.setText(soundEffectsVolPercentage + "%");
    }

    private void reset() {
        // Moving the sliders fires their listeners, which update the percentages
        musicSlider.setValue(DEFAULT_VOLUME);
        soundEffectsSlider.setValue(DEFAULT_VOLUME);
    }

    public int getMusicVolPercentage() {
        return musicVolPercentage;
    }

    public int getSoundEffectsVolPercentage() {
        return soundEffectsVolPercentage;
    }

    // Option row: name on the left, slider + percentage on the right
    private JPanel createVolumeOption(String name, JSlider slider, JLabel percentage) {
        JPanel option = new JPanel(new GridLayout(1, 2));
        option.setOpaque(false);
        option.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, MEDIUM_GREEN),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));

        JLabel label = new JLabel(name);
        label.setOpaque(false);
        label.setForeground(WHITE);
        label.setFont(luckiest.deriveFont(20f));

        // slider block
        JPanel sliderBlock = new JPanel(new BorderLayout());
        sliderBlock.setOpaque(false);
        sliderBlock.add(slider, BorderLayout.CENTER);
        sliderBlock.add(percentage, BorderLayout.EAST);

        option.add(label);
        option.add(sliderBlock);
        return option;
    }

    private JLabel createPercentageLabel(int value) {
        JLabel percentage = new JLabel(value + "%");
        percentage.setOpaque(false);
        percentage.setForeground(WHITE);
        percentage.setFont(luckiest.deriveFont(20f));
        percentage.setPreferredSize(new Dimension(50, 20));
        return percentage;
    }

    private JSlider createVolumeSlider(int value) {
        JSlider slider = new JSlider(0, 100, value); // max value of 100, min of 0
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setPreferredSize(new Dimension(115, 20));
        slider.setUI(new BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int h = 10; // track height
                int y = trackRect.y + (trackRect.height - h) / 2;

                // Empty part
                g2.setColor(new Color(35, 95, 30));
                g2.fillRoundRect(trackRect.x, y, trackRect.width, h, h, h);

                // Filled part (up to the thumb center)
                int filled = thumbRect.x + thumbRect.width / 2 - trackRect.x;
                g2.setColor(NORMAL_GREEN);
                g2.fillRoundRect(trackRect.x, y, filled, h, h, h);

                g2.dispose();
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillOval(thumbRect.x, thumbRect.y + (thumbRect.height - 16) / 2, 16, 16);
                g2.dispose();
            }

            @Override
            protected Dimension getThumbSize() {
                return new Dimension(16, 16);
            }

            @Override
            public void paintFocus(Graphics g) {
                // no focus rectangle
            }
        });
        return slider;
    }
}
