package net.omar.tutorial.last;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.indexes.ShulkerInventoryIndexes;

import java.util.HashMap;
import java.util.Map;

public class LastShulker {

    public static int filledSlots;
    public static int emptySlots;
    public static Map<String, Integer> itemCounts = new HashMap<>();

    public static void updateShulkerData(String operation) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        if(!(client.currentScreen instanceof ShulkerBoxScreen)) { resetShulkerData(); return; }
        DEBUG.Store("Operation: " + operation);
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        filledSlots = 0;
        emptySlots = 0;
        itemCounts.clear();

        ShulkerInventoryIndexes.SHULKER_BOX_INDEXES.forEach(index -> {
            Slot slot = slots.get(index);
            if (slot.hasStack()) {
                filledSlots++;
                String itemName = slot.getStack().getItem().getName().getString();
                itemCounts.merge(itemName, slot.getStack().getCount(), Integer::sum);
            } else {
                emptySlots++;
            }
        });
    }

    public static void resetShulkerData() {
        filledSlots = 0;
        emptySlots = 0;
        itemCounts.clear();
    }

    public static void showShulkerData() {
        DEBUG.Store("Data about the Shulker:");
        DEBUG.Store("Filled Slots: " + filledSlots);
        DEBUG.Store("Empty Slots: " + emptySlots);
        itemCounts.forEach((item, count) -> DEBUG.Store("Item: " + item + ", Count: " + count));
        DEBUG.Store(("-------------------"));
    }
}
