package com.ustavdica.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SquareButton extends JButton {

    private final int squareNumber;

    public SquareButton(int squareNumber, EventHandler eventHandler) {
        this.squareNumber = squareNumber;

        setBackground(new Color(30, 31, 34));
        setFocusPainted(false);
        setOpaque(true);
        setBorder(new LineBorder(new Color(47, 50, 53)));
        setPreferredSize(new Dimension(64, 64));

        addActionListener(eventHandler);
    }

    public int getSquareNumber() {
        return squareNumber;
    }

}
