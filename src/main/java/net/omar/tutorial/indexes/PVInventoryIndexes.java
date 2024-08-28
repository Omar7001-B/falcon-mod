package net.omar.tutorial.indexes;

import java.util.List;

public class PVInventoryIndexes {

    // PV slots (0-53)
    public static final List<Integer> PV_INDEXES = List.of(
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35,
            36, 37, 38, 39, 40, 41, 42, 43, 44,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    );

    // Main inventory indexes (54-80)
    public static final List<Integer> MAIN_INVENTORY_INDEXES = List.of(
            54, 55, 56, 57, 58, 59, 60, 61, 62,
            63, 64, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 75, 76, 77, 78, 79, 80
    );

    // Hotbar indexes (81-89)
    public static final List<Integer> HOTBAR_INDEXES = List.of(
            81, 82, 83, 84, 85, 86, 87, 88, 89
    );


    public static final List<Integer> TOTAL_INVENOTRY_INDEXES = List.of(
            54, 55, 56, 57, 58, 59, 60, 61, 62,
            63, 64, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 75, 76, 77, 78, 79, 80,
            81, 82, 83, 84, 85, 86, 87, 88, 89
    );

    // Utility method to check if an index is in the PV range
    public static boolean isPVIndex(int index) {
        return PV_INDEXES.contains(index);
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
