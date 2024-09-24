package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.omar.tutorial.Managers.Naming;
import net.omar.tutorial.Managers.Trading;
import net.omar.tutorial.classes.Trader;
import net.omar.tutorial.Data.Market;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class ItemsScreen extends Screen {
    private static final int SLIDER_WIDTH = 200;
    private static final Text TITLE = Text.literal("Items Screen");

    private final Screen parent;

    // Sliders for specific items
    private final SimpleOption<Integer> arrowHarmingSlider = createSliderOption("Arrow of Harming", 0, 14);
    private final SimpleOption<Integer> cobwebSlider = createSliderOption("Cobweb", 0, 14);
    private final SimpleOption<Integer> potionStrengthSlider = createSliderOption("Potion of Strength", 0, 14);
    private final SimpleOption<Integer> totemUndyingSlider = createSliderOption("Totem of Undying", 0, 14);
    private final SimpleOption<Integer> enchantedGoldenAppleSlider = createSliderOption("Enchanted Golden Apple", 0, 14);
    private final SimpleOption<Integer> fireworkSlider = createSliderOption("Firework", 0, 14); // Added Firework slider
    private final SimpleOption<Integer> obsidianSlider = createSliderOption("Obsidian", 0, 14); // Added Obsidian slider

    public ItemsScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int padding = 10; // Padding between elements
        int centerX = this.width / 2; // Single column layout, centered horizontally

        // Sliders in one column
        this.addDrawableChild(this.arrowHarmingSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 1, SLIDER_WIDTH));
        this.addDrawableChild(this.cobwebSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 2, SLIDER_WIDTH));
        this.addDrawableChild(this.potionStrengthSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 3, SLIDER_WIDTH));
        this.addDrawableChild(this.totemUndyingSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 4, SLIDER_WIDTH));
        this.addDrawableChild(this.enchantedGoldenAppleSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 5, SLIDER_WIDTH));
        this.addDrawableChild(this.fireworkSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 6, SLIDER_WIDTH)); // Added Firework slider
        this.addDrawableChild(this.obsidianSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 7, SLIDER_WIDTH)); // Added Obsidian slider

        // Buttons next to each other
        int buttonWidth = 100;
        int buttonHeight = 20;
        int totalButtonWidth = 2 * buttonWidth + padding;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent))
                .dimensions(centerX - totalButtonWidth / 2, this.height / 6 + 24 * 8, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Submit"), button -> {
            close();

            int[] values = {
                    this.arrowHarmingSlider.getValue(),
                    this.cobwebSlider.getValue(),
                    this.potionStrengthSlider.getValue(),
                    this.totemUndyingSlider.getValue(),
                    this.enchantedGoldenAppleSlider.getValue(),
                    this.fireworkSlider.getValue(), // Get Firework slider value
                    this.obsidianSlider.getValue() // Get Obsidian slider value
            };
            String[] items = {
                    "Arrow of Harming",
                    "Cobweb",
                    "Potion of Strength",
                    "Totem of Undying",
                    "Enchanted Golden Apple",
                    "Firework", // Added Firework to items
                    "Obsidian" // Added Obsidian to items
            };
            List<Trader> trades = List.of(
                    Market.goldIngotToArrowofHarmingII_t,
                    Market.rawgoldToCobweb_t,
                    Market.rawgoldToPotionofStrength_t,
                    Market.goldBlockToTotemofUndying_t,
                    Market.goldBlockToEnchantedGoldApple_t,
                    Market.rawGoldToFireworkII_t, // Added trade for Firework
                    Market.goldIngotToObsidian_t // Added trade for Obsidian
            );

            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                for (int i = 0; i < values.length; i++) {
                    int value = values[i];
                    String item = items[i];
                    Trader trade = trades.get(i);
                    int amount = calculateAmount(value, item);
                    Trading.buyAmountOfItemIntoShulker(trade, amount); // Updated function call
                }
            });

        }).dimensions(centerX + padding / 2, this.height / 6 + 24 * 8, buttonWidth, buttonHeight).build());
    }

    // Function to create a slider with labels, includes item name
    private static SimpleOption<Integer> createSliderOption(String label, int min, int max) {
        return new SimpleOption<>(
                label,
                SimpleOption.emptyTooltip(),
                (optionText, value) -> Text.literal(convertValueToDescription(label, value)), // Passes item name to be displayed
                new SimpleOption.ValidatingIntSliderCallbacks(min, max),
                min,
                value -> MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate()
        );
    }

    // Function to convert slider value into readable format and display item name
    private static String convertValueToDescription(String itemName, int value) {
        switch (value) {
            case 0: return itemName + " Zero";
            case 1: return itemName + " x 1 Unit";
            case 2: return itemName + " x 2 Units";
            case 3: return itemName + " x 4 Units";
            case 4: return itemName + " x 8 Units";
            case 5: return itemName + " x 16 Units";
            case 6: return itemName + " x 32 Units";
            case 7: return itemName + " x 1 Stack";
            case 8: return itemName + " x 2 Stacks";
            case 9: return itemName + " x 3 Stacks";
            case 10: return itemName + " x 4 Stacks";
            case 11: return itemName + " x 1 Shulker";
            case 12: return itemName + " x 2 Shulkers";
            case 13: return itemName + " x 3 Shulkers";
            case 14: return itemName + " Infinite Shulkers";
            default: return itemName + " Zero"; // Default to Zero
        }
    }

    // Function to calculate the amount based on slider value
    private static int calculateAmount(int value, String itemName) {
        // For stackable items, a shulker box can hold 64 * 27 items, while for non-stackable items, it holds only 27
        int shulkerCapacity = Naming.isStackedItem(itemName) ? 64 * 27 : 27;

        // Calculate the amount based on slider value
        switch (value) {
            case 1: return 1; // 1 Unit
            case 2: return 2; // 2 Units
            case 3: return 4; // 4 Units
            case 4: return 8; // 8 Units
            case 5: return 16; // 16 Units
            case 6: return 32; // 32 Units
            case 7: return 64; // 1 Stack (64 units for stackable items)
            case 8: return 2 * 64; // 2 Stacks (128 units for stackable items)
            case 9: return 3 * 64; // 3 Stacks (192 units for stackable items)
            case 10: return 4 * 64; // 4 Stacks (256 units for stackable items)
            case 11: return shulkerCapacity; // 1 Shulker
            case 12: return 2 * shulkerCapacity; // 2 Shulkers
            case 13: return 3 * shulkerCapacity; // 3 Shulkers
            case 14: return 4 * shulkerCapacity; // Infinite (4 Shulkers equivalent)
            default: return 0; // Default to Zero
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
        this.client.setScreen(null);
    }
}
