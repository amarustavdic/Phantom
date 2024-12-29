package com.ustavdica;

import com.ustavdica.state.Player;
import com.ustavdica.state.State;
import com.ustavdica.state.StateHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

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

            long startTime = System.nanoTime();
            handler.applyMove(state, targetSquare);
            long endTime = System.nanoTime();

            double timeInMs = (endTime - startTime) / 1_000_000.0;
            DecimalFormat df = new DecimalFormat("#.######");
            System.out.println("\nTime taken: " + df.format(timeInMs) + " ms");

            state.print();
        }

    }
}