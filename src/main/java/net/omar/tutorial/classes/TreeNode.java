package net.omar.tutorial.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    public String name;
    private TreeNode parent;
    private List<TreeNode> children = new ArrayList<>();
    private List<Trade> trades = new ArrayList<>();

    public TreeNode(String name) {
        this.name = name;
    }

    // Renamed addChild to addNode
    public TreeNode addNode(TreeNode child) {
        child.parent = this;
        children.add(child);
        return child;
    }

    // Renamed search to searchNode
    public TreeNode searchNode(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        for (TreeNode child : children) {
            TreeNode result = child.searchNode(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    // New method to add a Trade and set its Parent to the current TreeNode
    public Trade addTrade(Trade trade) {
        trade.Parent = this;  // Set the parent of the trade to this TreeNode
        trades.add(trade);
        return trade;
    }

    // New method to search for a Trade by index, including in child nodes
    public Trade searchTrade(int tradeIndex) {
        // Search in current node's trades
        for (Trade trade : trades) {
            if (trade.TradeIndex == tradeIndex) {
                return trade;
            }
        }
        return null;
    }

    public List<String> getPathFromRoot() {
        List<String> path = new ArrayList<>();
        for (TreeNode node = this; node != null; node = node.parent) {
            path.add(node.name);
        }

        List<String> reversedPath = new ArrayList<>();
        for (int i = path.size() - 1; i >= 0; i--) {
            reversedPath.add(path.get(i));
        }
        return reversedPath;
    }
}
