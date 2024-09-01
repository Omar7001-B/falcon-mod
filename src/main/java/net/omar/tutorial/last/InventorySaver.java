package net.omar.tutorial.last;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.indexes.Indexes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventorySaver {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Map<String, InventoryData> inventoryStates = new HashMap<>();

    private static class InventoryData {
        int filledSlots;
        int emptySlots;
        Map<String, Integer> itemCounts = new HashMap<>();
        Map<Integer, ItemSlotInfo> slotData = new HashMap<>();

        static class ItemSlotInfo {
            String itemName;
            int count;

            ItemSlotInfo(String itemName, int count) {
                this.itemName = itemName;
                this.count = count;
            }
        }

        int getItemCountByName(String searchStr) {
            DEBUG.Shop("Searching for: " + searchStr);
            for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                if (SlotOperations.containsIgnoreCase(entry.getKey(), searchStr)) {
                    DEBUG.Shop("Found: " + entry.getKey() + ", Count: " + entry.getValue());
                    return entry.getValue();
                }
            }
            return 0;
        }

        void showData() {
            DEBUG.Store("Data:");
            DEBUG.Store("Filled Slots: " + filledSlots);
            DEBUG.Store("Empty Slots: " + emptySlots);
            itemCounts.forEach((item, count) -> DEBUG.Store("Item: " + item + ", Count: " + count));
            slotData.forEach((index, info) -> DEBUG.Store("Slot: " + index + ", Item: " + info.itemName + ", Count: " + info.count));
            DEBUG.Store(("-------------------"));
        }
    }

    private static void updateInventoryState(String name, String operation, List<Integer> indexes) {
        if (client.currentScreen == null) return;

        InventoryData data = new InventoryData();
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        data.filledSlots = 0;
        data.emptySlots = 0;
        data.itemCounts.clear();
        data.slotData.clear();

        indexes.forEach(index -> {
            Slot slot = slots.get(index);
            if (slot.hasStack()) {
                data.filledSlots++;
                String itemName = slot.getStack().getItem().getName().getString();
                int count = slot.getStack().getCount();
                data.itemCounts.merge(itemName, count, Integer::sum);
                data.slotData.put(index, new InventoryData.ItemSlotInfo(itemName, count));
            } else {
                data.emptySlots++;
            }
        });

        inventoryStates.put(name, data);
        DEBUG.Store("Operation: " + operation);
    }

    public static class Shulker {

        public static void update(String name, String operation) {
            updateInventoryState(name, operation, Indexes.Shulker.SHULKER_BOX);
        }

        public static int getItemCountByName(String searchStr) {
            InventoryData data = inventoryStates.get(searchStr);
            return data != null ? data.getItemCountByName(searchStr) : 0;
        }

        public static void showData(String name) {
            InventoryData data = inventoryStates.get(name);
            if (data != null) data.showData();
        }
    }

    public static class PV {

        public static void update(String name, String operation) {
            updateInventoryState(name, operation, Indexes.PV.PV);
        }

        public static int getItemCountByName(String searchStr) {
            InventoryData data = inventoryStates.get(searchStr);
            return data != null ? data.getItemCountByName(searchStr) : 0;
        }

        public static void showData(String name) {
            InventoryData data = inventoryStates.get(name);
            if (data != null) data.showData();
        }
    }

    public static class Inventory {

        public static void update(String name, String operation) {
            updateInventoryState(name, operation, Indexes.Inventory.TOTAL_INVENTORY);
        }

        public static  void updateFromPV(String name, String operation) {
            updateInventoryState(name, operation, Indexes.PV.TOTAL_INVENTORY);
        }

        public static void updateFromShulker(String name, String operation) {
            updateInventoryState(name, operation, Indexes.Shulker.TOTAL_INVENTORY);
        }

        public static void updateFromTrade(String name, String operation) {
            updateInventoryState(name, operation, Indexes.Trade.TOTAL_INVENTORY);
        }

        public static int getItemCountByName(String searchStr) {
            InventoryData data = inventoryStates.get(searchStr);
            return data != null ? data.getItemCountByName(searchStr) : 0;
        }

        public static void showData(String name) {
            InventoryData data = inventoryStates.get(name);
            if (data != null) data.showData();
        }
    }
}
