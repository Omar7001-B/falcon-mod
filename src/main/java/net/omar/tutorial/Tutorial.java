package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.omar.tutorial.Inventory.SlotClicker;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.Managers.TradeManager;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Trade;
import net.omar.tutorial.classes.TreeNode;
import net.omar.tutorial.indexes.Market;
import net.omar.tutorial.last.InventorySaver;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static net.omar.tutorial.Inventory.SlotOperations.sendItems;
import static net.omar.tutorial.Inventory.SlotOperations.takeItems;
import static net.omar.tutorial.classes.TreeNode.pathFromItemToItem;


public class Tutorial implements ModInitializer {

    // Declare the client
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static String currentScreenString = "";
    public static final String MOD_ID = "tutorial";

    // Logger for console and log file
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Thread thread;

    // ----------------------------- Maps ------------------------------
    private final Map<KeyBinding, Consumer<String>> keyBindings = new HashMap<>();
    private final Map<Integer, Consumer<String>> keyPressBindings = new HashMap<>();
    private final Map<String, Consumer<String>> customCommands = new HashMap<>();

    public void loadAllKeyBinds() {
        registerKeyBinding("Random Message", "Chat", GLFW.GLFW_KEY_R, Tutorial::farmGoldBlock);
        registerKeyBinding("Open Shop", "Farm", GLFW.GLFW_KEY_KP_MULTIPLY, TradeManager::openShop);
        registerKeyBinding("Open PV", "Farm", GLFW.GLFW_KEY_SLASH, Tutorial::openPV1);
        // make capslock keybinding
        registerKeyBinding("Testing Function", "Debug", GLFW.GLFW_KEY_Z, Tutorial::farmRawGold);
        registerKeyBinding("Testing Armopr", "Debug", GLFW.GLFW_KEY_X, Tutorial::buyFullArmors);

    }

    public void loadAllKeyPressBinds() {
        //registerKeyPressBinding(GLFW.GLFW_KEY_X, (String s) -> SlotOperations.showAllSlots(null));
        registerKeyPressBinding(GLFW.GLFW_KEY_Y, (String s) -> {
            LOGGER.info("Y key pressed");
            TradeManager.getMaterialNeeded(Market.armors_P1);
            TradeManager.getMaterialNeeded(Market.swords_P1);
            TradeManager.getMaterialNeeded(Market.pickaxes_P1);
            TradeManager.getMaterialNeeded(Market.axes_P1);
        });
    }


    public void loadAllCustomCommands() {
        registerCustomCommand("!random", Tutorial::sendRandomChatMessage);
        registerCustomCommand("!shop", TradeManager::openShop);
        registerCustomCommand("!pv", Tutorial::openPV1);
        //registerCustomCommand("!test", Tutorial::testFunction);
    }

