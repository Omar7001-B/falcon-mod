package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import com.sun.jna.WString;
import com.sun.source.tree.Tree;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
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
import net.omar.tutorial.Inventory.NameConverter;
import net.omar.tutorial.Inventory.SlotClicker;
import net.omar.tutorial.Inventory.SlotOperations;
import net.omar.tutorial.classes.DEBUG;
import net.omar.tutorial.classes.Trade;
import net.omar.tutorial.classes.TreeNode;
import net.omar.tutorial.indexes.Indexes;
import net.omar.tutorial.indexes.Market;
import net.omar.tutorial.last.InventorySaver;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.logging.Log;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static net.omar.tutorial.Inventory.SlotOperations.sendItems;
import static net.omar.tutorial.Inventory.SlotOperations.takeItems;
import static net.omar.tutorial.classes.TreeNode.pathFromItemToItem;


public class Tutorial implements ModInitializer {

    // Declare the client
    private static final MinecraftClient client = MinecraftClient.getInstance();
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
        registerKeyBinding("Open Shop", "Farm", GLFW.GLFW_KEY_KP_MULTIPLY, Tutorial::openShop);
        registerKeyBinding("Open PV", "Farm", GLFW.GLFW_KEY_SLASH, Tutorial::openPV1);
        // make capslock keybinding
        registerKeyBinding("Testing Function", "Debug", GLFW.GLFW_KEY_Z, Tutorial::farmRawGold);
        registerKeyBinding("Testing Armopr", "Debug", GLFW.GLFW_KEY_X, Tutorial::buyFullArmors);

    }

    public void loadAllKeyPressBinds() {
        //registerKeyPressBinding(GLFW.GLFW_KEY_X, (String s) -> SlotOperations.showAllSlots(null));
        registerKeyPressBinding(GLFW.GLFW_KEY_Y, (String s) -> {
            LOGGER.info("Y key pressed");
            getMaterialNeeded(Market.armors_P1);
            getMaterialNeeded(Market.swords_P1);
            getMaterialNeeded(Market.pickaxes_P1);
            getMaterialNeeded(Market.axes_P1);
        });
    }

    public static int calculateInputNeeded(int targetOutput, List<Trade> tradePath) {
        if (tradePath.isEmpty()) return targetOutput;
        int currentOutput = 0;
        int initialInput = 0;
        Map<String, Integer> materialMap = new HashMap<>();

        while (currentOutput < targetOutput) {
            initialInput += tradePath.get(0).firstItemAmount;
            int inputAmount = initialInput;
            materialMap.clear();

            for (int i = 0; i < tradePath.size(); i++) {
                Trade currentTrade = tradePath.get(i);
                int inputNeeded = currentTrade.firstItemAmount + currentTrade.secondItemAmount;
                int outputAmount = currentTrade.resultAmount;
                int tradeMultiplier = inputAmount / inputNeeded;

                materialMap.put(currentTrade.firstItemName, inputAmount % inputNeeded);
                inputAmount = tradeMultiplier * outputAmount;
            }

            currentOutput = inputAmount;
        }

        return initialInput;
    }


    public static int maxInputForSlots(Map<String, Integer> inventory, List<Trade> tradePath) {
        int inventoryMaxSlots = 36;
        int input = tradePath.get(0).firstItemAmount;
        if (tradePath.get(0).secondItemAmount > 0 && tradePath.get(0).firstItemName.equals(tradePath.get(0).secondItemName))
            input += tradePath.get(0).secondItemAmount;
        int maxInput = 0;

        while (true) {
            Map<String, Integer> inv = new HashMap<>(inventory);
            Map<String, Integer> trade = new HashMap<>();

            String firstItem = tradePath.get(0).firstItemName;
            trade.put(firstItem, input);
            inv.put(firstItem, inv.getOrDefault(firstItem, 0) + input);

            int oldSlots = InventorySaver.calculateTotalSlots(inventory);
            int newSlots = InventorySaver.calculateTotalSlots(inv);

//            DEBUG.Store("Input: " + input);
//            DEBUG.Store("Old Slots: " + oldSlots + " New Slots: " + newSlots);
//            DEBUG.Store("Trade Path: " + tradePath);
//            DEBUG.Store("Inventory: " + inv);

            if (newSlots > inventoryMaxSlots) return maxInput;

            for (Trade t : tradePath) {
                int requiredInput = t.firstItemAmount + (t.firstItemName.equals(t.secondItemName) ? t.secondItemAmount : 0);
                int availableInput = trade.getOrDefault(t.firstItemName, 0);
                int producedAmount = (availableInput / requiredInput) * t.resultAmount;
                int remainingInput = availableInput % requiredInput;

                DEBUG.Store(t.firstItemAmount + " " + t.firstItemName + " + " + t.secondItemAmount + " -> " + t.resultAmount + " " + t.resultName);
//                DEBUG.Store("Required Input: " + requiredInput);
//                DEBUG.Store("Available Input: " + availableInput);
//                DEBUG.Store("Produced Amount: " + producedAmount);
//                DEBUG.Store("Remaining Input: " + remainingInput);

                trade.put(t.firstItemName, remainingInput);
                trade.put(t.resultName, inv.getOrDefault(t.resultName, 0) + producedAmount);

                inv.put(t.firstItemName, inv.getOrDefault(t.firstItemName, 0) - availableInput + remainingInput);
                inv.put(t.resultName, inv.getOrDefault(t.resultName, 0) + producedAmount);

//                DEBUG.Store("Inventory: " + inv);
//                DEBUG.Store("Trade: " + trade);

                if (InventorySaver.calculateTotalSlots(inv) > inventoryMaxSlots) return maxInput;
            }

            maxInput = input;
            input += tradePath.get(0).firstItemAmount + tradePath.get(0).secondItemAmount;
        }
    }


    public static Map<String, Integer> getMaterialNeeded(TreeNode item_p) {
        Map<String, Integer> materialNeeded = new HashMap<>();
        // make queue
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(item_p);
        while (!queue.isEmpty()) {
            TreeNode item = queue.poll();
            if (item.children.size() > 0) {
                for (TreeNode child : item.children) queue.add(child);
            }
            if (item.trades.size() > 0) {
                for (Trade trade : item.trades) {
                    //DEBUG.Shop("Trade: " + trade.TradeIndex + " " + trade.firstItemName + " x " + trade.firstItemAmount + " + " + trade.secondItemName + " x " + trade.secondItemAmount + " = " + trade.resultName + " x " + trade.resultAmount);

                    materialNeeded.put(trade.firstItemName, materialNeeded.getOrDefault(trade.firstItemName, 0) + trade.firstItemAmount);

                    /*
                    if(trade.secondItemAmount != 0) {
                        materialNeeded.put(trade.secondItemName, materialNeeded.getOrDefault(trade.secondItemName, 0) + trade.secondItemAmount);
                    }
                     */
                }
            }
        }

        DEBUG.Store("Material Needed: " + materialNeeded);
        return materialNeeded;
    }


    public void loadAllCustomCommands() {
        registerCustomCommand("!random", Tutorial::sendRandomChatMessage);
        registerCustomCommand("!shop", Tutorial::openShop);
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
        for (int i = 0; i < MAX_SCREEN_DELAY; i += SCREENS_DELAY) {
            Sleep(SCREENS_DELAY);
            currentScreenString = client.currentScreen == null ? "null" : client.currentScreen.toString();
            if (!oldScreen.equals(currentScreenString)) break;
        }
        Sleep(FREEZE_DELAY);
    }

    private static final Map<Integer, String> slotStates = new HashMap<>();

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

    public static void closeScreen() {
        MinecraftClient.getInstance().execute(() -> {
            if (MinecraftClient.getInstance().currentScreen != null) {
                MinecraftClient.getInstance().currentScreen.close();
            }
        });
        waitForScreenChange();
    }

    // ----------------------------- Trades -----------------------------
    public static int calcNumberOfClicks(TradeOffer offer, int type) {
        //SlotOperations.showAllSlots(null);

        String firstItemName = NameConverter.offerNamesToInventoryNames(offer.getAdjustedFirstBuyItem().getName().getString());
        String secondItemName = NameConverter.offerNamesToInventoryNames(offer.getSecondBuyItem().getName().getString());
        String resultName = NameConverter.offerNamesToInventoryNames(offer.getSellItem().getName().getString());

        int firstItemAmount = offer.getAdjustedFirstBuyItem().getCount();
        int secondItemAmount = offer.getSecondBuyItem().getCount();
        int resultAmount = offer.getSellItem().getCount();

        int totalFirstItemAmount = SlotOperations.countTotalElementAmount(Indexes.Trade.TOTAL_INVENTORY_WITHOUT_RESULT_SLOT, firstItemName);
        int totalSecondItemAmount = firstItemName.equals(secondItemName) ? 0 :
                SlotOperations.countTotalElementAmount(Indexes.Trade.TOTAL_INVENTORY_WITHOUT_RESULT_SLOT, secondItemName);

        //DEBUG.Shop("Total amount of " + firstItemName + ": " + totalFirstItemAmount);
        //DEBUG.Shop("Total amount of " + secondItemName + ": " + totalSecondItemAmount);

        int stack = 64;
        if (firstItemName.equals(secondItemName)) {
            firstItemAmount += secondItemAmount;
            stack += 64;
        }

        int numberOfClicks = 0;
        int numberOfEmptySlots = SlotOperations.countEmptySlots(Indexes.Trade.TOTAL_INVENTORY);
        int totalInputWaste = 0;
        int totalOutputAmount = 0;
        boolean isStacked = NameConverter.isStackedItem(resultName);
        boolean isUpgrade = !NameConverter.isStackedItem(secondItemName);

//
//        DEBUG.Shop("--------Before Clicks---------");
//        DEBUG.Shop("First Item: " + firstItemName + " x " + firstItemAmount);
//        DEBUG.Shop("Second Item: " + secondItemName + " x " + secondItemAmount);
//        DEBUG.Shop("Result: " + resultName + " x " + resultAmount + " (Stacked: " + isStacked + ")");
//        DEBUG.Shop("--------...---------");


        try {
            while (totalFirstItemAmount >= firstItemAmount) {
                int inputWeHave = type == 1 || isUpgrade ? firstItemAmount :
                        Math.min(totalFirstItemAmount - totalFirstItemAmount % firstItemAmount, stack - stack % firstItemAmount);

                if (inputWeHave == 0) break;

                int outPutWeGet = (inputWeHave / firstItemAmount) * resultAmount;
                totalFirstItemAmount -= inputWeHave;

                totalInputWaste += inputWeHave;
                totalOutputAmount += outPutWeGet;

                while (totalInputWaste >= 64) {
                    totalInputWaste -= 64;
                    numberOfEmptySlots++;
                }

                if ((!isStacked && totalOutputAmount <= numberOfEmptySlots) || (isStacked && Math.ceil(totalOutputAmount / 64.0) <= numberOfEmptySlots))
                    numberOfClicks++;
                else break;

            }
        } catch (Exception e) {
            LOGGER.error("Error in calcNumberOfClicks: " + e.getMessage());
            e.printStackTrace();
        }

//        DEBUG.Shop("Total First Item: " + totalFirstItemAmount);
//        DEBUG.Shop("Total Input Waste: " + totalInputWaste + " of " + firstItemName);
//        DEBUG.Shop("Total Output Amount: " + totalOutputAmount + " of " + resultName);
//        DEBUG.Shop("Number of Clicks: " + numberOfClicks);
//        DEBUG.Shop("Number of Empty Slots: " + numberOfEmptySlots);
//        DEBUG.Shop("--------END---------");

        return numberOfClicks;
    }

    public static boolean isAutomatedTrade;

    public static void makeTrade(int offerIndex, int clicks, int type) {
        for (int i = 0; !(client.currentScreen instanceof MerchantScreen) && i < MAX_SCREEN_DELAY; i += SCREENS_DELAY)
            Sleep(SCREENS_DELAY);
        if (!(client.currentScreen instanceof MerchantScreen)) return;
        slotStates.clear();
        TradeOffer offer = ((MerchantScreen) client.currentScreen).getScreenHandler().getRecipes().get(offerIndex);
        boolean isUpgrade = !NameConverter.isStackedItem(offer.getSecondBuyItem().getName().getString());
        int numberOfClicks = Math.min(clicks, calcNumberOfClicks(offer, type));
        isAutomatedTrade = true;
        for (int i = 0; i < numberOfClicks; i++) {
            client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));
            if (type == 1) {
                SlotClicker.slotNormalClick(Indexes.Trade.RESULT_SLOT);
                Sleep(SLOT_DELAY);
                SlotClicker.slotNormalClick(SlotOperations.getFirstEmptySlot(Indexes.Trade.TOTAL_INVENTORY));
            } else {
                SlotClicker.slotShiftLeftClick(Indexes.Trade.RESULT_SLOT);
            }
        }
        isAutomatedTrade = false;
    }

    // ----------------------------- Helper Functions -----------------------------
    private static void sendCommand(String command) {
        if (client.player == null) return;
        client.player.networkHandler.sendChatCommand(command);
    }

    private static void sendChatMessage(String message) {
        if (client.player == null) return;
        client.player.networkHandler.sendChatMessage(message);
    }

    private static void sendRandomChatMessage(String unused) {
        String[] messages = {"Hello guys!", "What's up everyone?", "Hey there!", "How's it going?", "Good day, folks!"};
        String randomMessage = messages[new Random().nextInt(messages.length)];
        sendChatMessage(randomMessage);
    }

    private static void openShop(String unused) {
        sendCommand("shop");
        waitForScreenChange();
    }

    private static void openInventory(String unused) {
        if (client.player == null) return;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new InventoryScreen(client.player));
        });
        waitForScreenChange();
        InventorySaver.Inventory("Inventory").update("Open Inventory");
    }

    public static void openPV1(String unused) {
        sendCommand("pv 1");
        waitForScreenChange();
        InventorySaver.PV("PV 1").update("Open PV");
    }

    // BuyItem
    private static void executeTrade(List<Triple<Trade, Integer, Integer>> tradeDataList) {
        if (tradeDataList.isEmpty()) return;

        // Extract path from the first item
        Triple<Trade, Integer, Integer> firstEntry = tradeDataList.get(0);
        Trade firstTrade = firstEntry.getLeft();
        List<String> path = firstTrade.getPathFromRoot();

        openShop("");

        SlotClicker.slotNormalClick(SlotOperations.getSlotIndexByName(path.get(1)));
        waitForScreenChange();
        SlotClicker.slotNormalClick(SlotOperations.getSlotIndexByName(path.get(2)));
        waitForScreenChange();

        for (Triple<Trade, Integer, Integer> entry : tradeDataList) {
            //DEBUG.Shop("Making trade: " + entry.getLeft().TradeIndex + " with " + entry.getMiddle() + " clicks");
            Trade trade = entry.getLeft();
            Integer clicks = entry.getMiddle();
            Integer type = entry.getRight();
            if (trade == null) continue;
            makeTrade(trade.TradeIndex - 1, clicks, type);
        }
        closeScreen();
    }


    public static void openShulkerBox(String name) {
        openInventory("");
        Sleep(1000);
        int slot = SlotOperations.getSlotIndexContainsName(name);
        //LOGGER.info("Found " + name + " at slot " + slot);
        if (slot == -1) return;
        SlotClicker.slotRightClick(slot);
        waitForScreenChange();

        // Update the shulker data after opening the shulker box
        // LastShulker.updateShulkerData();
        // LastShulker.showShulkerData();
    }

    public static void dropItem(String itemName) {
        openInventory("");
        int slot = SlotOperations.getSlotIndexContainsName(itemName);
        if (slot == -1) return;
        SlotClicker.slotDropOne(slot);
        closeScreen();
    }


    public static void farmRawGold(String unused) {
        for (int i = 0; i < 999999; i++) {
            takeItems(Map.of("Raw Gold", 90), "shulker");

            if (InventorySaver.Shulker("Shulker").emptySlots < 3) {
                sendItems(Map.of("Shulker", 1), "pv");
                executeTrade(List.of(Triple.of(Market.rawgoldToBlackBox_t, 1, 1)));
            }

            executeTrade(List.of(Triple.of(Market.rawGoldToDiamond_t, 99999, 0)));
            executeTrade(List.of(Triple.of(Market.diamondToGoldNugget_t, 99999, 0)));
            executeTrade(List.of(Triple.of(Market.goldNuggetToEmerald_t, 99999, 0)));
            executeTrade(List.of(Triple.of(Market.emeraldToRawGold_t, 99999, 0)));

            sendItems(Map.of("Raw Gold", 1000), "shulker");
        }
    }

    public static void buyFullArmors(String unused) {
        openInventory("");
        TreeNode mainItem = Market.armors_P1;

        Map<String, Integer> materialNeeded = getMaterialNeeded(mainItem);
        Map<String, Integer> transferMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet()) transferMap.put(entry.getKey(), 9999);
        sendItems(transferMap, "pv");
        DEBUG.Store("Material Needed:" + materialNeeded);
        for (Map.Entry<String, Integer> entry : materialNeeded.entrySet()) {
            String itemName = entry.getKey();
            int itemAmount = entry.getValue();
            int amountNeeded = itemAmount - InventorySaver.PV("pv 1").getItemCountByName(itemName);
            while (amountNeeded > 0) {
                DEBUG.Store("Amount Needed: " + amountNeeded);
                List<Trade> tradePath = pathFromItemToItem("Raw Gold", itemName);
                int emptySlots = InventorySaver.Shulker("Shulker").emptySlots;
                int rawGoldInput = Math.min(maxInputForSlots(InventorySaver.Inventory("Inventory").itemCounts, tradePath), calculateInputNeeded(amountNeeded, tradePath));
                DEBUG.Store("Raw Gold Needed: " + rawGoldInput);
                if (InventorySaver.Shulker("Shulker").filledSlots == 0){
                    dropItem("Shulker");
                    takeItems(Map.of("Shulker", 1), "pv");
                }
                int rawGoldWeHave = InventorySaver.Inventory("Inventory").getItemCountByName("Raw Gold");
                takeItems(Map.of("Raw Gold", rawGoldInput - rawGoldWeHave), "Shulker");
                for (Trade trade : tradePath) executeTrade(List.of(Triple.of(trade, 999999, 0)));
                sendItems(Map.of(itemName, 9999), "pv");
                amountNeeded = itemAmount - InventorySaver.PV("PV 1").getItemCountByName(itemName);
            }
        }

        takeItems(materialNeeded, "pv");

        // lop on armors_p1 and execute all it's trades:
        for (TreeNode child : mainItem.children) {
            List<Triple<Trade, Integer, Integer>> list = new ArrayList<>();
            for (Trade trade : child.trades) {
                list.add(Triple.of(trade, 1, 1));
            }
            executeTrade(list);
        }
    }


}


