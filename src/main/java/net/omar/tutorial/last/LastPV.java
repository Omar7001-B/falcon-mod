package net.omar.tutorial.last;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.indexes.Indexes;

import java.util.HashMap;
import java.util.Map;

public class LastPV {

    public static int filledSlots;
    public static int emptySlots;
    public static Map<String, Integer> itemCounts = new HashMap<>();

    public static void updatePVData(String operation) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        if(!(client.currentScreen instanceof GenericContainerScreen)) { resetPVData(); return; }
        DEBUG.Store("Operation: " + operation);
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        filledSlots = 0;
        emptySlots = 0;
        itemCounts.clear();

        Indexes.PV.PV.forEach(index -> {
            Slot slot = slots.get(index);
            if (slot.hasStack()) {
                filledSlots++;
                String itemName = slot.getStack().getItem().getName().getString();
                itemCounts.merge(itemName, slot.getStack().getCount(), Integer::sum);
            } else {
                emptySlots++;
            }
        });
        LastInventory.updateFromPV(operation  + " - LastPV");
    }

    public static void resetPVData() {
        filledSlots = 0;
        emptySlots = 0;
        itemCounts.clear();
    }

    public static void showPVData() {
        DEBUG.Store("Data about the Player's PV:");
        DEBUG.Store("Filled Slots: " + filledSlots);
        DEBUG.Store("Empty Slots: " + emptySlots);
        itemCounts.forEach((item, count) -> DEBUG.Store("Item: " + item + ", Count: " + count));
        DEBUG.Store(("-------------------"));
    }

    public static int getItemCountByName(String searchStr) {
        DEBUG.Shop("Searching for: " + searchStr);
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            if (SlotOperations.containsIgnoreCase(entry.getKey(), searchStr)) {
                DEBUG.Shop("Found: " + entry.getKey() + ", Count: " + entry.getValue());
                return entry.getValue();
            }
        }
        return 0;
    }
}
