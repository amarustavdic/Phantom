package com.ustavdica;

/**
 *  Represents the state of the game called 4mation.
 */
public class State {

    private long[] bitboards;
    private long metadata;

    // Experimenting for now
    private long magicMask = 0L;
    private long[] masks = new long[49];

    public State(Player startingPlayer) {
        this.bitboards = new long[2];
        this.metadata = 0L;
        setNextPlayer(startingPlayer);
    }


    public boolean makeMove(int square) {

        // If board is empty apply move and return true
        if ((bitboards[Player.BLUE.ordinal()] | bitboards[Player.PINK.ordinal()]) == 0) {
            bitboards[getNextPlayer().ordinal()] |= 1L << square;
            switchPlayer();

            long mask = (1L << 41) | (1L << 40) | (1L << 34) | (1L << 33) | (1L << 27) | (1L << 26);

            print(mask);

            return true;
        } else {
            // If board has last move validate if move can be played, then apply it




            return false;
        }
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
                if ((blueBoard & bit) > 0) background = "\u001B[48;5;21m";
                if ((pinkBoard & bit) > 0) background = "\u001B[48;5;201m";

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
