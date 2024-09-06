package net.omar.tutorial.Managers;

import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.Inventory.NameConverter;
import net.omar.tutorial.Inventory.SlotClicker;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.Tutorial;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Trade;
import net.omar.tutorial.classes.TreeNode;
import net.omar.tutorial.indexes.Indexes;
import net.omar.tutorial.last.InventorySaver;
import net.omar.tutorial.last.MyInventory;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

public class TradeManager {

    public static boolean isAutomatedTrade;

    public static boolean openShop(String unused) {
        Tutorial.sendCommand("shop");
        return Tutorial.waitForScreenChange();
    }

    // ----------------------------- Trades -----------------------------
    public static int calcNumberOfClicks(TradeOffer offer, int type) {
        //SlotOperations.showAllSlots(null);

        String firstItemName = NameConverter.offerNamesToInventoryNames(offer.getAdjustedFirstBuyItem().getName().getString());
        String secondItemName = NameConverter.offerNamesToInventoryNames(offer.getSecondBuyItem().getName().getString());
        String resultName = NameConverter.offerNamesToInventoryNames(offer.getSellItem().getName().getString());

        int firstItemAmount = offer.getAdjustedFirstBuyItem().getCount();
        int secondItemAmount = offer.getSecondBuyItem().getCount();
        int resultAmount = offer.getSellItem().getCount();

        int totalFirstItemAmount = SlotOperations.countTotalElementAmount(Indexes.Trade.TOTAL_INVENTORY_WITHOUT_RESULT_SLOT, firstItemName);
        int totalSecondItemAmount = firstItemName.equals(secondItemName) ? 0 :
                SlotOperations.countTotalElementAmount(Indexes.Trade.TOTAL_INVENTORY_WITHOUT_RESULT_SLOT, secondItemName);

        //DEBUG.Shop("Total amount of " + firstItemName + ": " + totalFirstItemAmount);
        //DEBUG.Shop("Total amount of " + secondItemName + ": " + totalSecondItemAmount);

        int stack = 64;
        if (firstItemName.equals(secondItemName)) {
            firstItemAmount += secondItemAmount;
            stack += 64;
        }

        int numberOfClicks = 0;
        int numberOfEmptySlots = SlotOperations.countEmptySlots(Indexes.Trade.TOTAL_INVENTORY);
        int totalInputWaste = 0;
        int totalOutputAmount = 0;
        boolean isStacked = NameConverter.isStackedItem(resultName);
        boolean isUpgrade = !NameConverter.isStackedItem(secondItemName);

//
//        DEBUG.Shop("--------Before Clicks---------");
//        DEBUG.Shop("First Item: " + firstItemName + " x " + firstItemAmount);
//        DEBUG.Shop("Second Item: " + secondItemName + " x " + secondItemAmount);
//        DEBUG.Shop("Result: " + resultName + " x " + resultAmount + " (Stacked: " + isStacked + ")");
//        DEBUG.Shop("--------...---------");


        try {
            while (totalFirstItemAmount >= firstItemAmount) {
                int inputWeHave = type == 1 || isUpgrade ? firstItemAmount :
                        Math.min(totalFirstItemAmount - totalFirstItemAmount % firstItemAmount, stack - stack % firstItemAmount);

                if (inputWeHave == 0) break;

                int outPutWeGet = (inputWeHave / firstItemAmount) * resultAmount;
                totalFirstItemAmount -= inputWeHave;

                totalInputWaste += inputWeHave;
                totalOutputAmount += outPutWeGet;

                while (totalInputWaste >= 64) {
                    totalInputWaste -= 64;
                    numberOfEmptySlots++;
                }

                if ((!isStacked && totalOutputAmount <= numberOfEmptySlots) || (isStacked && Math.ceil(totalOutputAmount / 64.0) <= numberOfEmptySlots))
                    numberOfClicks++;
                else break;

            }
        } catch (Exception e) {
            DEBUG.Error("Error in calcNumberOfClicks: " + e.getMessage());
            e.printStackTrace();
        }

//        DEBUG.Shop("Total First Item: " + totalFirstItemAmount);
//        DEBUG.Shop("Total Input Waste: " + totalInputWaste + " of " + firstItemName);
//        DEBUG.Shop("Total Output Amount: " + totalOutputAmount + " of " + resultName);
//        DEBUG.Shop("Number of Clicks: " + numberOfClicks);
//        DEBUG.Shop("Number of Empty Slots: " + numberOfEmptySlots);
//        DEBUG.Shop("--------END---------");

        return numberOfClicks;
    }

    public static int calcInputforTradeOutput(int targetOutput, List<Trade> tradePath) {
        if (tradePath.isEmpty()) return targetOutput;
        int currentOutput = 0;
        int initialInput = 0;
        Map<String, Integer> materialMap = new HashMap<>();

        while (currentOutput < targetOutput) {
            initialInput += tradePath.get(0).firstItemAmount;
            int inputAmount = initialInput;
            materialMap.clear();

            for (int i = 0; i < tradePath.size(); i++) {
                Trade currentTrade = tradePath.get(i);
                int inputNeeded = currentTrade.firstItemAmount + currentTrade.secondItemAmount;
                int outputAmount = currentTrade.resultAmount;
                int tradeMultiplier = inputAmount / inputNeeded;

                materialMap.put(currentTrade.firstItemName, inputAmount % inputNeeded);
                inputAmount = tradeMultiplier * outputAmount;
            }

            currentOutput = inputAmount;
        }

        return initialInput;
    }

