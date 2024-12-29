package com.ustavdica;

import com.ustavdica.state.Player;
import com.ustavdica.state.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        State state = new State(Player.BLUE);
        state.print();



    }
}