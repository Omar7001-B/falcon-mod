package net.omar.tutorial.Managers;

import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.classes.Trader;
import net.omar.tutorial.Data.Market;

import java.util.HashMap;
import java.util.List;

import static net.omar.tutorial.Managers.Screening.openInventory;

public class Shulkering {
    // HashMap to store item to Shulker Box color mapping
    private static HashMap<String, String> itemToShulker = new HashMap<>();

    static {
        // Add items and their corresponding shulker box color
        itemToShulker.put("Gold Nugget", "Yellow Shulker Box");
        itemToShulker.put("Raw Gold", "Orange Shulker Box");
        itemToShulker.put("Gold Ingot", "Brown Shulker Box");
        itemToShulker.put("Gold Block", "Red Shulker Box");
        itemToShulker.put("Block of Gold", "Red Shulker Box");

        // Add individual tool types with corresponding box color
        itemToShulker.put("Sword", "Gray Shulker Box");
        itemToShulker.put("Pickaxe", "Gray Shulker Box");
        itemToShulker.put("Axe", "Gray Shulker Box");
        itemToShulker.put("Shears", "Gray Shulker Box");
        itemToShulker.put("Bow", "Gray Shulker Box");

        // Add individual armor types with corresponding box color
        itemToShulker.put("Helmet", "Gray Shulker Box");
        itemToShulker.put("Chestplate", "Gray Shulker Box");
        itemToShulker.put("Leggings", "Gray Shulker Box");

        itemToShulker.put("Cap", "Gray Shulker Box");
        itemToShulker.put("Tunic", "Gray Shulker Box");
        itemToShulker.put("Pants", "Gray Shulker Box");
        itemToShulker.put("Boots", "Gray Shulker Box");

        itemToShulker.put("Elytra", "Gray Shulker Box");


        // Add remaining items
        itemToShulker.put("cobweb", "White Shulker Box");
        itemToShulker.put("potion", "Magenta Shulker Box");
        itemToShulker.put("apple", "Lime Shulker Box");
        itemToShulker.put("totem", "Pink Shulker Box");
        itemToShulker.put("arrow", "Cyan Shulker Box");
        itemToShulker.put("firework", "Blue Shulker Box");
        itemToShulker.put("trash", "Black Shulker Box");

    }

    // Function 1: Takes a Trade and returns the Shulker Box
    public static String getBoxNameForItem(Trader trade) {
        return getBoxNameForItem(trade.resultName);
    }

    // Function 2: Takes an item name and returns the Shulker Box
    public static String getBoxNameForItem(String itemName) {

        for(String key : itemToShulker.keySet()) {
            if(itemName.toLowerCase().contains(key.toLowerCase())){
                Debugging.Shulker("getBoxNameForItem: " + itemName + " -> " + itemToShulker.get(key));

                return itemToShulker.get(key);
            }
        }
        Debugging.Shulker("getBoxNameForItem: " + itemName + " -> " + "Black Shulker Box");
        return "Black Shulker Box";
    }


    public static int getTradeIdForShulkerBox(String shulkerBoxName) {
        for (Trader trade : Market.shulkers_P2.trades) {
            if (trade.resultName.equalsIgnoreCase(shulkerBoxName))
                return trade.TradeIndex;
        }
        return -1;
    }

    public static Trader getTradeFoShulkerBox(String shulkerBoxName) {
        //DEBUG.Shulker("getTradeFoShulkerBox: " + shulkerBoxName);
        for (Trader trade : Market.shulkers_P2.trades) {
            if (trade.resultName.equalsIgnoreCase(shulkerBoxName))
                return trade;
        }
        return null;
    }


    public static void debugShulkerBoxStorageInventory(){
        openInventory("");
        Slotting.showAllSlots(List.of());
        DefaultedList<Slot> slots = Slotting.getSlots();
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.hasStack()) {
                String itemName = slot.getStack().getItem().getName().getString();
                int itemCount = slot.getStack().getCount();
                String shulkerBox = Shulkering.getBoxNameForItem(itemName);
                int tradeId = Shulkering.getTradeIdForShulkerBox(shulkerBox);
                String shulkerType = Shulkering.getBoxNameForItem(shulkerBox);
                //DEBUG.Shulker("Slot " + i + ": " + itemName + " x" + itemCount + " -> " + shulkerBox + " (" + shulkerType + ") " + " -> " + tradeId);
            }
        }

    }

}
