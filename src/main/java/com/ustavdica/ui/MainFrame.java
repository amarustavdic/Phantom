package com.ustavdica.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final EventHandler eventHandler;

    public MainFrame(EventHandler eventHandler) {
        this.eventHandler = eventHandler;

        setTitle("4mation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        setResizable(false);

        JPanel gameBoard = createGameBoardPanel();

        add(gameBoard, BorderLayout.CENTER);

        setVisible(true);
        pack();
    }

    private JPanel createGameBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(7, 7));

        // Add buttons to the panel for each square
        for (int i = 48; i >= 0; i--) {
            SquareButton squareButton = new SquareButton(i, eventHandler);
            boardPanel.add(squareButton);
        }
        return boardPanel;
    }

}
