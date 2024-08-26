package net.omar.tutorial.unused;
import java.util.List;

public class Conversion {
    // Shop clicks
    private List<String> shopClicks;

    // Shop index
    private int conversionIndex;

    // Input item details
    private String inputItemName;
    private int inputAmount;

    // Output item details
    private String outputItemName;
    private int outputAmount;

    // Optional second input item details
    private String inputItem2;
    private int inputAmount2;

    // Constructor
    public Conversion(List<String> shopClicks, int conversionIndex,
                      String inputItemName, int inputAmount, String outputItemName, int outputAmount) {
        this.shopClicks = shopClicks;
        this.conversionIndex = conversionIndex;
        this.inputItemName = inputItemName;
        this.inputAmount = inputAmount;
        this.outputItemName = outputItemName;
        this.outputAmount = outputAmount;
    }

    // Overloaded constructor for optional second input item
    public Conversion(List<String> shopClicks, int conversionIndex,
                      String inputItemName, int inputAmount, String outputItemName, int outputAmount,
                      String inputItem2, int inputAmount2) {
        this(shopClicks, conversionIndex, inputItemName, inputAmount, outputItemName, outputAmount);
        this.inputItem2 = inputItem2;
        this.inputAmount2 = inputAmount2;
    }

    // Getters and setters (optional, add if needed)
    public List<String> getShopClicks() { return shopClicks; }
    public void setShopClicks(List<String> shopClicks) { this.shopClicks = shopClicks; }

    public int getConversionIndex() { return conversionIndex; }
    public void setConversionIndex(int conversionIndex) { this.conversionIndex = conversionIndex; }

    public String getInputItemName() { return inputItemName; }
    public void setInputItemName(String inputItemName) { this.inputItemName = inputItemName; }

    public int getInputAmount() { return inputAmount; }
    public void setInputAmount(int inputAmount) { this.inputAmount = inputAmount; }

    public String getOutputItemName() { return outputItemName; }
    public void setOutputItemName(String outputItemName) { this.outputItemName = outputItemName; }

    public int getOutputAmount() { return outputAmount; }
    public void setOutputAmount(int outputAmount) { this.outputAmount = outputAmount; }

    public String getInputItem2() { return inputItem2; }
    public void setInputItem2(String inputItem2) { this.inputItem2 = inputItem2; }

    public int getInputAmount2() { return inputAmount2; }
    public void setInputAmount2(int inputAmount2) { this.inputAmount2 = inputAmount2; }
}
