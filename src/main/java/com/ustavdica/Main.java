package com.ustavdica;

import com.ustavdica.features.board.controllers.BoardController;
import com.ustavdica.features.board.models.Board;
import com.ustavdica.features.board.views.BoardView;
import com.ustavdica.features.search.MonteCarloTreeSearch;
import com.ustavdica.features.state.Player;
import com.ustavdica.features.state.State;
import com.ustavdica.features.state.StateHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("4mation - Monte Carlo");
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


//        State state = new State(Player.BLUE);
//        StateHandler stateHandler = StateHandler.getInstance();
//
//        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(stateHandler);
//
//        int best = mcts.findBestMove(state, 1000);
//
//        System.out.println("Best move: " + best);






    }
}