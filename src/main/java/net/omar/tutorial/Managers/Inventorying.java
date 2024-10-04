package net.omar.tutorial.Managers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.Data.Indexes;
import net.omar.tutorial.Vaults.VaultsStateManager;
import net.omar.tutorial.Vaults.MyInventory;
import net.omar.tutorial.Vaults.MyPV;
import net.omar.tutorial.classes.Trader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.omar.tutorial.Tutorial.MOD_ID;
import static net.omar.tutorial.Tutorial.Sleep;

public class Inventorying {

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static void swapItemsInSlots(int index1, int index2) {
        if (index1 == index2) return;

        DefaultedList<Slot> slots = Slotting.getSlots();
        if (slots == null) return;

        if (slots.get(index1).hasStack() || slots.get(index2).hasStack()) {
            Clicking.slotNormalClick(index1);
            Clicking.slotNormalClick(index2);
            Clicking.slotNormalClick(index1);
        }
    }

    public static Map<String, Integer> transferItems(Map<String, Integer> itemAmounts, List<Integer> sourceIndexes, List<Integer> targetIndexes) {
        DefaultedList<Slot> slots = Slotting.getSlots();

        Map<String, Integer> remainingAmounts = new HashMap<>(itemAmounts);

        if (slots == null) return remainingAmounts;
        for (int sourceIndex : sourceIndexes) {
            Slot sourceSlot = slots.get(sourceIndex);
            if (!sourceSlot.hasStack()) continue;

            String itemName = sourceSlot.getStack().getItem().getName().getString();
            //DEBUG.Store("Item: " + itemName + ", Amount: " + sourceSlot.getStack().getCount());
            for (Map.Entry<String, Integer> entry : remainingAmounts.entrySet()) {
                //DEBUG.Store("Item: " + entry.getKey() + ", Amount: " + entry.getValue());
                if (entry.getValue() == 0 || !Naming.containsIgnoreCase(itemName, entry.getKey())) continue;

                int toTransfer = Math.min(entry.getValue(), sourceSlot.getStack().getCount());
                int actualTransfer = toTransfer - moveCompleteItem(sourceIndex, targetIndexes, toTransfer);
                entry.setValue(entry.getValue() - actualTransfer);
                break;
            }
        }

        return remainingAmounts;
    }

    public static void moveCompleteItem(int sourceIndex, List<Integer> targetIndexes) {
        int amount = Slotting.getElementAmountByIndex(sourceIndex);
        moveCompleteItem(sourceIndex, targetIndexes, amount);
    }


    public static int moveCompleteItem(int sourceIndex, List<Integer> targetIndexes, int amount) {
        DefaultedList<Slot> slots = Slotting.getSlots();
        if (slots == null) return amount;

        int sourceAmount = Slotting.getElementAmountByIndex(sourceIndex);
        String sourceName = Slotting.getSlotNameByIndex(sourceIndex);

        Clicking.slotNormalClick(sourceIndex);

        while(sourceAmount > 0 && amount > 0) {
            int targetIndex = Slotting.getSlotToComplete(sourceName, targetIndexes);
            if (targetIndex == -1) return amount;
            int availableAmountSpace = 64 - Slotting.getElementAmountByIndex(targetIndex);
            int transferAmount = Math.min(availableAmountSpace, Math.min(sourceAmount, amount));
            if(Math.min(availableAmountSpace, sourceAmount) <= amount) {
                Clicking.slotNormalClick(targetIndex);
            }
            else  {
                for (int i = 0; i < transferAmount; i++)
                    Clicking.slotRightClick(targetIndex);
            }
            sourceAmount -= transferAmount;
            amount -= transferAmount;
        }

        if(sourceAmount > 0)
            Clicking.slotNormalClick(sourceIndex);

        return amount;
    }


