package com.ustavdica.features.board.models;

import com.ustavdica.features.state.Player;
import com.ustavdica.features.state.State;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final State state;
    private final List<Square> squares;

    public Board() {

        // Initialize state with starting player blue
        this.state = new State(Player.BLUE);

        squares = new ArrayList<>(49);
        for (int i = 0; i < 49; i++) {
            squares.add(new Square(i));
        }
    }

    public Square getSquare(int squareNumber) {
        return squares.get(squareNumber);
    }

    public List<Square> getAllSquares() {
        return squares;
    }

    public State getState() {
        return state;
    }

}
