package net.omar.tutorial.Recovery;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.Data.Indexes;
import net.omar.tutorial.Managers.Debugging;
import net.omar.tutorial.Managers.Inventorying;
import net.omar.tutorial.Managers.Slotting;

import java.util.HashMap;
import java.util.Map;

public class Shulkery {

    public Map<Integer, Integer> itemsToSaveInShulker = new HashMap<>();
    public Map<Integer, ItemStack> shulkerItems = new HashMap<>();

    public static Map<Integer, ItemStack> getCurrentShulkerItems(){
        Map<Integer, ItemStack> items = new HashMap<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (MinecraftClient.getInstance().currentScreen == null) return null;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if (slots == null) return null;
        for(int i: Indexes.Shulker.SHULKER_BOX){
            Slot slot = slots.get(i);
            if(slot.hasStack()){
                ItemStack stack = slot.getStack();
                items.put(i, stack);
            }
        }
        Debugging.Save("Current Shulker Items: " + items);
        return items;
    }

    public void saveItemsInShulker(int countOfSlots) { // This moves items from the player's inventory to the shulker box
        int filledSlots = Slotting.countFilledSlots(Indexes.Shulker.TOTAL_INVENTORY);
        int count = Math.min(countOfSlots, filledSlots);

        for (int i = 0; i < count; i++) {
            int index = Slotting.getIndexFirstFilledSlotNot(Indexes.Shulker.TOTAL_INVENTORY, "shulker");
            int emptySlotIndex = Slotting.getIndexFirstEmptySlot(Indexes.Shulker.SHULKER_BOX);
            if (emptySlotIndex == -1) { break; }
            if(index == -1) { break; }
            Inventorying.swapItemsInSlots(index, emptySlotIndex);
            itemsToSaveInShulker.put(index, emptySlotIndex);
        }

        shulkerItems = getCurrentShulkerItems();
    }

    public void restoreItemsFromShulker(){ // This moves items from the shulker box back to the player's inventory
        for(Map.Entry<Integer, Integer> entry : itemsToSaveInShulker.entrySet()){
            Inventorying.swapItemsInSlots(entry.getValue(), entry.getKey());
        }
        itemsToSaveInShulker.clear();
        shulkerItems.clear();
    }

    public boolean areShulkerItemsEqual(Map<Integer, ItemStack> otherShulkerItems) {
        return shulkerItems.size() == otherShulkerItems.size() &&
                shulkerItems.entrySet().stream().allMatch(entry ->
                        ItemStack.areEqual(entry.getValue(), otherShulkerItems.get(entry.getKey())));
    }

    public Shulkery Clone() {
        Shulkery clone = new Shulkery();
        clone.itemsToSaveInShulker = new HashMap<>(itemsToSaveInShulker);
        clone.shulkerItems = new HashMap<>(shulkerItems);
        return clone;
    }

    public static void swap(Shulkery shulker1, Shulkery shulker2) {
        Map<Integer, Integer> tempItemsToSaveInShulker = shulker1.itemsToSaveInShulker;
        Map<Integer, ItemStack> tempShulkerItems = shulker1.shulkerItems;
        shulker1.itemsToSaveInShulker = shulker2.itemsToSaveInShulker;
        shulker1.shulkerItems = shulker2.shulkerItems;
        shulker2.itemsToSaveInShulker = tempItemsToSaveInShulker;
        shulker2.shulkerItems = tempShulkerItems;
    }


    public String toString() {
        return "Shulkery{" +
                "itemsToSaveInShulker=" + itemsToSaveInShulker +
                ", shulkerItems=" + shulkerItems +
                '}';
    }
}
