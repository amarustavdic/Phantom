package com.ustavdica;

/**
 *  Represents the state of the game called 4mation.
 */
public class State {

    private final long[] bitboards;
    private long metadata;

    // Experimenting for now
    private long magicMask = 0L;
    private final long[] masks = new long[49];

    public State(Player startingPlayer) {
        this.bitboards = new long[2];
        this.metadata = 0L;
        setNextPlayer(startingPlayer);
        setMasks();
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

        // todo Generate top masks

        // todo Generate bottom masks

        // todo Generate left masks

        // todo Generate right masks

    }

    private long generateMask(int[] squares) {
        long mask = 0L;
        for (int i = 0; i < squares.length; i++) {
            mask |= 1L << squares[i];
        }
        return mask;
    }

    public boolean makeMove(int square) {

        // If board is empty apply move and return true
        // if (getCombinedBitboard() == 0) {
            bitboards[getNextPlayer().ordinal()] |= 1L << square;
            switchPlayer();

            // long mask = (1L << 41) | (1L << 40) | (1L << 34) | (1L << 33) | (1L << 27) | (1L << 26);

            //print(masks[48]);
            //print(masks[47]);

            return true;
        //} else {
            // If board has last move validate if move can be played, then apply it




         //   return false;
        //}
    }

    // ---------------------

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
