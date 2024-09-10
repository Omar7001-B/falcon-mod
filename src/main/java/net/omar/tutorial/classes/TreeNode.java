package net.omar.tutorial.classes;

import com.sun.source.tree.Tree;
import net.omar.tutorial.indexes.Market;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TreeNode {
    public String name;
    public TreeNode parent;
    public List<TreeNode> children = new ArrayList<>();
    public List<Trade> trades = new ArrayList<>();

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

    public TreeNode clone() {
        TreeNode clone = new TreeNode(this.name);
        if(this.children != null) {
            clone.children = new ArrayList<>();
            for (TreeNode child : this.children) {
                clone.addNode(child.clone());
            }
        }

        if(this.trades != null) {
            clone.trades = new ArrayList<>();
            for (Trade trade : this.trades) {
                clone.addTrade(trade.clone());
            }
        }
        clone.parent = this.parent;
        return clone;
    }

    public static List<Trade> shortPathFromItemToItem(String item1, String item2) {
        //DEBUG.Shulker("shortPathFromItemToItem: " + item1 + " -> " + item2);
        if(item1.equals("Emerald") && item2.equals("Gold Block"))
            return List.of(Market.emeraldToGoldBlock_t);

        if(item1.equals("Emerald") && item2.equals("Raw Gold"))
            return List.of(Market.emeraldToRawGold_t);

        if(item1.equals("Emerald") && item2.equals("Gold Ingot"))
            return List.of(Market.emeraldToGoldIngot_t);

        if(item1.equals("Emerald") && item2.equals("Gold Nugget"))
            return List.of(Market.emeraldToGoldNugget_t);


        if(item2.equals("Gold Block"))
            return List.of(Market.rawGoldToEmerald_t, Market.emeraldToGoldBlock_t);

        if(item2.equals("Gold Ingot"))
            return List.of(Market.rawGoldToEmerald_t, Market.emeraldToGoldIngot_t);

        if(item2.equals("Gold Nugget"))
            return List.of(Market.rawGoldToEmerald_t, Market.emeraldToGoldNugget_t);

        if(item2.equals("Raw Gold"))
            return List.of(Market.rawGoldToEmerald_t, Market.emeraldToRawGold_t);

        return new ArrayList<>();
    }

    public static List<Trade> farmPathFromItem(String Item1){
        if(Item1.equals("Raw Gold"))
            return List.of(Market.rawGoldToDiamond_t, Market.diamondToGoldNugget_t, Market.goldNuggetToEmerald_t, Market.emeraldToRawGold_t);

        return new ArrayList<>();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        for (TreeNode child : children) {
            sb.append("  ").append(child.toString().replace("\n", "\n  "));
        }
        for (Trade trade : trades) {
            sb.append("  ").append(trade.toString()).append("\n");
        }
        return sb.toString();
    }
}
