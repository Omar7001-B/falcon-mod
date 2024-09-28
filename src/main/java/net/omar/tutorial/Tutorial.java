package net.omar.tutorial;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.omar.tutorial.Handlers.ChatMessageHandler;
import net.omar.tutorial.Handlers.KeyBindingHandler;
import net.omar.tutorial.Handlers.KeyPressingHandler;
import net.omar.tutorial.Managers.*;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


public class Tutorial implements ModInitializer {

    // Declare the client
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static String currentScreenString = "";
    public static final String MOD_ID = "tutorial";

    // Logger for console and log file
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Thread thread;


    // make function to watch end client tick
    public void watchEndClientTick() {
        // Register tick event to listen for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            KeyBindingHandler.onClientTick();
            KeyPressingHandler.onClientTick();
            Debugging.LogScreenChange(client.currentScreen == null ? "null" : client.currentScreen.toString());
        });
    }

    @Override
    public void onInitialize() {

        ClientPlayConnectionEvents.INIT.register((ClientPlayNetworkHandler handler, MinecraftClient client) -> {
            Debugging.Validation("Client connected to server: " + handler.getConnection().getAddress());
            showCurrentUser("Init Event");
        });

        Validating.initializeValidation(MinecraftClient.getInstance().getSession().getUsername()); // Initialize validation
        ChatMessageHandler.loadChatEvents(); // Load chat events
        KeyBindingHandler.loadAllKeyBinds(); // Load key bindings
        KeyPressingHandler.loadAllKeyPressBinds(); // Load key press bindings
        ChatMessageHandler.loadAllCustomCommands(); // Load custom commands
        watchEndClientTick(); // Watch end client tick
    }

    public static void showCurrentUser(String s) {
        /*
	private final String username;
	private final String uuid;
	private final String accessToken;
	private final Optional<String> xuid;
	private final Optional<String> clientId;
	private final Session.AccountType accountType;
         */
        Debugging.Validation("Username: " + MinecraftClient.getInstance().getSession().getUsername());
        Debugging.Validation("UUID: " + MinecraftClient.getInstance().getSession().getUuid());
        Debugging.Validation("Access Token: " + MinecraftClient.getInstance().getSession().getAccessToken());
        Debugging.Validation("XUID: " + MinecraftClient.getInstance().getSession().getXuid());
        Debugging.Validation("Client ID: " + MinecraftClient.getInstance().getSession().getClientId());
        Debugging.Validation("Account Type: " + MinecraftClient.getInstance().getSession().getAccountType());
        Debugging.Validation("----------------------");
    }

    public static void Sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted", e);
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}


// Comment Testaa
// Testgit comment