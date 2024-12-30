package com.ustavdica.features.board.controllers;

import com.ustavdica.features.board.views.BoardView;
import com.ustavdica.features.board.views.SquareView;
import com.ustavdica.features.board.models.Board;
import com.ustavdica.features.board.models.Square;
import com.ustavdica.features.state.Player;
import com.ustavdica.features.state.State;
import com.ustavdica.features.state.StateHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardController {

    private final Board model;
    private final BoardView view;

    private final StateHandler stateHandler;


    public BoardController(Board model, BoardView view) {
        this.model = model;
        this.view = view;

        // Instantiating state handler, this is singleton thus getInstance()
        this.stateHandler = StateHandler.getInstance();

        initializeBoardView();
    }

    private void initializeBoardView() {
        for (Square square : model.getAllSquares().reversed()) {
            SquareView squareView = new SquareView();

            // Add action listener to handle square clicks
            squareView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onSquareClicked(square, squareView);
                }
            });

            view.add(squareView);
        }
    }

    private void onSquareClicked(Square square, SquareView view) {

        // System.out.println("Square clicked: " + square.getSquareNumber());

        int clickedSquare = square.getSquareNumber();
        State state = model.getState();
        Player player = state.getNextPlayer();

        boolean isApplied = stateHandler.applyMove(state, clickedSquare);

        // Try to apply the move given by the user
        if (isApplied) {
            view.updateAppearance(player);
        } else {
            // Move cannot be made cuz it is against game rules
            view.blinkBackgroundRed();
        }

        // After applied move check if someone won
        boolean hasWinner = stateHandler.hasWinner(state);

        if (hasWinner) {
            System.out.println("Has winner: " + player.name());
        }


        // List<Integer> availableMoves = stateHandler.getAvailableMoves(currentState);
        // System.out.println("Available moves: " + availableMoves.toString());;
    }

}
