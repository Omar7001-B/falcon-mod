package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.InventoryIndexes;
import net.omar.tutorial.classes.Market;
import net.omar.tutorial.classes.Trade;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
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
            String screen = client.currentScreen == null ? "null" : client.currentScreen.toString();
            if (!currentScreenString.equals(screen)) {
                LOGGER.info("Screen : " + "[" + currentScreenString + "] -> [" + screen + "]");
                currentScreenString = screen;
            }
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
    public static int SCREENS_DELAY = 100;
    public static int FREEZE_DELAY = 200;
    public static int MAX_SCREEN_DELAY = 2000;

    public static int SLOT_DELAY = 100;
    public static int MAX_SLOT_DELAY = 2000;

    public static int TRADE_DELAY = 0;

    public static void Sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // -----------------------------  Screens Functions -----------------------------
    public static void waitForScreenChange() {
        String oldScreen = currentScreenString;
        for(int i = 0; i < MAX_SCREEN_DELAY; i+=SCREENS_DELAY){
            Sleep(SCREENS_DELAY);
            currentScreenString = client.currentScreen == null ? "null" : client.currentScreen.toString();
            if (!oldScreen.equals(currentScreenString)) break;
        }
        Sleep(FREEZE_DELAY);
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
        for (int i = 0; i < slots.size(); i++)
            if (slots.get(i).getStack().getName().getString().contains(name)) return i;
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
    public static int numberOfTradeClicks(String firstItem, int firstItemAmount, String secondItem, int secondItemAmount){
        int firstItemAccumalated = 0;
        int secondItemAccumalated = 0;
        int slotsSize = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots.size();
        for (int i = 0; i < slotsSize; i++) {
            Slot slot = getSlot(i);
            if (slot.getStack().getName().getString().equals(firstItem)) firstItemAccumalated += slot.getStack().getCount();
            if (slot.getStack().getName().getString().equals(secondItem)) secondItemAccumalated += slot.getStack().getCount();

        }
        return Math.min(firstItemAccumalated/firstItemAmount, secondItemAmount != 0 ? secondItemAccumalated/secondItemAmount : 99999);
    }

    public static void makeTrade(int offerIndex) {
        for(int i = 0; i < MAX_SCREEN_DELAY; i+=SCREENS_DELAY){
            if (client.currentScreen instanceof MerchantScreen) break;
            Sleep(SCREENS_DELAY);
        }
        if(!(client.currentScreen instanceof MerchantScreen)) return;
        TradeOffer offer = ((MerchantScreen) client.currentScreen).getScreenHandler().getRecipes().get(offerIndex);
        int numberOfTrades = numberOfTradeClicks(offer.getOriginalFirstBuyItem().getName().getString(), offer.getOriginalFirstBuyItem().getCount(),
                offer.getSecondBuyItem().getName().getString(), offer.getSecondBuyItem().getCount());
        for(int i = 0; i < numberOfTrades; i++){
            client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));
            for(int j = 0; j < MAX_SLOT_DELAY; j+= SLOT_DELAY){
                if (getSlot(0).hasStack()) break;
                Sleep(SLOT_DELAY);
            }
            clickSlot(2);
            Sleep(TRADE_DELAY);
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

    private static void openInventory(String unused) {
        MinecraftClient.getInstance().setScreen(new InventoryScreen(client.player));
    }

    private static void openPV1(String unused) {
        sendCommand("pv 1");
    }

    // BuyItem
    private static void executeTrade(Trade item) {
        List<String> path = item.getPathFromRoot();
        DEBUG.Screens("Opening Shop");
        openShop();
        waitForScreenChange();
        DEBUG.Screens("Clicking Slot 1");
        clickSlot(searchSlots(path.get(1)));
        waitForScreenChange();
        DEBUG.Screens("Clicking Slot 2");
        clickSlot(searchSlots(path.get(2)));
        waitForScreenChange();
        DEBUG.Screens("Making Trade");
        makeTrade(item.TradeIndex - 1);
        DEBUG.Screens("Trade Completed");
        closeScreen();
        waitForScreenChange();
    }

    public static void swapSlots(int slot1, int slot2) {
        client.interactionManager.clickSlot(0, slot1, 0, SlotActionType.PICKUP, client.player); // Pick up item from slot1
        client.interactionManager.clickSlot(0, slot2, 0, SlotActionType.PICKUP, client.player); // Place item into slot2
        client.interactionManager.clickSlot(0, slot1, 0, SlotActionType.PICKUP, client.player); // Place item into slot1
    }

    public static void openShulkerBox(String name) {
        openInventory("");
        int slot = searchSlots(name);
        LOGGER.info("Found " + name + " at slot " + slot);
        if (slot == -1) return;
        swapSlots(InventoryIndexes.HOTBAR_INDEXES.get(client.player.getInventory().selectedSlot), slot);
        LOGGER.info("Swapped index 0 with " + " with slot " + slot);
        closeScreen();
        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
    }

    // Test function to send a signed message
    private static void testFunction(String unused) {
        openShulkerBox("Shulker");
        //printAllSlots();
        if(true) return;

        Thread thread = new Thread(() -> {
            executeTrade(Market.rawGoldToDiamond_t);
            executeTrade(Market.diamondToGoldNugget_t);
            executeTrade(Market.goldNuggetToEmerald_t);
            executeTrade(Market.emeraldToRawGold_t);
        });
        thread.start();
    }


    public static void printAllSlots(){
        HandledScreen<?> screen = (HandledScreen<?>) client.currentScreen;
        if(screen == null) return;
        DefaultedList<Slot> slots = screen.getScreenHandler().slots;
        if(slots == null)  return;
        LOGGER.info("-------------------");
        for(int i = 0; i < slots.size(); i++){
            if(slots.get(i) != null) {
                LOGGER.info(""+i+": "+slots.get(i).getStack().getName().getString());
            }
        }
        LOGGER.info("-------------------");
    }

}


