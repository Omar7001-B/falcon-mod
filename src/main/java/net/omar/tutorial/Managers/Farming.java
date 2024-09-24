package net.omar.tutorial.Managers;

import net.omar.tutorial.classes.Shopper;
import net.omar.tutorial.classes.Trader;
import net.omar.tutorial.Vaults.InventorySaver;
import net.omar.tutorial.Vaults.MyInventory;
import net.omar.tutorial.Vaults.MyPV;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.omar.tutorial.Managers.Inventorying.*;
import static net.omar.tutorial.Managers.Trading.executeTrade;

public class Farming {
    // -----------------------------  Farming Functions -----------------------------
    public static void farmMaterialIntoShulker(String outputItem, int numberOfShulkers) {
        Debugging.Shulker("Farming: " + outputItem + " into " + numberOfShulkers + " shulkers");
        if (!outputItem.equals("Raw Gold") && !outputItem.equals("Gold Block") && !outputItem.equals("Gold Nugget") && !outputItem.equals("Gold Ingot"))
            return;
        if (numberOfShulkers < 1) return;
        String cycleItem = "Raw Gold";
        List<Trader> cycleTrades = new ArrayList<>(Shopper.farmPathFromItem(cycleItem));

        Trader lastCycleTrade = cycleTrades.remove(cycleTrades.size() - 1);
        Trader lastOutputTrade = Shopper.shortPathFromItemToItem(lastCycleTrade.firstItemName, outputItem).get(0);

        String shulker = Shulkering.getBoxNameForItem(lastOutputTrade);
        Trader shulkerTrade = Shulkering.getTradeFoShulkerBox(shulker);

        forceCompleteItemsToInventory(cycleItem, amountToCompleteInventory(cycleItem, Trading.calcMaxTradeInputForInventory(cycleTrades)));

        int shulkerFarmedCount = 0;
        //DEBUG.Shulker("Before Loop: " + shulker);

        for (int i = 0; i < 999999; i++) {
            //DEBUG.Shulker("Inside loop: " + outputItem + " into " + shulker + " Shulker: " + shulkerFarmedCount);

            int cycleInput = Trading.calcMaxTradeInputForInventory(cycleTrades);
            takeItems(Map.of(cycleItem, amountToCompleteInventory(cycleItem, cycleInput)), shulker, true);

            boolean isShulkerFull = InventorySaver.Shulker(shulker).filledSlots > 25;
            if (isShulkerFull) {
                //DEBUG.Shulker("Shulker is full: " + shulker);
                sendItems(Map.of(shulker, 1), MyPV.PV1, true);
                shulkerFarmedCount++;
                InventorySaver.Shulker(shulker).reset();
                if (shulkerFarmedCount >= numberOfShulkers) return;
            }

            boolean isShulkerExist = InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(shulker) > 0;
            if (!isShulkerExist) executeTrade(List.of(Triple.of(shulkerTrade, 0, 1)));

            // Execute trade for each item in cycleTrades
            for (Trader trade : cycleTrades)
                executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int itemsInShulker = InventorySaver.Shulker(shulker).getItemCountByName(cycleItem);
            if (itemsInShulker > cycleInput) {
                executeTrade(List.of(Triple.of(lastOutputTrade, 99999, 0)));
            } else {
                int preLastInput = Trading.calcInputforTradeOutput(cycleInput, List.of(lastCycleTrade));
                int clicks = preLastInput / lastCycleTrade.firstItemAmount;
                executeTrade(
                        List.of(Triple.of(lastCycleTrade, clicks, 0),
                                Triple.of(lastOutputTrade, 99999, 0),
                                Triple.of(lastCycleTrade, 10, 0))
                );
            }

            sendItems(new LinkedHashMap<>(Map.of((outputItem.equals(cycleItem) ? "ZZZZZZZ" : outputItem), 1000, cycleItem, 1000)), shulker, true);
        }
    }

    public static void farmMaterialIntoPV(String outputItem, int amountNeeded) {
        // Requirements : Player should have at least 16 of the output item in his PV
        if (!outputItem.equals("Raw Gold") && !outputItem.equals("Gold Block") && !outputItem.equals("Gold Nugget") && !outputItem.equals("Gold Ingot"))
            return;
        if (amountNeeded < 1) return;

        String cycleItem = "Raw Gold";
        List<Trader> cycleTrades = new ArrayList<>(Shopper.farmPathFromItem(cycleItem));

        Trader lastCycleTrade = cycleTrades.remove(cycleTrades.size() - 1);
        Trader lastOutputTrade = Shopper.shortPathFromItemToItem(lastCycleTrade.firstItemName, outputItem).get(0);


        forceCompleteItemsToInventory(cycleItem, amountToCompleteInventory(cycleItem, Trading.calcMaxTradeInputForInventory(cycleTrades)));

        int originalPVCycleItems = InventorySaver.PV(MyPV.PV1).getItemCountByName(cycleItem);
        sendItems(Map.of(cycleItem, 1000, (outputItem.equals(cycleItem) ? "ZZZZZZZ" : outputItem), 1000), MyPV.PV1, true);

        while (amountNeeded > 0) {
            int cycleInput = Trading.calcMaxTradeInputForInventory(cycleTrades);
            takeItems(Map.of(cycleItem, amountToCompleteInventory(cycleItem, cycleInput)), MyPV.PV1, true);

            for (Trader trade : cycleTrades)
                executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int itemsInPV = InventorySaver.PV(MyPV.PV1).getItemCountByName(cycleItem);
            if (itemsInPV - originalPVCycleItems > cycleInput) {
                executeTrade(List.of(Triple.of(lastOutputTrade, 99999, 0)));
            } else {
                int preLastInput = Trading.calcInputforTradeOutput(cycleInput, List.of(lastCycleTrade));
                int clicks = preLastInput / lastCycleTrade.firstItemAmount;
                executeTrade(
                        List.of(Triple.of(lastCycleTrade, clicks, 0),
                                Triple.of(lastOutputTrade, 99999, 0),
                                Triple.of(lastCycleTrade, 10, 0))
                );
            }

            if (cycleItem.equals(outputItem))
                amountNeeded -= InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(outputItem) - cycleInput;
            else
                amountNeeded -= InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(outputItem);

            sendItems(Map.of(cycleItem, 1000, (outputItem.equals(cycleItem) ? "ZZZZZZZ" : outputItem), 1000), MyPV.PV1, true);
        }
    }
}
