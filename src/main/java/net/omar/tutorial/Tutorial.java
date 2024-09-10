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
import net.omar.tutorial.GUI.FarmScreen;
import net.omar.tutorial.GUI.GearScreen;
import net.omar.tutorial.GUI.SimpleButtonScreen;
import net.omar.tutorial.Inventory.SlotClicker;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.Managers.TradeManager;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Trade;
import net.omar.tutorial.classes.TreeNode;
import net.omar.tutorial.indexes.Indexes;
import net.omar.tutorial.indexes.Market;
import net.omar.tutorial.indexes.ShulkerBoxStorage;
import net.omar.tutorial.last.InventorySaver;
import net.omar.tutorial.last.MyInventory;
import net.omar.tutorial.last.MyPV;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static net.omar.tutorial.Inventory.SlotOperations.*;
import static net.omar.tutorial.Inventory.SlotOperations.forceCompleteItemsToShulkers;
import static net.omar.tutorial.Managers.TradeManager.executeTrade;
import static net.omar.tutorial.classes.TreeNode.shortPathFromItemToItem;


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
        registerKeyBinding("Random Message", "Chat", GLFW.GLFW_KEY_R, Tutorial::sendRandomChatMessage);
        registerKeyBinding("Open Shop", "Farm", GLFW.GLFW_KEY_KP_MULTIPLY, TradeManager::openShop);
        registerKeyBinding("Open PV", "Farm", GLFW.GLFW_KEY_SLASH, Tutorial::openPV1);
        // make capslock keybinding
        registerKeyBinding("Farm", "Debug", GLFW.GLFW_KEY_Z, Tutorial::openFarmScreen);
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
                        DEBUG.Error("Thread interrupted" + e.getMessage());
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
                            DEBUG.Error("Thread interrupted" + e.getMessage());
                            LOGGER.error("Thread interrupted", e);
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
        Sleep(FREEZE_DELAY);
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
        InventorySaver.Inventory(MyInventory.NAME).update("Open Inventory");
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
            LOGGER.error("Shulker Box: " + name + " not found");
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


    // -----------------------------  Farming Functions -----------------------------
    public static void farmMaterialIntoShulker(String outputItem, int numberOfShulkers) {
        if (!outputItem.equals("Raw Gold") && !outputItem.equals("Gold Block") && !outputItem.equals("Gold Nugget") && !outputItem.equals("Gold Ingot"))
            return;
        if (numberOfShulkers < 1) return;
        String cycleItem = "Raw Gold";
        List<Trade> cycleTrades = new ArrayList<>(TreeNode.farmPathFromItem(cycleItem));

        Trade lastCycleTrade = cycleTrades.remove(cycleTrades.size() - 1);
        Trade lastOutputTrade = TreeNode.shortPathFromItemToItem(lastCycleTrade.firstItemName, outputItem).get(0);

        String shulker = ShulkerBoxStorage.getBoxNameForItem(lastOutputTrade);
        Trade shulkerTrade = ShulkerBoxStorage.getTradeFoShulkerBox(shulker);

        forceCompleteItemsToInventory(cycleItem, amountToCompleteInventory(cycleItem, TradeManager.calcMaxTradeInputForInventory(cycleTrades)));

        int shulkerFarmedCount = 0;

        for (int i = 0; i < 999999; i++) {

            int cycleInput = TradeManager.calcMaxTradeInputForInventory(cycleTrades);
            takeItems(Map.of(cycleItem, amountToCompleteInventory(cycleItem, cycleInput)), shulker, true);

            boolean isShulkerFull = InventorySaver.Shulker(shulker).filledSlots > 25;
            if (isShulkerFull) {
                sendItems(Map.of(shulker, 1), MyPV.PV1, true);
                shulkerFarmedCount++;
                if (shulkerFarmedCount >= numberOfShulkers) return;
            }

            boolean isShulkerExist = InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(shulker) > 0;
            if (!isShulkerExist) executeTrade(List.of(Triple.of(shulkerTrade, 0, 1)));

            // Execute trade for each item in cycleTrades
            for (Trade trade : cycleTrades)
                executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int itemsInShulker = InventorySaver.Shulker(shulker).getItemCountByName(cycleItem);
            if (itemsInShulker > cycleInput) {
                executeTrade(List.of(Triple.of(lastOutputTrade, 99999, 0)));
            } else {
                int preLastInput = TradeManager.calcInputforTradeOutput(cycleInput, List.of(lastCycleTrade));
                int clicks = preLastInput / lastCycleTrade.firstItemAmount;
                executeTrade(
                        List.of(Triple.of(lastCycleTrade, clicks, 0),
                                Triple.of(lastOutputTrade, 99999, 0),
                                Triple.of(lastCycleTrade, 10, 0))
                );
            }

            sendItems(new LinkedHashMap<>(Map.of((outputItem.equals(cycleItem) ? "ZZZZZZZ" : outputItem), 1000, cycleItem, 1000)), shulker, true);
        }
    }


    public static void farmMaterialIntoPV(String outputItem, int amountNeeded) {
        // Requirements : Player should have at least 16 of the output item in his PV
        if (!outputItem.equals("Raw Gold") && !outputItem.equals("Gold Block") && !outputItem.equals("Gold Nugget") && !outputItem.equals("Gold Ingot"))
            return;
        if (amountNeeded < 1) return;

        String cycleItem = "Raw Gold";
        List<Trade> cycleTrades = new ArrayList<>(TreeNode.farmPathFromItem(cycleItem));

        Trade lastCycleTrade = cycleTrades.remove(cycleTrades.size() - 1);
        Trade lastOutputTrade = TreeNode.shortPathFromItemToItem(lastCycleTrade.firstItemName, outputItem).get(0);


        forceCompleteItemsToInventory(cycleItem, amountToCompleteInventory(cycleItem, TradeManager.calcMaxTradeInputForInventory(cycleTrades)));

        int originalPVCycleItems = InventorySaver.PV(MyPV.PV1).getItemCountByName(cycleItem);
        sendItems(Map.of(cycleItem, 1000, (outputItem.equals(cycleItem) ? "ZZZZZZZ" : outputItem), 1000), MyPV.PV1, true);

        while (amountNeeded > 0) {
            int cycleInput = TradeManager.calcMaxTradeInputForInventory(cycleTrades);
            takeItems(Map.of(cycleItem, amountToCompleteInventory(cycleItem, cycleInput)), MyPV.PV1, true);

            for (Trade trade : cycleTrades)
                executeTrade(List.of(Triple.of(trade, 99999, 0)));

            int itemsInPV = InventorySaver.PV(MyPV.PV1).getItemCountByName(cycleItem);
            if (itemsInPV - originalPVCycleItems > cycleInput) {
                executeTrade(List.of(Triple.of(lastOutputTrade, 99999, 0)));
            } else {
                int preLastInput = TradeManager.calcInputforTradeOutput(cycleInput, List.of(lastCycleTrade));
                int clicks = preLastInput / lastCycleTrade.firstItemAmount;
                executeTrade(
                        List.of(Triple.of(lastCycleTrade, clicks, 0),
                                Triple.of(lastOutputTrade, 99999, 0),
                                Triple.of(lastCycleTrade, 10, 0))
                );
            }

            if (cycleItem.equals(outputItem))
                amountNeeded -= InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(outputItem) - cycleInput;
            else
                amountNeeded -= InventorySaver.Inventory(MyInventory.NAME).getItemCountByName(outputItem);

            sendItems(Map.of(cycleItem, 1000, (outputItem.equals(cycleItem) ? "ZZZZZZZ" : outputItem), 1000), MyPV.PV1, true);
        }
    }


    public static void buyFullArmors(String unused) {
//        List<Triple<Trade, Integer, Integer>> trades = new ArrayList<>();
//
////        for(Trade trade : Market.woodenSwords_P2.trades) {
////            trades.add(Triple.of(trade, 0, 10));
////        }
//
//        trades.add(Triple.of(Market.rawgoldToCobweb_t, 1, 3));
//        //trades.add(Triple.of(Market.rawgoldToCobweb_t, 0, 4));
//        executeTrade(trades);
//        //buyItem(Market.swords_P1, 1);
//        //getMaterialAndBuyItem(Market.elytra_P2, 1);
//        //openGearScreen("");
//        //getMaterialAndBuyItem(Market.pickaxes_P1, 1);
//
//        if(true) return;
//        getMaterialAndBuyItem(Market.goldBlockToTotemofUndying_t, 1);
//        if (true) return;
//        forceCompleteItemsToShulkers(Map.of("Totem", 2));
//        if (true) return;
//        getMaterialAndBuyItem(Market.goldBlockToTotemofUndying_t, 1);
//
//        if (true) return;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new GearScreen(MinecraftClient.getInstance().currentScreen));
        });
        if (true) return;
        ;
        buyItem(Market.swords_P1, 4);
        if (true) return;
        farmMaterialIntoShulker("Gold Nugget", 1);

        if (true) return;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new FarmScreen(MinecraftClient.getInstance().currentScreen));
        });
        //farmMaterialIntoShulker("Gold Nugget", 3);
        //farmMaterialIntoPV("Gold Block", 128);
        //getMaterialAndBuyItem(Market.goldIngotToBowVII_t, 1, "shulker", "inventory");
        // set screen SimpleButtonScreen
        //forceCompleteItemsToInventory("Raw Gold", 100);
        if (true) return;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new SimpleButtonScreen(MinecraftClient.getInstance().currentScreen));
        });
        if (true) return;
        //getMaterialAndBuyItem(Market.goldNuggetToArrow_t, 20, "shulker", "inventory");
