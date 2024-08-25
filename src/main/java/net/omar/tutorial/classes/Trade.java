package net.omar.tutorial.classes;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    public int TradeIndex;

    String firstItemName;
    int firstItemAmount;

    String secondItemName;
    int secondItemAmount;

    String resultName;
    int resultAmount;

    TreeNode Parent;


    // Constructor with all item parameters
    public Trade(int TradeIndex, String firstItemName, int firstItemAmount,
                 String secondItemName, int secondItemAmount,
                 String resultName, int resultAmount) {
        this.TradeIndex = TradeIndex;
        this.firstItemName = firstItemName;
        this.firstItemAmount = firstItemAmount;
        this.secondItemName = secondItemName;
        this.secondItemAmount = secondItemAmount;
        this.resultName = resultName;
        this.resultAmount = resultAmount;
        this.Parent = null;  // Parent is not set in constructor, can be set later
    }

    // Overloaded constructor with optional second item parameters
    public Trade(int TradeIndex, String firstItemName, int firstItemAmount,
                 String resultName, int resultAmount) {
        this.TradeIndex = TradeIndex;
        this.firstItemName = firstItemName;
        this.firstItemAmount = firstItemAmount;
        this.secondItemName = null;  // Optional, set to null
        this.secondItemAmount = 0;   // Optional, set to 0 or a default value
        this.resultName = resultName;
        this.resultAmount = resultAmount;
        this.Parent = null;  // Parent is not set in constructor, can be set later
    }

    public List<String> getPathFromRoot() {
        List<String> path = Parent.getPathFromRoot();
        path.add(Integer.toString(TradeIndex));
        return path;
    }
}
