package com.ustavdica.features.search;

import com.ustavdica.features.state.State;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private final double C = Math.sqrt(2);

    private int visits;
    private double value;
    private State state;
    private TreeNode parent;
    private final List<TreeNode> children;


    public TreeNode(State state, TreeNode parent) {
        this.visits = 0;
        this.value = 0;
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
    }


    public void expand() {


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
        // TODO: Implement hasChildren() in mcts tree node
        return false;
    }

    public boolean isSimulated() {
        return false;
    }





    public void addValue(double value) {
        this.value += value;
    }

    public void incrementVisits() {
        this.visits++;
    }




    public TreeNode getRandomChild() {
        return null;
    }

    public TreeNode getBestChild() {
        // TODO: Implement later
        return null;
    }

    public TreeNode getParent() {
        return parent;
    }

    public State getState() {
        return state;
    }


}
