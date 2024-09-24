package net.omar.tutorial.Managers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.Tutorial;
import net.omar.tutorial.classes.Shopper;
import net.omar.tutorial.classes.Trader;
import net.omar.tutorial.Data.Indexes;
import net.omar.tutorial.Vaults.InventorySaver;
import net.omar.tutorial.Vaults.MyInventory;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static net.omar.tutorial.Managers.Inventorying.*;


public class Trading {

    public static boolean isAutomatedTrade;

    public static boolean openShop(String unused) {
        Tutorial.sendCommand("shop");
        return Screening.waitForScreenChange();
    }

    // ----------------------------- Trades -----------------------------
    public static int calcNumberOfClicks(TradeOffer offer) {
        //SlotOperations.showAllSlots(null);

        String firstItemName = Naming.offerNamesToInventoryNames(offer.getAdjustedFirstBuyItem().getName().getString());
        String secondItemName = Naming.offerNamesToInventoryNames(offer.getSecondBuyItem().getName().getString());
        String resultName = Naming.offerNamesToInventoryNames(offer.getSellItem().getName().getString());

        int firstItemAmount = offer.getAdjustedFirstBuyItem().getCount();
        int secondItemAmount = offer.getSecondBuyItem().getCount();
        int resultAmount = offer.getSellItem().getCount();

        int totalFirstItemAmount = Slotting.countTotalElementAmount(Indexes.Trade.TOTAL_INVENTORY_WITHOUT_RESULT_SLOT, firstItemName);
        int totalSecondItemAmount = firstItemName.equals(secondItemName) ? 0 :
                Slotting.countTotalElementAmount(Indexes.Trade.TOTAL_INVENTORY_WITHOUT_RESULT_SLOT, secondItemName);

        //DEBUG.Shop("Total amount of " + firstItemName + ": " + totalFirstItemAmount);
        //DEBUG.Shop("Total amount of " + secondItemName + ": " + totalSecondItemAmount);

        int stack = 64;
        if (firstItemName.equals(secondItemName)) {
            firstItemAmount += secondItemAmount;
            stack += 64;
        }

        int numberOfClicks = 0;
        int numberOfEmptySlots = Slotting.countEmptySlots(Indexes.Trade.TOTAL_INVENTORY);
        int totalInputWaste = 0;
        int totalOutputAmount = 0;
        boolean isStacked = Naming.isStackedItem(resultName);
        boolean isUpgrade = !Naming.isStackedItem(secondItemName);

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
            Debugging.Error("Error in calcNumberOfClicks: " + e.getMessage());
        }

//        DEBUG.Shop("Total First Item: " + totalFirstItemAmount);
//        DEBUG.Shop("Total Input Waste: " + totalInputWaste + " of " + firstItemName);
//        DEBUG.Shop("Total Output Amount: " + totalOutputAmount + " of " + resultName);
//        DEBUG.Shop("Number of Clicks: " + numberOfClicks);
//        DEBUG.Shop("Number of Empty Slots: " + numberOfEmptySlots);
//        DEBUG.Shop("--------END---------");

