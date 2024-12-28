package com.ustavdica;

import java.util.ArrayList;
import java.util.List;

/**
 *  Represents the state of the game called 4mation.
 */
public class State {

    private final long[] bitboards;
    private long metadata;

    // Experimenting for now
    private long tracer = 0L;
    private long nextValidMovesMask = 0L;
    private final long[] masks = new long[49];

    public State(Player startingPlayer) {
        this.bitboards = new long[2];
        this.metadata = 0L;
        setNextPlayer(startingPlayer);
        setMasks();
    }



    // Some testing for the program structure improvement


    // Internal methods accessible by StateHandler

    long[] getBitboards() {
        return bitboards;
    }









    // ------- working on this rn

    private void setMasks() {

        // Generate corner masks (0, 6, 42, 48)
        masks[48] = 0x0001830000000000L; // Top left
        masks[42] = 0x00000C1800000000L; // Top right
        masks[6] =  0x0000000000003060L; // Bottom left
        masks[0] =  0x0000000000000183L; // Bottom right

        // Generate inner masks (8-12, 15-19, 22-26, 29-33, 36-40)
        long mask = 0x000000000001C387L; // Base mask
        for (int row = 1; row <= 5; row++) {
            int start = row * 7 + 1;
            for (int shift = 0; shift < 5; shift++) {
                masks[start + shift] = mask << (row - 1) * 7 + shift;
            }
        }

        // Generate top masks (47-43)
        long x = generateMask(new int[]{44, 43, 42, 37, 36, 35});
        masks[43] = x;
        masks[44] = x << 1;
        masks[45] = x << 2;
        masks[46] = x << 3;
        masks[47] = x << 4;

        // Generate bottom masks (5-1)
        long y = generateMask(new int[]{9, 8, 7, 2, 1, 0});
        masks[1] = y;
        masks[2] = y << 1;
        masks[3] = y << 2;
        masks[4] = y << 3;
        masks[5] = y << 4;

        // Generate left masks
        long z = generateMask(new int[]{20, 19, 13, 12, 6, 5});
        masks[13] = z;
        masks[20] = z << 7;
        masks[27] = z << 14;
        masks[34] = z << 21;
        masks[41] = z << 28;

        // Generate right masks
        long u = generateMask(new int[]{15, 14, 8, 7, 1, 0});
        masks[7] = u;
        masks[14] = u << 7;
        masks[21] = u << 14;
        masks[28] = u << 21;
        masks[35] = u << 28;

    }

    private long generateMask(int[] squares) {
        long mask = 0L;
        for (int square : squares) mask |= 1L << square;
        return mask;
    }

    public boolean makeMove(int square) {
        long squareMask = 1L << square;
        long combinedBoard = getCombinedBitboard();

        // Handle the first move (empty board)
        if (combinedBoard == 0) {
            return applyFirstMove(squareMask, square);
        }

        // Validate and handle standard and non-standard move
        if ((nextValidMovesMask & squareMask) != 0) {
            // Standard move
            return applyMove(squareMask, square);
        } else {
            // Non-standard move
            return validateAndApplyNonStandardMove(squareMask, square);
        }
//
//
//
//        // If board is empty apply move and return true
//        if (getCombinedBitboard() == 0) {
//
//            // Apply the move
//            bitboards[getNextPlayer().ordinal()] |= squareMask;
//            switchPlayer();
//
//            // Calculate next valid moves
//            long x = squareMask ^ masks[square];
//
//            // Remember what are next valid moves (save mask)
//            nextValidMovesMask = (x & getCombinedBitboard()) ^ x;
//
//            // Update tracer mask
//            tracer |= x;
//
//            return true;
//        } else {
//            // If board has last move validate if move can be played, then apply it
//
//            // Check if wanted move can be played, if not exit
//            if ((nextValidMovesMask & squareMask) == 0) {
//
//                // TODO: There is bug here figure out what exactly, happens, I know I know shitty code needs refactoring
//
//                // The problem is that when you create an island, and then yes you can play on squares that are
//                // just next to that island, but the bug is that it lets you play more times near that island...
//
//
//                System.out.println("Not a standard move!");
//
//                // Check if the played move is in tracer ^ combined
//                long y = tracer ^ getCombinedBitboard();
//
//                print(y);
//
//                if ((y & squareMask) == 0) {
//                    return false;
//                } else {
//                    // Apply the move
//                    bitboards[getNextPlayer().ordinal()] |= squareMask;
//                    long x = squareMask ^ masks[square];
//                    nextValidMovesMask = (x & getCombinedBitboard()) ^ x;
//                    tracer |= x;
//                    switchPlayer();
//                    return true;
//                }
//            }
//
//            // Apply the move
//            bitboards[getNextPlayer().ordinal()] |= squareMask;
//            long x = squareMask ^ masks[square];
//            nextValidMovesMask = (x & getCombinedBitboard()) ^ x;
//            tracer |= x;
//            switchPlayer();
//
//            return true;
//        }
    }

    /**
     * Applies the first move on an empty board.
     */
    private boolean applyFirstMove(long squareMask, int square) {
        bitboards[getNextPlayer().ordinal()] |= squareMask;
        switchPlayer();
        updateMasks(squareMask, square);
        return true;
    }

