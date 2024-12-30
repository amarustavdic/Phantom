package com.ustavdica.mvc;

import com.ustavdica.mvc.controller.BoardController;
import com.ustavdica.mvc.controller.SquareController;
import com.ustavdica.mvc.model.Board;
import com.ustavdica.mvc.model.Square;
import com.ustavdica.mvc.view.BoardView;
import com.ustavdica.mvc.view.SquareView;

import javax.swing.*;
import java.awt.*;

public class MvcMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MVC Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