    // another fucntion that  move complete certain item amount
    public static void moveCompleteItemAmount(int sourceIndex, List<Integer> targetIndexes, int amount) {
        DefaultedList<Slot> slots = Slotting.getSlots();
        if (slots == null) return;

        Slot sourceSlot = slots.get(sourceIndex);
        if (!sourceSlot.hasStack()) return;

        int sourceAmount = sourceSlot.getStack().getCount();
        String sourceName = Slotting.getSlotNameByIndex(sourceIndex);

        Clicking.slotNormalClick(sourceIndex);


        if (Naming.isStackedItem(sourceName)) {
            for (int targetIndex : targetIndexes) {
                if (Slotting.getSlotNameByIndex(targetIndex).equals(sourceName)) {
                    int targetAmount = Slotting.getElementAmountByIndex(targetIndex);
                    if (targetAmount < 64) {
                        int transferAmount = Math.min(64 - targetAmount, sourceAmount);
                        Clicking.slotNormalClick(targetIndex);
                        sourceAmount -= transferAmount;
                        if (sourceAmount == 0) return;
                    }
                }
            }
        }

        int emptySlotIndex = Slotting.getIndexFirstEmptySlot(targetIndexes);
        if (emptySlotIndex != -1) {
            Clicking.slotNormalClick(emptySlotIndex);
        }
    }


    public static Map<String, Integer> sendItems(Map<String, Integer> itemAmounts, String targetContainer, boolean front) {
        //DEBUG.Shulker("Material to send: " + itemAmounts + " to " + targetContainer);
        Map<String, Integer> result = new LinkedHashMap<>(itemAmounts);
        List<Integer> sourceIndexes = null;
        List<Integer> targetIndexes = null;

        if(isEmptyMap(itemAmounts)) return result;


        if (Naming.containsIgnoreCase(targetContainer, "Shulker")) {
            if (!Screening.openShulkerBox(targetContainer)) return result;
            sourceIndexes = Indexes.Shulker.TOTAL_INVENTORY;
            targetIndexes = (front ? Indexes.Shulker.SHULKER_BOX : Indexes.Shulker.SHULKER_BOX_REVERSE);
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            VaultsStateManager.Shulker(targetContainer).update("Send Item");
            Screening.closeScreen();
        } else if (Naming.containsIgnoreCase(targetContainer, "pv")) {
            if (!Screening.openPV1("")) return result;
            sourceIndexes = Indexes.PV.TOTAL_INVENTORY;
            targetIndexes = (front ? Indexes.PV.PV : Indexes.PV.PV_REVERSE);
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            VaultsStateManager.PV(targetContainer).update("Send Item");
            Screening.closeScreen();
        } else if (Naming.containsIgnoreCase(targetContainer, "enderchest")) {
            LOGGER.error("EnderChest not implemented yet");
        } else {
            LOGGER.error("Invalid target container specified" + targetContainer);
        }
        return result;
    }

    public static Map<String, Integer> takeItems(Map<String, Integer> itemAmounts, String sourceContainer, boolean front) {
        //DEBUG.Shulker("Material to take: " + itemAmounts + " from " + sourceContainer);
        Map<String, Integer> result = itemAmounts;
        List<Integer> sourceIndexes = null;
        List<Integer> targetIndexes = null;

        if(isEmptyMap(itemAmounts)) return result;


        if (Naming.containsIgnoreCase(sourceContainer, "Shulker")) {
            if (!Screening.openShulkerBox(sourceContainer)) return result;
            sourceIndexes = (front ? Indexes.Shulker.SHULKER_BOX : Indexes.Shulker.SHULKER_BOX_REVERSE);
            targetIndexes = Indexes.Shulker.TOTAL_INVENTORY;
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            VaultsStateManager.Shulker(sourceContainer).update("Take Item");
            Screening.closeScreen();
        } else if (Naming.containsIgnoreCase(sourceContainer, "pv")) {
            if (!Screening.openPV1("")) return result;
            sourceIndexes = (front ? Indexes.PV.PV : Indexes.PV.PV_REVERSE);
            targetIndexes = Indexes.PV.TOTAL_INVENTORY;
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            VaultsStateManager.PV(sourceContainer).update("Take Item");
            Screening.closeScreen();
        } else if (Naming.containsIgnoreCase(sourceContainer, "enderchest")) {
            LOGGER.error("EnderChest not implemented yet");
        } else {
            LOGGER.error("Invalid source container specified" + sourceContainer);
        }


        return result;
    }

    public static boolean isEmptyMap(Map<String, Integer> mp) {
        for (Map.Entry<String, Integer> entry : mp.entrySet())
            if (entry.getValue() > 0) return false;
        return true;
    }

