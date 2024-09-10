package net.omar.tutorial.classes;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    public int TradeIndex;

    public String firstItemName;
    public int firstItemAmount;

    public String secondItemName;
    public int secondItemAmount;

    public String resultName;
    public int resultAmount;

    public TreeNode Parent;


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

    @Override
    public String toString() {
        return String.format(
                "%s x%d + %s x%d -> %s x%d",
                firstItemName,
                firstItemAmount,
                secondItemName,
                secondItemAmount,
                resultName,
                resultAmount
        );
    }

    public Trade clone() {
        return new Trade(TradeIndex, firstItemName, firstItemAmount, secondItemName, secondItemAmount, resultName, resultAmount);
    }
}