//        buyItem(Market.swords_P1, 4);
//        farmAnyThing("Gold Block", 3);
//        farmAnyThing("Gold Ingot", 2);
//        farmAnyThing("Gold Nugget", 2);
//        farmAnyThing("Raw Gold", 1);

        if (true) return;
        TreeNode mainItem = Market.swords_P1;
        openPV1("");
        closeScreen();

        Map<String, Integer> materialNeeded = TradeManager.getMaterialNeeded(mainItem);
        Map<String, Integer> transferMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet()) transferMap.put(entry.getKey(), 9999);
        sendItems(transferMap, "pv", true);
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet()) {
            String itemName = entry.getKey();
            int itemAmount = entry.getValue();
            int amountNeeded = itemAmount - InventorySaver.PV("pv 1").getItemCountByName(itemName);
            while (amountNeeded > 0) {
                //DEBUG.Store("Amount Needed: " + amountNeeded);
                List<Trade> tradePath = shortPathFromItemToItem("Raw Gold", itemName);
                int emptySlots = InventorySaver.Shulker("Shulker").emptySlots;
                int rawGoldInput = Math.min(TradeManager.calcMaxTradeInputForInventory(tradePath), TradeManager.calcInputforTradeOutput(amountNeeded, tradePath));
                //DEBUG.Store("Raw Gold Needed: " + rawGoldInput);
                takeItems(Map.of("", 0), "Shulker", true);
                if (InventorySaver.Shulker("Shulker").filledSlots == 0) {
                    dropItem("Shulker");
                    takeItems(Map.of("Shulker", 1), "pv", true);
                }
                int rawGoldWeHave = InventorySaver.Inventory(MyInventory.NAME).getItemCountByName("Raw Gold");
                takeItems(Map.of("Raw Gold", rawGoldInput - rawGoldWeHave), "Shulker", true);
//                DEBUG.Store("Trade path Size: "  + tradePath.size());
                for (Trade trade : tradePath)
                    executeTrade(List.of(Triple.of(trade, 999999, 0)));
                sendItems(Map.of(itemName, 9999), "pv", true);
                amountNeeded = itemAmount - InventorySaver.PV("PV 1").getItemCountByName(itemName);
            }
        }

        takeItems(materialNeeded, "pv", true);

        // lop on armors_p1 and execute all it's trades:
        for (TreeNode child : mainItem.children) {
            List<Triple<Trade, Integer, Integer>> list = new ArrayList<>();
            for (Trade trade : child.trades) {
                list.add(Triple.of(trade, 0, 1));
            }
            executeTrade(list);
        }
    }

    public static void buyItem(Trade trade, int normalClicks) {
        List<Triple<Trade, Integer, Integer>> trades = new ArrayList<>();
        trades = List.of(Triple.of(trade, 0, normalClicks));
        executeTrade(trades);
    }

    public static void buyItem(Trade trade, int shiftClicks, int normalClicks) {
        List<Triple<Trade, Integer, Integer>> trades = new ArrayList<>();
        trades = List.of(Triple.of(trade, shiftClicks, normalClicks));
        executeTrade(trades);
    }

    public static void buyItem(TreeNode node, int normalClicks) {
        if (!node.trades.isEmpty()) {
            List<Triple<Trade, Integer, Integer>> trades = new ArrayList<>();
            for (Trade trade : node.trades)
                trades.add(Triple.of(trade, 0, normalClicks));
            executeTrade(trades);
        } else {
            for (TreeNode child : node.children)
                buyItem(child, normalClicks);
        }
    }


    public static boolean getMaterialAndBuyItem(Trade trade, int count) {
        if(count < 1) return false;
        if(count > 10) {
            for(int i = 0; getMaterialAndBuyItem(trade, 1) && i < count; i++);
            return true;
        }

        DEBUG.Shulker("Trade: " + trade.toString() + " Count: " + count);
        Map<String, Integer> materialNeeded = TradeManager.getMaterialNeeded(trade);
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet())
            materialNeeded.put(entry.getKey(), entry.getValue() * count);

        if (!forceCompleteItemsToInventory(materialNeeded)) {
            forceCompleteItemsToShulkers(materialNeeded);
            return false;
        }

        buyItem(trade, count);

        Map<String, Integer> outputMaterial = new HashMap<>();
        outputMaterial.put(trade.resultName, trade.resultAmount * count);
        forceCompleteItemsToShulkers(outputMaterial);
        return true;
    }

    public static boolean getMaterialAndBuyItem(TreeNode node, int count) {
        if(count < 1) return false;
        if(count > 10) {
            for(int i = 0; getMaterialAndBuyItem(node, 1) && i < count; i++);
            return true;
        }
        Map<String, Integer> materialNeeded = TradeManager.getMaterialNeeded(node);
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet())
            materialNeeded.put(entry.getKey(), entry.getValue() * count);

        DEBUG.Shulker("Material Needed: " + materialNeeded.toString());

        if (!forceCompleteItemsToInventory(materialNeeded)) {
            forceCompleteItemsToShulkers(materialNeeded);
            return false;
        }

        buyItem(node, count);

        Map<String, Integer> outputMaterial = new HashMap<>();

        if (!node.children.isEmpty())
            for (TreeNode child : node.children) for (Trade trade : child.trades)
                    outputMaterial.put(trade.resultName, trade.resultAmount * count);
        else if (!node.trades.isEmpty())
            for (Trade trade : node.trades)
                outputMaterial.put(trade.resultName, trade.resultAmount * count);

        DEBUG.Shulker("Output Material: " + outputMaterial.toString());
        forceCompleteItemsToShulkers(outputMaterial);
        return true;
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


