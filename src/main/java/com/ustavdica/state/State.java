package com.ustavdica.state;

/**
 *  Represents the state of the game.
 */
public class State {

    private final long[] bitboards;
    private long metadata;

    public State(Player startingPlayer) {
        this.bitboards = new long[2];
        this.metadata = 0L;
        setNextPlayer(startingPlayer);
    }


    // Internal methods accessible by StateHandler

    /**
     * Sets the next player to make a move.
     *
     * @param nextPlayer The player to set as the next player (Player.BLUE or Player.PINK).
     */
    void setNextPlayer(Player nextPlayer) {
        if (nextPlayer == Player.PINK) metadata |= 0x8000000000000000L;
        else metadata &= 0x7FFFFFFFFFFFFFFFL;
    }

    /**
     * Switches the current player to the next player.
     */
    void switchPlayer() {
        metadata ^= 0x8000000000000000L;
    }

    /**
     * Retrieves the bitboard for the specified player.
     *
     * @param player the player whose bitboard is to be retrieved
     * @return the bitboard representing the player's current state
     */
    long getBitboard(Player player) {
        return bitboards[player.ordinal()];
    }

    /**
     * Sets the bitboard for the specified player.
     *
     * @param player   the player whose bitboard is to be updated
     * @param bitboard the new bitboard representing the player's state
     */
    void setBitboard(Player player, long bitboard) {
        bitboards[player.ordinal()] = bitboard;
    }


    // Bellow are methods that are accessible to every class

    /**
     * Retrieves the player whose turn is next.
     *
     * @return The next player to make a move (Player.BLUE or Player.PINK).
     */
    public Player getNextPlayer() {
        return (metadata & 0x8000000000000000L) == 0 ? Player.BLUE : Player.PINK;
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








    // TODO: Refactor commented out code bellow



//    public boolean makeMove(int square) {
//        long squareMask = 1L << square;
//        long combinedBoard = getCombinedBitboard();
//
//        // Handle the first move (empty board)
//        if (combinedBoard == 0) {
//            return applyFirstMove(squareMask, square);
//        }
//
//        // Validate and handle standard and non-standard move
//        if ((nextValidMovesMask & squareMask) != 0) {
//            // Standard move
//            return applyMove(squareMask, square);
//        } else {
//            // Non-standard move
//            return validateAndApplyNonStandardMove(squareMask, square);
//        }
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
//    }

//    /**
//     * Applies the first move on an empty board.
//     */
//    private boolean applyFirstMove(long squareMask, int square) {
//        bitboards[getNextPlayer().ordinal()] |= squareMask;
//        switchPlayer();
//        updateMasks(squareMask, square);
//        return true;
//    }

//    /**
//     * Applies a move and updates relevant masks.
//     */
//    private boolean applyMove(long squareMask, int square) {
//
//        // Looks like a quick fix
//        if ((nextValidMovesMask & squareMask) == 0) return false;
//
//        bitboards[getNextPlayer().ordinal()] |= squareMask;
//        updateMasks(squareMask, square);
//        switchPlayer();
//        return true;
//    }

//    /**
//     * Validates and applies a non-standard move if it is allowed.
//     */
//    private boolean validateAndApplyNonStandardMove(long squareMask, int square) {
//        System.out.println("Not a standard move!");
//
//        long alternativeMask = tracer ^ getCombinedBitboard();
//        if ((alternativeMask & squareMask) == 0) {
//            // Move is invalid
//            return false;
//        }
//
//        // Apply the non-standard move
//        return applyMove(squareMask, square);
//    }

//    /**
//     * Updates the next valid moves mask and tracer after a move.
//     */
//    private void updateMasks(long squareMask, int square) {
//        long x = squareMask ^ masks[square];
//        long combinedBoard = getCombinedBitboard();
//
//        nextValidMovesMask = (x & combinedBoard) ^ x;
//        tracer |= x;
//    }


//    /**
//     * Retrieves a list of legal moves based on the current game state.
//     *
//     * @return A list of integers representing the indices (square numbers) of legal moves.
//     *         If there are standard moves available, they are returned. Otherwise, alternative
//     *         moves are computed as the difference between the combined bitboard and the tracer.
//     */
//    public List<Integer> getLegalMoves() {
//        if (nextValidMovesMask != 0) {
//            return bitboardToMoves(nextValidMovesMask);
//        } else {
//            long alternative = getCombinedBitboard() ^ tracer;
//            return bitboardToMoves(alternative);
//        }
//    }

}
