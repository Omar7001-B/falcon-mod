package net.omar.tutorial.GUI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class SimpleButtonScreen extends Screen {
    private static final int BUTTON_WIDTH = 200;
    private static final Text TITLE = Text.literal("Simple Button Screen");
    private static final Text BUTTON_ONE_TEXT = Text.literal("Submit");
    private static final Text INPUT_LABEL = Text.literal("Input Port:");
    private static final Text TOGGLE_COLOR_TEXT = Text.literal("Select Color:");

    private final Screen parent;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private TextFieldWidget inputField;
    private String selectedColor = "Red"; // Default color

    // SimpleOption for the slider (similar to FOV)
// Slider definition with simplified text display
    private final SimpleOption<Integer> customSlider = new SimpleOption<>(
            "options.customSlider",  // This won't be displayed
            SimpleOption.emptyTooltip(),  // No tooltip
            (optionText, value) -> {
                // Return just the value or short word
                return switch (value) {
                    case 30 -> Text.literal("Min");  // Display "Min" for the minimum value
                    case 100 -> Text.literal("Max"); // Display "Max" for the maximum value
                    default -> Text.literal(String.valueOf(value));  // Display the number for other values
                };
            },
            new SimpleOption.ValidatingIntSliderCallbacks(30, 100),  // Range from 30 to 100
            70,  // Initial value is 70
            value -> MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate()  // No-op for simplicity
    );

    public SimpleButtonScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer));
        GridWidget gridWidget = this.layout.addBody(new GridWidget()).setSpacing(8);
        gridWidget.getMainPositioner().alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(1);

        // Adding input text field
        this.inputField = new TextFieldWidget(this.textRenderer, this.width / 2 - 75, 160, 150, 20, INPUT_LABEL);
        this.inputField.setPlaceholder(Text.literal("Enter a port number").formatted(Formatting.DARK_GRAY));
        adder.add(inputField); // Add the input field to the layout

        // Adding the toggle button for color selection (Red, Blue, Green)
        adder.add(CyclingButtonWidget.builder(value -> Text.literal(value.toString()))
                .values("Red", "Blue", "Green")
                .initially("Red")
                .build(this.width / 2 - 75, 200, 150, 20, TOGGLE_COLOR_TEXT, (button, color) -> {
                    this.selectedColor = color.toString(); // Capture the selected color
                    System.out.println("Selected Color: " + this.selectedColor); // Handle color selection
                })
        );

        // Adding the slider widget (similar to FOV)
        adder.add(this.customSlider.createWidget(this.client.options, this.width / 2 - 75, 240, 150)); // Add slider widget

        // Submit button to handle the input field value and selected color
        adder.add(ButtonWidget.builder(BUTTON_ONE_TEXT, button -> {
            String inputText = inputField.getText(); // Capture the input text
            System.out.println("Inputted Port: " + inputText); // Handle the input (for now, just print it)
            System.out.println("Selected Color: " + this.selectedColor); // Print the selected color
            System.out.println("Slider Value: " + this.customSlider.getValue()); // Get slider value

            // You can add validation or further handling logic here
        }).width(BUTTON_WIDTH).build());

        // Adding footer/back button
        this.layout.addFooter(ButtonWidget.builder(Text.literal("Back"), button -> this.client.setScreen(this.parent)).build());
        this.layout.refreshPositions();
        this.layout.forEachChild(this::addDrawableChild);
    }

    @Override
    protected void initTabNavigation() {
        this.layout.refreshPositions();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }
}
