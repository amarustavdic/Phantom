package com.ustavdica;

import com.ustavdica.state.Player;
import com.ustavdica.state.State;
import com.ustavdica.state.StateHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        StateHandler handler = StateHandler.getInstance();

        State state = new State(Player.BLUE);
        state.print();

        while (true) {
            System.out.print("Enter square number: ");
            int targetSquare = Integer.parseInt(br.readLine());
            System.out.println();

            handler.applyMove(state, targetSquare);
            state.print();
        }

    }
}