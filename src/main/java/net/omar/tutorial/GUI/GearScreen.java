package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.omar.tutorial.Managers.Trading;
import net.omar.tutorial.Managers.Debugging;
import net.omar.tutorial.classes.Shopper;
import net.omar.tutorial.Data.Market;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    private final SimpleOption<Integer> BowSlider = createSliderOption("Bow", 0, 7);          // Added Bow Slider
    private final SimpleOption<Integer> ShearsSlider = createSliderOption("Shears", 0, 7);    // Added Shears Slider

    public GearScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int padding = 10;
        int centerX = this.width / 2;
        int startY = this.height / 6 + 24;

        // Adding sliders for the gear
        this.addDrawableChild(this.ArmorSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, startY, SLIDER_WIDTH));
        this.addDrawableChild(this.ElytraSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, startY + 24, SLIDER_WIDTH));
        this.addDrawableChild(this.SwordSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, startY + 48, SLIDER_WIDTH));
        this.addDrawableChild(this.PickAxeSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, startY + 72, SLIDER_WIDTH));
        this.addDrawableChild(this.AxeSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, startY + 96, SLIDER_WIDTH));
        this.addDrawableChild(this.BowSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, startY + 120, SLIDER_WIDTH));          // Positioned Bow Slider
        this.addDrawableChild(this.ShearsSlider.createWidget(this.client.options, centerX - SLIDER_WIDTH / 2, startY + 144, SLIDER_WIDTH));    // Positioned Shears Slider

        // Back and Submit buttons
        int buttonWidth = 100;
        int buttonHeight = 20;
        int totalButtonWidth = 2 * buttonWidth + padding;

        // Adjust button Y position to accommodate additional sliders
        int buttonsY = startY + 168; // 144 + 24

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent))
                .dimensions(centerX - totalButtonWidth / 2, buttonsY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Submit"), button -> {
            close();

            // Clone and modify TreeNodes as per original logic
            Shopper elytraOnly_p2 = Market.elytra_P2.clone();
            elytraOnly_p2.trades.remove(0);
            elytraOnly_p2.trades.remove(0);

            Shopper bowOnly_p2 = Market.pvpUtilitys_P2.clone();
            bowOnly_p2.trades = new ArrayList<>();
            bowOnly_p2.trades.add(Market.goldIngotToBowVI_t);
            bowOnly_p2.trades.add(Market.goldIngotToBowVII_t);

            Shopper shearsOnly_p2 = Market.pvpUtilitys_P2.clone();
            shearsOnly_p2.trades = new ArrayList<>();
            shearsOnly_p2.trades.add(Market.rawgoldToShearsIII_t);

            Debugging.Shulker("Armor" + elytraOnly_p2);

            // Include Bow and Shears in the values array
            int[] values = {
                    this.ArmorSlider.getValue(),
                    this.ElytraSlider.getValue(),
                    this.SwordSlider.getValue(),
                    this.PickAxeSlider.getValue(),
                    this.AxeSlider.getValue(),
                    this.BowSlider.getValue(),          // Bow value
                    this.ShearsSlider.getValue()        // Shears value
            };

            String[] items = {
                    "Armor",
                    "Elytra",
                    "Sword",
                    "PickAxe",
                    "Axe",
                    "Bow",          // Bow
                    "Shears"        // Shears
            };

            // Include Bow and Shears TreeNodes
            List<Shopper> nodes = List.of(
                    Market.armors_P1,
                    elytraOnly_p2,
                    Market.swords_P1,
                    Market.pickaxes_P1,
                    Market.axes_P1,
                    bowOnly_p2,     // Bow TreeNode
                    shearsOnly_p2   // Shears TreeNode
            );

            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                for (int i = 0; i < values.length; i++) {
                    int value = values[i];
                    if (value == 0) {
                        continue;
                    } else if (value <= 6) {
                        Trading.getMaterialAndBuyItem(nodes.get(i), value);
                    } else {
                        Trading.getMaterialAndBuyItem(nodes.get(i), 9999);
                    }
                }
            });
        }).dimensions(centerX + padding / 2, buttonsY, buttonWidth, buttonHeight).build());
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
