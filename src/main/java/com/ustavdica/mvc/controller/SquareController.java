package com.ustavdica.mvc.controller;

import com.ustavdica.mvc.model.SquareModel;
import com.ustavdica.mvc.view.SquareView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SquareController {

    private final SquareModel model;
    private final SquareView view;

    public SquareController(SquareModel model, SquareView view) {
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

        view.updateAppearance(true);

        System.out.println("Clicked on square: " + model.getSquareNumber());
    }

}