    public static boolean isAllShulkersItems(Map<String, Integer> itemsAmount){
        for(Map.Entry<String, Integer> entry : itemsAmount.entrySet())
            if(!Naming.containsIgnoreCase(entry.getKey(), "shulker")) return false;
        return true;
    }

    public static int amountToCompleteInventory(String itemName, int amount){
        return Math.max(0, amount - VaultsStateManager.Inventory(MyInventory.NAME).getItemCountByName(itemName));
    }

    public static boolean forceCompleteItemsToInventory(Map<String, Integer> itemsAmount){
        Debugging.Force("Force Complete Items To Inventory: " + itemsAmount);
        if(isEmptyMap(itemsAmount)){
            return false;
        }
        for(Map.Entry<String, Integer> entry : itemsAmount.entrySet())
            if(!forceCompleteItemsToInventory(entry.getKey(), entry.getValue()))
                return false;
        return true;
    }

    public static boolean forceCompleteItemsToInventory(String itemName, int amount){
        // This function will take items from  PV and shulkers and shukers in PV to complete the required amount into the inventory
        // Starts with PV1 then shulkers then PV1 shulkers
        Debugging.Force("Force Complete Items To Inventory: " + itemName + " -> " + amount);
        if(amountToCompleteInventory(itemName, amount) > 0)
            takeItems(Map.of(itemName, amountToCompleteInventory(itemName, amount)), MyPV.PV1, false);

        String shulkerName = Shulkering.getBoxNameForItem(itemName);
        while(VaultsStateManager.Inventory(MyInventory.NAME).getItemCountByName(shulkerName) > 0 && amountToCompleteInventory(itemName, amount) > 0) {
            takeItems(Map.of(itemName, amountToCompleteInventory(itemName, amount)), shulkerName, false);
            if(VaultsStateManager.Shulker(shulkerName).getItemCountByName(itemName) <= 0)
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, false);
            else
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
        }


