package com.ustavdica.ui;

import com.ustavdica.state.State;
import com.ustavdica.state.StateHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventHandler implements ActionListener {

    private State state;
    private StateHandler stateHandler;
    private boolean isBlueTurn = true;

    public EventHandler(State state, StateHandler stateHandler) {
        this.state = state;
        this.stateHandler = stateHandler;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        // Figure out the source of the event
        SquareButton squareButton = (SquareButton) e.getSource();
        int square = squareButton.getSquareNumber();

        boolean moveApplied = stateHandler.applyMove(state, square);

        if (moveApplied) {
            handleButtonClick(squareButton);
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

}
