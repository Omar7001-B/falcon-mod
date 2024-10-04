package net.omar.tutorial.Managers;

import net.omar.tutorial.Data.Indexes;
import net.omar.tutorial.Recovery.Shulkery;
import net.omar.tutorial.Tutorial;
import net.omar.tutorial.Vaults.MyPV;
import net.omar.tutorial.classes.Trader;

import java.util.HashMap;
import java.util.Map;


public class Saving {

    public static String shulkerToSaveInventory = "Purple Shulker Box";
    public static Shulkery shulker1 = new Shulkery();
    public static Shulkery shulker2 = new Shulkery();

    public static Map<Integer, Integer> itemsToSaveInPV = new HashMap<>();

    public static void saveItemsInPv(int countOfSlots){
        if(countOfSlots == 0) return;
        int emptySlots = Slotting.countEmptySlots(Indexes.PV.PV);
        if(emptySlots == 0) return;
        int count = Math.min(countOfSlots, emptySlots);
        for(int i = 0; i < count; i++){
            int index = Slotting.getIndexFirstFilledSlot(Indexes.PV.TOTAL_INVENTORY);
            if(index == -1) return;
            int emptySlotIndex = Slotting.getIndexFirstEmptySlot(Indexes.PV.PV);
            if(emptySlotIndex == -1) return;
            //Inventorying.swapItemsInSlots(index, emptySlotIndex);
            Clicking.slotNormalClick(index);
            Tutorial.Sleep(Clicking.SLOT_CLICK_DELAY);
            Clicking.slotNormalClick(emptySlotIndex);
            itemsToSaveInPV.put(index, emptySlotIndex);
        }
    }

    public static void saveItemInPV(int fromSlot, int toSlot){
        if(Slotting.isEmptySlot(fromSlot) || !Slotting.isEmptySlot(toSlot)) return;
        Clicking.slotNormalClick(fromSlot);
        Tutorial.Sleep(Clicking.SLOT_CLICK_DELAY);
        Clicking.slotNormalClick(toSlot);
        itemsToSaveInPV.put(fromSlot, toSlot);
    }

    public static void freeUpFirst2SlotInInventoryToPV(){
        int firstInventorySlot = Indexes.PV.TOTAL_INVENTORY.get(0);
        int secondInventorySlot = Indexes.PV.TOTAL_INVENTORY.get(1);

        if(!Slotting.isEmptySlot(firstInventorySlot)){
            int emptySlotIndex = Slotting.getIndexFirstEmptySlot(Indexes.PV.PV);
            saveItemInPV(firstInventorySlot, emptySlotIndex);
        }

        if(!Slotting.isEmptySlot(secondInventorySlot)){
            int emptySlotIndex = Slotting.getIndexFirstEmptySlot(Indexes.PV.PV);
            saveItemInPV(secondInventorySlot, emptySlotIndex);
        }
    }


    public static void saveSHulkersInPv(int countOfSlots){
        int emptySlots = Slotting.countEmptySlots(Indexes.PV.PV);
        if(emptySlots == 0) return;
        int count = Math.min(countOfSlots, emptySlots);
        for(int i = 0; i < count; i++){
            int index = Slotting.getSlotIndexByItemNameIgnoreCase(Indexes.PV.TOTAL_INVENTORY, "shulker");
            if(index == -1) return;
            int emptySlotIndex = Slotting.getIndexFirstEmptySlot(Indexes.PV.PV);
            if(emptySlotIndex == -1) return;
            //Inventorying.swapItemsInSlots(index, emptySlotIndex);
            Clicking.slotNormalClick(index);
            Tutorial.Sleep(Clicking.SLOT_CLICK_DELAY);
            Clicking.slotNormalClick(emptySlotIndex);
            itemsToSaveInPV.put(index, emptySlotIndex);
        }
    }

    public static void restoreItemsFromPv(){
        for(Map.Entry<Integer, Integer> entry : itemsToSaveInPV.entrySet()){
            Inventorying.swapItemsInSlots(entry.getValue(), entry.getKey());
        }
        itemsToSaveInPV.clear();
    }


    // Function to save inventory data into purple shulkers
    public static void saveInventoryItemsIntoShulker() {
        Trader shulkerTrade = Shulkering.getTradeFoShulkerBox(shulkerToSaveInventory);
        Screening.openPV1("");
        int inventoryFilledSlots = Slotting.countFilledSlots(Indexes.PV.TOTAL_INVENTORY);
        if (inventoryFilledSlots == 0) { Screening.closeScreen(); return; }
        freeUpFirst2SlotInInventoryToPV();
        saveSHulkersInPv(27);
        Map<String, Integer> itemsLeft = Inventorying.transferItems(Map.of(shulkerToSaveInventory, 2), Indexes.PV.PV, Indexes.PV.TOTAL_INVENTORY);
        Screening.closeScreen();

        Trading.getMaterialAndBuyItem(shulkerTrade, itemsLeft.get(shulkerToSaveInventory));

        Screening.openShulkerBox(shulkerToSaveInventory);
        shulker1.saveItemsInShulker(27);
        Screening.closeScreen();
        Inventorying.sendItems(Map.of(shulkerToSaveInventory, 1), MyPV.PV1, true);
        Screening.openShulkerBox(shulkerToSaveInventory);
        shulker2.saveItemsInShulker(27);
        Screening.closeScreen();
        Inventorying.sendItems(Map.of(shulkerToSaveInventory, 1), MyPV.PV1, true);
    }

    public static void recoverInventoryItemsFromShulker() {
        Inventorying.takeItems(Map.of(shulkerToSaveInventory, 2), MyPV.PV1, true);
        Screening.openShulkerBox(shulkerToSaveInventory);
        if(shulker2.areShulkerItemsEqual(Shulkery.getCurrentShulkerItems())){
            Shulkery.swap(shulker1, shulker2);
        }
        shulker1.restoreItemsFromShulker();
        Screening.closeScreen();
        Inventorying.sendItems(Map.of(shulkerToSaveInventory, 1), MyPV.PV1, true);

        Screening.openShulkerBox(shulkerToSaveInventory);
        shulker2.restoreItemsFromShulker();
        Screening.closeScreen();
        Inventorying.sendItems(Map.of(shulkerToSaveInventory, 1), MyPV.PV1, true);


        Screening.openPV1("");
        restoreItemsFromPv();
        Screening.closeScreen();
    }

    public static void sendAllItemsToShulkers(){
        Map<String, Integer> itemsInInventory = new HashMap<>();
        Screening.openPV1("");
        saveSHulkersInPv(27);
        for(int i: Indexes.PV.TOTAL_INVENTORY){
            if(Slotting.isEmptySlot(i)) continue;
            String itemName = Slotting.getSlotNameByIndex(i);
            int count = Slotting.getElementAmountByIndex(i);
            if(itemsInInventory.containsKey(itemName)){
                itemsInInventory.put(itemName, itemsInInventory.get(itemName) + count);
            }else{
                itemsInInventory.put(itemName, count);
            }
        }
        Screening.closeScreen();
        Inventorying.forceCompleteItemsToShulkers(itemsInInventory);
    }
}
