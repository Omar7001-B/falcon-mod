package net.omar.tutorial.Handlers;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.omar.tutorial.Managers.Debugging;
import net.omar.tutorial.Tutorial;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class ChatMessageHandler {
    public static final Map<String, Consumer<String>> customCommands = new HashMap<>();

    public static void loadAllCustomCommands() {
//        registerCustomCommand("!random", Tutorial::sendRandomChatMessage);
//        registerCustomCommand("!shop", TradeManager::openShop);
//        registerCustomCommand("!pv", Tutorial::openPV1);
        //registerCustomCommand("!test", Tutorial::testFunction);
        registerCustomCommand("!save", ChatMessageHandler::saveInventory);
        registerCustomCommand("!recover", ChatMessageHandler::recoverInventory);
    }

    public static void registerCustomCommand(String command, Consumer<String> action) {
        customCommands.put(command, action);
    }

    // ----------------------------- Chat Message Events -----------------------------
    public static boolean onChatMessageSent(String message) {
        Tutorial.LOGGER.info("Received message: " + message);
        for (Map.Entry<String, Consumer<String>> entry : customCommands.entrySet()) {
            if (message.startsWith(entry.getKey())) {
                if (Tutorial.thread != null && Tutorial.thread.isAlive()) return false;
                Tutorial.thread = new Thread(() -> {
                    try {
                        entry.getValue().accept("");
                    } catch (Exception e) {
                        Debugging.Error("Thread interrupted" + e.getMessage());
                        Tutorial.LOGGER.error("Thread interrupted", e);
                        Thread.currentThread().interrupt();
                    }
                });
                Tutorial.thread.start();
                return false; // Prevent further processing of the message
            }
        }
        return true; // Allow the message to be processed normally if no command matches
    }

    public static void onChatMessageReceived(Text messageText, SignedMessage signedMessage, GameProfile profile, MessageType.Parameters parameters, Instant timestamp) {
        // Log the message to the console
        Tutorial.LOGGER.info("Received chat message: " + messageText.getString());

        // Optionally log more details about the sender or message metadata
        if (profile != null) {
            Tutorial.LOGGER.info("Message sent by: " + profile.getName());
        }
    }

    // Updated: No 'this' reference in static context
    public static void loadChatEvents() {
        ClientSendMessageEvents.ALLOW_CHAT.register(ChatMessageHandler::onChatMessageSent);
        ClientReceiveMessageEvents.CHAT.register(ChatMessageHandler::onChatMessageReceived);
    }

    // ----------------------------- Helper Functions -----------------------------
    public static void sendCommand(String command) {
        if (Tutorial.client.player == null) return;
        Tutorial.client.player.networkHandler.sendChatCommand(command);
    }

    public static void sendChatMessage(String message) {
        if (Tutorial.client.player == null) return;
        Tutorial.client.player.networkHandler.sendChatMessage(message);
    }

    public static void sendRandomChatMessage(String unused) {
        String[] messages = {"Hello guys!", "What's up everyone?", "Hey there!", "How's it going?", "Good day, folks!"};
        String randomMessage = messages[new Random().nextInt(messages.length)];
        sendChatMessage(randomMessage);
    }

    public static void saveInventory(String unused) {
        // Implement save inventory logic here
    }

    public static void recoverInventory(String unused) {
        // Implement recover inventory logic here
    }
}
