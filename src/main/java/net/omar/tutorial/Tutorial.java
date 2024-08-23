package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class Tutorial implements ModInitializer {
	// declare the client
	private static final MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
	public static final String MOD_ID = "tutorial";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private final Map<String, Consumer<String>> commands = new HashMap<>();

	// make map for the commands, key = string, value = function

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		commands.put("!ran", this::generateRandomNumber);
		ClientSendMessageEvents.ALLOW_CHAT.register(this::onChatMessage);
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

}