    /**
     * Applies a move and updates relevant masks.
     */
    private boolean applyMove(long squareMask, int square) {

        // Looks like a quick fix
        if ((nextValidMovesMask & squareMask) == 0) return false;

        bitboards[getNextPlayer().ordinal()] |= squareMask;
        updateMasks(squareMask, square);
        switchPlayer();
        return true;
    }

    /**
     * Validates and applies a non-standard move if it is allowed.
     */
    private boolean validateAndApplyNonStandardMove(long squareMask, int square) {
        System.out.println("Not a standard move!");

        long alternativeMask = tracer ^ getCombinedBitboard();
        if ((alternativeMask & squareMask) == 0) {
            // Move is invalid
            return false;
        }

        // Apply the non-standard move
        return applyMove(squareMask, square);
    }

    /**
     * Updates the next valid moves mask and tracer after a move.
     */
    private void updateMasks(long squareMask, int square) {
        long x = squareMask ^ masks[square];
        long combinedBoard = getCombinedBitboard();

        nextValidMovesMask = (x & combinedBoard) ^ x;
        tracer |= x;
    }

    public long getTracerMask() {
        return tracer;
    }

    public long getNextValidMovesMask() {
        return nextValidMovesMask;
    }

    /**
     * Retrieves a list of legal moves based on the current game state.
     *
     * @return A list of integers representing the indices (square numbers) of legal moves.
     *         If there are standard moves available, they are returned. Otherwise, alternative
     *         moves are computed as the difference between the combined bitboard and the tracer.
     */
    public List<Integer> getLegalMoves() {
        if (nextValidMovesMask != 0) {
            return bitboardToMoves(nextValidMovesMask);
        } else {
            long alternative = getCombinedBitboard() ^ tracer;
            return bitboardToMoves(alternative);
        }
    }

    /**
     * Extracts the indices of all set bits (1s) in the given bitboard.
     *
     * @param bitboard A long representing the bitboard, where each bit set to 1 indicates a move.
     * @return A list of integers representing the indices of the set bits (0-based).
     *
     * @implNote This method uses {@code Long.numberOfTrailingZeros} to find the index of the least
     *           significant bit (LSB) set to 1 and clears it using {@code bitboard &= bitboard - 1}.
     *           Runs in O(k), where k is the number of set bits in the bitboard.
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
     * Retrieves the combined bitboard representing all occupied squares.
     *
     * @return A long value where bits set to 1 indicate occupied squares by any player.
     */
    public long getCombinedBitboard() {
        return bitboards[Player.BLUE.ordinal()] | bitboards[Player.PINK.ordinal()];
    }

    /**
     * Switches the current player to the next player.
     */
    private void switchPlayer() {
        metadata ^= 0x8000000000000000L;
    }

    /**
     * Retrieves the player whose turn is next.
     *
     * @return The next player to make a move (Player.BLUE or Player.PINK).
     */
    public Player getNextPlayer() {
        return (metadata & 0x8000000000000000L) == 0 ? Player.BLUE : Player.PINK;
    }

    /**
     * Sets the next player to make a move.
     *
     * @param nextPlayer The player to set as the next player (Player.BLUE or Player.PINK).
     */
    private void setNextPlayer(Player nextPlayer) {
        if (nextPlayer == Player.PINK) metadata |= 0x8000000000000000L;
        else metadata &= 0x7FFFFFFFFFFFFFFFL;
    }

    /**
     *  Prints the current state of the game board to the console.<br>
     *  <br>
     *  The board is displayed as a 7x7 grid. Each cell shows the square number<br>
     *  and is color-coded based on the player occupying the square:<br>
     *  <br>
     *  - Blue squares are highlighted with a blue background.<br>
     *  - Pink squares are highlighted with a pink background.<br>
     *  - Empty squares are uncolored.<br>
     *  <br>
     *  Square numbering starts from 0 (bottom-right corner) and increments<br>
     *  right-to-left, bottom-to-top.<br>
     *  <br>
     *  Example Output:<br>
     *  ----------------<br>
     *  48 47 46 45 44 43 42<br>
     *  41 40 39 38 37 36 35<br>
     *  ...<br>
     *  6  5  4  3  2  1  0<br>
     *  <br>
     *  Color Key:<br>
     *  - Blue: Player.BLUE<br>
     *  - Pink: Player.PINK<br>
     */
    public void print() {
        long blueBoard = bitboards[Player.BLUE.ordinal()];
        long pinkBoard = bitboards[Player.PINK.ordinal()];

        blueBoard <<= 15;
        pinkBoard <<= 15;

        StringBuilder sb = new StringBuilder();
        for (int row = 7; row > 0; row--) {

            for (int col = 0; col < 7 ; col++) {
                int square = (row * 7) - col - 1;
                long bit = 1L << square << 15;

                String background = "\u001B[0m";
                if ((blueBoard & bit) != 0) background = "\u001B[48;5;21m";
                if ((pinkBoard & bit) != 0) background = "\u001B[48;5;201m";

                sb.append(background);
                if (square < 10) sb.append(" ");
                sb.append(square).append(' ').append("\u001B[0m");
            }
            sb.append("\n");
        }
        System.out.println(sb);
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
            for (int col = 0; col < 7 ; col++) {
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
