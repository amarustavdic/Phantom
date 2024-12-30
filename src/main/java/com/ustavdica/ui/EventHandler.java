package com.ustavdica.ui;

import com.ustavdica.search.MonteCarloTreeSearch;
import com.ustavdica.state.State;
import com.ustavdica.state.StateHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventHandler implements ActionListener {

    private State state;
    private StateHandler stateHandler;
    private MonteCarloTreeSearch mcts;
    private boolean isBlueTurn = true;

    public EventHandler(State state, StateHandler stateHandler) {
        this.state = state;
        this.stateHandler = stateHandler;
        this.mcts = new MonteCarloTreeSearch(stateHandler);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        // Figure out the source of the event
        SquareButton squareButton = (SquareButton) e.getSource();
        int square = squareButton.getSquareNumber();

        boolean moveApplied = stateHandler.applyMove(state, square);

        if (moveApplied) {
            handleButtonClick(squareButton);

            // Offload MCTS computation to a SwingWorker
            SwingWorker<Integer, Void> mctsWorker = new SwingWorker<>() {

                @Override
                protected Integer doInBackground() throws Exception {
                    // Perform the MCTS computation
                    return mcts.findBestMove(state, 1000);
                }

                @Override
                protected void done() {
                    try {
                        // Get the computed best move and apply it
                        int bestMove = get(); // Get the result of doInBackground
                        stateHandler.applyMove(state, bestMove);

                        System.out.println("Best move: " + bestMove);

                        // Find the button corresponding to the best move
                        // and update it (assuming there's a way to get the button from the square number)
                        SquareButton bestMoveButton = findButtonBySquare(bestMove);
                        if (bestMoveButton != null) {
                            handleButtonClick(bestMoveButton);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace(); // Handle any exceptions from doInBackground
                    }
                }
            };

            mctsWorker.execute();

        }

        // TODO: Here has to be done better job, later

    }

    private void handleButtonClick(JButton button) {
        if (isBlueTurn) {
            button.setBackground(Color.BLUE);
        } else {
            button.setBackground(new Color(227, 0, 255));
        }
        isBlueTurn = !isBlueTurn;
        button.setEnabled(false);
    }


    // Helper method to find the button by square number
    private SquareButton findButtonBySquare(int square) {
        // Implement a way to find the button corresponding to the square number
        // For example, you might maintain a map of square numbers to buttons
        return null;
    }
}
