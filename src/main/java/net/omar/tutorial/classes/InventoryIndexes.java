package net.omar.tutorial.classes;

import java.util.List;

public class InventoryIndexes {

    // Hotbar indexes
    public static final List<Integer> HOTBAR_INDEXES = List.of(36, 37, 38, 39, 40, 41, 42, 43, 44);

    // Offhand slot index
    public static final int OFFHAND_SLOT = 45;

    // Armor indexes
    public static final List<Integer> ARMOR_INDEXES = List.of(5, 6, 7, 8);

    // Main inventory indexes
    public static final List<Integer> MAIN_INVENTORY_INDEXES = List.of(
            9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,
            28, 29, 30, 31, 32, 33, 34, 35
    );

    // Utility method to check if an index is in the hotbar range
    public static boolean isHotbarIndex(int index) {
        return HOTBAR_INDEXES.contains(index);
    }

    // Utility method to check if an index is in the main inventory range
    public static boolean isMainInventoryIndex(int index) {
        return MAIN_INVENTORY_INDEXES.contains(index);
    }

    // Utility method to check if an index is an armor slot
    public static boolean isArmorIndex(int index) {
        return ARMOR_INDEXES.contains(index);
    }

    // Utility method to check if an index is the offhand slot
    public static boolean isOffhandSlot(int index) {
        return index == OFFHAND_SLOT;
    }
}
