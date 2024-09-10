package net.omar.tutorial.Managers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SleepingChatScreen;
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

import static net.omar.tutorial.Inventory.SlotOperations.isEmptySlot;
import static net.omar.tutorial.Inventory.SlotOperations.moveCompleteItem;


public class TradeManager {

    public static boolean isAutomatedTrade;

    public static boolean openShop(String unused) {
        Tutorial.sendCommand("shop");
        return Tutorial.waitForScreenChange();
    }

    // ----------------------------- Trades -----------------------------
    public static int calcNumberOfClicks(TradeOffer offer) {
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
                int inputWeHave = isUpgrade ? firstItemAmount : Math.min(totalFirstItemAmount - totalFirstItemAmount % firstItemAmount, stack - stack % firstItemAmount);

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

        while (currentOutput < targetOutput) {
            initialInput += tradePath.get(0).firstItemAmount;
            int inputAmount = initialInput;
            for (Trade currentTrade : tradePath) {
                int inputNeeded = currentTrade.firstItemAmount + currentTrade.secondItemAmount;
                int outputAmount = currentTrade.resultAmount;
                int tradeMultiplier = inputAmount / inputNeeded;
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

    public static Map<String, Integer> getMaterialNeeded(Trade trade) {
        Map<String, Integer> materialNeeded = new HashMap<>();
        materialNeeded.put(trade.firstItemName, trade.firstItemAmount);
        if (trade.secondItemAmount != 0 && NameConverter.isStackedItem(trade.secondItemName)) {
            materialNeeded.put(trade.secondItemName, materialNeeded.getOrDefault(trade.secondItemName, 0) + trade.secondItemAmount);
        }
        return materialNeeded;
    }

    public static Map<String, Integer> getMaterialNeeded(TreeNode item_p) {
        Map<String, Integer> materialNeeded = new HashMap<>();
        // make queue
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(item_p);
        while (!queue.isEmpty()) {
            TreeNode item = queue.poll();
            if (!item.children.isEmpty()) {
                queue.addAll(item.children);
            }
            if (!item.trades.isEmpty()) {
                for (Trade trade : item.trades) {
                    //DEBUG.Shop("Trade: " + trade.TradeIndex + " " + trade.firstItemName + " x " + trade.firstItemAmount + " + " + trade.secondItemName + " x " + trade.secondItemAmount + " = " + trade.resultName + " x " + trade.resultAmount);

                    materialNeeded.put(trade.firstItemName, materialNeeded.getOrDefault(trade.firstItemName, 0) + trade.firstItemAmount);

                    if (trade.secondItemAmount != 0 && NameConverter.isStackedItem(trade.secondItemName)) {
                        materialNeeded.put(trade.secondItemName, materialNeeded.getOrDefault(trade.secondItemName, 0) + trade.secondItemAmount);
                    }
                }
            }
        }

        DEBUG.Store("Material Needed: " + materialNeeded);
        return materialNeeded;
    }

    public static void makeTrade(int offerIndex, int shiftClicks, int normalClicks) {
        for (int i = 0; !(Tutorial.client.currentScreen instanceof MerchantScreen) && i < Tutorial.MAX_SCREEN_DELAY; i += Tutorial.SCREENS_DELAY)
            Tutorial.Sleep(Tutorial.SCREENS_DELAY);
        if (!(Tutorial.client.currentScreen instanceof MerchantScreen)) return;

        // make while receipes size is less than offer Index, wait for the screen to update
        for (int i = 0; ((MerchantScreen) Tutorial.client.currentScreen).getScreenHandler().getRecipes().size() <= offerIndex && i < Tutorial.MAX_SCREEN_DELAY; i += Tutorial.SCREENS_DELAY)
            Tutorial.Sleep(Tutorial.SCREENS_DELAY);
        TradeOffer offer = ((MerchantScreen) Tutorial.client.currentScreen).getScreenHandler().getRecipes().get(offerIndex);
        boolean isUpgrade = !NameConverter.isStackedItem(offer.getSecondBuyItem().getName().getString());
        int numberOfClicks = Math.min(shiftClicks, calcNumberOfClicks(offer)) + normalClicks;

        isAutomatedTrade = true;
        for (int i = 0; i < numberOfClicks; i++) {
            Objects.requireNonNull(Tutorial.client.getNetworkHandler()).sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));

            if (i < shiftClicks) {
                SlotClicker.slotShiftLeftClick(Indexes.Trade.RESULT_SLOT);
            } else {
                String resultName = NameConverter.offerNamesToInventoryNames(offer.getSellItem().getName().getString());
                int resultAmount = offer.getSellItem().getCount();
                moveCompleteItemTrade(resultName , resultAmount);
            }
        }
        isAutomatedTrade = false;
    }

    public static void moveCompleteItemTrade(String resultName, int resultAmount) {
        //DEBUG.Shulker("Moving " + resultAmount + " of " + resultName + " to inventory");
        //DEBUG.Shulker("Current: " + SlotOperations.getElementAmountByIndex(Indexes.Trade.RESULT_SLOT) + " of " + SlotOperations.getSlotNameByIndex(Indexes.Trade.RESULT_SLOT));

        SlotClicker.slotNormalClick(Indexes.Trade.RESULT_SLOT);
        Tutorial.Sleep(100);
        int pickedAmount = resultAmount;
        while (pickedAmount > 0) {
            int targetIndex = SlotOperations.getSlotToComplete(resultName, Indexes.Trade.TOTAL_INVENTORY);
            if (targetIndex == -1) break;
            int availableSpace = 64 - SlotOperations.getElementAmountByIndex(targetIndex);
            int transferedAmount = Math.min(pickedAmount, availableSpace);
            pickedAmount -= transferedAmount;
            SlotClicker.slotNormalClick(targetIndex);
        }
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

       // showAllTrades();

        for (Triple<Trade, Integer, Integer> entry : tradeDataList) {
            //DEBUG.Shop("Making trade: " + entry.getLeft().TradeIndex + " with " + entry.getMiddle() + " clicks");
            Trade trade = entry.getLeft();
            Integer shiftClicks = entry.getMiddle();
            Integer normalClicks = entry.getRight();
            if (trade == null) continue;
            makeTrade(trade.TradeIndex - 1, shiftClicks, normalClicks);
        }
        InventorySaver.Inventory(MyInventory.NAME).updateFromTrade();
        Tutorial.closeScreen();
    }


    public static void showAllTrades() {
        DEBUG.Shulker("Showing all trades:");
        List<TradeOffer> offers = ((MerchantScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().getRecipes();
        for (int i = 0; i < offers.size(); i++) {
            TradeOffer offer = offers.get(i);

            // Get the first buy item details
            int firstPriceCount = offer.getOriginalFirstBuyItem().getCount();
            String firstPriceName = offer.getOriginalFirstBuyItem().getName().getString();

            // Get the second buy item details (if present)
            int secondPriceCount = offer.getSecondBuyItem().isEmpty() ? 0 : offer.getSecondBuyItem().getCount();
            String secondPriceName = offer.getSecondBuyItem().isEmpty() ? "None" : offer.getSecondBuyItem().getName().getString();

            // Get the sell (output) item details
            int sellCount = offer.getSellItem().getCount();
            String sellName = offer.getSellItem().getName().getString();

            DEBUG.Shulker("Trade " + (i + 1) + ":");
            DEBUG.Shulker("    First Item: " + firstPriceCount + " x " + firstPriceName);
            DEBUG.Shulker("    Second Item: " + (secondPriceCount > 0 ? secondPriceCount + " x " + secondPriceName : "None"));
            DEBUG.Shulker("    Result: " + sellCount + " x " + sellName);
            DEBUG.Shulker("------------------------------------");
        }

    }
}
