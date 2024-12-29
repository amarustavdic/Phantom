package com.ustavdica.state;

/**
 * Represents the state of the game.
 */
public class State {

    // Number of bits needed to store the last move (0-48)
    private static final int LAST_MOVE_BITS = 6; // 6 bits can represent numbers from 0 to 63

    private final long[] bitboards;
    private long outlineAccumulator;
    private long metadata;

    public State(Player startingPlayer) {
        this.bitboards = new long[2];
        this.metadata = 0L;
        setNextPlayer(startingPlayer);
    }

    /**
     * Creates a deep copy of the given State object.
     *
     * @param other the State object to copy
     */
    public State(State other) {
        this.bitboards = other.bitboards.clone();
        this.outlineAccumulator = other.outlineAccumulator;
        this.metadata = other.metadata;
    }

    // Internal methods accessible by StateHandler

    /**
     * Sets the outline accumulator for the state.
     * <p>
     * This method updates the outline accumulator, which tracks the combined
     * outline masks of all applied moves in the game.
     *
     * @param outlineAccumulator the new outline accumulator value
     */
    void setOutlineAccumulator(long outlineAccumulator) {
        this.outlineAccumulator = outlineAccumulator;
    }

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
     * Sets the bitboard for the specified player.
     *
     * @param player   the player whose bitboard is to be updated
     * @param bitboard the new bitboard representing the player's state
     */
    void setBitboard(Player player, long bitboard) {
        bitboards[player.ordinal()] = bitboard;
    }

    /**
     * Sets the last move in the metadata.
     *
     * @param square the index of the square representing the last move (0-48)
     */
    void setLastMove(int square) {
        // Clear the last move bits (they occupy the least significant 6 bits)
        metadata &= -(1L << LAST_MOVE_BITS);

        // Set the new last move
        metadata |= (square & ((1L << LAST_MOVE_BITS) - 1));
    }


    // Bellow are methods that are accessible to every class

    /**
     * Retrieves the outline accumulator for the state.
     * <p>
     * The outline accumulator represents the combined outline masks of all
     * moves applied so far in the game.
     *
     * @return the current value of the outline accumulator
     */
    public long getOutlineAccumulator() {
        return outlineAccumulator;
    }

    /**
     * Retrieves the last move from the metadata.
     *
     * @return the index of the square representing the last move (0-48)
     */
    public int getLastMove() {
        // Extract the last move bits (assuming they occupy the least significant 6 bits)
        return (int) (metadata & ((1L << LAST_MOVE_BITS) - 1));
    }

    /**
     * Retrieves the bitboard for the specified player.
     *
     * @param player the player whose bitboard is to be retrieved
     * @return the bitboard representing the player's current state
     */
    public long getBitboard(Player player) {
        return bitboards[player.ordinal()];
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
     * Retrieves the combined bitboard representing all occupied squares.
     *
     * @return A long value where bits set to 1 indicate occupied squares by any player.
     */
    public long getCombinedBitboard() {
        return bitboards[Player.BLUE.ordinal()] | bitboards[Player.PINK.ordinal()];
    }

    public boolean isTerminal() {
        // TODO: Implement check if state is terminal...
        return false;
    }

    /**
     * Prints the current state of the game board to the console.<br>
     * <br>
     * The board is displayed as a 7x7 grid. Each cell shows the square number<br>
     * and is color-coded based on the player occupying the square:<br>
     * <br>
     * - Blue squares are highlighted with a blue background.<br>
     * - Pink squares are highlighted with a pink background.<br>
     * - Empty squares are uncolored.<br>
     * <br>
     * Square numbering starts from 0 (bottom-right corner) and increments<br>
     * right-to-left, bottom-to-top.<br>
     * <br>
     * Example Output:<br>
     * ----------------<br>
     * 48 47 46 45 44 43 42<br>
     * 41 40 39 38 37 36 35<br>
     * ...<br>
     * 6  5  4  3  2  1  0<br>
     * <br>
     * Color Key:<br>
     * - Blue: Player.BLUE<br>
     * - Pink: Player.PINK<br>
     */
    public void print() {
        long blueBoard = bitboards[Player.BLUE.ordinal()];
        long pinkBoard = bitboards[Player.PINK.ordinal()];

        blueBoard <<= 15;
        pinkBoard <<= 15;

        StringBuilder sb = new StringBuilder();
        for (int row = 7; row > 0; row--) {

            for (int col = 0; col < 7; col++) {
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

}
