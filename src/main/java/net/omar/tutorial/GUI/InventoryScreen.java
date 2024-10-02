package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.omar.tutorial.Managers.Saving;
import net.omar.tutorial.Managers.Statting;
import net.omar.tutorial.Managers.Validating;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class InventoryScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;

    private final Screen parent;

    public InventoryScreen(Screen parent) {
        super(Text.literal("Inventory Saver"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int buttonStartY = this.height / 4;
        int buttonSpacing = 24;
        int additionalSpacing = 10; // Additional space between button groups
        int backButtonSpacing = 10; // Additional space before the "Back" button

        // Add "Statistics" button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Falcon Statistics"), button -> {
            this.client.setScreen(new FalconStatsScreen(this)); // Change to your actual FalconStatsScreen class
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Additional space after "Statistics" button
        int statisticsButtonY = buttonStartY + BUTTON_HEIGHT + additionalSpacing;

        // Add "Save Inventory" button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save Inventory"), button -> {
            this.close();
            if (Validating.enableSaveInventory)
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                    Saving.saveInventoryItemsIntoShulker();
                });
        }).dimensions(centerX - BUTTON_WIDTH / 2, statisticsButtonY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add "Recover Inventory" button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Recover Inventory"), button -> {
            this.close();
            if (Validating.enableRecoverInventory)
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                    Saving.recoverInventoryItemsFromShulker();
                });
        }).dimensions(centerX - BUTTON_WIDTH / 2, statisticsButtonY + buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add additional space before "Send Inventory" button
        int secondGroupStartY = statisticsButtonY + 2 * buttonSpacing + additionalSpacing;

        // Add "Send Inventory" button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Send Inventory"), button -> {
            // Implement functionality for sending inventory here
            this.close();
            // Example functionality
            if (Validating.enableSendInventory)
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                    Saving.sendAllItemsToShulkers();
                });
        }).dimensions(centerX - BUTTON_WIDTH / 2, secondGroupStartY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add "Complete Inventory" button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Complete Inventory"), button -> {
            if (Validating.enableCompleteInventory)
                this.client.setScreen(new TakeItemsScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, secondGroupStartY + buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add additional space before "Back" button
        int backButtonStartY = secondGroupStartY + 2 * buttonSpacing + backButtonSpacing;

        // Add "Back" button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(this.parent);
        }).dimensions(centerX - BUTTON_WIDTH / 2, backButtonStartY, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }
}
