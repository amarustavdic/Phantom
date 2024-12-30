package com.ustavdica.mvc.view;

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
}
