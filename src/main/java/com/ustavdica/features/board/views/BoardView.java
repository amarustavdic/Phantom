package com.ustavdica.features.board.views;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardView extends JPanel {

    private final List<SquareView> squareViews;

    public BoardView() {
        setLayout(new GridLayout(7, 7));
        squareViews = new ArrayList<>();
    }

    public void initializeBoard(List<SquareView> squareViews) {
        for (SquareView squareView : squareViews) {
            add(squareView);
            this.squareViews.add(squareView);
        }
    }

    public List<SquareView> getAllSquareViews() {
        return squareViews;
    }

    public SquareView getSquareView(int index) {
        return squareViews.reversed().get(index);
    }

}
