package com.ustavdica;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StateTest {

    @Test
    void testInitialState() {
        // Arrange
        State state = new State(Player.BLUE);

        // Assert
        assertEquals(Player.BLUE, state.getNextPlayer(), "Starting player should be BLUE.");
        assertEquals(0L, state.getCombinedBitboard(), "Initial bitboard should be empty.");
    }

    @Test
    void testMakeMove() {
        // Arrange
        State state = new State(Player.BLUE);

        // Act
        boolean result = state.makeMove(0);

        // Assert
        assertTrue(result, "First move should be valid.");
        assertNotEquals(0L, state.getCombinedBitboard(), "Bitboard should reflect the move.");
    }

    @Test
    void testSwitchPlayer() {
        // Arrange
        State state = new State(Player.BLUE);

        // Act
        state.makeMove(0);

        // Assert
        assertEquals(Player.PINK, state.getNextPlayer(), "Next player should be PINK.");
    }

}
