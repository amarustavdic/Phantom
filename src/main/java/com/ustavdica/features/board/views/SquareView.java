package com.ustavdica.features.board.views;

import com.ustavdica.features.state.Player;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SquareView extends JButton {

    public SquareView() {
        setBackground(new Color(30, 30, 30));
        setBorder(new LineBorder(new Color(40, 40, 40)));
        setPreferredSize(new Dimension(64, 64));
        setFocusPainted(false);
        setOpaque(true);
    }

    // Bellow functions to communicate with this view, e.g: updateAppearance();
    public void updateAppearance(Player player) {

        if (player == Player.BLUE) {
            setBackground(new Color(50, 71, 205));
        } else {
            setBackground(new Color(255, 0, 255));
        }
    }

    // Blinks the background red 3 times to indicate an invalid move
    public void blinkBackgroundRed() {
        Color originalColor = getBackground();
        SwingWorker<Void, Void> blinker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < 3; i++) {
                    setBackground(Color.RED);
                    Thread.sleep(150);
                    setBackground(originalColor);
                    Thread.sleep(150);
                }
                return null;
            }
        };
        blinker.execute();
    }

}
