package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.omar.tutorial.Managers.Validating;

import java.time.temporal.ChronoUnit;

@Environment(EnvType.CLIENT)
public class MainScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final Text TITLE = Text.literal("Falcon Farm Main Menu");

    private static final Text JOIN_DISCORD_TEXT = Text.literal("Join Discord With Us!")
            .setStyle(Style.EMPTY.withColor(Formatting.BLACK));
    private static final String DISCORD_LINK = Validating.discordLink;

    private final Screen parent;

    public MainScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int padding = 10;
        int buttonStartY = this.height / 4; // Starting Y position for buttons
        int buttonSpacing = 24; // Spacing between buttons
        int additionalSpacing = 10; // Additional space before Discord and Back buttons

        // Add a button for "Farm Screen"
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Farm Material"), button -> {
            if (Validating.enableFarming)
                this.client.setScreen(new FarmScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add a button for "Gear Screen"
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Buy Gear"), button -> {
            if (Validating.enableBuyGears)
                this.client.setScreen(new GearScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add a button for "Items Screen"
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Buy Items"), button -> {
            if (Validating.enableBuyItems)
                this.client.setScreen(new ItemsScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + 2 * buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add a button for "Inventory Saver"
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Inventory"), button -> {
            this.client.setScreen(new InventoryScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + 3 * buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add the "Join Discord With Us!" button with additional space
        this.addDrawableChild(ButtonWidget.builder(JOIN_DISCORD_TEXT, button -> {
            ConfirmLinkScreen.open(DISCORD_LINK, this, true);
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + 4 * buttonSpacing + additionalSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add a "Back" button to return to the parent screen with additional space
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(this.parent);
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + 5 * buttonSpacing + additionalSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Display username, mod update status, and time left at the top
        String currenVersion = "Version: " + Validating.CURRENT_VERSION;
        String modStatus = Validating.isModUpToDate() ? "Mod is up to date!" : "Mod is outdated. Please check Discord.";

        // Get the total time left in seconds
        long totalSecondsLeft = Validating.getTimeLeft(ChronoUnit.SECONDS);

        // Format the time left
        String timeLeft = getFormattedTimeLeft(totalSecondsLeft);

        // Render the title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);

        // Adjust Y position to avoid intersection with buttons
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(modStatus).setStyle(Style.EMPTY.withColor(Validating.isModUpToDate() ? Formatting.GREEN : Formatting.RED)),
                this.width / 2, 50, 16777215);

        // Display the current version below the mod status
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(currenVersion).setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                this.width / 2, 65, 16777215); // Y position adjusted below modStatus

        // Render buttons and other screen elements
        super.render(context, mouseX, mouseY, delta);

        // Render the footer with username and time left, with more space for username
        String username = "Username: " + MinecraftClient.getInstance().getSession().getUsername();
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(username).setStyle(Style.EMPTY.withColor(Formatting.YELLOW)),
                this.width / 2, this.height - 50, 16777215); // Adjusted to be lower
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(timeLeft).setStyle(Style.EMPTY.withColor(Formatting.WHITE)),
                this.width / 2, this.height - 30, 16777215);
    }

    private String getFormattedTimeLeft(long totalSecondsLeft) {
        if (totalSecondsLeft <= 0) {
            return "Expiry date has passed"; // Handle case where no time is left
        }

        long days = totalSecondsLeft / 86400; // 60 seconds * 60 minutes * 24 hours
        totalSecondsLeft %= 86400;
        long hours = totalSecondsLeft / 3600; // 60 seconds * 60 minutes
        totalSecondsLeft %= 3600;
        long minutes = totalSecondsLeft / 60; // 60 seconds
        long seconds = totalSecondsLeft % 60; // Remaining seconds

        // Build the formatted string
        StringBuilder timeLeftBuilder = new StringBuilder();
        if (days > 0) {
            timeLeftBuilder.append(days).append("d ");
        }
        if (hours > 0) {
            timeLeftBuilder.append(hours).append("h ");
        }
        if (minutes > 0) {
            timeLeftBuilder.append(minutes).append("m ");
        }
        if (seconds > 0) {
            timeLeftBuilder.append(seconds).append("s");
        }

        return timeLeftBuilder.toString().trim(); // Trim to remove any trailing space
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }
}
