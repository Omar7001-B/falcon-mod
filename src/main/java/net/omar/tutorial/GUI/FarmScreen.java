package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static net.omar.tutorial.Tutorial.*;

@Environment(EnvType.CLIENT)
public class FarmScreen extends Screen {
    private static final int SLIDER_WIDTH = 200;
    private static final Text TITLE = Text.literal("Farm Screen");

    private final Screen parent;

    // Sliders with updated ranges
    private final SimpleOption<Integer> goldNuggetSlider = createSliderOption("Gold Nugget", 0, 17);
    private final SimpleOption<Integer> rawGoldSlider = createSliderOption("Raw Gold", 0, 17);
    private final SimpleOption<Integer> goldIngotSlider = createSliderOption("Gold Ingot", 0, 17);
    private final SimpleOption<Integer> goldBlockSlider = createSliderOption("Gold Block", 0, 17);

    public FarmScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int padding = 10; // Padding between elements

        // Single column layout, centered horizontally
        int centerX = this.width / 2;

        // Sliders in one column
        this.addDrawableChild(this.goldNuggetSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24, SLIDER_WIDTH));
        this.addDrawableChild(this.rawGoldSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 2, SLIDER_WIDTH));
        this.addDrawableChild(this.goldIngotSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 3, SLIDER_WIDTH));
        this.addDrawableChild(this.goldBlockSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 4, SLIDER_WIDTH));

        // Buttons next to each other
        int buttonWidth = 100; // Adjust width of buttons if needed
        int buttonHeight = 20;
        int totalButtonWidth = 2 * buttonWidth + padding; // Total width of both buttons + padding between them


        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent))
                .dimensions(centerX - totalButtonWidth / 2, this.height / 6 + 24 * 6, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Submit"), button -> {
            // Close the current screen first
            close();

            int[] values = {this.goldNuggetSlider.getValue(), this.rawGoldSlider.getValue(), this.goldIngotSlider.getValue(), this.goldBlockSlider.getValue()};
            String[] items = {"Gold Nugget", "Raw Gold", "Gold Ingot", "Gold Block"};

            // Asynchronously delay for 1 second (1000 ms) using CompletableFuture
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {  // I think same thing could be done with a Thread, and put the sleep in the thread
                for (int i = 0; i < values.length; i++) {
                    int value = values[i];
                    System.out.println(items[i] + ": " + convertValueToDescription(items[i], value));
                    System.out.println("Value: " + value);
                    if (value <= 6) {
                        farmMaterialIntoPV(items[i], value * 64);
                    } else if (value == 17) {
                        farmMaterialIntoShulker(items[i], 9999999);
                    } else {
                        farmMaterialIntoShulker(items[i], value - 6);
                    }

                    // Print for debugging
                }
            });

        }).dimensions(centerX + padding / 2, this.height / 6 + 24 * 6, buttonWidth, buttonHeight).build());
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
        if (value == 0) {
            return itemName + ": Zero";
        } else if (value <= 6) {
            return itemName + ": " + value + " x Stack";
        } else if (value == 17) {
            return itemName + ": Infinite Shulkers";
        } else {
            return itemName + ": " + (value - 6) + " x Shulker";
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