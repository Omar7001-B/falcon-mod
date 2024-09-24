package net.omar.tutorial.Managers;

public class Olding {

    /*

    public static void farmGoldBlock(String unused) {
        List<Trade> trades = List.of(Market.rawGoldToDiamond_t, Market.diamondToGoldNugget_t, Market.goldNuggetToEmerald_t);
        for (int i = 0; i < 999999; i++) {
            int inputForSlots = TradeManager.calcMaxTradeInputForInventory(trades);

            takeItems(Map.of("Raw Gold", inputForSlots), "Black Shulker", true);

            if (InventorySaver.Shulker("Black Shulker").emptySlots < 3) {
                sendItems(Map.of("Black Shulker", 1), "pv", true);
                TradeManager.executeTrade(List.of(Triple.of(Market.rawgoldToBlackBox_t, 1, 1)));
            }

            for(Trade trade: trades)
                TradeManager.executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int rawGoldInShulker = InventorySaver.Shulker("Black Shulker").getItemCountByName("Raw Gold");
            if(rawGoldInShulker > inputForSlots){
                TradeManager.executeTrade( List.of( Triple.of(Market.emeraldToGoldBlock_t, 99999, 0) ));
            }
            else {
                int emerled = TradeManager.calcInputforTradeOutput(inputForSlots, List.of(Market.emeraldToRawGold_t));
                int clicks = emerled / 64;
                TradeManager.executeTrade(
                        List.of(Triple.of(Market.emeraldToRawGold_t, clicks, 0),
                                Triple.of(Market.emeraldToGoldBlock_t, 99999, 0),
                                Triple.of(Market.emeraldToRawGold_t, 10, 0)
                        ));
            }
            sendItems(Map.of("Raw Gold", 1000, "Gold Block", 1000), "Black Shulker", true);
        }
    }
    public static void farmGoldIngot(String unused) {
        List<Trade> trades = List.of(Market.rawGoldToDiamond_t, Market.diamondToGoldNugget_t, Market.goldNuggetToEmerald_t);
        for (int i = 0; i < 999999; i++) {
            int inputForSlots = TradeManager.calcMaxTradeInputForInventory(trades);

            takeItems(Map.of("Raw Gold", inputForSlots), "Black Shulker", true);

            if (InventorySaver.Shulker("Black Shulker").emptySlots < 3) {
                sendItems(Map.of("Black Shulker", 1), "pv", true);
                TradeManager.executeTrade(List.of(Triple.of(Market.rawgoldToBlackBox_t, 1, 1)));
            }

            for(Trade trade: trades)
                TradeManager.executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int rawGoldInShulker = InventorySaver.Shulker("Black Shulker").getItemCountByName("Raw Gold");
            if(rawGoldInShulker > inputForSlots){
                TradeManager.executeTrade( List.of( Triple.of(Market.emeraldToGoldIngot_t, 99999, 0) ));
            }
            else {
                int emerled = TradeManager.calcInputforTradeOutput(inputForSlots, List.of(Market.emeraldToRawGold_t));
                int clicks = emerled / 64;
                TradeManager.executeTrade(
                        List.of(Triple.of(Market.emeraldToRawGold_t, clicks, 0),
                                Triple.of(Market.emeraldToGoldIngot_t, 99999, 0),
                                Triple.of(Market.emeraldToRawGold_t, 10, 0)
                        ));
            }
            sendItems(Map.of("Raw Gold", 1000, "Gold Block", 1000), "Black Shulker", true);
        }
    }


    public static void farmGoldNugget(String unused) {
        List<Trade> trades = List.of(Market.rawGoldToDiamond_t, Market.diamondToGoldNugget_t, Market.goldNuggetToEmerald_t);
        for (int i = 0; i < 999999; i++) {
            int inputForSlots = TradeManager.calcMaxTradeInputForInventory(trades);

            takeItems(Map.of("Raw Gold", inputForSlots), "Black Shulker", true);

            if (InventorySaver.Shulker("Black Shulker").emptySlots < 3) {
                sendItems(Map.of("Black Shulker", 1), "pv", true);
                TradeManager.executeTrade(List.of(Triple.of(Market.rawgoldToBlackBox_t, 1, 1)));
            }

            for(Trade trade: trades)
                TradeManager.executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int rawGoldInShulker = InventorySaver.Shulker("Black Shulker").getItemCountByName("Raw Gold");
            if(rawGoldInShulker > inputForSlots){
                TradeManager.executeTrade( List.of( Triple.of(Market.emeraldToGoldNugget_t, 99999, 0) ));
            }
            else {
                int emerled = TradeManager.calcInputforTradeOutput(inputForSlots, List.of(Market.emeraldToRawGold_t));
                int clicks = emerled / 64;
                TradeManager.executeTrade(
                        List.of(Triple.of(Market.emeraldToRawGold_t, clicks, 0),
                                Triple.of(Market.emeraldToGoldNugget_t, 99999, 0),
                                Triple.of(Market.emeraldToRawGold_t, 10, 0)
                        ));
            }
            sendItems(Map.of("Raw Gold", 1000, "Gold Block", 1000), "Black Shulker", true);
        }
    }
     */
}

/*

    public static void farmRawGold(String unused) {
        List<Trade> trades = List.of(Market.rawGoldToDiamond_t, Market.diamondToGoldNugget_t, Market.goldNuggetToEmerald_t, Market.emeraldToRawGold_t);
        for (int i = 0; i < 999999; i++) {
            int inputForSlots = TradeManager.calcMaxTradeInputForInventory(trades);

            takeItems(Map.of("Raw Gold", inputForSlots), "Black Shulker", true);

            if (InventorySaver.Shulker("Black Shulker").emptySlots < 3) {
                sendItems(Map.of("Black Shulker", 1), "pv", true);
                TradeManager.executeTrade(List.of(Triple.of(Market.rawgoldToBlackBox_t, 1, 1)));
            }

            for(Trade trade: trades)
                TradeManager.executeTrade(List.of(Triple.of(trade, 99999, 0)));

            sendItems(Map.of("Raw Gold", 1000), "Black Shulker", true);
        }
    }

    public static void farmRawGold2(Trade outTrade) {
        String farmCycleItem = "Raw Gold";
        List<Trade> trades = TreeNode.farmPathFromItem(farmCycleItem);
        trades.remove(trades.size() - 1);
        String shulkerBox = ShulkerBoxStorage.getBoxNameForItem(outTrade);
        for (int i = 0; i < 999999; i++) {
            int inputForSlots = TradeManager.calcMaxTradeInputForInventory(trades);

            takeItems(Map.of(farmCycleItem, inputForSlots), shulkerBox, true);

            if (InventorySaver.Shulker(shulkerBox).emptySlots < 3) {
                sendItems(Map.of(farmCycleItem, 1), MyPV.PV1, true);
                TradeManager.executeTrade(List.of(Triple.of(outTrade, 1, 1)));
            }

            for(Trade trade: trades)
                TradeManager.executeTrade(List.of(Triple.of(trade, 99999, 0)));

            sendItems(Map.of(farmCycleItem, 1000), shulkerBox, true);
        }
    }
 */

//    public static void dropItem(String itemName) {
//        openInventory("");
//        int slot = SlotOperations.getSlotIndexContainsName(itemName);
//        if (slot == -1) return;
//        SlotClicker.slotDropOne(slot);
//        closeScreen();
//    }
