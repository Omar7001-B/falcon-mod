package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.omar.tutorial.Managers.Inventorying;
import net.omar.tutorial.Managers.Naming;
import net.omar.tutorial.Managers.Trading;
import net.omar.tutorial.classes.Trader;
import net.omar.tutorial.Data.Market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class TakeItemsScreen extends Screen {
    private static final int SLIDER_WIDTH = 200;
    private static final Text TITLE = Text.literal("Take Items Screen");

    private final Screen parent;

    // Sliders for specific items
    private final SimpleOption<Integer> arrowHarmingSlider = createSliderOption("Arrow of Harming", 0, 24);
    private final SimpleOption<Integer> cobwebSlider = createSliderOption("Cobweb", 0, 24);
    private final SimpleOption<Integer> potionStrengthSlider = createSliderOption("Potion of Strength", 0, 24);
    private final SimpleOption<Integer> totemUndyingSlider = createSliderOption("Totem of Undying", 0, 24);
    private final SimpleOption<Integer> enchantedGoldenAppleSlider = createSliderOption("Enchanted Golden Apple", 0, 24);
    private final SimpleOption<Integer> fireworkSlider = createSliderOption("Firework", 0, 24);
    private final SimpleOption<Integer> obsidianSlider = createSliderOption("Obsidian", 0, 24);

    public TakeItemsScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int padding = 10; // Padding between elements
        int centerX = this.width / 2; // Center horizontally

        // Add sliders in one column
        this.addDrawableChild(this.arrowHarmingSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 1, SLIDER_WIDTH));
        this.addDrawableChild(this.cobwebSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 2, SLIDER_WIDTH));
        this.addDrawableChild(this.potionStrengthSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 3, SLIDER_WIDTH));
        this.addDrawableChild(this.totemUndyingSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 4, SLIDER_WIDTH));
        this.addDrawableChild(this.enchantedGoldenAppleSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 5, SLIDER_WIDTH));
        this.addDrawableChild(this.fireworkSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 6, SLIDER_WIDTH));
        this.addDrawableChild(this.obsidianSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 7, SLIDER_WIDTH));

        // Buttons with space above the back button
        int buttonWidth = 100;
        int buttonHeight = 20;
        int totalButtonWidth = 2 * buttonWidth + padding;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent))
                .dimensions(centerX - totalButtonWidth / 2, this.height / 6 + 24 * 8 + 10, buttonWidth, buttonHeight).build()); // Added space before the Back button

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Take Items"), button -> {
            this.client.setScreen(null);
            int[] values = {
                    this.arrowHarmingSlider.getValue(),
                    this.cobwebSlider.getValue(),
                    this.potionStrengthSlider.getValue(),
                    this.totemUndyingSlider.getValue(),
                    this.enchantedGoldenAppleSlider.getValue(),
                    this.fireworkSlider.getValue(),
                    this.obsidianSlider.getValue()
            };
            String[] items = {
                    "Arrow",
                    "Cobweb",
                    "Potion",
                    "Totem",
                    "Apple",
                    "Firework",
                    "Obsidian"
            };

            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                Map<String,Integer>itemsToTake = new HashMap<>();
                for (int i = 0; i < values.length; i++) {
                    int value = values[i];
                    String item = items[i];
                    int amount = calculateAmount(value, item);
                    itemsToTake.put(item, amount);
                }
                Inventorying.forceCompleteItemsToInventory(itemsToTake);
            });

        }).dimensions(centerX + padding / 2, this.height / 6 + 24 * 8 + 10, buttonWidth, buttonHeight).build()); // Added space before the Take Items button
    }

    // Function to create a slider with labels
    private static SimpleOption<Integer> createSliderOption(String label, int min, int max) {
        return new SimpleOption<>(
                label,
                SimpleOption.emptyTooltip(),
                (optionText, value) -> Text.literal(convertValueToDescription(label, value)),
                new SimpleOption.ValidatingIntSliderCallbacks(min, max),
                min,
                value -> MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate()
        );
    }

    // Function to convert slider value into readable format
    private static String convertValueToDescription(String itemName, int value) {
        switch (value) {
            case 0: return itemName + " Zero";
            case 1: return itemName + " x 1 Unit";
            case 2: return itemName + " x 2 Units";
            case 3: return itemName + " x 4 Units";
            case 4: return itemName + " x 8 Units";
            case 5: return itemName + " x 16 Units";
            case 6: return itemName + " x 32 Units";
            case 7: return itemName + " x 64 Units (1 Stack)";
            case 8: return itemName + " x 128 Units (2 Stacks)";
            case 9: return itemName + " x 192 Units (3 Stacks)";
            case 10: return itemName + " x 256 Units (4 Stacks)";
            case 11: return itemName + " x 320 Units (5 Stacks)";
            case 12: return itemName + " x 384 Units (6 Stacks)";
            case 13: return itemName + " x 448 Units (7 Stacks)";
            case 14: return itemName + " x 512 Units (8 Stacks)";
            case 15: return itemName + " x 576 Units (9 Stacks)";
            case 16: return itemName + " x 640 Units (10 Stacks)";
            case 17: return itemName + " x 704 Units (11 Stacks)";
            case 18: return itemName + " x 768 Units (12 Stacks)";
            case 19: return itemName + " x 832 Units (13 Stacks)";
            case 20: return itemName + " x 896 Units (14 Stacks)";
            case 21: return itemName + " x 960 Units (15 Stacks)";
            case 22: return itemName + " x 1024 Units (16 Stacks)";
            case 23: return itemName + " x 1088 Units (17 Stacks)";
            case 24: return itemName + " x 1152 Units (18 Stacks)";
            case 25: return itemName + " x 1216 Units (19 Stacks)";
            case 26: return itemName + " x 1280 Units (20 Stacks)";
            case 27: return itemName + " x 1344 Units (21 Stacks)";
            case 28: return itemName + " x 1408 Units (22 Stacks)";
            case 29: return itemName + " x 1472 Units (23 Stacks)";
            case 30: return itemName + " x 1536 Units (24 Stacks)";
            default: return itemName + " Zero"; // Default to Zero
        }
    }

    // Function to calculate the amount based on slider value
    private static int calculateAmount(int value, String itemName) {
        int shulkerCapacity = Naming.isStackedItem(itemName) ? 64 * 27 : 27;

        // Calculate the amount for stacks
        switch (value) {
            case 0: return 0; // Zero
            case 1: return 1; // 1
            case 2: return 2; // 2
            case 3: return 4; // 4
            case 4: return 8; // 8
            case 5: return 16; // 16
            case 6: return 32; // 32
            case 7: return 64; // 1 Stack
            case 8: return 128; // 2 Stacks
            case 9: return 192; // 3 Stacks
            case 10: return 256; // 4 Stacks
            case 11: return 320; // 5 Stacks
            case 12: return 384; // 6 Stacks
            case 13: return 448; // 7 Stacks
            case 14: return 512; // 8 Stacks
            case 15: return 576; // 9 Stacks
            case 16: return 640; // 10 Stacks
            case 17: return 704; // 11 Stacks
            case 18: return 768; // 12 Stacks
            case 19: return 832; // 13 Stacks
            case 20: return 896; // 14 Stacks
            case 21: return 960; // 15 Stacks
            case 22: return 1024; // 16 Stacks
            case 23: return 1088; // 17 Stacks
            case 24: return 1152; // 18 Stacks
            case 25: return 1216; // 19 Stacks
            case 26: return 1280; // 20 Stacks
            case 27: return 1344; // 21 Stacks
            case 28: return 1408; // 22 Stacks
            case 29: return 1472; // 23 Stacks
            case 30: return 1536; // 24 Stacks
            default: return 0; // Default to zero
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
