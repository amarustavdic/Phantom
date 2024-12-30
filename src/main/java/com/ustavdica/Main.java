package com.ustavdica;

import com.ustavdica.features.board.controllers.BoardController;
import com.ustavdica.features.board.models.Board;
import com.ustavdica.features.board.views.BoardView;
import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MVC Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // Create MVC components
            Board board = new Board();
            BoardView boardView = new BoardView();
            BoardController boardController = new BoardController(board, boardView);

            // Add view to the frame
            frame.add(boardView);

            frame.pack();
            frame.setVisible(true);
        });
    }
}