package com.ustavdica.search;

public class MonteCarloTreeSearch {

    private TreeNode root;


    /**
     * Finds the best move using the Monte Carlo Tree Search (MCTS) algorithm.
     * <p>
     * This method executes the MCTS algorithm within a specified time limit to
     * determine the optimal move from the given game state. It performs the following steps:<br>
     * - Selection: Navigates the search tree to select a promising node.<br>
     * - Expansion: Expands the selected node by adding a child node.<br>
     * - Simulation: Simulates a random game from the expanded node to estimate its value.<br>
     * - Backpropagation: Propagates the simulation result up the tree to update node statistics.<br>
     *
     * @param state     the current game state from which to start the MCTS process
     * @param timeLimit the time limit in milliseconds to run the MCTS algorithm
     * @return the best move as an integer, representing the optimal action for the current player
     */
    public int findBestMove(GameState state, long timeLimit) {

        long startTime = System.currentTimeMillis();
        root = new TreeNode(state, null);

        while (System.currentTimeMillis() - startTime < timeLimit) {
            TreeNode selected = select();
            TreeNode expanded = expand(selected);
            double simulationResult = simulate(expanded);
            backpropagate(expanded, simulationResult);
        }

        // TODO: Return the result, best child should be best move...
        return 0;
    }

    /**
     * Selects the most promising node in the tree for expansion.
     * <p>
     * This method starts at the root node and traverses the tree by repeatedly
     * selecting the best child node (e.g., based on a selection strategy like UCT)
     * until a leaf node or an expandable node is reached.
     *
     * @return the selected TreeNode for expansion or simulation
     */
    private TreeNode select() {
        TreeNode node = root;
        while (node.hasChildren()) {
            node = node.getBestChild();
        }
        return node;
    }

    private TreeNode expand(TreeNode node) {
        if (node.isSimulated() && !node.getState().isTerminal()) {
            node.expand();
            return node.getRandomChild();
        }
        return node;
    }

    private double simulate(TreeNode node) {

        return 0;
    }

    private void backpropagate(TreeNode node, double simulationResult) {

    }


}
