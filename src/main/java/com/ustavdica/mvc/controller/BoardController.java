package com.ustavdica.mvc.controller;

import com.ustavdica.mvc.model.Board;
import com.ustavdica.mvc.model.Square;
import com.ustavdica.mvc.view.BoardView;
import com.ustavdica.mvc.view.SquareView;
import com.ustavdica.state.Player;
import com.ustavdica.state.State;
import com.ustavdica.state.StateHandler;
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

        System.out.println("Square clicked: " + square.getSquareNumber());

        int clickedSquare = square.getSquareNumber();
        State currentState = model.getState();
        Player player = currentState.getNextPlayer();

        boolean isApplied = stateHandler.applyMove(currentState, clickedSquare);

        if (isApplied) {
            view.updateAppearance(player);
        }

        // List<Integer> availableMoves = stateHandler.getAvailableMoves(currentState);
        // System.out.println("Available moves: " + availableMoves.toString());;
    }

}
