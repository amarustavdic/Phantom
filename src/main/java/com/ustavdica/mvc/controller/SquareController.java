package com.ustavdica.mvc.controller;

import com.ustavdica.mvc.model.Square;
import com.ustavdica.mvc.view.SquareView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SquareController {

    private final Square model;
    private final SquareView view;

    public SquareController(Square model, SquareView view) {
        this.model = model;
        this.view = view;

        // Add action listener to handle button clicks
        this.view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSquareClicked();
            }
        });
    }

    private void onSquareClicked() {

        // TODO: Figure out what to do with this...

        // view.updateAppearance();

        System.out.println("Clicked on square: " + model.getSquareNumber());
    }

}
