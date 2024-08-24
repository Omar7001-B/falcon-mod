package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;


public class Tutorial implements ModInitializer {
	// Declare the client
	private static final MinecraftClient client = MinecraftClient.getInstance();
	public static final String MOD_ID = "tutorial";

	// Logger for console and log file
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// ----------------------------- Key Bindings -----------------------------
	// Container for key bindings and their corresponding functions
	private final Map<KeyBinding, Runnable> keyBindings = new HashMap<>();

	// Register a new key binding and store it in the container
	private void registerKeyBinding(String translationKey, String categoryName, int keyCode, Runnable action) {
		KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				translationKey,
				InputUtil.Type.KEYSYM,
				keyCode,
				categoryName
		));
		keyBindings.put(keyBinding, action);
	}

	// ----------------------------- Custom Chat Commands -----------------------------
	// Container for custom chat commands and their corresponding functions
	private final Map<String, Consumer<String>> customCommands = new HashMap<>();

	// Register a new custom chat command and store it in the container
	private void registerCustomCommand(String command, Consumer<String> action) { customCommands.put(command, action); }

	// Handle incoming chat messages and trigger corresponding custom commands
	private boolean onChatMessage(String message) {
		for (Map.Entry<String, Consumer<String>> entry : customCommands.entrySet()) {
			if (message.startsWith(entry.getKey())) {
				entry.getValue().accept(message);
				return false; // Prevent further processing of the message
			}
		}
		return true; // Allow the message to be processed normally if no command matches
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// Register chat message event to handle custom commands
		ClientSendMessageEvents.ALLOW_CHAT.register(this::onChatMessage);

		// Register key bindings
		registerKeyBinding("Random Message", "Chat", GLFW.GLFW_KEY_R, () -> sendRandomChatMessage(""));
		registerKeyBinding("Open Shop", "Farm", GLFW.GLFW_KEY_SLASH, () -> sendShopCommand(""));
		registerKeyBinding("Open PV", "Farm", GLFW.GLFW_KEY_KP_MULTIPLY, () -> openPV1(""));

		// Register custom chat commands
		registerCustomCommand("!random", this::sendRandomChatMessage);

		// Register tick event to listen for key presses
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Log the key presses
			keyBindings.forEach((keyBinding, action) -> {
				if (keyBinding.wasPressed()) action.run();
			});
		});
	}

	// ----------------------------- Helper Functions -----------------------------
	// Make function to send command
	private void sendCommand(String command) { if (client.player != null) client.player.networkHandler.sendChatCommand(command); }

	// Make function to send message
	private void sendChatMessage(String message) { if (client.player != null) client.player.networkHandler.sendChatMessage(message); }

	// ----------------------------- Functions -----------------------------
	// Send a random chat message
	private void sendRandomChatMessage(String unused) {
		String[] messages = { "Hello guys!", "What's up everyone?", "Hey there!", "How's it going?", "Good day, folks!" };
		String randomMessage = messages[new Random().nextInt(messages.length)];
		sendChatMessage(randomMessage);
	}

	// Send the /shop command
	private void sendShopCommand(String unused) { sendCommand("shop"); }

	// Open PV 1
	private void openPV1(String unused) { sendCommand("pv 1"); }
}
