package com.ustavdica;

import com.ustavdica.logic.state.Player;
import com.ustavdica.logic.state.State;
import com.ustavdica.logic.state.StateHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StateHandlerGetValidMoveMaskTest {

    @Test
    void testEmptyBoard() {
        State state = new State(Player.BLUE);
        StateHandler handler = StateHandler.getInstance();

        long validMoves = handler.getValidMoveMask(state);

        // Since the board is empty, all bits should be set
        assertEquals(~0L, validMoves, "Valid move mask should have all bits set for an empty board.");
    }

    @Test
    void testStandardMove() {
        // TODO: Write test for standard move
    }

    @Test
    void testIslandScenario() {
        // TODO: Write test for island (when players close them self's in)
    }

    @Test
    void testMixedScenarios() {
        // TODO: Write test for both scenarios possible
    }

    @Test
    void testInvalidSquareMove() {
        State state = new State(Player.BLUE);
        StateHandler handler = StateHandler.getInstance();

        boolean result = handler.applyMove(state, 50); // Valid squares are [0-48]

        // Assert that moves to invalid squares outside the range [0-48] are not allowed
        assertFalse(result, "Applying a move to an invalid square (50) should return false.");
    }

    @Test
    void testFullyOccupiedBoard() {
        // TODO: Write test for fully occupied board...

        /*
        Fully occupied board for this game means that there is single unset bit on the board,
        to make it fair to the players since board is 7 x 7 and each of the players have even
        number of cubes to place inside the grid
         */
    }

}