        int numShulkers = Math.min(2, VaultsStateManager.PV(MyPV.PV1).getItemCountByName(shulkerName));
        while(numShulkers > 0 && amountToCompleteInventory(itemName, amount) > 0) {
            takeItems(Map.of(shulkerName, 1), MyPV.PV1, true);
            takeItems(Map.of(itemName, amountToCompleteInventory(itemName, amount)), shulkerName, false);
            if(VaultsStateManager.Shulker(shulkerName).getItemCountByName(itemName) > 0)
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
            else
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, false);
            numShulkers--;
        }

        return amountToCompleteInventory(itemName, amount) <= 0;
    }

    public static Map<String,Integer> filterItemsForSending(Map<String, Integer>itemsAmount){
        Map<String, Integer> result = new LinkedHashMap<>();
        // loop on the itemsAmount
        for(Map.Entry<String, Integer> entry : itemsAmount.entrySet()){
            String itemName = entry.getKey();
            int amount = entry.getValue();
            int invAmount = Math.max(countItemByNameInInventory(itemName),  VaultsStateManager.Inventory(MyInventory.NAME).getItemCountByName(itemName));

            int actualAmount = Math.min(amount, invAmount);
            if(actualAmount > 0)
                result.put(itemName, actualAmount);
        }

        return result;
    }


    public static Map<String, Integer> getInventoryMap() {
        Map<String, Integer> items = new HashMap<>();
        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                String itemName = stack.getItem().getName().getString().toLowerCase().replace("_", " ");
                items.put(itemName, items.getOrDefault(itemName, 0) + stack.getCount());
            }
        }
        return items;
    }

    public static Map<String, Integer> getInventoryChanges(Map<String, Integer> before, Map<String, Integer> after) {
        Map<String, Integer> newItems = new HashMap<>();
        for (Map.Entry<String, Integer> entry : after.entrySet()) {
            String itemName = entry.getKey();
            if (!before.containsKey(itemName))
                newItems.put(itemName, entry.getValue());
        }
        return newItems;
    }

    public static void forceCompleteItemsToShulkers(Map<String, Integer> itemsAmount) {
        Debugging.Force("Force Complete Items To Shulkers: " + itemsAmount);
        if (isEmptyMap(itemsAmount)) {
            Debugging.Force("Items amount is empty. Exiting method.");
            return;
        }

        if (isAllShulkersItems(itemsAmount)) {
            Debugging.Force("All items are already in shulkers. Exiting method.");
            return;
        }

        Screening.openPV1("");
        Screening.closeScreen();
        Debugging.Force("Opened and closed PV1.");

        Map<String, Map<String, Integer>> shulkerItems = new HashMap<>();
        Debugging.Force("Items Amount: " + itemsAmount);

        for (Map.Entry<String, Integer> entry : itemsAmount.entrySet()) {
            String itemName = entry.getKey();
            String shulkerName = Shulkering.getBoxNameForItem(itemName);
            Debugging.Force("Item Name: " + itemName + ", Shulker: " + shulkerName);

            shulkerItems.putIfAbsent(shulkerName, new HashMap<>());
            int amountWeHave = VaultsStateManager.Inventory(MyInventory.NAME).getItemCountByName(itemName);
            int amountToSend = Math.min(entry.getValue(), amountWeHave);
            shulkerItems.get(shulkerName).put(itemName, amountToSend);

            Debugging.Force("Item: " + itemName + ", Amount to send: " + amountToSend + ", Available: " + amountWeHave);
        }

        Debugging.Force("Shulker Items: " + shulkerItems);

        for (Map.Entry<String, Map<String, Integer>> entry : shulkerItems.entrySet()) {
            String shulkerName = entry.getKey();
            int numOfShulkersInPv = Math.min(VaultsStateManager.PV(MyPV.PV1).getItemCountByName(shulkerName), 2);
            Trader shulkerTrade = Shulkering.getTradeFoShulkerBox(shulkerName);
            Map<String, Integer> items = entry.getValue();

            if (isEmptyMap(items)) {
                Debugging.Force("No items for shulker: " + shulkerName + ". Skipping.");
                continue;
            }

            Map<String, Integer> remainingItems = new HashMap<>(items);
            Debugging.Force("Remaining items for shulker " + shulkerName + ": " + remainingItems);

            // Check if we have the shulker box in the inventory
            boolean invHaveShulker = VaultsStateManager.Inventory(MyInventory.NAME).getItemCountByName(shulkerName) > 0;
            while (invHaveShulker && !isEmptyMap(remainingItems)) {
                remainingItems = sendItems(remainingItems, shulkerName, true);
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
                Debugging.Force("Sent items to shulker " + shulkerName + ". Remaining items: " + remainingItems);
                invHaveShulker = VaultsStateManager.Inventory(MyInventory.NAME).getItemCountByName(shulkerName) > 0;
            }

            // Check if we have the shulker box in the PV
            for (int i = 0; i < numOfShulkersInPv && !isEmptyMap(remainingItems); i++) {
                takeItems(Map.of(shulkerName, 1), MyPV.PV1, false);
                Debugging.Shulker("Took 1 shulker " + shulkerName + " from PV. Remaining items: " + remainingItems);
                remainingItems = sendItems(remainingItems, shulkerName, true);
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
                Debugging.Force("Sent items to shulker " + shulkerName + ". Remaining items: " + remainingItems);
            }

            // Buy shulker and fill it
            while (!isEmptyMap(remainingItems)) {
                forceCompleteItemsToInventory("Raw Gold", 3);
                Trading.buyItem(shulkerTrade, 0, 1);
                Sleep(2000);
                Screening.openInventory("");
                Screening.closeScreen();
                remainingItems = sendItems(remainingItems, shulkerName, true);
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
                Debugging.Force("Bought 1 " + shulkerName + ". Remaining items: " + remainingItems);
            }
        }
    }


    public static boolean isShulkerFull(String shulkerName) {
        Screening.openShulkerBox(shulkerName);
        int emptySlots = Slotting.countEmptySlots(Indexes.Shulker.SHULKER_BOX);
        Screening.closeScreen();
        return emptySlots <= 1;
    }

    public static int countItemByNameInInventory(String targetItemName) {
        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            String itemName = stack.getItem().getName().getString();
            itemName = itemName.toLowerCase().replace("_", " ");
            //DEBUG.Shulker("Item Name: " + itemName);
            if (!stack.isEmpty())
                if (targetItemName.contains(itemName.toLowerCase()) || itemName.toLowerCase().contains(targetItemName))
                    count += stack.getCount();
        }
        return count;
    }
}
