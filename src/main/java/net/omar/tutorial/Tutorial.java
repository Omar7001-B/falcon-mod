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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.Inventory.NameConverter;
import net.omar.tutorial.Inventory.SlotClicker;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Trade;
import net.omar.tutorial.indexes.InventoryIndexes;
import net.omar.tutorial.indexes.Market;
import net.omar.tutorial.indexes.ShulkerInventoryIndexes;
import net.omar.tutorial.indexes.TradeInventoryIndexes;
import org.apache.commons.logging.Log;
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

    // ----------------------------- Maps ------------------------------
    private final Map<KeyBinding, Consumer<String>> keyBindings = new HashMap<>();
    private final Map<Integer, Consumer<String>> keyPressBindings = new HashMap<>();
    private final Map<String, Consumer<String>> customCommands = new HashMap<>();

    public void loadAllKeyBinds() {
        registerKeyBinding("Random Message", "Chat", GLFW.GLFW_KEY_R, Tutorial::sendRandomChatMessage);
        registerKeyBinding("Open Shop", "Farm", GLFW.GLFW_KEY_KP_MULTIPLY, Tutorial::openShop);
        registerKeyBinding("Open PV", "Farm", GLFW.GLFW_KEY_SLASH, Tutorial::openPV1);
        // make capslock keybinding
        registerKeyBinding("Testing Function", "Debug", GLFW.GLFW_KEY_Z, Tutorial::testFunction);

    }

    public void loadAllKeyPressBinds() {
        registerKeyPressBinding(GLFW.GLFW_KEY_X, (String s) -> SlotOperations.showAllSlots(null));
        registerKeyPressBinding(GLFW.GLFW_KEY_Y, (String s) -> LOGGER.info("Y key pressed"));
    }

    public void loadAllCustomCommands() {
        registerCustomCommand("!random", Tutorial::sendRandomChatMessage);
        registerCustomCommand("!shop", Tutorial::openShop);
        registerCustomCommand("!pv", Tutorial::openPV1);
        registerCustomCommand("!test", Tutorial::testFunction);
    }

    private void registerKeyBinding(String translationKey, String categoryName, int keyCode, Consumer<String> action) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding( translationKey, InputUtil.Type.KEYSYM, keyCode, categoryName ));
        keyBindings.put(keyBinding, action);
    }
    private void registerKeyPressBinding(int keyCode, Consumer<String> action) { keyPressBindings.put(keyCode, action); }
    private void registerCustomCommand(String command, Consumer<String> action) { customCommands.put(command, action); }

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

    // make function to watch end client tick
    public void watchEndClientTick() {
        // Register tick event to listen for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Log the key presses
            keyBindings.forEach((keyBinding, action) -> {
                if (keyBinding.wasPressed()) {
                    // run it inside thread
                    Thread thread = new Thread(() -> {
                        action.accept("");
                    });
                    thread.start();
                }
            });

            // Log the current screen
            DEBUG.LogScreenChange(client.currentScreen == null ? "null" : client.currentScreen.toString());
            //String screen = client.currentScreen == null ? "null" : client.currentScreen.toString();
            //if (!currentScreenString.equals(screen)) {
                //LOGGER.info("Screen : " + "[" + currentScreenString + "] -> [" + screen + "]");
                //
                //currentScreenString = screen;
            //}

            // Log the key presses



            //DEBUG.Shop("Current Screen: " + currentScreenString);
            //LOGGER.info("KeyPressesMapSize: " + keyPressBindings.size());
            keyPressBindings.forEach((keyCode, action) -> {
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyCode)) {
                    LOGGER.info("Key code " + keyCode + " pressed");
                    Thread thread = new Thread(() -> {
                        action.accept("");
                    });
                    thread.start();
                }
            });


            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_ESCAPE)) {
                LOGGER.info("ESC key pressed");
            }
        });
    }

    @Override
    public void onInitialize() {
        loadChatEvents();
        loadAllKeyBinds();
        loadAllKeyPressBinds();
        loadAllCustomCommands();
        watchEndClientTick();
    }

    // ----------------------------- Shop Functions -----------------------------
    public static int SCREENS_DELAY = 100;
    public static int FREEZE_DELAY = 200;
    public static int MAX_SCREEN_DELAY = 2000;

    public static int SLOT_DELAY = 50;
    public static int MAX_SLOT_DELAY = 2000;

    public static int TRADE_DELAY = 0;

    public static void Sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error("Thread interrupted", e);
            e.printStackTrace();
            Thread.currentThread().interrupt();
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
        waitForScreenChange();
    }

    // ----------------------------- Clicks and Slots -----------------------------
    public static Slot getSlotByIndex(int index) {
        if(client.currentScreen == null) return null;
        return ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots.get(index);
    }

    public static int getIndexOfItemName(String name) {
        if (client.currentScreen == null) return -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        if(slots == null) return -1;
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
                SlotActionType.PICKUP,
                client.player
        );
    }


    // ----------------------------- Trades -----------------------------
    public static int calcNumberOfClicks(TradeOffer offer){
        //SlotOperations.showAllSlots(null);
        //SlotOperations.showAllTrades();
        int firstItemAmount = offer.getAdjustedFirstBuyItem().getCount();
        int secondItemAmount = offer.getSecondBuyItem().getCount();
        int resultAmount = offer.getSellItem().getCount();
        String firstItemName = NameConverter.offerNamesToInventoryNames(offer.getAdjustedFirstBuyItem().getName().getString());
        String secondItemName = NameConverter.offerNamesToInventoryNames(offer.getSecondBuyItem().getName().getString());
        String resultName = NameConverter.offerNamesToInventoryNames(offer.getSellItem().getName().getString());


        // calculate total amount of items of the first and second item
        int totalAmountOfFirstItem = SlotOperations.countTotalElementAmount(TradeInventoryIndexes.TOTAL_INVENTORY, firstItemName);
        DEBUG.Shop("Total amount of " + firstItemName + " is " + totalAmountOfFirstItem);
        int totalAmountOfSecondItem = SlotOperations.countTotalElementAmount(TradeInventoryIndexes.TOTAL_INVENTORY, secondItemName);
        DEBUG.Shop("Total amount of " + secondItemName + " is " + totalAmountOfSecondItem);
        if(firstItemName == secondItemName) {
            totalAmountOfSecondItem = 0;
            firstItemAmount += secondItemAmount;
        }
        int numberOfClicks = 0;
        int numberOfEmptySlots = SlotOperations.countEmptySlots(TradeInventoryIndexes.TOTAL_INVENTORY);
        int totalInputWaste = 0;
        int totalOutputAmount = 0;

        // Debug before calculations
        DEBUG.Shop("--------Before Clicks---------");
        DEBUG.Shop("First Item: " + firstItemName + " x " + firstItemAmount);
        DEBUG.Shop("Second Item: " + secondItemName + " x " + secondItemAmount);
        DEBUG.Shop("Result: " + resultName + " x " + resultAmount);
        DEBUG.Shop("Total Amount of First Item: " + totalAmountOfFirstItem);
        DEBUG.Shop("Total Amount of Second Item: " + totalAmountOfSecondItem);
        DEBUG.Shop("Number of Empty Slots: " + numberOfEmptySlots);
        DEBUG.Shop("--------...---------");





        while(totalAmountOfFirstItem >= firstItemAmount){
                int inputWeHave = Math.min(totalAmountOfFirstItem - totalOutputAmount%firstItemAmount, 64 - 64%firstItemAmount);
                int outPutWeGet = (inputWeHave / firstItemAmount) * resultAmount;
                totalAmountOfFirstItem -= inputWeHave;

                totalInputWaste += inputWeHave;
                totalOutputAmount += outPutWeGet;

                if(totalInputWaste >= 64) {
                    totalInputWaste-= 64;
                    numberOfEmptySlots++;
                }

                if(Math.ceil(totalOutputAmount/64.0) < numberOfEmptySlots) numberOfClicks++;
                else break;
        }

        // Debug after calculations
        DEBUG.Shop("Total Amount of First Item: " + totalAmountOfFirstItem);
        DEBUG.Shop("Total Amount of Second Item: " + totalAmountOfSecondItem);
        DEBUG.Shop("Total Input Waste: " + totalInputWaste + " of " + firstItemName);
        DEBUG.Shop("Total Output Amount: " + totalOutputAmount + " of " + resultName);
        DEBUG.Shop("Number of Clicks: " + numberOfClicks);
        DEBUG.Shop("Number of Empty Slots: " + numberOfEmptySlots);
        DEBUG.Shop("--------END---------");



        return numberOfClicks;
    }

    public static void makeTrade(int offerIndex) {
        for(int i = 0; !(client.currentScreen instanceof MerchantScreen) && i < MAX_SCREEN_DELAY; i+=SCREENS_DELAY) Sleep(SCREENS_DELAY);
        if(!(client.currentScreen instanceof MerchantScreen)) return;
        TradeOffer offer = ((MerchantScreen) client.currentScreen).getScreenHandler().getRecipes().get(offerIndex);
        int numberOfClicks = calcNumberOfClicks(offer);
        for(int i = 0; i < numberOfClicks; i++){
            client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));
            for(int j = 0; SlotOperations.isEmptySlot(TradeInventoryIndexes.FIRST_ITEM_SLOT) && j < MAX_SLOT_DELAY; j+= SLOT_DELAY) Sleep(SLOT_DELAY);
            SlotClicker.slotShiftLeftClick(TradeInventoryIndexes.RESULT_SLOT);
            //Sleep(TRADE_DELAY);
        }
    }

    // ----------------------------- Helper Functions -----------------------------
    private static void sendCommand(String command) {
        if(client.player == null) return;
        client.player.networkHandler.sendChatCommand(command);
    }

    private static void sendChatMessage(String message) {
        if(client.player == null) return;
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
        waitForScreenChange();
    }

    private static void openInventory(String unused) {
        if(client.player == null) return;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new InventoryScreen(client.player));
        });
        waitForScreenChange();
    }

    private static void openPV1(String unused) {
        sendCommand("pv 1");
        waitForScreenChange();
    }

    // BuyItem
    private static void executeTrade(Trade item) {
        List<String> path = item.getPathFromRoot();
        openShop();
        SlotClicker.slotNormalClick(getIndexOfItemName(path.get(1)));
        waitForScreenChange();
        SlotClicker.slotNormalClick(getIndexOfItemName(path.get(2)));
        waitForScreenChange();
        makeTrade(item.TradeIndex - 1);
        closeScreen();
    }

    public static void swapSlots(int slot1, int slot2) {
        if(slot1 == slot2) return;
        if(client.currentScreen == null) return;
        client.interactionManager.clickSlot(0, slot1, 0, SlotActionType.PICKUP, client.player); // Pick up item from slot1
        client.interactionManager.clickSlot(0, slot2, 0, SlotActionType.PICKUP, client.player); // Place item into slot2
        client.interactionManager.clickSlot(0, slot1, 0, SlotActionType.PICKUP, client.player); // Place item into slot1
    }

    public static void openShulkerBox(String name) {
        openInventory("");
        Sleep(1000);
        int slot = SlotOperations.getSlotIndexByName(name);
        //LOGGER.info("Found " + name + " at slot " + slot);
        if (slot == -1) return;
        SlotClicker.slotRightClick(slot);
        waitForScreenChange();
    }

    public static void takeElementsFromShulker(String name, int amount, String shulkerName) {
        openShulkerBox(shulkerName);
        Sleep(1000);

        SlotOperations.sendAmountFromSourceToTarget(ShulkerInventoryIndexes.SHULKER_BOX_INDEXES, ShulkerInventoryIndexes.TOTAL_INVENTORY_INDEXES, name, amount);
        if(true) return;

        int slot = getIndexOfItemName(name);

        // count number of elements in this slot
        int elementsInSlot = getSlotByIndex(slot).getStack().getCount();

        ////LOGGER.info("Found " + name + " at slot " + slot);
        if (slot == -1) return;

        SlotClicker.slotPickAll(slot);

        Sleep(1000);

        // Get an empty slot in the inventory
        int emptySlot = -1;
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        for (int indx : ShulkerInventoryIndexes.MAIN_INVENTORY_INDEXES) {
            if (slots.get(indx).getStack().isEmpty()) {
                emptySlot = indx;
                break;
            }
        }

        if (emptySlot == -1) {
            //LOGGER.info("No empty slot found.");
            return;
        }

        //LOGGER.info("Found empty slot at " + emptySlot);


        // Right-click (mouse button 1) on the empty slot the specified number of times
        for (int i = 0; i < amount; i++) {
            SlotClicker.slotRightClick(emptySlot);
            Sleep(50); // Optional: Add a slight delay between clicks
        }

        // Place the remaining items back into the original slot (if any are left)
        SlotClicker.slotRightClick(slot);

        //LOGGER.info("Moved " + amount + " " + name + " to slot " + emptySlot);
    }

    public static void farmRawGold(){
        for(int  i = 0; i < 1; i++){
            openShulkerBox("Shulker");
            Sleep(1000);
            SlotOperations.takeItem("Raw Gold", 90, "Shulker");
            Sleep(1000);
            closeScreen();

            executeTrade(Market.rawGoldToDiamond_t);
            executeTrade(Market.diamondToGoldNugget_t);
            executeTrade(Market.goldNuggetToEmerald_t);
            executeTrade(Market.emeraldToRawGold_t);

            openShulkerBox("Shulker");
            Sleep(1000);
            SlotOperations.sendItem("Raw Gold", 1000, "Shulker");
            Sleep(1000);
            closeScreen();
        }
    }

    public static Thread thread;
//    public static  Thread testFarm(){
//        Thread thread = new Thread(() -> {
//            farmRawGold();
//        });
//        thread.start();
//        return thread;
//    }
//
private static void testFunction(String unused) {
    if (thread != null && thread.isAlive()) {
        //LOGGER.info("A previous instance of the thread is still running.");
        return;
    }

    thread = new Thread(() -> {
        farmRawGold();
        //executeTrade(Market.rawGoldToDiamond_t);
        //executeTrade(Market.diamondToRawGold_t);
    });

    thread.start();
}



}


