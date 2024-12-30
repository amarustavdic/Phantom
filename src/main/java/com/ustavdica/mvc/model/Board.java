package com.ustavdica.mvc.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final List<Square> squares;

    public Board() {
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

}
