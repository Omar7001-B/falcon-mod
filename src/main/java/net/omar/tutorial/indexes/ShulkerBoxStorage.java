package net.omar.tutorial.indexes;

import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Trade;

import java.util.HashMap;
import java.util.List;

import static net.omar.tutorial.Tutorial.openInventory;

public class ShulkerBoxStorage {
    // HashMap to store item to Shulker Box color mapping
    private static HashMap<String, String> itemToShulker = new HashMap<>();

    static {
        // Add items and their corresponding shulker box color
        itemToShulker.put("Gold Nugget", "Yellow Shulker Box");
        itemToShulker.put("Raw Gold", "Orange Shulker Box");
        itemToShulker.put("Gold Ingot", "Brown Shulker Box");
        itemToShulker.put("Gold Block", "Red Shulker Box");

        // Add individual tool types with corresponding box color
        itemToShulker.put("Sword", "Light Gray Shulker Box");
        itemToShulker.put("Pickaxe", "Light Gray Shulker Box");
        itemToShulker.put("Axe", "Light Gray Shulker Box");
        itemToShulker.put("Shears", "Light Gray Shulker Box");
        itemToShulker.put("Bow", "Light Gray Shulker Box");

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
        itemToShulker.put("Cobweb", "Cyan Shulker Box");
        itemToShulker.put("Potion", "Magenta Shulker Box");
        itemToShulker.put("Golden Apple", "Lime Shulker Box");
        itemToShulker.put("Totem", "Pink Shulker Box");
        itemToShulker.put("Trash", "Black Shulker Box");

    }

    // Function 1: Takes a Trade and returns the Shulker Box
    public static String getBoxNameForItem(Trade trade) {
        return getBoxNameForItem(trade.resultName);
    }

    // Function 2: Takes an item name and returns the Shulker Box
    public static String getBoxNameForItem(String itemName) {

        for(String key : itemToShulker.keySet()) {
            if(itemName.toLowerCase().contains(key.toLowerCase())){
                DEBUG.Shulker("getBoxNameForItem: " + itemName + " -> " + itemToShulker.get(key));

                return itemToShulker.get(key);
            }
        }
        DEBUG.Shulker("getBoxNameForItem: " + itemName + " -> " + "Black Shulker Box");
        return "Black Shulker Box";
    }


    public static int getTradeIdForShulkerBox(String shulkerBoxName) {
        for (Trade trade : Market.shulkers_P2.trades) {
            if (trade.resultName.equalsIgnoreCase(shulkerBoxName))
                return trade.TradeIndex;
        }
        return -1;
    }

    public static Trade getTradeFoShulkerBox(String shulkerBoxName) {
        //DEBUG.Shulker("getTradeFoShulkerBox: " + shulkerBoxName);
        for (Trade trade : Market.shulkers_P2.trades) {
            if (trade.resultName.equalsIgnoreCase(shulkerBoxName))
                return trade;
        }
        return null;
    }


    public static void debugShulkerBoxStorageInventory(){
        openInventory("");
        SlotOperations.showAllSlots(List.of());
        DefaultedList<Slot> slots = SlotOperations.getSlots();
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot.hasStack()) {
                String itemName = slot.getStack().getItem().getName().getString();
                int itemCount = slot.getStack().getCount();
                String shulkerBox = ShulkerBoxStorage.getBoxNameForItem(itemName);
                int tradeId = ShulkerBoxStorage.getTradeIdForShulkerBox(shulkerBox);
                String shulkerType = ShulkerBoxStorage.getBoxNameForItem(shulkerBox);
                //DEBUG.Shulker("Slot " + i + ": " + itemName + " x" + itemCount + " -> " + shulkerBox + " (" + shulkerType + ") " + " -> " + tradeId);
            }
        }

    }

}