    private void registerKeyBinding(String translationKey, String categoryName, int keyCode, Consumer<String> action) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey, InputUtil.Type.KEYSYM, keyCode, categoryName));
        keyBindings.put(keyBinding, action);
    }

    private void registerKeyPressBinding(int keyCode, Consumer<String> action) {
        keyPressBindings.put(keyCode, action);
    }

    private void registerCustomCommand(String command, Consumer<String> action) {
        customCommands.put(command, action);
    }

    // ----------------------------- Chat Message Events -----------------------------
    private boolean onChatMessageSent(String message) {
        LOGGER.info("Received message: " + message);
        for (Map.Entry<String, Consumer<String>> entry : customCommands.entrySet()) {
            if (message.startsWith(entry.getKey())) {
                if (thread != null && thread.isAlive()) return false;
                thread = new Thread(() -> {
                    try {
                        entry.getValue().accept("");
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                });
                thread.start();
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
                    int shiftKeyCode = GLFW.GLFW_KEY_LEFT_SHIFT;
                    if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), shiftKeyCode)) {
                        LOGGER.info("Shift key pressed");
                        thread = new Thread(() -> {
                            try {
                                action.accept("");
                            } catch (Exception e) {
                                Thread.currentThread().interrupt();
                            }
                        });
                        thread.start();
                        return;
                    }
                    if (thread != null && thread.isAlive()) {
                        LOGGER.info("Thread is alive");
                        return;
                    }
                    thread = new Thread(() -> {
                        try {
                            action.accept("");
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    thread.start();
                }
            });
            // Log the current screen
            DEBUG.LogScreenChange(client.currentScreen == null ? "null" : client.currentScreen.toString());
            /*
            String screen = client.currentScreen == null ? "null" : client.currentScreen.toString();
            if (!currentScreenString.equals(screen)) {
                LOGGER.info("Screen : " + "[" + currentScreenString + "] -> [" + screen + "]");

                currentScreenString = screen;
            }
            */


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
                LOGGER.info("Escape key pressed");
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    LOGGER.info("Thread interrupted");
                }
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
    public static int MAX_SCREEN_DELAY = 10000;

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
    public static boolean waitForScreenChange() {
        String oldScreen = currentScreenString;
        boolean changed = false;
        for (int i = 0; i < MAX_SCREEN_DELAY; i += SCREENS_DELAY) {
            Sleep(SCREENS_DELAY);
            currentScreenString = client.currentScreen == null ? "null" : client.currentScreen.toString();
            if (!oldScreen.equals(currentScreenString)) {
                changed = true;
                break;
            }
        }
        Sleep(FREEZE_DELAY);
        return changed;
    }

    public static final Map<Integer, String> slotStates = new HashMap<>();

    public static void waitForSlotChange(int index) {
        String oldState = slotStates.getOrDefault(index, "");
        for (int i = 0; i < MAX_SLOT_DELAY; i += SLOT_DELAY) {
            Sleep(SLOT_DELAY);
            String currentState = SlotOperations.isEmptySlot(index) ? "empty" : SlotOperations.getElementAmountByIndex(index) + "x" + SlotOperations.getSlotNameByIndex(index);
            if (!currentState.equals(oldState)) {
                DEBUG.Shop("Old State: " + oldState + " Current State: " + currentState + " Changed  ✅" + " at index: " + index + " in " + i + "ms");
                slotStates.put(index, currentState);
                return;
            }
        }

        DEBUG.Shop("Old State: " + oldState + " Current State: " + slotStates.getOrDefault(index, "") + " Not Changed ❌" + " at index: " + index);
    }

    public static boolean closeScreen() {
        MinecraftClient.getInstance().execute(() -> {
            if (MinecraftClient.getInstance().currentScreen != null) {
                MinecraftClient.getInstance().currentScreen.close();
            }
        });
        return waitForScreenChange();
    }


    // ----------------------------- Helper Functions -----------------------------
    public static void sendCommand(String command) {
        if (client.player == null) return;
        client.player.networkHandler.sendChatCommand(command);
    }

    public static void sendChatMessage(String message) {
        if (client.player == null) return;
        client.player.networkHandler.sendChatMessage(message);
    }

    public static void sendRandomChatMessage(String unused) {
        String[] messages = {"Hello guys!", "What's up everyone?", "Hey there!", "How's it going?", "Good day, folks!"};
        String randomMessage = messages[new Random().nextInt(messages.length)];
        sendChatMessage(randomMessage);
    }

    public static boolean openInventory(String unused) {
        if (client.player == null) return false;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new InventoryScreen(client.player));
        });
        boolean changed = waitForScreenChange();
        InventorySaver.Inventory("Inventory").update("Open Inventory");
        return changed;
    }

    public static boolean openPV1(String unused) {
        sendCommand("pv 1");
        boolean changed = waitForScreenChange();
        InventorySaver.PV("PV 1").update("Open PV");
        return changed;
    }


    public static boolean openShulkerBox(String name) {
        openInventory("");
        Sleep(1000);
        int slot = SlotOperations.getSlotIndexContainsName(name);
        //LOGGER.info("Found " + name + " at slot " + slot);
        if (slot == -1) {
            closeScreen();
            return false;
        }


        SlotClicker.slotRightClick(slot);
        boolean changed = waitForScreenChange();
        InventorySaver.Shulker(name).update("Open Shulker Box");

        DEBUG.Slots("Shulker Box: " + name + " Changed: " + changed);
        return changed;
    }

    public static void dropItem(String itemName) {
        openInventory("");
        int slot = SlotOperations.getSlotIndexContainsName(itemName);
        if (slot == -1) return;
        SlotClicker.slotDropOne(slot);
        closeScreen();
    }


    public static void farmRawGold(String unused) {
        List<Trade> trades = List.of(Market.rawGoldToDiamond_t, Market.diamondToGoldNugget_t, Market.goldNuggetToEmerald_t, Market.emeraldToRawGold_t);
        for (int i = 0; i < 999999; i++) {
            Map<String, Integer> curInv = InventorySaver.Inventory("Inventory").itemCounts;
            int inputForSlots = TradeManager.calcMaxTradeInputForInventory(curInv, trades);

            takeItems(Map.of("Raw Gold", inputForSlots), "Black Shulker", true);

            if (InventorySaver.Shulker("Black Shulker").emptySlots < 3) {
                sendItems(Map.of("Black Shulker", 1), "pv", true);
                TradeManager.executeTrade(List.of(Triple.of(Market.rawgoldToBlackBox_t, 1, 1)));
            }

            for(Trade trade: trades)
                TradeManager.executeTrade(List.of(Triple.of(trade, 99999, 0)));

            sendItems(Map.of("Raw Gold", 1000), "Black Shulker", true);
        }
    }

    public static void farmGoldBlock(String unused) {
        List<Trade> trades = List.of(Market.rawGoldToDiamond_t, Market.diamondToGoldNugget_t, Market.goldNuggetToEmerald_t);
        for (int i = 0; i < 999999; i++) {
            Map<String, Integer> curInv = InventorySaver.Inventory("Inventory").itemCounts;
            int inputForSlots = TradeManager.calcMaxTradeInputForInventory(curInv, trades);

            takeItems(Map.of("Raw Gold", inputForSlots), "Black Shulker", true);

            if (InventorySaver.Shulker("Black Shulker").emptySlots < 3) {
                sendItems(Map.of("Black Shulker", 1), "pv", true);
                TradeManager.executeTrade(List.of(Triple.of(Market.rawgoldToBlackBox_t, 1, 1)));
            }

            for(Trade trade: trades)
                TradeManager.executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int rawGoldInShulker = InventorySaver.Shulker("Black Shulker").getItemCountByName("Raw Gold");
            if(rawGoldInShulker > inputForSlots){
                TradeManager.executeTrade( List.of( Triple.of(Market.emeraldToGoldBlock_t, 99999, 0) ));
            }
            else {
                int emerled = TradeManager.calcInputforTradeOutput(inputForSlots, List.of(Market.emeraldToRawGold_t));
                int clicks = emerled / 64;
                TradeManager.executeTrade(
                        List.of(Triple.of(Market.emeraldToRawGold_t, clicks, 0),
                                Triple.of(Market.emeraldToGoldBlock_t, 99999, 0),
                                Triple.of(Market.emeraldToRawGold_t, 10, 0)
                        ));
            }
            sendItems(Map.of("Raw Gold", 1000, "Gold Block", 1000), "Black Shulker", true);
        }
    }

    public static void buyFullArmors(String unused) {
        sendItems(Map.of("Black Shulker", 1), "pv", true);
        if(true) return;
        sendItems(Map.of("Raw Gold", 1000), "Black Shulker", true);
        if(true) return;
        sendItems(Map.of("Raw Gold", 1000), "Black Shulker", true);

        if(true) return;
        SlotOperations.forceTakeFromShulker(Map.of("Raw Gold", 200), "Black Shulker");
        //DEBUG.Slots("" +  sendItems(Map.of("Raw Gold", 200), "pv", false));
        //DEBUG.Slots("" +  takeItems(Map.of("Black Shulker", 1), "pv", false));
        if(true) return;

        openInventory("");
        TreeNode mainItem = Market.swords_P1;

        Map<String, Integer> materialNeeded = TradeManager.getMaterialNeeded(mainItem);
        Map<String, Integer> transferMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet()) transferMap.put(entry.getKey(), 9999);
        sendItems(transferMap, "pv", true);
        //DEBUG.Store("Material Needed:" + materialNeeded);
        //DEBUG.Store("Material we have: " + InventorySaver.PV("pv 1").itemCounts);
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet()) {
            String itemName = entry.getKey();
            int itemAmount = entry.getValue();
            int amountNeeded = itemAmount - InventorySaver.PV("pv 1").getItemCountByName(itemName);
            while (amountNeeded > 0) {
                //DEBUG.Store("Amount Needed: " + amountNeeded);
                List<Trade> tradePath = pathFromItemToItem("Raw Gold", itemName);
                int emptySlots = InventorySaver.Shulker("Shulker").emptySlots;
                int rawGoldInput = Math.min(TradeManager.calcMaxTradeInputForInventory(InventorySaver.Inventory("Inventory").itemCounts, tradePath), TradeManager.calcInputforTradeOutput(amountNeeded, tradePath));
                //DEBUG.Store("Raw Gold Needed: " + rawGoldInput);
                takeItems(Map.of("", 0), "Shulker", true);
                if (InventorySaver.Shulker("Shulker").filledSlots == 0){
                    dropItem("Shulker");
                    takeItems(Map.of("Shulker", 1), "pv", true);
                }
                int rawGoldWeHave = InventorySaver.Inventory("Inventory").getItemCountByName("Raw Gold");
                takeItems(Map.of("Raw Gold", rawGoldInput - rawGoldWeHave), "Shulker", true);
//                DEBUG.Store("Trade path Size: "  + tradePath.size());
                for (Trade trade : tradePath)
                    TradeManager.executeTrade(List.of(Triple.of(trade, 999999, 0)));
                sendItems(Map.of(itemName, 9999), "pv", true);
                amountNeeded = itemAmount - InventorySaver.PV("PV 1").getItemCountByName(itemName);
            }
        }

        takeItems(materialNeeded, "pv", true);

        // lop on armors_p1 and execute all it's trades:
        for (TreeNode child : mainItem.children) {
            List<Triple<Trade, Integer, Integer>> list = new ArrayList<>();
            for (Trade trade : child.trades) {
                list.add(Triple.of(trade, 1, 1));
            }
            TradeManager.executeTrade(list);
        }
    }


}


