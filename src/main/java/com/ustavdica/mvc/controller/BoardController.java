package com.ustavdica.mvc.controller;

import com.ustavdica.mvc.model.Board;
import com.ustavdica.mvc.model.Square;
import com.ustavdica.mvc.view.BoardView;
import com.ustavdica.mvc.view.SquareView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardController {

    private final Board model;
    private final BoardView view;

    public BoardController(Board model, BoardView view) {
        this.model = model;
        this.view = view;

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
        System.out.println("Square " + square.getSquareNumber() + " clicked!");
        view.updateAppearance(true);
    }

}
