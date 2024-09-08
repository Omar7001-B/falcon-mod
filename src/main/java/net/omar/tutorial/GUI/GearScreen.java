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
public class GearScreen extends Screen {
    private static final int SLIDER_WIDTH = 200;
    private static final Text TITLE = Text.literal("Gear Screen");

    private final Screen parent;

    // Sliders for different equipment
    private final SimpleOption<Integer> ArmorSlider = createSliderOption("Armor", 0, 7);
    private final SimpleOption<Integer> ElytraSlider = createSliderOption("Elytra", 0, 7);
    private final SimpleOption<Integer> SwordSlider = createSliderOption("Sword", 0, 7);
    private final SimpleOption<Integer> PickAxeSlider = createSliderOption("PickAxe", 0, 7);
    private final SimpleOption<Integer> AxeSlider = createSliderOption("Axe", 0, 7);

    public GearScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int padding = 10;
        int centerX = this.width / 2;

        // Adding sliders for the gear
        this.addDrawableChild(this.ArmorSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24, SLIDER_WIDTH));
        this.addDrawableChild(this.ElytraSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 2, SLIDER_WIDTH));
        this.addDrawableChild(this.SwordSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 3, SLIDER_WIDTH));
        this.addDrawableChild(this.PickAxeSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 4, SLIDER_WIDTH));
        this.addDrawableChild(this.AxeSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, this.height / 6 + 24 * 5, SLIDER_WIDTH));

        // Back and Submit buttons
        int buttonWidth = 100;
        int buttonHeight = 20;
        int totalButtonWidth = 2 * buttonWidth + padding;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent))
                .dimensions(centerX - totalButtonWidth / 2, this.height / 6 + 24 * 7, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Submit"), button -> {
            close();
            int[] values = {this.ArmorSlider.getValue(), this.ElytraSlider.getValue(),
                    this.SwordSlider.getValue(), this.AxeSlider.getValue(),
                    this.PickAxeSlider.getValue()};
            String[] items = {"Armor", "Elytra", "Sword", "PickAxe", "Axe"};

            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                for (int i = 0; i < values.length; i++) {
                    int value = values[i];
                    System.out.println(items[i] + ": " + convertValueToDescription(items[i], value));
                    System.out.println("Value: " + value);
                    if (value <= 6) {
                    } else {
                    }
                }
            });
        }).dimensions(centerX + padding / 2, this.height / 6 + 24 * 7, buttonWidth, buttonHeight).build());
    }

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

    private static String convertValueToDescription(String itemName, int value) {
        if (value == 0) {
            return itemName + ": Zero";
        } else if (value <= 6) {
            return itemName + ": " + value + " x Units";
        } else {
            return itemName + ": Infinite";
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
