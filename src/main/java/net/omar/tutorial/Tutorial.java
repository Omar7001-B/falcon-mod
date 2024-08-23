package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
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

	// Map for the commands, key = string, value = function
	private final Map<String, Consumer<String>> commands = new HashMap<>();

	// Keybinding declaration
	private static KeyBinding keyBinding;

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// Register commands
		commands.put("!omar", this::generateRandomNumber);
		ClientSendMessageEvents.ALLOW_CHAT.register(this::onChatMessage);

		// Register the keybinding
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.tutorial.randomchat", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding (KEYSYM for keyboard)
				GLFW.GLFW_KEY_R, // Default key: R
				"category.tutorial.keybindings" // The translation key of the keybinding's category
		));

		// Register tick event to listen for key presses
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				sendRandomChatMessage();
			}
		});
	}

	private boolean onChatMessage(String message) {
		for (Map.Entry<String, Consumer<String>> entry : commands.entrySet()) {
			if (message.startsWith(entry.getKey())) {
				entry.getValue().accept(message);
				return false;
			}
		}
		return true;
	}

	// ----------------------------- Functions -----------------------------

	// Generate a random number between 1 and 100
	void generateRandomNumber(String message) {
		Random random = new Random();
		int randomNumber = random.nextInt(100) + 1; // Generates a number between 1 and 100

		// Send the random number as a chat message
		if (client.player != null) {
			client.player.sendMessage(Text.literal("Random Dude: " + randomNumber), false);
		}
	}

	// Send a random chat message when the keybinding is pressed
	void sendRandomChatMessage() {
		String[] messages = {
				"Hello guys!",
				"What's up everyone?",
				"Hey there!",
				"How's it going?",
				"Good day, folks!"
		};

		// Choose a random message from the array
		String randomMessage = messages[new Random().nextInt(messages.length)];

		// Send the message in chat
		if (client.player != null) {
			client.player.networkHandler.sendChatMessage(randomMessage);
		}
	}
}
