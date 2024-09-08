package net.omar.tutorial.Inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Trade;
import net.omar.tutorial.indexes.Indexes;
import net.omar.tutorial.indexes.ShulkerBoxStorage;
import net.omar.tutorial.last.InventorySaver;
import net.omar.tutorial.last.MyInventory;
import net.omar.tutorial.last.MyPV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.omar.tutorial.Tutorial.*;

public class SlotOperations {

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;
        //LOGGER.info("Checking if " + str + " contains " + searchStr);

        // Split the string between each lowercase and uppercase letter
        //DEBUG.Store("Original String: " + searchStr);
        searchStr = searchStr.replaceAll("([a-z])([A-Z])", "$1 $2");
        //DEBUG.Store("Modified String: " + searchStr);

        // Convert both strings to lowercase and split the search string into words
        String[] wordsToSearch = searchStr.toLowerCase().split("\\s+");
        String lowerStr = str.toLowerCase();

        // Check if each word in the search string is contained in the main string
        String debugString = "Words to search: ";
        //for (String word : wordsToSearch) debugString += word + " ";
        //debugString += " In: " + lowerStr;
        //DEBUG.Shulker(debugString);
        for (String word : wordsToSearch) {
            //LOGGER.info("Checking for word: " + word);
            if (!lowerStr.contains(word)) {
                //DEBUG.Shulker("Word not found: " + word);
                return false;
            }
        }
        //DEBUG.Shulker("Word found");
        return true;
    }

    public static DefaultedList<Slot> getSlots() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) {
            LOGGER.error("No screen found");
            return null;
        }
        return ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
    }

    public static void showAllSlots(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) {
            LOGGER.info("No slots found");
            return;
        }

        if (indexes == null || indexes.isEmpty()) {
            indexes = IntStream.range(0, slots.size()).boxed().collect(Collectors.toList());
        }

        LOGGER.info("Showing slots:");
        indexes.forEach(index -> {
            Slot slot = slots.get(index);
            LOGGER.info("Slot Index: " + index);
            LOGGER.info("Item: " + (slot.hasStack() ? slot.getStack().getItem().getName().getString() : "Empty"));
            LOGGER.info("Amount: " + (slot.hasStack() ? slot.getStack().getCount() : 0));
            LOGGER.info("-------------------");
        });

    }

    public static void showAllTrades() {
        List<TradeOffer> offers = ((MerchantScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().getRecipes();
        for (int i = 0; i < offers.size(); i++) {
            TradeOffer offer = offers.get(i);

            // Get the first buy item details
            int firstPriceCount = offer.getOriginalFirstBuyItem().getCount();
            String firstPriceName = offer.getOriginalFirstBuyItem().getName().getString();

            // Get the second buy item details (if present)
            int secondPriceCount = offer.getSecondBuyItem().isEmpty() ? 0 : offer.getSecondBuyItem().getCount();
            String secondPriceName = offer.getSecondBuyItem().isEmpty() ? "None" : offer.getSecondBuyItem().getName().getString();

            // Get the sell (output) item details
            int sellCount = offer.getSellItem().getCount();
            String sellName = offer.getSellItem().getName().getString();

            LOGGER.info("Trade " + (i + 1) + ":");
            LOGGER.info("    First Item: " + firstPriceCount + " x " + firstPriceName);
            LOGGER.info("    Second Item: " + (secondPriceCount > 0 ? secondPriceCount + " x " + secondPriceName : "None"));
            LOGGER.info("    Result: " + sellCount + " x " + sellName);
            LOGGER.info("------------------------------------");
        }

    }

    public static int getSlotIndexContainsName(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if (slots == null) return -1;
        //DEBUG.Store("Item We Search: " + itemName);
        for (int i = 0; i < slots.size(); i++){
            //DEBUG.Store("Item In Slot: " + slots.get(i).getStack().getItem().getName().getString());
            if (slots.get(i).getStack().getItem().getName().getString().contains(itemName)) return i;
        }
        return -1;
    }


    public static int getSlotIndexByName(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if (slots == null) return -1;
        for (int i = 0; i < slots.size(); i++)
            if (slots.get(i).getStack().getName().getString().equals(itemName)) return i;
        return -1;
    }

    public static int getSlotIndexByItemNameIgnoreCase(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if (slots == null) return -1;
        for (int i = 0; i < slots.size(); i++)
            if (containsIgnoreCase(slots.get(i).getStack().getItem().getName().getString(), itemName)) return i;
        return -1;
    }

    public static String getSlotNameByIndex(int index) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return "null";
        return slots.get(index).hasStack() ? slots.get(index).getStack().getItem().getName().getString() : "empty";
    }

    public static int getElementAmountByIndex(int index) {
        DefaultedList<Slot> slots = getSlots();
        return slots != null && slots.get(index).hasStack() ? slots.get(index).getStack().getCount() : 0;
    }

    public static int countTotalElementAmount(List<Integer> indexes, String itemName) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return indexes.stream()
                .filter(index -> slots.get(index).hasStack() && containsIgnoreCase(slots.get(index).getStack().getItem().getName().getString(), itemName))
                .mapToInt(index -> slots.get(index).getStack().getCount())
                .sum();
    }

    public static boolean isEmptySlot(int index) {
        DefaultedList<Slot> slots = getSlots();
        return slots != null && !slots.get(index).hasStack();
    }

    public static int getIndexFirstEmptySlot(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return -1;

        return indexes.stream()
                .filter(SlotOperations::isEmptySlot)
                .findFirst()
                .orElse(-1);
    }

    public static int countEmptySlots(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return (int) indexes.stream()
                .filter(SlotOperations::isEmptySlot)
                .count();
    }

    public static int countFilledSlots(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return (int) indexes.stream()
                .filter(index -> slots.get(index).hasStack())
                .count();
    }

    public static void swapItemsInSlots(int index1, int index2) {
        if (index1 == index2) return;

        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return;

        if (slots.get(index1).hasStack() || slots.get(index2).hasStack()) {
            SlotClicker.slotNormalClick(index1);
            SlotClicker.slotNormalClick(index2);
            SlotClicker.slotNormalClick(index1);
        }
    }

    public static Map<String, Integer> transferItems(Map<String, Integer> itemAmounts, List<Integer> sourceIndexes, List<Integer> targetIndexes) {
        DefaultedList<Slot> slots = getSlots();

        Map<String, Integer> remainingAmounts = new HashMap<>(itemAmounts);

        if (slots == null) return remainingAmounts;
        for (int sourceIndex : sourceIndexes) {
            Slot sourceSlot = slots.get(sourceIndex);
            if (!sourceSlot.hasStack()) continue;

            String itemName = sourceSlot.getStack().getItem().getName().getString();
            //DEBUG.Store("Item: " + itemName + ", Amount: " + sourceSlot.getStack().getCount());
            for (Map.Entry<String, Integer> entry : remainingAmounts.entrySet()) {
                //DEBUG.Store("Item: " + entry.getKey() + ", Amount: " + entry.getValue());
                if (entry.getValue() == 0 || !containsIgnoreCase(itemName, entry.getKey())) continue;

                int toTransfer = Math.min(entry.getValue(), sourceSlot.getStack().getCount());
                int actualTransfer = toTransfer - moveCompleteItem(sourceIndex, targetIndexes, toTransfer);
                entry.setValue(entry.getValue() - actualTransfer);
                break;
            }
        }

        return remainingAmounts;
    }

    public static int getSlotToComplete(String itemName, List<Integer> targetIndexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return -1;

        if(NameConverter.isStackedItem(itemName)) {
            for(int i: targetIndexes) {
                String name = slots.get(i).getStack().getItem().getName().getString();
                int amount = slots.get(i).getStack().getCount();
                if(containsIgnoreCase(name, itemName) && amount < 64) return i;
            }
        }

        for(int i: targetIndexes)
            if(!slots.get(i).hasStack()) return i;

        return -1;
    }

    public static void moveCompleteItem(int sourceIndex, List<Integer> targetIndexes) {
        int amount = getElementAmountByIndex(sourceIndex);
        moveCompleteItem(sourceIndex, targetIndexes, amount);
    }


    public static int moveCompleteItem(int sourceIndex, List<Integer> targetIndexes, int amount) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return amount;

        Slot sourceSlot = slots.get(sourceIndex);
        if (!sourceSlot.hasStack()) return amount;

        int sourceAmount = sourceSlot.getStack().getCount();
        String sourceName = getSlotNameByIndex(sourceIndex);

        SlotClicker.slotNormalClick(sourceIndex);

        while(sourceAmount > 0 && amount > 0) {
            int targetIndex = getSlotToComplete(sourceName, targetIndexes);
            if (targetIndex == -1) return amount;
            int availableAmountSpace = 64 - getElementAmountByIndex(targetIndex);
            int transferAmount = Math.min(availableAmountSpace, Math.min(sourceAmount, amount));
            if(Math.min(availableAmountSpace, sourceAmount) <= amount) {
                SlotClicker.slotNormalClick(targetIndex);
            }
            else  {
                for (int i = 0; i < transferAmount; i++)
                    SlotClicker.slotRightClick(targetIndex);
            }
            sourceAmount -= transferAmount;
            amount -= transferAmount;
        }

        if(sourceAmount > 0)
            SlotClicker.slotNormalClick(sourceIndex);

        return amount;
    }

    // another fucntion that  move complete certain item amount
    public static void moveCompleteItemAmount(int sourceIndex, List<Integer> targetIndexes, int amount) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return;

        Slot sourceSlot = slots.get(sourceIndex);
        if (!sourceSlot.hasStack()) return;

        int sourceAmount = sourceSlot.getStack().getCount();
        String sourceName = getSlotNameByIndex(sourceIndex);

        SlotClicker.slotNormalClick(sourceIndex);


        if (NameConverter.isStackedItem(sourceName)) {
            for (int targetIndex : targetIndexes) {
                if (getSlotNameByIndex(targetIndex).equals(sourceName)) {
                    int targetAmount = getElementAmountByIndex(targetIndex);
                    if (targetAmount < 64) {
                        int transferAmount = Math.min(64 - targetAmount, sourceAmount);
                        SlotClicker.slotNormalClick(targetIndex);
                        sourceAmount -= transferAmount;
                        if (sourceAmount == 0) return;
                    }
                }
            }
        }

        int emptySlotIndex = getIndexFirstEmptySlot(targetIndexes);
        if (emptySlotIndex != -1) {
            SlotClicker.slotNormalClick(emptySlotIndex);
        }
    }


    public static Map<String, Integer> sendItems(Map<String, Integer> itemAmounts, String targetContainer, boolean front) {
        DEBUG.Shulker("Material to send: " + itemAmounts + " to " + targetContainer);
        Map<String, Integer> result = new LinkedHashMap<>(itemAmounts);
        List<Integer> sourceIndexes = null;
        List<Integer> targetIndexes = null;

        if(isEmptyMap(itemAmounts)) return result;


        if (SlotOperations.containsIgnoreCase(targetContainer, "Shulker")) {
            if (!openShulkerBox(targetContainer)) return result;
            sourceIndexes = Indexes.Shulker.TOTAL_INVENTORY;
            targetIndexes = (front ? Indexes.Shulker.SHULKER_BOX : Indexes.Shulker.SHULKER_BOX_REVERSE);
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            InventorySaver.Shulker(targetContainer).update("Send Item");
            closeScreen();
        } else if (SlotOperations.containsIgnoreCase(targetContainer, "pv")) {
            if (!openPV1("")) return result;
            sourceIndexes = Indexes.PV.TOTAL_INVENTORY;
            targetIndexes = (front ? Indexes.PV.PV : Indexes.PV.PV_REVERSE);
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            InventorySaver.PV(targetContainer).update("Send Item");
            closeScreen();
        } else if (SlotOperations.containsIgnoreCase(targetContainer, "enderchest")) {
            LOGGER.error("EnderChest not implemented yet");
        } else {
            LOGGER.error("Invalid target container specified" + targetContainer);
        }
        return result;
    }

    public static Map<String, Integer> takeItems(Map<String, Integer> itemAmounts, String sourceContainer, boolean front) {
        DEBUG.Shulker("Material to take: " + itemAmounts + " from " + sourceContainer);
        Map<String, Integer> result = itemAmounts;
        List<Integer> sourceIndexes = null;
        List<Integer> targetIndexes = null;

        if(isEmptyMap(itemAmounts)) return result;


        if (SlotOperations.containsIgnoreCase(sourceContainer, "Shulker")) {
            if (!openShulkerBox(sourceContainer)) return result;
            sourceIndexes = (front ? Indexes.Shulker.SHULKER_BOX : Indexes.Shulker.SHULKER_BOX_REVERSE);
            targetIndexes = Indexes.Shulker.TOTAL_INVENTORY;
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            InventorySaver.Shulker(sourceContainer).update("Take Item");
            closeScreen();
        } else if (SlotOperations.containsIgnoreCase(sourceContainer, "pv")) {
            if (!openPV1("")) return result;
            sourceIndexes = (front ? Indexes.PV.PV : Indexes.PV.PV_REVERSE);
            targetIndexes = Indexes.PV.TOTAL_INVENTORY;
            result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
            InventorySaver.PV(sourceContainer).update("Take Item");
            closeScreen();
        } else if (SlotOperations.containsIgnoreCase(sourceContainer, "enderchest")) {
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

    public static int amountToCompleteInventory(String itemName, int amount){
        return Math.max(0, amount - InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(itemName));
    }

    public static boolean forceCompleteItemsToInventory(Map<String, Integer> itemsAmount){
        for(Map.Entry<String, Integer> entry : itemsAmount.entrySet())
            if(!forceCompleteItemsToInventory(entry.getKey(), entry.getValue()))
                return false;
        return true;
    }

    public static boolean forceCompleteItemsToInventory(String itemName, int amount){
        // This function will take items from  PV and shulkers and shukers in PV to complete the required amount into the inventory
        // Starts with PV1 then shulkers then PV1 shulkers

        if(amountToCompleteInventory(itemName, amount) > 0)
            takeItems(Map.of(itemName, amountToCompleteInventory(itemName, amount)), MyPV.PV1, false);

        String shulkerName = ShulkerBoxStorage.getBoxNameForItem(itemName);
        while(InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(shulkerName) > 0 && amountToCompleteInventory(itemName, amount) > 0) {
            takeItems(Map.of(itemName, amountToCompleteInventory(itemName, amount)), shulkerName, false);
            if(InventorySaver.Shulker(shulkerName).getItemCountByName(itemName) <= 0)
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, false);
        }


        int numShulkers = InventorySaver.PV(MyPV.PV1).getItemCountByName(shulkerName);
        while(numShulkers > 0 && amountToCompleteInventory(itemName, amount) > 0) {
            takeItems(Map.of(shulkerName, 1), MyPV.PV1, true);
            takeItems(Map.of(itemName, amountToCompleteInventory(itemName, amount)), shulkerName, false);
            if(InventorySaver.Shulker(shulkerName).getItemCountByName(itemName) > 0)
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
            else
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, false);
            numShulkers--;
        }

        return amountToCompleteInventory(itemName, amount) <= 0;
    }

    public static void forceCompleteItemsToShulkers(Map<String, Integer> itemsAmount){

        openPV1("");
        closeScreen();

        Map<String, Map<String, Integer>> shulkerItems = new HashMap<>();
        for(Map.Entry<String, Integer> entry : itemsAmount.entrySet()) {
            String itemName = entry.getKey();
            String shulkerName = ShulkerBoxStorage.getBoxNameForItem(itemName);
            shulkerItems.putIfAbsent(shulkerName, new HashMap<>());
            int amountWeHave = InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(itemName);
            shulkerItems.get(shulkerName).put(itemName, Math.min(entry.getValue(), amountWeHave));
        }

        DEBUG.Shulker("Shulker Items: " + shulkerItems);

        for(Map.Entry<String, Map<String, Integer>> entry : shulkerItems.entrySet()){
            String shulkerName = entry.getKey();
            int numOfShulkersInPv = InventorySaver.PV(MyPV.PV1).getItemCountByName(shulkerName);
            DEBUG.Shulker("Shulker Name: " + shulkerName + ", Num of Shulkers in PV: " + numOfShulkersInPv);
            Trade shulkerTrade = ShulkerBoxStorage.getTradeFoShulkerBox(shulkerName);
            Map<String, Integer> items = entry.getValue();
            if(isEmptyMap(items)) continue;

            Map<String, Integer> remainingItems = new HashMap<>(items);

            // Check if we have the shulker box in the inventory
            boolean invHaveShulker = InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(shulkerName) > 0;
            while(invHaveShulker && !isEmptyMap(remainingItems)){
                remainingItems = sendItems(remainingItems, shulkerName, false);
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
                invHaveShulker = InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(shulkerName) > 0;
            }

            // Check if we have the shulker box in the PV
            for(int i = 0; i < numOfShulkersInPv && !isEmptyMap(remainingItems); i++){
                takeItems(Map.of(shulkerName, 1), MyPV.PV1, false);
                remainingItems = sendItems(remainingItems, shulkerName, false);
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
            }

            // Buy shulker and fill it
            while(!isEmptyMap(remainingItems)){
                forceCompleteItemsToInventory("Raw Gold", 3);
                buyItem(shulkerTrade, 1);
                Sleep(2000);
                openInventory("");
                closeScreen();
                remainingItems = sendItems(remainingItems, shulkerName, false);
                sendItems(Map.of(shulkerName, 1), MyPV.PV1, true);
            }
        }

    }


    public static boolean isShulkerFull(String shulkerName) {
        openShulkerBox(shulkerName);
        int emptySlots = countEmptySlots(Indexes.Shulker.SHULKER_BOX);
        closeScreen();
        return emptySlots <= 1;
    }

}
