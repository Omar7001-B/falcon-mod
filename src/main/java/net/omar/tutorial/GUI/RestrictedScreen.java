package net.omar.tutorial.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.omar.tutorial.Managers.Validating;

@Environment(EnvType.CLIENT)
public class RestrictedScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final Text TITLE = Text.literal("Restricted Access");

    private final Screen parent;

    public RestrictedScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int padding = 30;

        // Use ThreePartsLayoutWidget for centering and spacing
        ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
        layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
        GridWidget gridWidget = layout.addBody(new GridWidget()).setSpacing(8);
        gridWidget.getMainPositioner().alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(1);

        // Validation message
        String username = MinecraftClient.getInstance().getSession().getUsername();
        if (Validating.isUserEnabled()) {
            adder.add(new TextWidget(Text.literal("User validation: " + username + " is valid!").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), this.textRenderer));
        } else {
            adder.add(new TextWidget(Text.literal("User validation: " + username + " is not valid!  Your access has expired.").setStyle(Style.EMPTY.withColor(Formatting.RED)), this.textRenderer));
            adder.add(new TextWidget(Text.literal("Please contact us on Discord to renew.").setStyle(Style.EMPTY.withColor(Formatting.RED)), this.textRenderer));
        }

        // Mod update status message
        if (Validating.isModUpToDate()) {
            adder.add(new TextWidget(Text.literal("Mod status: Up to date!").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), this.textRenderer));
        } else {
            adder.add(new TextWidget(Text.literal("Mod status: Outdated.").setStyle(Style.EMPTY.withColor(Formatting.RED)), this.textRenderer));
            adder.add(new TextWidget(Text.literal("Please contact us on Discord for the latest update.").setStyle(Style.EMPTY.withColor(Formatting.RED)), this.textRenderer));
        }

        // Join Discord button
        adder.add(ButtonWidget.builder(Text.literal("Join Discord With Us!"), button ->
                ConfirmLinkScreen.open(Validating.discordLink, this, true)
        ).width(BUTTON_WIDTH).build());

        // Back button
        adder.add(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent)).width(BUTTON_WIDTH).build());

        //layout.addFooter(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent)).build());
        layout.refreshPositions();
        layout.forEachChild(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