    public static int calcMaxTradeInputForInventory(List<Trade> tradePath) {
        Map<String, Integer> inventory = InventorySaver.Inventory(MyInventory.NAME).itemCounts;
        int inventoryMaxSlots = 34;
        int input = tradePath.get(0).firstItemAmount;
        if (tradePath.get(0).secondItemAmount > 0 && tradePath.get(0).firstItemName.equals(tradePath.get(0).secondItemName))
            input += tradePath.get(0).secondItemAmount;
        int maxInput = 0;

        while (true) {
            Map<String, Integer> inv = new HashMap<>(inventory);
            Map<String, Integer> trade = new HashMap<>();

            String firstItem = tradePath.get(0).firstItemName;
            trade.put(firstItem, input);
            inv.put(firstItem, inv.getOrDefault(firstItem, 0) + input);

            int oldSlots = InventorySaver.calculateTotalSlots(inventory);
            int newSlots = InventorySaver.calculateTotalSlots(inv);

            if (newSlots > inventoryMaxSlots) return maxInput;

            for (Trade t : tradePath) {
                int requiredInput = t.firstItemAmount + (t.firstItemName.equals(t.secondItemName) ? t.secondItemAmount : 0);
                int availableInput = trade.getOrDefault(t.firstItemName, 0);
                int producedAmount = (availableInput / requiredInput) * t.resultAmount;
                int remainingInput = availableInput % requiredInput;


                trade.put(t.firstItemName, remainingInput);
                trade.put(t.resultName, inv.getOrDefault(t.resultName, 0) + producedAmount);

                inv.put(t.firstItemName, inv.getOrDefault(t.firstItemName, 0) - availableInput + remainingInput);
                inv.put(t.resultName, inv.getOrDefault(t.resultName, 0) + producedAmount);

                if (InventorySaver.calculateTotalSlots(inv) > inventoryMaxSlots) return maxInput;
            }

            maxInput = input;
            input += tradePath.get(0).firstItemAmount + tradePath.get(0).secondItemAmount;
        }
    }

    public static Map<String, Integer> getMaterialNeeded(TreeNode item_p) {
        Map<String, Integer> materialNeeded = new HashMap<>();
        // make queue
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(item_p);
        while (!queue.isEmpty()) {
            TreeNode item = queue.poll();
            if (!item.children.isEmpty()) {
                for (TreeNode child : item.children) queue.add(child);
            }
            if (!item.trades.isEmpty()) {
                for (Trade trade : item.trades) {
                    //DEBUG.Shop("Trade: " + trade.TradeIndex + " " + trade.firstItemName + " x " + trade.firstItemAmount + " + " + trade.secondItemName + " x " + trade.secondItemAmount + " = " + trade.resultName + " x " + trade.resultAmount);

                    materialNeeded.put(trade.firstItemName, materialNeeded.getOrDefault(trade.firstItemName, 0) + trade.firstItemAmount);

                    if(trade.secondItemAmount != 0 && NameConverter.isStackedItem(trade.secondItemName)) {
                        materialNeeded.put(trade.secondItemName, materialNeeded.getOrDefault(trade.secondItemName, 0) + trade.secondItemAmount);
                    }
                }
            }
        }

        DEBUG.Store("Material Needed: " + materialNeeded);
        return materialNeeded;
    }

    public static void makeTrade(int offerIndex, int clicks, int type) {
        for (int i = 0; !(Tutorial.client.currentScreen instanceof MerchantScreen) && i < Tutorial.MAX_SCREEN_DELAY; i += Tutorial.SCREENS_DELAY)
            Tutorial.Sleep(Tutorial.SCREENS_DELAY);
        if (!(Tutorial.client.currentScreen instanceof MerchantScreen)) return;
        TradeOffer offer = ((MerchantScreen) Tutorial.client.currentScreen).getScreenHandler().getRecipes().get(offerIndex);
        boolean isUpgrade = !NameConverter.isStackedItem(offer.getSecondBuyItem().getName().getString());
        int numberOfClicks = Math.min(clicks, calcNumberOfClicks(offer, type));
        isAutomatedTrade = true;
        for (int i = 0; i < numberOfClicks; i++) {
            Tutorial.client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));
            if (type == 1) {
                SlotClicker.slotNormalClick(Indexes.Trade.RESULT_SLOT);
                Tutorial.Sleep(Tutorial.SLOT_DELAY);
                SlotClicker.slotNormalClick(SlotOperations.getIndexFirstEmptySlot(Indexes.Trade.TOTAL_INVENTORY));
            } else {
                SlotClicker.slotShiftLeftClick(Indexes.Trade.RESULT_SLOT);
            }
        }
        isAutomatedTrade = false;
    }

    // BuyItem
    public static void executeTrade(List<Triple<Trade, Integer, Integer>> tradeDataList) {
        if (tradeDataList.isEmpty()) return;

        // Extract path from the first item
        Triple<Trade, Integer, Integer> firstEntry = tradeDataList.get(0);
        Trade firstTrade = firstEntry.getLeft();
        List<String> path = firstTrade.getPathFromRoot();

        openShop("");

        SlotClicker.slotNormalClick(SlotOperations.getSlotIndexByName(path.get(1)));
        Tutorial.waitForScreenChange();
        SlotClicker.slotNormalClick(SlotOperations.getSlotIndexByName(path.get(2)));
        Tutorial.waitForScreenChange();

        for (Triple<Trade, Integer, Integer> entry : tradeDataList) {
            //DEBUG.Shop("Making trade: " + entry.getLeft().TradeIndex + " with " + entry.getMiddle() + " clicks");
            Trade trade = entry.getLeft();
            Integer clicks = entry.getMiddle();
            Integer type = entry.getRight();
            if (trade == null) continue;
            makeTrade(trade.TradeIndex - 1, clicks, type);
        }
        InventorySaver.Inventory(MyInventory.NAME).updateFromTrade();
        Tutorial.closeScreen();
    }
}
