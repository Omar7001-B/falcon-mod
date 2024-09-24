package net.omar.tutorial.Managers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.omar.tutorial.Tutorial;
import net.omar.tutorial.Vaults.InventorySaver;
import net.omar.tutorial.Vaults.MyInventory;

import java.util.List;

public class Screening {
    // ----------------------------- Shop Functions -----------------------------
    public static int SCREENS_DELAY = 100;
    public static int FREEZE_DELAY = 200;
    public static int MAX_SCREEN_DELAY = 6000;

    public static boolean waitForScreenChange() {
        String oldScreen = Tutorial.currentScreenString;
        boolean changed = false;
        for (int i = 0; i < MAX_SCREEN_DELAY; i += SCREENS_DELAY) {
            Tutorial.Sleep(SCREENS_DELAY);
            Tutorial.currentScreenString = Tutorial.client.currentScreen == null ? "null" : Tutorial.client.currentScreen.toString();
            if (!oldScreen.equals(Tutorial.currentScreenString)) {
                changed = true;
                break;
            }
        }
        Tutorial.Sleep(FREEZE_DELAY);
        return changed;
    }


    public static boolean openInventory(String unused) {
        if (Tutorial.client.player == null) return false;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new InventoryScreen(Tutorial.client.player));
        });
        boolean changed = waitForScreenChange();
        InventorySaver.Inventory(MyInventory.NAME).update("Open Inventory");
        return changed;
    }

    public static boolean openPV1(String unused) {
        Tutorial.sendCommand("pv 1");
        boolean changed = waitForScreenChange();
        InventorySaver.PV("PV 1").update("Open PV");
        Slotting.showAllSlots(List.of()); // Show all slots
        return changed;
    }

    public static boolean openShulkerBox(String name) {
        openInventory("");
        Tutorial.Sleep(1000);
        int slot = Slotting.getSlotIndexContainsName(name);
        //LOGGER.info("Found " + name + " at slot " + slot);
        if (slot == -1) {
            Tutorial.LOGGER.error("Shulker Box: " + name + " not found");
            closeScreen();
            return false;
        }


        Clicking.slotRightClick(slot);
        boolean changed = waitForScreenChange();
        InventorySaver.Shulker(name).update("Open Shulker Box");

        Debugging.Slots("Shulker Box: " + name + " Changed: " + changed);
        return changed;
    }

    public static boolean closeScreen() {
        //updateInevntoryFromAnyScreen();
        Tutorial.Sleep(FREEZE_DELAY);
        MinecraftClient.getInstance().execute(() -> {
            if (MinecraftClient.getInstance().currentScreen != null) {
                MinecraftClient.getInstance().currentScreen.close();
            }
        });
        return waitForScreenChange();
    }
}
