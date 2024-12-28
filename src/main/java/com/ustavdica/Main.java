package com.ustavdica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        State state = new State(Player.BLUE);
        state.print();

        while (true) {
            int square = Integer.parseInt(br.readLine());
            state.makeMove(square);
            state.print();
            // state.print(state.getNextValidMovesMask());
            state.getLegalMoves().forEach(System.out::println);
        }
    }
}