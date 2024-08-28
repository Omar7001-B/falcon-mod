package net.omar.tutorial.Inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.indexes.PVInventoryIndexes;
import net.omar.tutorial.indexes.ShulkerInventoryIndexes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.omar.tutorial.Tutorial.MOD_ID;

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
        LOGGER.info("Showing slots:");
        if (slots == null) {
            LOGGER.info("No slots found");
            return;
        }

        for (int index : indexes) {
            Slot slot = slots.get(index);
            LOGGER.info("Slot Index: " + index);
            LOGGER.info("Item: " + (slot.hasStack() ? slot.getStack().getItem().getName().getString() : "Empty"));
            LOGGER.info("Amount: " + (slot.hasStack() ? slot.getStack().getCount() : 0));
            LOGGER.info("-------------------");
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

    public static int countTotalElementAmount(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return indexes.stream()
                .mapToInt(index -> slots.get(index).hasStack() ? slots.get(index).getStack().getCount() : 0)
                .sum();
    }

    public static int getFirstEmptySlot(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return -1;

        return indexes.stream()
                .filter(index -> !slots.get(index).hasStack())
                .findFirst()
                .orElse(-1); // No empty slot found
    }

    public static int countEmptySlots(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return (int) indexes.stream()
                .filter(index -> !slots.get(index).hasStack())
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

    public static void sendAmountFromSourceToTarget(List<Integer> sourceIndexes, List<Integer> targetIndexes, String itemName, int amount) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return;

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
                    if (targetIndex == -1) return;
                    for(int i = 0; i < toTransfer; i++) {
                        SlotClicker.slotRightClick(targetIndex);
                    }
                    SlotClicker.slotNormalClick(sourceIndex); // Return remaining items
                }
                amount -= toTransfer;
                if (amount <= 0) return;
            }
        }
    }

    public static void sendItem(String itemName, int amount, String targetContainer) {
        if(containsIgnoreCase(targetContainer, "pv")) {
            sendAmountFromSourceToTarget(PVInventoryIndexes.TOTAL_INVENOTRY_INDEXES, PVInventoryIndexes.PV_INDEXES, itemName, amount);
        }
        else if(containsIgnoreCase(targetContainer, "Shulker")) {
            sendAmountFromSourceToTarget(ShulkerInventoryIndexes.TOTAL_INVENTORY_INDEXES, ShulkerInventoryIndexes.SHULKER_BOX_INDEXES, itemName, amount);
        }
    }

    public static void takeItem(String itemName, int amount, String sourceContainer) {
        if(containsIgnoreCase(sourceContainer, "pv")) {
            sendAmountFromSourceToTarget(PVInventoryIndexes.PV_INDEXES, PVInventoryIndexes.MAIN_INVENTORY_INDEXES, itemName, amount);
        }
        else if(containsIgnoreCase(sourceContainer, "Shulker")) {
            sendAmountFromSourceToTarget(ShulkerInventoryIndexes.SHULKER_BOX_INDEXES, ShulkerInventoryIndexes.TOTAL_INVENTORY_INDEXES, itemName, amount);
        }
        else {
            LOGGER.error("Invalid source container");
        }
    }
}
