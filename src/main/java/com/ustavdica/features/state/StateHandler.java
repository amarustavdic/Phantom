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

    private long[] SQUARE_BIT_MASKS;
    private long[] SQUARE_OUTLINE_MASKS;
    private long[] ROW_MASKS;
    private long[] COLUMN_MASKS;
    private long[] DIAGONAL_MASKS;
    private long[] ANTI_DIAGONAL_MASKS;


    // Private constructor to prevent instantiation
    private StateHandler() {
        initializeSquareBitMasks();
        initializeSquareOutlineMasks();
        initializeRowMasks();
        initializeColumnMasks();
        initializeDiagonalMasks();
        initializeAntiDiagonalMasks();
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
     * Initializes bit masks for each square on a 7x7 board.
     * <p>
     * These masks are precomputed and are allowing efficient manipulation
     * and checks of individual squares on the board.
     */
    private void initializeSquareBitMasks() {
        SQUARE_BIT_MASKS = new long[49];
        for (int square = 0; square < SQUARE_BIT_MASKS.length; square++) {
            SQUARE_BIT_MASKS[square] = 1L << square;
        }
    }

    /**
     * Initializes outline masks, which are needed for calculating available moves
     */
    private void initializeSquareOutlineMasks() {
        SQUARE_OUTLINE_MASKS = new long[49];

        // Generate corner masks (0, 6, 42, 48)
        SQUARE_OUTLINE_MASKS[48] = 0x0001830000000000L; // Top left
        SQUARE_OUTLINE_MASKS[42] = 0x00000C1800000000L; // Top right
        SQUARE_OUTLINE_MASKS[6] = 0x0000000000003060L; // Bottom left
        SQUARE_OUTLINE_MASKS[0] = 0x0000000000000183L; // Bottom right

        // Generate inner masks (8-12, 15-19, 22-26, 29-33, 36-40)
        long mask = 0x000000000001C387L; // Base mask
        for (int row = 1; row <= 5; row++) {
            int start = row * 7 + 1;
            for (int shift = 0; shift < 5; shift++) {
                SQUARE_OUTLINE_MASKS[start + shift] = mask << (row - 1) * 7 + shift;
            }
        }

        // Generate top masks (47-43)
        long x = createMask(new int[]{44, 43, 42, 37, 36, 35});
        SQUARE_OUTLINE_MASKS[43] = x;
        SQUARE_OUTLINE_MASKS[44] = x << 1;
        SQUARE_OUTLINE_MASKS[45] = x << 2;
        SQUARE_OUTLINE_MASKS[46] = x << 3;
        SQUARE_OUTLINE_MASKS[47] = x << 4;

        // Generate bottom masks (5-1)
        long y = createMask(new int[]{9, 8, 7, 2, 1, 0});
        SQUARE_OUTLINE_MASKS[1] = y;
        SQUARE_OUTLINE_MASKS[2] = y << 1;
        SQUARE_OUTLINE_MASKS[3] = y << 2;
        SQUARE_OUTLINE_MASKS[4] = y << 3;
        SQUARE_OUTLINE_MASKS[5] = y << 4;

        // Generate left masks
        long z = createMask(new int[]{20, 19, 13, 12, 6, 5});
        SQUARE_OUTLINE_MASKS[13] = z;
        SQUARE_OUTLINE_MASKS[20] = z << 7;
        SQUARE_OUTLINE_MASKS[27] = z << 14;
        SQUARE_OUTLINE_MASKS[34] = z << 21;
        SQUARE_OUTLINE_MASKS[41] = z << 28;

        // Generate right masks
        long u = createMask(new int[]{15, 14, 8, 7, 1, 0});
        SQUARE_OUTLINE_MASKS[7] = u;
        SQUARE_OUTLINE_MASKS[14] = u << 7;
        SQUARE_OUTLINE_MASKS[21] = u << 14;
        SQUARE_OUTLINE_MASKS[28] = u << 21;
        SQUARE_OUTLINE_MASKS[35] = u << 28;
    }

    /**
     * Initializes row masks for a 7x7 board.
     * Each row mask is a bitmask representing the positions in a specific row
     * of the board, where:
     * - Row 0 corresponds to the bottom row.
     * - Row 6 corresponds to the top row.
     * <p>
     * These masks are used for efficiently checking if there is a winner by determining
     * if a specific row contains a winning condition.
     */
    private void initializeRowMasks() {
        ROW_MASKS = new long[7];
        ROW_MASKS[0] = createMask(new int[]{6, 5, 4, 3, 2, 1, 0});
        ROW_MASKS[1] = createMask(new int[]{13, 12, 11, 10, 9, 8, 7});
        ROW_MASKS[2] = createMask(new int[]{20, 19, 18, 17, 16, 15, 14});
        ROW_MASKS[3] = createMask(new int[]{27, 26, 25, 24, 23, 22, 21});
        ROW_MASKS[4] = createMask(new int[]{34, 33, 32, 31, 30, 29, 28});
        ROW_MASKS[5] = createMask(new int[]{41, 40, 39, 38, 37, 36, 35});
        ROW_MASKS[6] = createMask(new int[]{48, 47, 46, 45, 44, 43, 42});
    }

    /**
     * Initializes column masks for a 7x7 board.
     * Each column mask is a bitmask representing the positions in a specific column
     * of the board, where:
     * - Column 0 corresponds to the most right column.
     * - Column 6 corresponds to the most left column.
     * <p>
     * These masks are used for efficiently checking if there is a winner by determining
     * if a specific column contains a winning condition.
     */
    private void initializeColumnMasks() {
        COLUMN_MASKS = new long[7];
        COLUMN_MASKS[0] = createMask(new int[]{0, 7, 14, 21, 28, 35, 42});
        COLUMN_MASKS[1] = createMask(new int[]{1, 8, 15, 22, 29, 36, 43});
        COLUMN_MASKS[2] = createMask(new int[]{2, 9, 16, 23, 30, 37, 44});
        COLUMN_MASKS[3] = createMask(new int[]{3, 10, 17, 24, 31, 38, 45});
        COLUMN_MASKS[4] = createMask(new int[]{4, 11, 18, 25, 32, 39, 46});
        COLUMN_MASKS[5] = createMask(new int[]{5, 12, 19, 26, 33, 40, 47});
        COLUMN_MASKS[6] = createMask(new int[]{6, 13, 20, 27, 34, 41, 48});
    }

    /**
     * Initializes diagonal masks for a 7x7 board, focusing on diagonals
     * that run from the top-left to the bottom-right (diagonals) of the board.
     * <p>
     * Each diagonal mask is a bitmask representing the positions in a specific diagonal,
     * allowing efficient checks for winning conditions along these diagonals.
     */
    private void initializeDiagonalMasks() {
        DIAGONAL_MASKS = new long[7];
        DIAGONAL_MASKS[0] = createMask(new int[]{3, 11, 19, 27});
        DIAGONAL_MASKS[1] = createMask(new int[]{2, 10, 18, 26, 34});
        DIAGONAL_MASKS[2] = createMask(new int[]{1, 9, 17, 25, 33, 41});
        DIAGONAL_MASKS[3] = createMask(new int[]{0, 8, 16, 24, 32, 40, 48});
        DIAGONAL_MASKS[4] = createMask(new int[]{7, 15, 23, 31, 39, 47});
        DIAGONAL_MASKS[5] = createMask(new int[]{14, 22, 30, 38, 46});
        DIAGONAL_MASKS[6] = createMask(new int[]{21, 29, 37, 45});
    }

    /**
     * Initializes anti-diagonal masks for a 7x7 board, focusing on diagonals
     * that run from the top-right to the bottom-left of the board.
     * <p>
     * Each anti-diagonal mask is a bitmask representing the positions in a specific anti-diagonal,
     * enabling efficient checks for winning conditions along these diagonals.
     */
    private void initializeAntiDiagonalMasks() {
        ANTI_DIAGONAL_MASKS = new long[7];
        ANTI_DIAGONAL_MASKS[0] = createMask(new int[]{3, 9, 15, 21});
        ANTI_DIAGONAL_MASKS[1] = createMask(new int[]{4, 10, 16, 22, 28});
        ANTI_DIAGONAL_MASKS[2] = createMask(new int[]{5, 11, 17, 23, 29, 35});
        ANTI_DIAGONAL_MASKS[3] = createMask(new int[]{6, 12, 18, 24, 30, 36, 42});
        ANTI_DIAGONAL_MASKS[4] = createMask(new int[]{13, 19, 25, 31, 37, 43});
        ANTI_DIAGONAL_MASKS[5] = createMask(new int[]{20, 26, 32, 38, 44});
        ANTI_DIAGONAL_MASKS[6] = createMask(new int[]{27, 33, 39, 45});
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

        if ((validMoveMask & SQUARE_BIT_MASKS[square]) == 0) {
            // This move cannot be played by the game rules, thus false
            return false;
        }

        // Update this player bitboard, applying his move
        state.setBitboard(targetPlayer, currentBitboard | SQUARE_BIT_MASKS[square]);
        state.switchPlayer();

        // Update outline accumulator
        state.setOutlineAccumulator(state.getOutlineAccumulator() | SQUARE_OUTLINE_MASKS[square]);

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
        long lastSquareOutline = SQUARE_OUTLINE_MASKS[lastSquare];

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
     * Checks if the {@code player} is winner.
     *
     * @param state the current game state to evaluate
     * @param player the player that has made last move
     * @return {@code true} {@code player} has won, {@code false} otherwise.
     */
    public boolean hasWon(State state, Player player) {

        // Bitboard of the player that played last move
        long playersBitboard = state.getBitboard(player);

        // Calculate index based on last move
        int lastMove = state.getLastMove();
        int rowIndex = lastMove / 7;
        int colIndex = lastMove % 7;

        // Check if there is win on row
        long rowMask = ROW_MASKS[rowIndex];
        long extractedRow = playersBitboard & rowMask;


        System.out.println("Has four ones: " + hasFourOnesWithSpacing(extractedRow, 0));


        // Nobody wins yet
        return false;
    }

    /**
     * Checks if there are 4 `1`s in a long number, each separated by a specified number of spaces.
     *
     * @param number The long number to check.
     * @param space  The number of spaces (bits) between each `1`.
     * @return true if there are 4 `1`s with the specified spacing, false otherwise.
     */
    public boolean hasFourOnesWithSpacing(long number, int space) {
        // Build the pattern mask for 4 `1`s with the specified spacing
        long pattern = 0b1;
        for (int i = 1; i < 4; i++) pattern |= (1L << (i * (space + 1)));

        // Slide the pattern across the 64 bit long number
        for (int i = 0; i <= 64 - (4 * (space + 1)) + space; i++) {
            if ((number & (pattern << i)) == (pattern << i)) return true;
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
