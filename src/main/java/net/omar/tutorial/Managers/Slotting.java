package net.omar.tutorial.Managers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.omar.tutorial.Tutorial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Slotting {
    public static final Map<Integer, String> slotStates = new HashMap<>();
    public static int SLOT_DELAY = 50;
    public static int MAX_SLOT_DELAY = 2000;

    public static DefaultedList<Slot> getSlots() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) {
            Inventorying.LOGGER.error("No screen found");
            return null;
        }
        return ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
    }

    public static void showAllSlots(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) {
            Debugging.Slots("No slots found");
            return;
        }

        if (indexes == null || indexes.isEmpty()) {
            indexes = IntStream.range(0, slots.size()).boxed().collect(Collectors.toList());
        }

        Inventorying.LOGGER.info("Showing slots:");
        indexes.forEach(index -> {
            Slot slot = slots.get(index);
            Debugging.Slots("Slot Index: " + index);
            Debugging.Slots("Item: " + (slot.hasStack() ? slot.getStack().getItem().getName().getString() : "Empty"));
            Debugging.Slots("Amount: " + (slot.hasStack() ? slot.getStack().getCount() : 0));
            Debugging.Slots("-------------------");
        });

    }

    public static int getSlotIndexContainsName(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if (slots == null) return -1;
        //DEBUG.Store("Item We Search: " + itemName);
        for (int i = 0; i < slots.size(); i++){
            //DEBUG.Store("Item In Slot: " + slots.get(i).getStack().getItem().getName().getString());
            if (slots.get(i).getStack().getItem().getName().getString().contains(itemName)) return i;
        }
        return -1;
    }

    public static int getSlotIndexByName(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if (slots == null) return -1;
        for (int i = 0; i < slots.size(); i++)
            if (slots.get(i).getStack().getName().getString().equals(itemName)) return i;
        return -1;
    }

    public static int getSlotIndexByItemNameIgnoreCase(String itemName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if (slots == null) return -1;
        for (int i = 0; i < slots.size(); i++)
            if (Naming.containsIgnoreCase(slots.get(i).getStack().getItem().getName().getString(), itemName)) return i;
        return -1;
    }

    public static String getSlotNameByIndex(int index) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return "null";
        return slots.get(index).hasStack() ? slots.get(index).getStack().getItem().getName().getString() : "empty";
    }

    public static int getElementAmountByIndex(int index) {
        DefaultedList<Slot> slots = getSlots();
        return slots != null && slots.get(index).hasStack() ? slots.get(index).getStack().getCount() : 0;
    }

    public static int countTotalElementAmount(List<Integer> indexes, String itemName) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return indexes.stream()
                .filter(index -> slots.get(index).hasStack() && Naming.containsIgnoreCase(slots.get(index).getStack().getItem().getName().getString(), itemName))
                .mapToInt(index -> slots.get(index).getStack().getCount())
                .sum();
    }

    public static boolean isEmptySlot(int index) {
        DefaultedList<Slot> slots = getSlots();
        return slots != null && !slots.get(index).hasStack();
    }

    public static int getIndexFirstEmptySlot(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return -1;

        return indexes.stream()
                .filter(Slotting::isEmptySlot)
                .findFirst()
                .orElse(-1);
    }

    public static int countEmptySlots(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return (int) indexes.stream()
                .filter(Slotting::isEmptySlot)
                .count();
    }

    public static int countFilledSlots(List<Integer> indexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return 0;

        return (int) indexes.stream()
                .filter(index -> slots.get(index).hasStack())
                .count();
    }

    public static int getSlotToComplete(String itemName, List<Integer> targetIndexes) {
        DefaultedList<Slot> slots = getSlots();
        if (slots == null) return -1;

        if(Naming.isStackedItem(itemName)) {
            for(int i: targetIndexes) {
                String name = slots.get(i).getStack().getItem().getName().getString();
                int amount = slots.get(i).getStack().getCount();
                if(Naming.containsIgnoreCase(name, itemName) && amount < 64) return i;
            }
        }

        for(int i: targetIndexes)
            if(!slots.get(i).hasStack()) return i;

        return -1;
    }

    public static void waitForSlotChange(int index) {
        String oldState = slotStates.getOrDefault(index, "");
        for (int i = 0; i < MAX_SLOT_DELAY; i += SLOT_DELAY) {
            Tutorial.Sleep(SLOT_DELAY);
            String currentState = isEmptySlot(index) ? "empty" : getElementAmountByIndex(index) + "x" + getSlotNameByIndex(index);
            if (!currentState.equals(oldState)) {
                Debugging.Shop("Old State: " + oldState + " Current State: " + currentState + " Changed  ✅" + " at index: " + index + " in " + i + "ms");
                slotStates.put(index, currentState);
                return;
            }
        }

        Debugging.Shop("Old State: " + oldState + " Current State: " + slotStates.getOrDefault(index, "") + " Not Changed ❌" + " at index: " + index);
    }
}
