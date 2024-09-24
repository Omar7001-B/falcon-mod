package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.omar.tutorial.GUI.*;
import net.omar.tutorial.Managers.*;
import net.omar.tutorial.Vaults.InventorySaver;
import net.omar.tutorial.Vaults.MyInventory;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;


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
//        registerKeyBinding("Random Message", "Chat", GLFW.GLFW_KEY_R, Tutorial::sendRandomChatMessage);
        registerKeyBinding("Falcon Farm", "Falcon", GLFW.GLFW_KEY_Z, Tutorial::openFalconFarmrScreen);
        registerKeyBinding("Shop", "Falcon", GLFW.GLFW_KEY_KP_MULTIPLY, Trading::openShop);
        registerKeyBinding("PV", "Falcon", GLFW.GLFW_KEY_KP_DIVIDE, Screening::openPV1);
        // make capslock keybinding
//        registerKeyBinding("Testing Armopr", "Debug", GLFW.GLFW_KEY_X, Tutorial::buyFullArmors);

    }

    public void loadAllKeyPressBinds() {
        //registerKeyPressBinding(GLFW.GLFW_KEY_X, (String s) -> SlotOperations.showAllSlots(null));
//        registerKeyPressBinding(GLFW.GLFW_KEY_Y, (String s) -> {
//            LOGGER.info("Y key pressed");
//            TradeManager.getMaterialNeeded(Market.armors_P1);
//            TradeManager.getMaterialNeeded(Market.swords_P1);
//            TradeManager.getMaterialNeeded(Market.pickaxes_P1);
//            TradeManager.getMaterialNeeded(Market.axes_P1);
//        });
    }


    public void loadAllCustomCommands() {
//        registerCustomCommand("!random", Tutorial::sendRandomChatMessage);
//        registerCustomCommand("!shop", TradeManager::openShop);
//        registerCustomCommand("!pv", Tutorial::openPV1);
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
                        Debugging.Error("Thread interrupted" + e.getMessage());
                        LOGGER.error("Thread interrupted", e);
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
                            Debugging.Error("Thread interrupted" + e.getMessage());
                            LOGGER.error("Thread interrupted", e);
                            Thread.currentThread().interrupt();
                        }
                    });
                    thread.start();
                }
            });
            // Log the current screen
            Debugging.LogScreenChange(client.currentScreen == null ? "null" : client.currentScreen.toString());
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
                    client.scheduleStop();
                    thread.interrupt();
                    LOGGER.info("Thread interrupted");
                }
            }
        });
    }

    @Override
    public void onInitialize() {
        Validating.initializeValidation(MinecraftClient.getInstance().getSession().getUsername());
        loadChatEvents();
        loadAllKeyBinds();
        loadAllKeyPressBinds();
        loadAllCustomCommands();
        watchEndClientTick();
    }

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
    public static void updateInevntoryFromAnyScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null) return;
        if (client.currentScreen instanceof InventoryScreen)
            InventorySaver.Inventory(MyInventory.NAME).update("Inventory Screen");
        if (client.currentScreen instanceof GenericContainerScreen)
            InventorySaver.Inventory(MyInventory.NAME).updateFromPV();
        if (client.currentScreen instanceof ShulkerBoxScreen)
            InventorySaver.Inventory(MyInventory.NAME).updateFromShulker();
    }

    public static final Map<Integer, String> slotStates = new HashMap<>();

    public static void waitForSlotChange(int index) {
        String oldState = slotStates.getOrDefault(index, "");
        for (int i = 0; i < MAX_SLOT_DELAY; i += SLOT_DELAY) {
            Sleep(SLOT_DELAY);
            String currentState = Slotting.isEmptySlot(index) ? "empty" : Slotting.getElementAmountByIndex(index) + "x" + Slotting.getSlotNameByIndex(index);
            if (!currentState.equals(oldState)) {
                Debugging.Shop("Old State: " + oldState + " Current State: " + currentState + " Changed  ✅" + " at index: " + index + " in " + i + "ms");
                slotStates.put(index, currentState);
                return;
            }
        }

        Debugging.Shop("Old State: " + oldState + " Current State: " + slotStates.getOrDefault(index, "") + " Not Changed ❌" + " at index: " + index);
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


    //    public static void dropItem(String itemName) {
//        openInventory("");
//        int slot = SlotOperations.getSlotIndexContainsName(itemName);
//        if (slot == -1) return;
//        SlotClicker.slotDropOne(slot);
//        closeScreen();
//    }



    public static void openFalconFarmrScreen(String unused) {
        if(Validating.isUserValid && Validating.isModUpToDate && Validating.getHoursLeft() > 0){
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(new MainScreen(MinecraftClient.getInstance().currentScreen));
            });
        }
        else {
            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(new RestrictedScreen(MinecraftClient.getInstance().currentScreen));
            });
        }
    }

    public static void openFarmScreen(String unused) {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new FarmScreen(MinecraftClient.getInstance().currentScreen));
        });
    }

    public static void openGearScreen(String unused) {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new GearScreen(MinecraftClient.getInstance().currentScreen));
        });
    }
}


// Comment Testaa
// Testgit comment