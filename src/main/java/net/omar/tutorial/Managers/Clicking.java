package net.omar.tutorial.Managers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.SlotActionType;

public class Clicking {

    public static int SLOT_CLICK_DELAY = 50;

    private static void Sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void slotNormalClick(int slotIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                0, // left click
                SlotActionType.PICKUP,
                client.player
        );
        Sleep(SLOT_CLICK_DELAY);
    }

    public static void slotRightClick(int slotIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                1, // right click
                SlotActionType.PICKUP,
                client.player
        );
        Sleep(SLOT_CLICK_DELAY);
    }

    public static void slotShiftLeftClick(int slotIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                0, // left click
                SlotActionType.QUICK_MOVE, // shift
                client.player
        );
        Sleep(SLOT_CLICK_DELAY);
    }

    public static void slotPickAll(int slotIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                0, // left click
                SlotActionType.PICKUP,
                client.player
        );
        Sleep(100);
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                0, // left click
                SlotActionType.PICKUP_ALL,
                client.player
        );
        Sleep(SLOT_CLICK_DELAY);
    }

    public static void slotDropOne(int slotIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                0, // left click
                SlotActionType.THROW,
                client.player
        );
        Sleep(SLOT_CLICK_DELAY);
    }

    public static void slotDropAll(int slotIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen == null) return;
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                1, // drop all
                SlotActionType.THROW,
                client.player
        );
        Sleep(SLOT_CLICK_DELAY);
    }
}
