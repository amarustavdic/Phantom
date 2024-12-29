package com.ustavdica.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing operations on the game state.
 * <p>
 * This class implements StateOperations and provides methods for mutating the
 * State object, such as applying moves. It ensures consistent logic and
 * centralized handling of state-related operations.
 */
public class StateHandler {

    private static StateHandler instance;

    private final long[] squareMasks;
    private final long[] squareOutlineMasks;


    // Private constructor to prevent instantiation
    private StateHandler() {

        // Initializes the precomputed masks for each square
        this.squareMasks = new long[49];
        for (int square = 0; square < squareMasks.length; square++) {
            squareMasks[square] = 1L << square;
        }

        // Initializes the precomputed square outline masks
        this.squareOutlineMasks = new long[49];

        // Generate corner masks (0, 6, 42, 48)
        squareOutlineMasks[48] = 0x0001830000000000L; // Top left
        squareOutlineMasks[42] = 0x00000C1800000000L; // Top right
        squareOutlineMasks[6] = 0x0000000000003060L; // Bottom left
        squareOutlineMasks[0] = 0x0000000000000183L; // Bottom right

        // Generate inner masks (8-12, 15-19, 22-26, 29-33, 36-40)
        long mask = 0x000000000001C387L; // Base mask
        for (int row = 1; row <= 5; row++) {
            int start = row * 7 + 1;
            for (int shift = 0; shift < 5; shift++) {
                squareOutlineMasks[start + shift] = mask << (row - 1) * 7 + shift;
            }
        }

        // Generate top masks (47-43)
        long x = createMask(new int[]{44, 43, 42, 37, 36, 35});
        squareOutlineMasks[43] = x;
        squareOutlineMasks[44] = x << 1;
        squareOutlineMasks[45] = x << 2;
        squareOutlineMasks[46] = x << 3;
        squareOutlineMasks[47] = x << 4;

        // Generate bottom masks (5-1)
        long y = createMask(new int[]{9, 8, 7, 2, 1, 0});
        squareOutlineMasks[1] = y;
        squareOutlineMasks[2] = y << 1;
        squareOutlineMasks[3] = y << 2;
        squareOutlineMasks[4] = y << 3;
        squareOutlineMasks[5] = y << 4;

        // Generate left masks
        long z = createMask(new int[]{20, 19, 13, 12, 6, 5});
        squareOutlineMasks[13] = z;
        squareOutlineMasks[20] = z << 7;
        squareOutlineMasks[27] = z << 14;
        squareOutlineMasks[34] = z << 21;
        squareOutlineMasks[41] = z << 28;

        // Generate right masks
        long u = createMask(new int[]{15, 14, 8, 7, 1, 0});
        squareOutlineMasks[7] = u;
        squareOutlineMasks[14] = u << 7;
        squareOutlineMasks[21] = u << 14;
        squareOutlineMasks[28] = u << 21;
        squareOutlineMasks[35] = u << 28;

    }

    /**
     * Retrieves the single instance of StateHandler.
     * <p>
     * This method implements the Singleton pattern, ensuring that only one
     * instance of StateHandler exists throughout the application.
     * It is thread-safe and lazily initializes the instance.
     *
     * @return the single instance of StateHandler
     */
    public static synchronized StateHandler getInstance() {
        if (instance == null) instance = new StateHandler();
        return instance;
    }

    /**
     * Creates a mask from the given square indices.
     * <p>
     * This helper method generates a mask where the bits corresponding to
     * the specified square indices are set to 1.
     *
     * @param squares an array of square indices for which to set bits in the mask
     * @return a long value representing the generated bitmask/bitboard
     */
    public long createMask(int[] squares) {
        long mask = 0L;
        for (int square : squares) mask |= 1L << square;
        return mask;
    }

    /**
     * Applies a move to the given state, transitioning it to the next state.
     *
     * @param state  the current state to modify
     * @param square the square representing the move to apply
     */
    public boolean applyMove(State state, int square) {

        // Gracefully handling the input
        if (square < 0 || square > 48) {
            System.out.println("Invalid move: Square index must be between 0 and 48 (inclusive).");
            return false;
        }

        Player targetPlayer = state.getNextPlayer();
        long currentBitboard = state.getBitboard(targetPlayer);

        // Get mask of squares where next move can be placed (following game rules)
        long validMoveMask = getValidMoveMask(state);

        if ((validMoveMask & squareMasks[square]) == 0) {
            System.out.println("Can't play that move currently!");
            return false;
        }

        // Update this player bitboard, applying his move
        state.setBitboard(targetPlayer, currentBitboard | squareMasks[square]);
        state.switchPlayer();

        // Update outline accumulator
        state.setOutlineAccumulator(state.getOutlineAccumulator() | squareOutlineMasks[square]);

        // Setting the last move played
        state.setLastMove(square);

        return true;
    }

    /**
     * Computes the bitmask of valid moves for the current player.
     * <p>
     * This method evaluates the current game state and uses precomputed masks
     * to generate a bitmask where each bit set to 1 represents a valid square
     * for the next move, based on the game rules.
     * <p>
     * As part of StateHandler, this method ensures efficient computation by
     * leveraging precomputed data and encapsulated logic for move validation.
     *
     * @param state the current game state
     * @return a long value representing the bitmask of valid moves
     */
    public long getValidMoveMask(State state) {

        long combined = state.getCombinedBitboard();

        /*
        Check if the board is empty, if board is empty by the rules
        of the game this means that player can place his move on
        any square on the board, thus we return mask that has all 1's
         */
        if (combined == 0) return ~combined;

        // Otherwise we have more work to do, this part is for standard move
        int lastSquare = state.getLastMove();
        long lastSquareOutline = squareOutlineMasks[lastSquare];

        // Make clip so we can cut lastSquareOutline
        long clip = combined & lastSquareOutline;
        long standardMoves = lastSquareOutline ^ clip;

        /*
        But it can happen that our player have closed itself in so-called island
        (at least that is what I like to call it) and it has no way out, so
        we need to make mask for non-standard move
         */

        // If there is still standard move available, return that
        if (standardMoves != 0) return standardMoves;

        // Return mask for the non-standard move
        return combined ^ state.getOutlineAccumulator();
    }

    /**
     * Extracts the indices of all set bits (1s) in the given bitboard.
     *
     * @param bitboard A long representing the bitboard, where each bit set to 1 indicates a move.
     * @return A list of integers representing the indices of the set bits (0-based).
     * @implNote This method uses {@code Long.numberOfTrailingZeros} to find the index of the least
     * significant bit (LSB) set to 1 and clears it using {@code bitboard &= bitboard - 1}.
     * Runs in O(k), where k is the number of set bits in the bitboard.
     */
    public List<Integer> bitboardToMoves(long bitboard) {
        List<Integer> moves = new ArrayList<>();

        while (bitboard != 0) {
            int move = Long.numberOfTrailingZeros(bitboard);
            moves.add(move);
            bitboard &= bitboard - 1;
        }
        return moves;
    }


    /**
     * Prints the given bitboard as a 7x7 grid.
     *
     * @param bitboard The bitboard to display.
     */
    public void print(long bitboard) {
        bitboard <<= 15;

        StringBuilder sb = new StringBuilder();
        for (int row = 7; row > 0; row--) {
            for (int col = 0; col < 7; col++) {
                int square = (row * 7) - col - 1;
                long bit = 1L << square << 15;

                int value = (bitboard & bit) != 0 ? 1 : 0;
                sb.append(value).append(' ');
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
