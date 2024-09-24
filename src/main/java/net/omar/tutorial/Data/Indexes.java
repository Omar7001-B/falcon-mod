package net.omar.tutorial.Data;

import java.util.List;

public class Indexes {

    // General Inventory Indexes
    public static class Inventory {

        // Offhand slot index
        public static final int OFFHAND_SLOT = 45;

        // Armor indexes
        public static final List<Integer> ARMOR = List.of(5, 6, 7, 8);

        // Main inventory indexes
        public static final List<Integer> MAIN = List.of(
                9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 35
        );

        // Hotbar indexes
        public static final List<Integer> HOTBAR = List.of(
                36, 37, 38, 39, 40, 41, 42, 43, 44
        );

        // Total inventory indexes
        public static final List<Integer> TOTAL_INVENTORY = List.of(
                9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        );
    }

    // PV Inventory Indexes
    public static class PV {

        // PV slots (0-53)
        public static final List<Integer> PV = List.of(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53
        );

        // PV Inventory Indexes Reverse
        public static final List<Integer> PV_REVERSE = List.of(
                53, 52, 51, 50, 49, 48, 47, 46, 45,
                44, 43, 42, 41, 40, 39, 38, 37, 36,
                35, 34, 33, 32, 31, 30, 29, 28, 27,
                26, 25, 24, 23, 22, 21, 20, 19, 18,
                17, 16, 15, 14, 13, 12, 11, 10, 9,
                8, 7, 6, 5, 4, 3, 2, 1, 0
        );

        // Main inventory indexes (54-80)
        public static final List<Integer> MAIN_INVENTORY = List.of(
                54, 55, 56, 57, 58, 59, 60, 61, 62,
                63, 64, 65, 66, 67, 68, 69, 70, 71,
                72, 73, 74, 75, 76, 77, 78, 79, 80
        );

        // Hotbar indexes (81-89)
        public static final List<Integer> HOTBAR = List.of(
                81, 82, 83, 84, 85, 86, 87, 88, 89
        );

        // Total inventory indexes
        public static final List<Integer> TOTAL_INVENTORY = List.of(
                54, 55, 56, 57, 58, 59, 60, 61, 62,
                63, 64, 65, 66, 67, 68, 69, 70, 71,
                72, 73, 74, 75, 76, 77, 78, 79, 80,
                81, 82, 83, 84, 85, 86, 87, 88, 89
        );

        // Total Inventory Indexes Reverse
        public static final List<Integer> TOTAL_INVENTORY_REVERSE = List.of(
                89, 88, 87, 86, 85, 84, 83, 82, 81,
                80, 79, 78, 77, 76, 75, 74, 73, 72,
                71, 70, 69, 68, 67, 66, 65, 64, 63,
                62, 61, 60, 59, 58, 57, 56, 55, 54
        );
    }

    // Shulker Inventory Indexes
    public static class Shulker {

        // Shulker Box indexes (0-26)
        public static final List<Integer> SHULKER_BOX = List.of(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23, 24, 25, 26
        );

        // Shulker Inventory Indexes Reverse
        public static final List<Integer> SHULKER_BOX_REVERSE = List.of(
                26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11,
                10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
        );

        // Main inventory indexes (27-53)
        public static final List<Integer> MAIN_INVENTORY = List.of(
                27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
                43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
        );

        // Hotbar indexes (54-62)
        public static final List<Integer> HOTBAR = List.of(
                54, 55, 56, 57, 58, 59, 60, 61, 62
        );

        // Total inventory indexes
        public static final List<Integer> TOTAL_INVENTORY = List.of(
                27, 28, 29, 30, 31, 32, 33, 34, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53,
                54, 55, 56, 57, 58, 59, 60, 61, 62
        );

        // Total Inventory Indexes Reverse
        public static final List<Integer> TOTAL_INVENTORY_REVERSE = List.of(
                62, 61, 60, 59, 58, 57, 56, 55, 54,
                53, 52, 51, 50, 49, 48, 47, 46, 45,
                44, 43, 42, 41, 40, 39, 38, 37, 36,
                35, 34, 33, 32, 31, 30, 29, 28, 27
        );
    }

    public static class Trade {
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

        public static final List<Integer> TOTAL_INVENTORY_WITHOUT_RESULT_SLOT = List.of(
                0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                12, 13, 14, 15, 16, 17, 18, 19, 20,
                21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, 36, 37, 38
        );
    }
}
