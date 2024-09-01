package net.omar.tutorial.Inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.indexes.Indexes;
import net.omar.tutorial.last.InventorySaver;
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

        // Split the string between each lowercase and uppercase letter
        //DEBUG.Store("Original String: " + searchStr);
        searchStr = searchStr.replaceAll("([a-z])([A-Z])", "$1 $2");
        //DEBUG.Store("Modified String: " + searchStr);

        // Convert both strings to lowercase and split the search string into words
        String[] wordsToSearch = searchStr.toLowerCase().split("\\s+");
        String lowerStr = str.toLowerCase();

        // Check if each word in the search string is contained in the main string
        for (String word : wordsToSearch) {
            if (!lowerStr.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private static DefaultedList<Slot> getSlots() {
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

    public static void showAllTrades(){
        List<TradeOffer> offers = ((MerchantScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().getRecipes();
        for(int i = 0; i < offers.size(); i++){
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
        if(slots == null) return -1;
        for (int i = 0; i < slots.size(); i++)
            if(slots.get(i).getStack().getName().getString().contains(itemName)) return i;
        return -1;
    }


    public static int getSlotIndexByName(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if(slots == null) return -1;
        for (int i = 0; i < slots.size(); i++)
            if (slots.get(i).getStack().getName().getString().equals(itemName)) return i;
        return -1;
    }

    public static int getSlotIndexByItemNameIgnoreCase(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if(slots == null) return -1;
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

    public static int getFirstEmptySlot(List<Integer> indexes) {
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

    public static boolean transferItems(Map<String, Integer> itemAmounts, List<Integer> sourceIndexes, List<Integer> targetIndexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return false;

        Map<String, Integer> remainingAmounts = new HashMap<>(itemAmounts);

        for (int sourceIndex : sourceIndexes) {
            Slot sourceSlot = slots.get(sourceIndex);
            if (!sourceSlot.hasStack()) continue;

            String itemName = sourceSlot.getStack().getItem().getName().getString();
            //DEBUG.Store("Item: " + itemName + ", Amount: " + sourceSlot.getStack().getCount());
            for (Map.Entry<String, Integer> entry : remainingAmounts.entrySet()) {
                //DEBUG.Store("Item: " + entry.getKey() + ", Amount: " + entry.getValue());
                if (entry.getValue() == 0 || !containsIgnoreCase(itemName, entry.getKey())) continue;

                int toTransfer = Math.min(entry.getValue(), sourceSlot.getStack().getCount());
                if (toTransfer == sourceSlot.getStack().getCount()) {
                    SlotClicker.slotShiftLeftClick(sourceIndex);
                } else {
                    SlotClicker.slotNormalClick(sourceIndex);
                    int targetIndex = getFirstEmptySlot(targetIndexes);
                    if (targetIndex == -1) return false;
                    for (int i = 0; i < toTransfer; i++) SlotClicker.slotRightClick(targetIndex);
                    SlotClicker.slotNormalClick(sourceIndex);
                }

                entry.setValue(entry.getValue() - toTransfer);
                break;
            }
        }

        return remainingAmounts.values().stream().allMatch(v -> v <= 0);
    }


    public static boolean sendItems(Map<String, Integer> itemAmounts, String targetContainer) {
        boolean result = false;
        List<Integer> sourceIndexes = null;
        List<Integer> targetIndexes = null;

        switch (targetContainer.toLowerCase()) {
            case "pv":
                openPV1("");
                sourceIndexes = Indexes.PV.TOTAL_INVENTORY;
                targetIndexes = Indexes.PV.PV;
                result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
                InventorySaver.PV("pv 1").update("Send Item To PV");
                closeScreen();
                break;

            case "shulker":
                openShulkerBox("Shulker");
                sourceIndexes = Indexes.Shulker.TOTAL_INVENTORY;
                targetIndexes = Indexes.Shulker.SHULKER_BOX;
                result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
                InventorySaver.Shulker("Shulker").update("Send Item To Shulker");
                closeScreen();
                break;

            case "enderchest":
                LOGGER.error("EnderChest not implemented yet");
                break;

            default:
                LOGGER.error("Invalid target container specified");
                break;
        }

        return result;
    }

    public static boolean takeItems(Map<String, Integer> itemAmounts, String sourceContainer) {
        boolean result = false;
        List<Integer> sourceIndexes = null;
        List<Integer> targetIndexes = null;

        switch (sourceContainer.toLowerCase()) {
            case "pv":
                openPV1("");
                sourceIndexes = Indexes.PV.PV;
                targetIndexes = Indexes.PV.MAIN_INVENTORY;
                result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
                InventorySaver.PV("pv 1").update("Take Item");
                closeScreen();
                break;

            case "shulker":
                openShulkerBox("Shulker");
                sourceIndexes = Indexes.Shulker.SHULKER_BOX;
                targetIndexes = Indexes.Shulker.TOTAL_INVENTORY;
                result = transferItems(itemAmounts, sourceIndexes, targetIndexes);
                InventorySaver.Shulker("Shulker").update("Take Item");
                closeScreen();
                break;

            case "enderchest":
                LOGGER.error("EnderChest not implemented yet");
                break;

            default:
                LOGGER.error("Invalid source container specified");
                break;
        }

        return result;
    }

    public static boolean isShulkerFull(String shulkerName) {
        openShulkerBox(shulkerName);
        int emptySlots = countEmptySlots(Indexes.Shulker.SHULKER_BOX);
        closeScreen();
        return emptySlots <= 1;
    }

}
