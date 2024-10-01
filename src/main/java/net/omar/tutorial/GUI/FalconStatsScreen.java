package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.omar.tutorial.Managers.Statting; // Import your Statting class
import org.slf4j.Logger;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class FalconStatsScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final StatHandler statHandler; // Initialize statHandler here
    private GeneralStatsListWidget statsListWidget;
    private final Screen parent;

    public FalconStatsScreen(Screen parent) {
        super(Text.literal("Falcon Stats"));
        this.parent = parent;
        this.statHandler = new StatHandler(); // Initialize StatHandler here
    }

    @Override
    protected void init() {
        super.init(); // Call the super method first to initialize the screen properly


        // Add Copy to Clipboard button
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Copy to Clipboard"), button -> {
                            copyStatsToClipboard();
                            this.client.setScreen(new FalconStatsScreen(this.parent)); // Refresh screen or show a message
                        })
                        .dimensions(this.width / 2 - 155, this.height - 38, 150, 20) // Adjust dimensions based on the screen width
                        .build()
        );

        // Add Close button
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Close"), button -> this.client.setScreen(this.parent)) // Use this.parent to go back to the previous screen
                        .dimensions(this.width / 2 + 5, this.height - 38, 150, 20) // Adjust dimensions and position
                        .build()
        );

        // Create the stats list widget
        this.statsListWidget = new GeneralStatsListWidget(MinecraftClient.getInstance());
        this.addDrawableChild(statsListWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, tickDelta);
        this.statsListWidget.render(context, mouseX, mouseY, tickDelta);
    }

    private void copyStatsToClipboard() {
        StringBuilder statsBuilder = new StringBuilder();

        // Append Farming stats
        Map<String, Integer> farmingStats = Statting.getFarmingStats();
        if (!farmingStats.isEmpty()) {
            statsBuilder.append("Farming:\n");
            for (Map.Entry<String, Integer> entry : farmingStats.entrySet()) {
                statsBuilder.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        // Append Item stats
        Map<String, Integer> itemStats = Statting.getItemStats();
        if (!itemStats.isEmpty()) {
            statsBuilder.append("\nItems:\n");
            for (Map.Entry<String, Integer> entry : itemStats.entrySet()) {
                statsBuilder.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        // Append Gear stats
        Map<String, Integer> gearStats = Statting.getGearStats();
        if (!gearStats.isEmpty()) {
            statsBuilder.append("\nGear:\n");
            for (Map.Entry<String, Integer> entry : gearStats.entrySet()) {
                statsBuilder.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        MinecraftClient.getInstance().keyboard.setClipboard("" + statsBuilder);
    }

    private class GeneralStatsListWidget extends AlwaysSelectedEntryListWidget<GeneralStatsListWidget.Entry> {
        public GeneralStatsListWidget(MinecraftClient client) {
            super(client, FalconStatsScreen.this.width, FalconStatsScreen.this.height, 32, FalconStatsScreen.this.height - 64, 10);
            this.populateEntries();
        }

        private void populateEntries() {
            // Populate stats from your Statting class
            // Farming Stats
            Map<String, Integer> farmingStats = Statting.getFarmingStats();
            if (!farmingStats.isEmpty()) {
                this.addEntry(new Entry("Farming:")); // Add the category entry
                for (Map.Entry<String, Integer> entry : farmingStats.entrySet()) {
                    this.addEntry(new Entry("  " + entry.getKey(), entry.getValue())); // Indent entries
                }
            }

            // Item Stats
            Map<String, Integer> itemStats = Statting.getItemStats();
            if (!itemStats.isEmpty()) {
                this.addEntry(new Entry("Items:")); // Add the category entry
                for (Map.Entry<String, Integer> entry : itemStats.entrySet()) {
                    this.addEntry(new Entry("  " + entry.getKey(), entry.getValue())); // Indent entries
                }
            }

            // Gear Stats
            Map<String, Integer> gearStats = Statting.getGearStats();
            if (!gearStats.isEmpty()) {
                this.addEntry(new Entry("Gear:")); // Add the category entry
                for (Map.Entry<String, Integer> entry : gearStats.entrySet()) {
                    this.addEntry(new Entry("  " + entry.getKey(), entry.getValue())); // Indent entries
                }
            }
        }

        class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
            private final String statName;
            private final int statValue;

            Entry(String statName, int statValue) {
                this.statName = statName;
                this.statValue = statValue;
            }

            Entry(String statName) {
                this.statName = statName;
                this.statValue = 0; // Default value for category entries
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawTextWithShadow(FalconStatsScreen.this.textRenderer, this.statName, x + 2, y + 1, 16777215);
                if (statValue > 0) { // Only render value for actual stats
                    String formattedStat = String.valueOf(this.statValue);
                    context.drawTextWithShadow(FalconStatsScreen.this.textRenderer, formattedStat, x + entryWidth - FalconStatsScreen.this.textRenderer.getWidth(formattedStat) - 2, y + 1, 16777215);
                }
            }

            @Override
            public Text getNarration() {
                return Text.literal(this.statName + (statValue > 0 ? ": " + this.statValue : "")); // Optional narration text
            }
        }
    }
}
