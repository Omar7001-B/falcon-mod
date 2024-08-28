package net.omar.tutorial.indexes;

import java.util.List;

public class ShulkerInventoryIndexes {

    // Shulker Box indexes (0-26)
    public static final List<Integer> SHULKER_BOX_INDEXES = List.of(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
    );

    // Main inventory indexes (27-53)
    public static final List<Integer> MAIN_INVENTORY_INDEXES = List.of(
            27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
            43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
    );

    // Hotbar indexes (54-62)
    public static final List<Integer> HOTBAR_INDEXES = List.of(
            54, 55, 56, 57, 58, 59, 60, 61, 62
    );

    // Total inventory indexes
    public static final List<Integer> TOTAL_INVENTORY_INDEXES = List.of(
             27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53,
            54, 55, 56, 57, 58, 59, 60, 61, 62
    );

    // Utility method to check if an index is in the Shulker Box range
    public static boolean isShulkerBoxIndex(int index) {
        return SHULKER_BOX_INDEXES.contains(index);
    }

    // Utility method to check if an index is in the main inventory range
    public static boolean isMainInventoryIndex(int index) {
        return MAIN_INVENTORY_INDEXES.contains(index);
    }

    // Utility method to check if an index is in the hotbar range
    public static boolean isHotbarIndex(int index) {
        return HOTBAR_INDEXES.contains(index);
    }
}
