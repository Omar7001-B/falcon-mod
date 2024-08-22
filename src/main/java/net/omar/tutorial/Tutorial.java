package net.omar.tutorial;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Tutorial implements ModInitializer {
	// declare the client
	private static final MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
	public static final String MOD_ID = "tutorial";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Register chat message event handler
		ClientSendMessageEvents.ALLOW_CHAT.register(this::onChatMessage);

		LOGGER.info("Random Number Generator Mod Initialized!");
	}


	private boolean onChatMessage(String message) {
		if (message.startsWith("!ran")) {
			// Generate a random number
			Random random = new Random();
			int randomNumber = random.nextInt(100) + 1; // Generates a number between 1 and 100

			// Send the random number as a chat message
			if (client.player != null) {
				client.player.sendMessage(Text.literal("Random Number: " + randomNumber), false);
			}

			// Returning false to prevent the original message from being sent
			return false;
		}

		// Allow normal message processing if it's not the "!ran" command
		return true;
	}
}