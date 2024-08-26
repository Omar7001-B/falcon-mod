package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Market;
import net.omar.tutorial.classes.Trade;
import net.omar.tutorial.classes.TreeNode;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class Tutorial implements ModInitializer {

    // Declare the client
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static String currentScreenString = "";
    public static final String MOD_ID = "tutorial";

    // Logger for console and log file
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // ----------------------------- Key Bindings -----------------------------
    // Container for key bindings and their corresponding functions
    private final Map<KeyBinding, Consumer<String>> keyBindings = new HashMap<>();

    // Register a new key binding and store it in the container
    private void registerKeyBinding(String translationKey, String categoryName, int keyCode, Consumer<String> action) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey,
                InputUtil.Type.KEYSYM,
                keyCode,
                categoryName
        ));
        keyBindings.put(keyBinding, action);
    }

    public void loadAllKeyBinds() {
        registerKeyBinding("Random Message", "Chat", GLFW.GLFW_KEY_R, Tutorial::sendRandomChatMessage);
        registerKeyBinding("Open Shop", "Farm", GLFW.GLFW_KEY_KP_MULTIPLY, Tutorial::openShop);
        registerKeyBinding("Open PV", "Farm", GLFW.GLFW_KEY_SLASH, Tutorial::openPV1);
        // make capslock keybinding
        registerKeyBinding("Testing Function", "Debug", GLFW.GLFW_KEY_Z, Tutorial::testFunction);

        // Register tick event to listen for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Log the key presses
            keyBindings.forEach((keyBinding, action) -> {
                if (keyBinding.wasPressed()) action.accept("");
            });

            DEBUG.LogScreenChange(client.currentScreen == null ? "null" : client.currentScreen.toString());
        });
    }

    // ----------------------------- Custom Chat Commands -----------------------------
    // Container for custom chat commands and their corresponding functions
    private final Map<String, Consumer<String>> customCommands = new HashMap<>();

    // Register a new custom chat command and store it in the container
    private void registerCustomCommand(String command, Consumer<String> action) {
        customCommands.put(command, action);
    }

    // Handle incoming chat messages and trigger corresponding custom commands
    public void loadAllCustomCommands() {
        registerCustomCommand("!random", Tutorial::sendRandomChatMessage);
        registerCustomCommand("!shop", Tutorial::openShop);
        registerCustomCommand("!pv", Tutorial::openPV1);
        registerCustomCommand("!test", Tutorial::testFunction);
    }

    // ----------------------------- Chat Message Events -----------------------------
    private boolean onChatMessageSent(String message) {
        LOGGER.info("Received message: " + message);
        for (Map.Entry<String, Consumer<String>> entry : customCommands.entrySet()) {
            if (message.startsWith(entry.getKey())) {
                entry.getValue().accept(message);
                return false; // Prevent further processing of the message
            }
        }
        return true; // Allow the message to be processed normally if no command matches
    }

    private void onChatMessageReceived(Text messageText, SignedMessage signedMessage, GameProfile profile, MessageType.Parameters parameters, Instant timestamp) {
        // Log the message to the console
        LOGGER.info("Received chat message: " + messageText.getString());

        // Optionally log more details about the sender or message metadata
        if (profile != null) {
            LOGGER.info("Message sent by: " + profile.getName());
        }
    }

    public void loadChatEvents() {
        ClientSendMessageEvents.ALLOW_CHAT.register(this::onChatMessageSent);
        ClientReceiveMessageEvents.CHAT.register(this::onChatMessageReceived);
    }

    @Override
    public void onInitialize() {

        loadChatEvents();
        loadAllKeyBinds();
        loadAllCustomCommands();

    }

    // ----------------------------- Shop Functions -----------------------------
    public static int SCREENS_DELAY = 500;
    public static int TRADE_DELAY = 100;

    public static void Sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // -----------------------------  Screens Functions -----------------------------
    public static void waitForScreenChange(int maxDelay) {
        String oldScreen = currentScreenString;
        while (oldScreen == currentScreenString && maxDelay > 0) {
            currentScreenString = client.currentScreen == null ? "null" : client.currentScreen.toString();
            Sleep(SCREENS_DELAY);
            maxDelay -= SCREENS_DELAY;
        }
    }

    public static void watchCurrentScreen() {
        MinecraftClient.getInstance().execute(() -> {
            if (MinecraftClient.getInstance().currentScreen != null) {
                LOGGER.info("Current Open Screen is: " + MinecraftClient.getInstance().currentScreen.toString());
                DEBUG.Screens("Current Open Screen is: " + MinecraftClient.getInstance().currentScreen.toString());
            }
        });
    }

    public static void closeScreen() {
        MinecraftClient.getInstance().execute(() -> {
            if (MinecraftClient.getInstance().currentScreen != null) {
                MinecraftClient.getInstance().currentScreen.close();
            }
        });
    }

    // ----------------------------- Clicks and Slots -----------------------------
    public static Slot getSlot(int index) {
        return ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots.get(index);
    }

    public static int searchSlots(String name) {
        if (client.currentScreen == null) return -1;
        MinecraftClient client = MinecraftClient.getInstance();
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        int index = 0;
        for (var val : slots) {
            if (name.equals(val.getStack().getName().getString())) return index;
            index++;
        }
        return -1;
    }

    public static void clickSlot(int slotIndex) {
        if (client.player == null || client.currentScreen == null) return;
        client.interactionManager.clickSlot(
                ((HandledScreen<?>) client.currentScreen).getScreenHandler().syncId,
                slotIndex,
                0,
                SlotActionType.QUICK_MOVE,
                client.player
        );
    }


    // ----------------------------- Trades -----------------------------
    public static void ShowOffer(TradeOffer offer) {
        DEBUG.Shop("First Item: " + offer.getOriginalFirstBuyItem().getName().getString() + " x " + offer.getOriginalFirstBuyItem().getCount());
        DEBUG.Shop("Second Item: " + offer.getSecondBuyItem().getName().getString() + " x " + offer.getSecondBuyItem().getCount());
        DEBUG.Shop("Result: " + offer.getSellItem().getName().getString() + " x " + offer.getSellItem().getCount());
    }

    public static boolean canTrade(String firstItem, int firstItemAmount, String secondItem, int secondItemAmount){
        int firstItemAccumalated = 0;
        int secondItemAccumalated = 0;
        int slotsSize = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots.size();
        for (int i = 0; i < slotsSize; i++) {
            Slot slot = getSlot(i);
            if (slot.getStack().getName().getString().equals(firstItem)) firstItemAccumalated += slot.getStack().getCount();
            if (slot.getStack().getName().getString().equals(secondItem)) secondItemAccumalated += slot.getStack().getCount();

        }
        //DEBUG.Shop("First Item Accumulated: " + firstItemAccumalated  + ", Second Item Accumulated: " + secondItemAccumalated);
        return firstItemAccumalated >= firstItemAmount && secondItemAccumalated >= secondItemAmount;
    }

    public static void makeTrade(int offerIndex) {
        DEBUG.Shop("Current Screen in makeTrade function: " + client.currentScreen.toString());
        if (!(client.currentScreen instanceof MerchantScreen)) return;
        TradeOffer offer = ((MerchantScreen) client.currentScreen).getScreenHandler().getRecipes().get(offerIndex);
        ShowOffer(offer);
        while(canTrade(offer.getOriginalFirstBuyItem().getName().getString(), offer.getOriginalFirstBuyItem().getCount(), offer.getSecondBuyItem().getName().getString(), offer.getSecondBuyItem().getCount())){
            client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));
            int maxDelay = 5000;
            do {
                Sleep(TRADE_DELAY);
                maxDelay -= TRADE_DELAY;
            }
            while (!getSlot(0).hasStack() && maxDelay > 0);
            clickSlot(2);
        }
    }

    // ----------------------------- Helper Functions -----------------------------
    private static void sendCommand(String command) {
        client.player.networkHandler.sendChatCommand(command);
    }

    private static void sendChatMessage(String message) {
        client.player.networkHandler.sendChatMessage(message);
    }

    private static void sendRandomChatMessage(String unused) {
        String[] messages = {"Hello guys!", "What's up everyone?", "Hey there!", "How's it going?", "Good day, folks!"};
        String randomMessage = messages[new Random().nextInt(messages.length)];
        sendChatMessage(randomMessage);
    }

    private static void openShop() {
        openShop("");
    }

    private static void openShop(String unused) {
        sendCommand("shop");
    }

    private static void openPV1(String unused) {
        sendCommand("pv 1");
    }

    // BuyItem
    private static void executeTrade(Trade item) {
        List<String> path = item.getPathFromRoot();
        DEBUG.Screens("Opening Shop");
        openShop();
        waitForScreenChange(5000);
        DEBUG.Screens("Clicking Slot 1");
        clickSlot(searchSlots(path.get(1)));
        waitForScreenChange(5000);
        DEBUG.Screens("Clicking Slot 2");
        clickSlot(searchSlots(path.get(2)));
        waitForScreenChange(5000);
        DEBUG.Screens("Making Trade");
        makeTrade(item.TradeIndex - 1);
        DEBUG.Screens("Trade Completed");
        closeScreen();
        waitForScreenChange(5000);
    }

    // Test function to send a signed message
    private static void testFunction(String unused) {
        Thread thread = new Thread(() -> {
            executeTrade(Market.rawGoldToDiamond_t);
            executeTrade(Market.diamondToGoldNugget_t);
            executeTrade(Market.goldNuggetToEmerald_t);
            executeTrade(Market.emeraldToRawGold_t);
        });
        thread.start();
    }
}
