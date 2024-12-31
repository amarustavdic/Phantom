package com.ustavdica.features.search;

import com.ustavdica.features.state.Player;
import com.ustavdica.features.state.State;
import com.ustavdica.features.state.StateHandler;

import java.util.Comparator;
import java.util.List;

public class MonteCarloTreeSearch {

    private final StateHandler stateHandler;
    private TreeNode root;

    /**
     * Constructs a MonteCarloTreeSearch instance with the specified StateHandler.
     * <p>
     * Injecting StateHandler ensures clear delegation of state-related operations,
     * improves testability by allowing mock implementations, and decouples MCTS
     * from a specific StateHandler instance.
     *
     * @param stateHandler the StateHandler responsible for state-related operations
     */
    public MonteCarloTreeSearch(StateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }


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
    public int findBestMove(State state, long timeLimit) {

        long startTime = System.currentTimeMillis();
        root = new TreeNode(state, null, stateHandler);

        while (System.currentTimeMillis() - startTime < timeLimit) {
            TreeNode selected = select();
            TreeNode expanded = expand(selected);
            double simulationResult = simulate(expanded);
            backpropagate(expanded, simulationResult);
        }

        System.out.println(stateHandler.getAvailableMoves(root.getState()));

        List<TreeNode> bestChildren = root.getChildren();
        TreeNode bestChild = bestChildren.stream().max(Comparator.comparingInt(TreeNode::getVisits)).orElse(null);

        if (bestChild == null) {
            return -1; // if there is no children that means that no valid move, so indicate that with -1
        }

        int bestMove = bestChild.getState().getLastMove();
        return bestMove;
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
        if (node.isSimulated() && !stateHandler.isTerminal(node.getState())) {
            node.expand();
            return node.getRandomChild();
        }
        return node;
    }

    private double simulate(TreeNode node) {
        State deepCopy = new State(node.getState());
        while (!stateHandler.isTerminal(deepCopy)) {
            stateHandler.performRandomMove(deepCopy);
        }

        // TODO: This could be further optimized (not urgent)
        int outcome = (stateHandler.isDraw(node.getState()) ? 0 : (
                /*
                Returning -1 for BLUE since for the current setup human player is blue and pink is mcts agent
                 */
                stateHandler.hasWon(node.getState(), Player.BLUE) ? -1 : 1
        ));

        return outcome;
    }

    private void backpropagate(TreeNode node, double simulationResult) {
        while (node != null) {
            node.incrementVisits();
            node.addValue(simulationResult);
            node = node.getParent();
        }
    }

}