        return numberOfClicks;
    }

    public static int calcInputforTradeOutput(int targetOutput, List<Trader> tradePath) {
        if (tradePath.isEmpty()) return targetOutput;
        int currentOutput = 0;
        int initialInput = 0;

        while (currentOutput < targetOutput) {
            initialInput += tradePath.get(0).firstItemAmount;
            int inputAmount = initialInput;
            for (Trader currentTrade : tradePath) {
                int inputNeeded = currentTrade.firstItemAmount + currentTrade.secondItemAmount;
                int outputAmount = currentTrade.resultAmount;
                int tradeMultiplier = inputAmount / inputNeeded;
                inputAmount = tradeMultiplier * outputAmount;
            }

            currentOutput = inputAmount;
        }

        return initialInput;
    }

    public static int calcMaxTradeInputForInventory(List<Trader> tradePath) {
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

            for (Trader t : tradePath) {
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

    public static Map<String, Integer> getMaterialNeeded(Trader trade) {
        Map<String, Integer> materialNeeded = new HashMap<>();
        materialNeeded.put(trade.firstItemName, trade.firstItemAmount);
        if (trade.secondItemAmount != 0 && Naming.isStackedItem(trade.secondItemName)) {
            materialNeeded.put(trade.secondItemName, materialNeeded.getOrDefault(trade.secondItemName, 0) + trade.secondItemAmount);
        }
        return materialNeeded;
    }

    public static Map<String, Integer> getMaterialNeeded(Shopper item_p) {
        Map<String, Integer> materialNeeded = new HashMap<>();
        // make queue
        Queue<Shopper> queue = new LinkedList<>();
        queue.add(item_p);
        while (!queue.isEmpty()) {
            Shopper item = queue.poll();
            if (!item.children.isEmpty()) {
                queue.addAll(item.children);
            }
            if (!item.trades.isEmpty()) {
                for (Trader trade : item.trades) {
                    //DEBUG.Shop("Trade: " + trade.TradeIndex + " " + trade.firstItemName + " x " + trade.firstItemAmount + " + " + trade.secondItemName + " x " + trade.secondItemAmount + " = " + trade.resultName + " x " + trade.resultAmount);

                    materialNeeded.put(trade.firstItemName, materialNeeded.getOrDefault(trade.firstItemName, 0) + trade.firstItemAmount);

                    if (trade.secondItemAmount != 0 && Naming.isStackedItem(trade.secondItemName)) {
                        materialNeeded.put(trade.secondItemName, materialNeeded.getOrDefault(trade.secondItemName, 0) + trade.secondItemAmount);
                    }
                }
            }
        }

        Debugging.Store("Material Needed: " + materialNeeded);
        return materialNeeded;
    }

    public static void makeTrade(int offerIndex, int shiftClicks, int normalClicks) {
        for (int i = 0; !(Tutorial.client.currentScreen instanceof MerchantScreen) && i < Screening.MAX_SCREEN_DELAY; i += Screening.SCREENS_DELAY)
            Tutorial.Sleep(Screening.SCREENS_DELAY);
        if (!(Tutorial.client.currentScreen instanceof MerchantScreen)) return;

        // make while receipes size is less than offer Index, wait for the screen to update
        for (int i = 0; ((MerchantScreen) Tutorial.client.currentScreen).getScreenHandler().getRecipes().size() <= offerIndex && i < Screening.MAX_SCREEN_DELAY; i += Screening.SCREENS_DELAY)
            Tutorial.Sleep(Screening.SCREENS_DELAY);
        TradeOffer offer = ((MerchantScreen) Tutorial.client.currentScreen).getScreenHandler().getRecipes().get(offerIndex);
        boolean isUpgrade = !Naming.isStackedItem(offer.getSecondBuyItem().getName().getString());
        int numberOfClicks = Math.min(shiftClicks, calcNumberOfClicks(offer)) + normalClicks;

        isAutomatedTrade = true;
        for (int i = 0; i < numberOfClicks; i++) {
            Objects.requireNonNull(Tutorial.client.getNetworkHandler()).sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));

            if (i < shiftClicks) {
                Clicking.slotShiftLeftClick(Indexes.Trade.RESULT_SLOT);
            } else {
                String resultName = Naming.offerNamesToInventoryNames(offer.getSellItem().getName().getString());
                int resultAmount = offer.getSellItem().getCount();
                moveCompleteItemTrade(resultName , resultAmount);
            }
        }
        isAutomatedTrade = false;
    }

    public static void moveCompleteItemTrade(String resultName, int resultAmount) {
        //DEBUG.Shulker("Moving " + resultAmount + " of " + resultName + " to inventory");
        //DEBUG.Shulker("Current: " + SlotOperations.getElementAmountByIndex(Indexes.Trade.RESULT_SLOT) + " of " + SlotOperations.getSlotNameByIndex(Indexes.Trade.RESULT_SLOT));

        Clicking.slotNormalClick(Indexes.Trade.RESULT_SLOT);
        Tutorial.Sleep(100);
        int pickedAmount = resultAmount;
        while (pickedAmount > 0) {
            int targetIndex = Slotting.getSlotToComplete(resultName, Indexes.Trade.TOTAL_INVENTORY);
            if (targetIndex == -1) break;
            int availableSpace = 64 - Slotting.getElementAmountByIndex(targetIndex);
            int transferedAmount = Math.min(pickedAmount, availableSpace);
            pickedAmount -= transferedAmount;
            Clicking.slotNormalClick(targetIndex);
        }
    }


    // BuyItem
    public static void executeTrade(List<Triple<Trader, Integer, Integer>> tradeDataList) {
        if (tradeDataList.isEmpty()) return;

        // Extract path from the first item
        Triple<Trader, Integer, Integer> firstEntry = tradeDataList.get(0);
        Trader firstTrade = firstEntry.getLeft();
        List<String> path = firstTrade.getPathFromRoot();

        openShop("");

        Clicking.slotNormalClick(Slotting.getSlotIndexByName(path.get(1)));
        Screening.waitForScreenChange();
        Clicking.slotNormalClick(Slotting.getSlotIndexByName(path.get(2)));
        Screening.waitForScreenChange();

       // showAllTrades();

        for (Triple<Trader, Integer, Integer> entry : tradeDataList) {
            //DEBUG.Shop("Making trade: " + entry.getLeft().TradeIndex + " with " + entry.getMiddle() + " clicks");
            Trader trade = entry.getLeft();
            Integer shiftClicks = entry.getMiddle();
            Integer normalClicks = entry.getRight();
            if (trade == null) continue;
            makeTrade(trade.TradeIndex - 1, shiftClicks, normalClicks);
        }
        InventorySaver.Inventory(MyInventory.NAME).updateFromTrade();
        Screening.closeScreen();
    }


    public static void showAllTrades() {
        Debugging.Shulker("Showing all trades:");
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

            Debugging.Shulker("Trade " + (i + 1) + ":");
            Debugging.Shulker("    First Item: " + firstPriceCount + " x " + firstPriceName);
            Debugging.Shulker("    Second Item: " + (secondPriceCount > 0 ? secondPriceCount + " x " + secondPriceName : "None"));
            Debugging.Shulker("    Result: " + sellCount + " x " + sellName);
            Debugging.Shulker("------------------------------------");
        }

    }

    public static void buyItem(Trader trade, int shiftClicks, int normalClicks) {
        List<Triple<Trader, Integer, Integer>> trades = new ArrayList<>();
        trades = List.of(Triple.of(trade, shiftClicks, normalClicks));
        executeTrade(trades);
    }

    public static void buyItem(Shopper node, int shiftClicks, int normalClicks) {
        if (!node.trades.isEmpty()) {
            List<Triple<Trader, Integer, Integer>> trades = new ArrayList<>();
            for (Trader trade : node.trades)
                trades.add(Triple.of(trade, 0, normalClicks));
            executeTrade(trades);
        } else {
            for (Shopper child : node.children)
                buyItem(child, 0, normalClicks);
        }
    }

    public static boolean getMaterialAndBuyItem(Trader trade, int count) {
        if (count < 1) return false;
        if (count > 10) {
            for (int i = 0; getMaterialAndBuyItem(trade, 1) && i < count; i++) ;
            return true;
        }

        Debugging.Shulker("Trade: " + trade.toString() + " Count: " + count);
        Map<String, Integer> materialNeeded = getMaterialNeeded(trade);
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet())
            materialNeeded.put(entry.getKey(), entry.getValue() * count);

        if (!forceCompleteItemsToInventory(materialNeeded)) {
            forceCompleteItemsToShulkers(materialNeeded);
            return false;
        }

        buyItem(trade, 0, count);

        Map<String, Integer> outputMaterial = new HashMap<>();
        outputMaterial.put(trade.resultName, trade.resultAmount * count);
        forceCompleteItemsToShulkers(outputMaterial);
        return true;
    }

    public static boolean getMaterialAndBuyItem(Shopper node, int count) {
        if (count < 1) return false;
        if (count > 10) {
            for (int i = 0; getMaterialAndBuyItem(node, 1) && i < count; i++) ;
            return true;
        }
        Map<String, Integer> materialNeeded = getMaterialNeeded(node);
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet())
            materialNeeded.put(entry.getKey(), entry.getValue() * count);

        Debugging.Shulker("Material Needed: " + materialNeeded.toString());

        if (!forceCompleteItemsToInventory(materialNeeded)) {
            forceCompleteItemsToShulkers(materialNeeded);
            return false;
        }

        buyItem(node, 0, count);

        Map<String, Integer> outputMaterial = new HashMap<>();

        if (!node.children.isEmpty())
            for (Shopper child : node.children)
                for (Trader trade : child.trades)
                    outputMaterial.put(trade.resultName, trade.resultAmount * count);
        else if (!node.trades.isEmpty())
            for (Trader trade : node.trades)
                outputMaterial.put(trade.resultName, trade.resultAmount * count);

        Debugging.Shulker("Output Material: " + outputMaterial.toString());
        forceCompleteItemsToShulkers(outputMaterial);
        return true;
    }

    public static int calcInputNeed(Trader trade, int targetAmount) {
        int requiredInput = (int) Math.ceil((double) targetAmount / trade.resultAmount) * (trade.firstItemAmount + trade.secondItemAmount);
        return requiredInput;
    }

    public static int countItemByNameInInventory(String targetItemName) {
        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            String itemName = stack.getItem().getName().getString();
            itemName = itemName.toLowerCase().replace("_", " ");
            //DEBUG.Shulker("Item Name: " + itemName);
            if (!stack.isEmpty())
                if (targetItemName.contains(itemName.toLowerCase()) || itemName.toLowerCase().contains(targetItemName))
                    count += stack.getCount();
        }
        return count;
    }

    public static void buyAmountOfItemIntoShulker(Trader trade, int amount) {
        Debugging.Shulker("(BuyAmountOfItemIntoShulker) Trade: " + trade.toString() + " Amount: " + amount);
        if(amount < 1) return;
        String outputName = Naming.offerNamesToInventoryNames(trade.resultName);
        if(countItemByNameInInventory(outputName) > 0)
            forceCompleteItemsToShulkers(Map.of(outputName, 9999));
        Debugging.Shulker("Trade: " + trade.toString() + " Amount: " + amount);
        while (amount > 0) {
            int input = Math.min(calcMaxTradeInputForInventory(List.of(trade)), calcInputNeed(trade, amount));
            forceCompleteItemsToInventory(trade.firstItemName, input);
            executeTrade(List.of(Triple.of(trade, 9999, 0)));
            int output = countItemByNameInInventory(outputName);
            //DEBUG.Shulker("Name : " + trade.resultName + " > " + NameConverter.offerNamesToInventoryNames(trade.resultName) + " Output: " + output);
            //DEBUG.Shulker("Before Sub: Amount: " + amount + " Input: " + input + " Output: " + output + " Inventory: " + InventorySaver.Inventory(MyInventory.NAME).itemCounts);
            amount -= output;
            //DEBUG.Shulker("After Sub: Amount: " + amount + " Input: " + input + " Output: " + output + " Inventory: " + InventorySaver.Inventory(MyInventory.NAME).itemCounts);
            forceCompleteItemsToShulkers(Map.of(outputName, 9999));
        }
    }
}
