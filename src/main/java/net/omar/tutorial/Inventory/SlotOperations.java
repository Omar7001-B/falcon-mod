package net.omar.tutorial.Inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.indexes.PVInventoryIndexes;
import net.omar.tutorial.indexes.ShulkerInventoryIndexes;
import net.omar.tutorial.last.LastShulker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.omar.tutorial.Tutorial.*;

public class SlotOperations {

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;
        return str.toLowerCase().contains(searchStr.toLowerCase());
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

    public static int getSlotIndexByName(String itemName) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) {
            LOGGER.info("No slots found");
            return -1;
        }

        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).hasStack() && containsIgnoreCase(slots.get(i).getStack().getItem().getName().getString(), itemName)) {
                return i;
            }
        }
        return -1; // Not found
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

    public static boolean sendAmountFromSourceToTarget(List<Integer> sourceIndexes, List<Integer> targetIndexes, String itemName, int amount) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return false;

        for (int sourceIndex : sourceIndexes) {
            Slot sourceSlot = slots.get(sourceIndex);
            if (sourceSlot.hasStack() && containsIgnoreCase(sourceSlot.getStack().getItem().getName().getString(), itemName)) {
                int availableAmount = sourceSlot.getStack().getCount();
                int toTransfer = Math.min(amount, availableAmount);

                if(toTransfer == availableAmount) {
                    SlotClicker.slotShiftLeftClick(sourceIndex);
                }
                else {
                    SlotClicker.slotNormalClick(sourceIndex);
                    int targetIndex = getFirstEmptySlot(targetIndexes);
                    if (targetIndex == -1) return false;
                    for(int i = 0; i < toTransfer; i++) {
                        SlotClicker.slotRightClick(targetIndex);
                    }
                    SlotClicker.slotNormalClick(sourceIndex); // Return remaining items
                }
                amount -= toTransfer;
                if (amount <= 0) return true;
            }
        }
        return false;
    }

    public static boolean sendItem(String itemName, int amount, String targetContainer) {
        boolean result = false;
        if(containsIgnoreCase(targetContainer, "pv")) {
            openPV1("");
            result = sendAmountFromSourceToTarget(PVInventoryIndexes.TOTAL_INVENOTRY_INDEXES, PVInventoryIndexes.PV_INDEXES, itemName, amount);
            closeScreen();
        }
        else if(containsIgnoreCase(targetContainer, "Shulker")) {
            openShulkerBox("Shulker");
            result = sendAmountFromSourceToTarget(ShulkerInventoryIndexes.TOTAL_INVENTORY_INDEXES, ShulkerInventoryIndexes.SHULKER_BOX_INDEXES, itemName, amount);

            // Update the shulker data after taking the item
            LastShulker.updateShulkerData("Send Item");
            LastShulker.showShulkerData();

            closeScreen();
        }
        else if(containsIgnoreCase(targetContainer, "EnderChest")) {
            LOGGER.error("EnderChest not implemented yet");
            //openTrade();
            //result = sendAmountFromSourceToTarget(TradeInventoryIndexes.TOTAL_INVENTORY, TradeInventoryIndexes.MAIN_INVENTORY, itemName, amount);
            //closeScreen();
        }
        else {
            LOGGER.error("Invalid target container");
        }
        return result;
    }

    public static void takeItem(String itemName, int amount, String sourceContainer) {
        if(containsIgnoreCase(sourceContainer, "pv")) {
            openPV1("");
            sendAmountFromSourceToTarget(PVInventoryIndexes.PV_INDEXES, PVInventoryIndexes.MAIN_INVENTORY_INDEXES, itemName, amount);
            closeScreen();
        }
        else if(containsIgnoreCase(sourceContainer, "Shulker")) {
            openShulkerBox("Shulker");
            sendAmountFromSourceToTarget(ShulkerInventoryIndexes.SHULKER_BOX_INDEXES, ShulkerInventoryIndexes.TOTAL_INVENTORY_INDEXES, itemName, amount);

            // Update the shulker data after taking the item
             LastShulker.updateShulkerData("Take Item");
             LastShulker.showShulkerData();

             closeScreen();
        }
        else {
            LOGGER.error("Invalid source container");
        }
    }

    public static boolean isShulkerFull(String shulkerName) {
        openShulkerBox(shulkerName);
        int emptySlots = countEmptySlots(ShulkerInventoryIndexes.SHULKER_BOX_INDEXES);
        closeScreen();
        return emptySlots <= 1;
    }

}
