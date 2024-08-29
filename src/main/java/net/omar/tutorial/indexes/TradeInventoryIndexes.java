package net.omar.tutorial.indexes;

import java.util.List;

public class TradeInventoryIndexes {
    // Slot indexes
    public static final int FIRST_ITEM_SLOT = 0;
    public static final int SECOND_ITEM_SLOT = 1;
    public static final int RESULT_SLOT = 2;

    // Main inventory slots from 3 to 29
    public static final List<Integer> MAIN_INVENTORY = List.of(
            3, 4, 5, 6, 7, 8, 9, 10, 11,
            12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29
    );

    // Hotbar slots from 30 to 38
    public static final List<Integer> HOTBAR = List.of(
            30, 31, 32, 33, 34, 35, 36, 37, 38
    );

    // Total inventory slots from 3 to 38
    public static final List<Integer> TOTAL_INVENTORY = List.of(
            3, 4, 5, 6, 7, 8, 9, 10, 11,
            12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, 36, 37, 38
    );

    // Utility method to check if an index is the first item slot
    public static boolean isFirstItemSlot(int index) {
        return index == FIRST_ITEM_SLOT;
    }

    // Utility method to check if an index is the second item slot
    public static boolean isSecondItemSlot(int index) {
        return index == SECOND_ITEM_SLOT;
    }

    // Utility method to check if an index is the result slot
    public static boolean isResultSlot(int index) {
        return index == RESULT_SLOT;
    }

    // Utility method to check if an index is in the main inventory range
    public static boolean isMainInventorySlot(int index) {
        return MAIN_INVENTORY.contains(index);
    }

    // Utility method to check if an index is in the hotbar range
    public static boolean isHotbarSlot(int index) {
        return HOTBAR.contains(index);
    }

    // Utility method to check if an index is in the total inventory range
    public static boolean isTotalInventorySlot(int index) {
        return TOTAL_INVENTORY.contains(index);
    }
}
