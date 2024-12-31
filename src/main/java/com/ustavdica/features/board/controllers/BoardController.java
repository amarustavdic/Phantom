package com.ustavdica.features.board.controllers;

import com.ustavdica.features.board.views.BoardView;
import com.ustavdica.features.board.views.SquareView;
import com.ustavdica.features.board.models.Board;
import com.ustavdica.features.board.models.Square;
import com.ustavdica.features.search.MonteCarloTreeSearch;
import com.ustavdica.features.state.Player;
import com.ustavdica.features.state.State;
import com.ustavdica.features.state.StateHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BoardController {

    private final Board model;
    private final BoardView view;

    private final StateHandler stateHandler;
    private final MonteCarloTreeSearch mcts;


    public BoardController(Board model, BoardView view) {
        this.model = model;
        this.view = view;

        // Instantiating state handler, this is singleton thus getInstance()
        this.stateHandler = StateHandler.getInstance();
        this.mcts = new MonteCarloTreeSearch(stateHandler);

        initializeBoardView();
    }

    private void initializeBoardView() {

        List<SquareView> squareViews = new ArrayList<>();

        for (Square square : model.getAllSquares().reversed()) {
            SquareView squareView = new SquareView();
            squareViews.add(squareView);

            // Add action listener to handle square clicks
            squareView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onSquareClicked(square, squareView);
                }
            });

            view.add(squareView);
        }

        view.initializeBoard(squareViews);
    }

    private void onSquareClicked(Square square, SquareView squareView) {
        int clickedSquare = square.getSquareNumber();
        State state = model.getState();
        Player player = state.getNextPlayer();

        boolean isApplied = stateHandler.applyMove(state, clickedSquare);

        // Try to apply the move given by the user
        if (isApplied) {
            squareView.updateAppearance(player);

            // Check if the user has won
            if (stateHandler.hasWon(state, player)) {
                showEndGameMessage(player.name() + " has won!");
                lockAllSquares();
                return;
            }

            // Check if the board is full (draw)
            if (stateHandler.getAvailableMoves(state).isEmpty()) {
                showEndGameMessage("It's a draw!");
                lockAllSquares();
                return;
            }

            // Run Monte Carlo Tree Search on a separate thread
            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    // Disable clicks while AI is thinking
                    lockAllSquares();

                    // Perform AI move computation
                    int best = mcts.findBestMove(state, 3000);

                    // System.out.println("Performed " + mcts.getIterations() + " random game simulations!");

                    return best;
                }

                @Override
                protected void done() {
                    try {
                        int bestMove = get(); // -1 if no moves are available
                        if (bestMove == -1) {
                            showEndGameMessage("It's a draw!");
                            return;
                        }

                        // Apply the AI's move
                        boolean aiMoveApplied = stateHandler.applyMove(state, bestMove);
                        if (aiMoveApplied) {
                            SquareView aiSquareView = view.getSquareView(bestMove);
                            aiSquareView.updateAppearance(Player.PINK);
                        }

                        // Check if AI has won
                        if (stateHandler.hasWon(state, Player.PINK)) {
                            showEndGameMessage("AI has won!");
                            lockAllSquares();
                            return;
                        }

                        // Check if the board is full (draw)
                        if (stateHandler.getAvailableMoves(state).isEmpty()) {
                            showEndGameMessage("It's a draw!");
                            lockAllSquares();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // Enable clicks if no winner or draw
                        view.getAllSquareViews().forEach(
                                squareView -> squareView.setEnabled(true)
                        );
                    }
                }
            };

            worker.execute();

        } else {
            // Move cannot be made because it is against game rules
            squareView.blinkBackgroundRed(squareView);
        }

        // After applied move check if someone won
        boolean hasWinner = stateHandler.hasWon(state, player);

        if (hasWinner) {
            System.out.println("Has winner: " + player.name());
        }

    }


    private void showEndGameMessage(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void lockAllSquares() {
        view.getAllSquareViews().forEach(
                squareView -> squareView.setEnabled(false)
        );
    }

}
