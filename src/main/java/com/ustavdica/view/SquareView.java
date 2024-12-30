package com.ustavdica.view;

import com.ustavdica.logic.state.Player;
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
}
