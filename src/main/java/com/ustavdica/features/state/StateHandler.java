package com.ustavdica.features.state;

import java.util.ArrayList;
import java.util.Collections;
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

    // Precomputed masks for speed
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
        if (square < 0 || square > 48) return false;

        Player targetPlayer = state.getNextPlayer();
        long currentBitboard = state.getBitboard(targetPlayer);

        // Get mask of squares where next move can be placed (following game rules)
        long validMoveMask = getValidMoveMask(state);

        if ((validMoveMask & squareMasks[square]) == 0) {
            // This move cannot be played by the game rules, thus false
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
        if (combined == 0) return 0x1ffffffffffffL;

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
     * Retrieves a list of all legal moves for the current player.
     *
     * @param state the current game state
     * @return a list of square indices representing all legal moves
     */
    public List<Integer> getAvailableMoves(State state) {
        return bitboardToMoves(getValidMoveMask(state));
    }

    // This one is needed for mcts simulation phase where you do random games
    public boolean performRandomMove(State state) {
        List<Integer> moves = getAvailableMoves(state);
        if (moves.isEmpty()) return false;
        Collections.shuffle(moves);
        int randomMove = moves.getFirst();
        applyMove(state, randomMove);
        return true;
    }

    /**
     * Checks if there is a winner in the current game state.
     *
     * @param state the current game state to evaluate
     * @return true if a winner is determined, false otherwise
     */
    public boolean hasWinner(State state) {

        // TODO: This function cry's for refactoring, to much wasted compute

        long[] rowMasks = new long[7];
        rowMasks[0] = 0x7fL;
        rowMasks[1] = 0x7fL << 7;
        rowMasks[2] = 0x7fL << 14;
        rowMasks[3] = 0x7fL << 21;
        rowMasks[4] = 0x7fL << 28;
        rowMasks[5] = 0x7fL << 35;
        rowMasks[6] = 0x7fL << 32;
        long[] colMasks = new long[7];
        colMasks[0] = 0x1010101010101L;
        colMasks[1] = 0x1010101010101L << 1;
        colMasks[2] = 0x1010101010101L << 2;
        colMasks[3] = 0x1010101010101L << 3;
        colMasks[4] = 0x1010101010101L << 4;
        colMasks[5] = 0x1010101010101L << 5;
        colMasks[6] = 0x1010101010101L << 6;

        // From top-left to bottom-right
        long[] diagonalMasks = new long[7];
        diagonalMasks[0] = createMask(new int[]{3, 11, 19, 27});
        diagonalMasks[1] = createMask(new int[]{2, 10, 18, 26, 34});
        diagonalMasks[2] = createMask(new int[]{1, 9, 17, 25, 33, 41});
        diagonalMasks[3] = createMask(new int[]{0, 8, 16, 24, 32, 40, 48});
        diagonalMasks[4] = createMask(new int[]{7, 15, 23, 31, 39, 47});
        diagonalMasks[5] = createMask(new int[]{14, 22, 30, 38, 46});
        diagonalMasks[6] = createMask(new int[]{21, 29, 37, 45});

        // From top-right to bottom-left
        long[] antiDiagonalMasks = new long[7];
        antiDiagonalMasks[0] = createMask(new int[]{3, 9, 15, 21});
        antiDiagonalMasks[1] = createMask(new int[]{4, 10, 16, 22, 28});
        antiDiagonalMasks[2] = createMask(new int[]{5, 11, 17, 23, 29, 35});
        antiDiagonalMasks[3] = createMask(new int[]{6, 12, 18, 24, 30, 36, 42});
        antiDiagonalMasks[4] = createMask(new int[]{13, 19, 25, 31, 37, 43});
        antiDiagonalMasks[5] = createMask(new int[]{20, 26, 32, 38, 44});
        antiDiagonalMasks[6] = createMask(new int[]{27, 33, 39, 45});

        int lastMove = state.getLastMove();

        int rowIndex = lastMove / 7;
        int colIndex = lastMove % 7;


        // Checking if there is win for blue
        long blue = state.getBitboard(Player.BLUE);

        // Check if there is win on row
        long rowBlue = rowMasks[rowIndex] & blue;
        int rowBc = Long.bitCount(rowBlue);

        if (rowBc == 4) {
            System.out.println("Blue wins");
            return true;
        }

        // Check if there is win on col
        long colBlue = colMasks[colIndex] & blue;
        int colBc = Long.bitCount(colBlue);

        if (colBc == 4) {
            System.out.println("Blue wins");
            return true;
        }

        // Check if there is win on any diagonal
        for (long diagonalMask : diagonalMasks) {
            int c = Long.bitCount(diagonalMask & blue);
            if (c == 4) {
                System.out.println("Blue wins");
                return true;
            }
        }

        // Check if there is win on any anti-diagonal
        for (long antiDiagonalMask : antiDiagonalMasks) {
            int c = Long.bitCount(antiDiagonalMask & blue);
            if (c == 4) {
                System.out.println("Blue wins");
                return true;
            }
        }



        return false;
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
