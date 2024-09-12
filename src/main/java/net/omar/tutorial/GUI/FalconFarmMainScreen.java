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
import net.omar.tutorial.classes.ModValidator;

@Environment(EnvType.CLIENT)
public class FalconFarmMainScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final Text TITLE = Text.literal("Falcon Farm Main Menu");

    private static final Text JOIN_DISCORD_TEXT = Text.literal("Join Discord With Us!")
            .setStyle(Style.EMPTY.withColor(Formatting.BLACK));
    private static final String DISCORD_LINK = ModValidator.discordLink;

    private final Screen parent;

    public FalconFarmMainScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int padding = 10;
        int buttonStartY = this.height / 4; // Starting Y position for buttons
        int buttonSpacing = 24; // Spacing between buttons

        // Add a button for "Farm Screen"
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Farm Material"), button -> {
            this.client.setScreen(new FarmScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add a button for "Gear Screen"
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Buy Gear"), button -> {
            this.client.setScreen(new GearScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add a button for "Items Screen"
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Buy Items"), button -> {
            this.client.setScreen(new ItemsScreen(this));
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + 2 * buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add a "Back" button to return to the parent screen
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(this.parent);
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + 5 * buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        // Add the "Join Discord With Us!" button
        this.addDrawableChild(ButtonWidget.builder(JOIN_DISCORD_TEXT, button -> {
            ConfirmLinkScreen.open(DISCORD_LINK, this, true);
        }).dimensions(centerX - BUTTON_WIDTH / 2, buttonStartY + 4 * buttonSpacing, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Display username, mod update status, and time left at the top
        String modStatus = ModValidator.isModUpToDate ? "Mod is up to date!" : "Mod is outdated. Please check Discord.";
        long daysLeft = ModValidator.getDaysLeft();
        long hoursLeft = ModValidator.getHoursLeft();

        String timeLeft = getFormattedTimeLeft(daysLeft, hoursLeft);

        // Render the title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);

        // Adjust Y position to avoid intersection with buttons
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(modStatus).setStyle(Style.EMPTY.withColor(ModValidator.isModUpToDate ? Formatting.GREEN : Formatting.RED)), this.width / 2, 50, 16777215);

        // Render buttons and other screen elements
        super.render(context, mouseX, mouseY, delta);

        // Render the footer with username and time left, with more space for username
        String username = "Username: " + MinecraftClient.getInstance().getSession().getUsername();
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(username).setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), this.width / 2, this.height - 50, 16777215); // Adjusted to be lower
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(timeLeft).setStyle(Style.EMPTY.withColor(Formatting.WHITE)), this.width / 2, this.height - 30, 16777215);
    }

    private String getFormattedTimeLeft(long daysLeft, long hoursLeft) {
        if (daysLeft > 0) {
            return String.format("Time left: %d days %d hours", daysLeft, hoursLeft % 24);
        } else if (hoursLeft > 0) {
            return String.format("Time left: %d hours", hoursLeft);
        } else {
            return "Expiry date has passed";
        }
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }
}
