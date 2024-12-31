package com.ustavdica.features.search;

import com.ustavdica.features.state.State;
import com.ustavdica.features.state.StateHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TreeNode {

    private final double C = Math.sqrt(2);

    private int visits;
    private double value;
    private final State state;
    private final TreeNode parent;
    private final List<TreeNode> children;

    private final StateHandler stateHandler;


    public TreeNode(State state, TreeNode parent, StateHandler stateHandler) {
        this.visits = 0;
        this.value = 0;
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.stateHandler = stateHandler;
    }


    public void expand() {
        stateHandler.getAvailableMoves(state).stream().map(move -> {
            State stateCopy = new State(state); // Deep copy the current state
            stateHandler.applyMove(stateCopy, move);
            return new TreeNode(stateCopy, this, stateHandler);
        }).forEach(children::add);
    }



    /**
     * Calculates the Upper Confidence Bound for Trees (UCT) value for the current node.
     * <p>
     * UCT balances exploration and exploitation by combining the node's average value
     * and a confidence term based on the visit count of the node and its parent.
     *
     * @return the UCT value for the node
     */
    private double uct() {
        return visits == 0 ? Double.MAX_VALUE : (value / visits) + C * Math.sqrt(Math.log(parent.visits) / visits);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean isSimulated() {
        return visits > 0;
    }

    public void addValue(double value) {
        this.value += value;
    }

    public void incrementVisits() {
        this.visits++;
    }

    public TreeNode getRandomChild() {
        Collections.shuffle(children);
        return children.getFirst();
    }

    public TreeNode getBestChild() {
        return children.stream().max(Comparator.comparingDouble(TreeNode::uct)).orElse(null);
    }

    public TreeNode getBestMove() {

        TreeNode bestChild = children.stream().max(
                Comparator.comparingInt(TreeNode::getVisits)
        ).orElse(null);

        if (bestChild == null) {
            throw new RuntimeException("Unable to get best state. The root has no children!");
        }

        return bestChild;
    }

    public TreeNode getParent() {
        return parent;
    }

    public State getState() {
        return state;
    }

    public int getVisits() {
        return visits;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
}
