package net.omar.tutorial;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import net.omar.tutorial.classes.Conversion;
import net.omar.tutorial.classes.TreeNode;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.List;


public class Tutorial implements ModInitializer {

	// Shop first page
	public static String compressors_P1 = "ᴄᴏᴍᴘʀᴇssᴏʀs";
		public static String compressOre_P2 = "ᴄᴏᴍᴘʀᴇss ᴏʀᴇ";
		public static String decompressOre_P2 = "ᴅᴇᴄᴏᴍᴘʀᴇss ᴏʀᴇ";

	public static String swords_P1 = "sᴡᴏʀᴅs";
		public static String woodenSwords_P2 = "ᴡᴏᴏᴅᴇɴ sᴡᴏʀᴅs";
		public static String stoneSwords_P2 = "sᴛᴏɴᴇ sᴡᴏʀᴅs";
		public static String ironSwords_P2 = "ɪʀᴏɴ sᴡᴏʀᴅs";
		public static String diamondSwords_P2 = "ᴅɪᴀᴍᴏɴᴅ sᴡᴏʀᴅs";
		public static String netheriteSwords_P2 = "ɴᴇᴛʜᴇʀɪᴛᴇ sᴡᴏʀᴅs";


	public static String armors_P1 = "ᴀʀᴍᴏʀs";
		public static String leatherArmors_P2 = "ʟᴇᴀᴛʜᴇʀ ᴀʀᴍᴏʀs";
		public static String ironArmors_P2 = "ɪʀᴏɴ ᴀʀᴍᴏʀs";
		public static String diamondArmors_P2 = "ᴅɪᴀᴍᴏɴᴅ ᴀʀᴍᴏʀs";
		public static String netheriteArmors_P2 = "ɴᴇᴛʜᴇʀɪᴛᴇ ᴀʀᴍᴏʀs";

	public static String pickaxes_P1 = "ᴘɪᴄᴋᴀxᴇs";
		public static String woodenPickaxes_P2 = "ᴡᴏᴏᴅᴇɴ ᴘɪᴄᴋᴀxᴇs";
		public static String stonePickaxes_P2 = "sᴛᴏɴᴇ ᴘɪᴄᴋᴀxᴇs";
		public static String ironPickaxes_P2 = "ɪʀᴏɴ ᴘɪᴄᴋᴀxᴇs";
		public static String diamondPickaxes_P2 = "ᴅɪᴀᴍᴏɴᴅ ᴘɪᴄᴋᴀxᴇs";
		public static String netheritePickaxes_P2 = "ɴᴇᴛʜᴇʀɪᴛᴇ ᴘɪᴄᴋᴀxᴇs";

	public static String axes_P1 = "ᴀxᴇs";
		public static String woodenAxes_P2 = "ᴡᴏᴏᴅᴇɴ ᴀxᴇs";
		public static String stoneAxes_P2 = "sᴛᴏɴᴇ ᴀxᴇs";
		public static String ironAxes_P2 = "ɪʀᴏɴ ᴀxᴇs";
		public static String diamondAxes_P2 = "ᴅɪᴀᴍᴏɴᴅ ᴀxᴇs";
		public static String netheriteAxes_P2 = "ɴᴇᴛʜᴇʀɪᴛᴇ ᴀxᴇs";

	public static String misc_P1 = "ᴍɪsᴄ";
		public static String foods_P2 = "ғᴏᴏᴅs";
		public static String pvpUtilities_P2 = "ᴘᴠᴘ ᴜᴛɪʟɪᴛɪᴇs";
		public static String shulkers_P2 = "sʜᴜʟᴋᴇʀs";
		public static String air_P2 = "Air";
		public static String potions_P2 = "ᴘᴏᴛɪᴏɴs";
		public static String elytra_P2 = "ᴇʟʏᴛʀᴀ";
		public static String blocks_P2 = "ʙʟᴏᴄᴋs";


	public static TreeNode buildTree() {
		TreeNode root = new TreeNode("Root");

		// Construct tree using search and addChild in one line
		root.addChild(new TreeNode(compressors_P1));
		root.search(compressors_P1).addChild(new TreeNode(compressOre_P2));
		root.search(compressors_P1).addChild(new TreeNode(decompressOre_P2));

		root.addChild(new TreeNode(swords_P1));
		root.search(swords_P1).addChild(new TreeNode(woodenSwords_P2));
		root.search(swords_P1).addChild(new TreeNode(stoneSwords_P2));
		root.search(swords_P1).addChild(new TreeNode(ironSwords_P2));
		root.search(swords_P1).addChild(new TreeNode(diamondSwords_P2));
		root.search(swords_P1).addChild(new TreeNode(netheriteSwords_P2));

		root.addChild(new TreeNode(armors_P1));
		root.search(armors_P1).addChild(new TreeNode(leatherArmors_P2));
		root.search(armors_P1).addChild(new TreeNode(ironArmors_P2));
		root.search(armors_P1).addChild(new TreeNode(diamondArmors_P2));
		root.search(armors_P1).addChild(new TreeNode(netheriteArmors_P2));

		root.addChild(new TreeNode(pickaxes_P1));
		root.search(pickaxes_P1).addChild(new TreeNode(woodenPickaxes_P2));
		root.search(pickaxes_P1).addChild(new TreeNode(stonePickaxes_P2));
		root.search(pickaxes_P1).addChild(new TreeNode(ironPickaxes_P2));
		root.search(pickaxes_P1).addChild(new TreeNode(diamondPickaxes_P2));
		root.search(pickaxes_P1).addChild(new TreeNode(netheritePickaxes_P2));

		root.addChild(new TreeNode(axes_P1));
		root.search(axes_P1).addChild(new TreeNode(woodenAxes_P2));
		root.search(axes_P1).addChild(new TreeNode(stoneAxes_P2));
		root.search(axes_P1).addChild(new TreeNode(ironAxes_P2));
		root.search(axes_P1).addChild(new TreeNode(diamondAxes_P2));
		root.search(axes_P1).addChild(new TreeNode(netheriteAxes_P2));

		root.addChild(new TreeNode(misc_P1));
		root.search(misc_P1).addChild(new TreeNode(foods_P2));
		root.search(misc_P1).addChild(new TreeNode(pvpUtilities_P2));
		root.search(misc_P1).addChild(new TreeNode(shulkers_P2));
		root.search(misc_P1).addChild(new TreeNode(air_P2));
		root.search(misc_P1).addChild(new TreeNode(potions_P2));
		root.search(misc_P1).addChild(new TreeNode(elytra_P2));
		root.search(misc_P1).addChild(new TreeNode(blocks_P2));

		return root;
	}

	public static TreeNode shop = buildTree();

	// Shop second page

	// Compress Ore Conversions
	Conversion cobblestoneToGoldenNugget = new Conversion( List.of(compressors_P1, compressOre_P2), 1, "Cobblestone", 64, "Golden Nugget", 1 );
	Conversion coalToGoldenNuggets = new Conversion( List.of(compressors_P1, compressOre_P2), 2, "Coal", 64, "Golden Nugget", 2 );
	Conversion ironToGoldenNuggets = new Conversion( List.of(compressors_P1, compressOre_P2), 3, "Iron Ingot", 64, "Golden Nugget", 3 );
	Conversion diamondsToGoldenNuggets = new Conversion( List.of(compressors_P1, compressOre_P2), 4, "Diamond", 12, "Golden Nugget", 5 );
	Conversion emeraldsToGoldenNuggets = new Conversion( List.of(compressors_P1, compressOre_P2), 5, "Emerald", 12, "Golden Nugget", 6 );
	Conversion ironToCompressedGold = new Conversion( List.of(compressors_P1, compressOre_P2), 7, "Iron Ingot", 64, "Compressed Raw Gold", 2, "Iron Ingot", 32 );
	Conversion diamondsToCompressedRawGold = new Conversion(List.of(compressors_P1, compressOre_P2), 8, "Diamond", 64, "Compressed Raw Gold", 3);
	Conversion emeraldsToCompressedRawGold = new Conversion(List.of(compressors_P1, compressOre_P2), 9, "Emerald", 64, "Compressed Raw Gold", 6);
	Conversion ironToGoldenIngot = new Conversion( List.of(compressors_P1, compressOre_P2), 11, "Iron Ingot", 64, "Golden Ingot", 1, "Iron Ingot", 64);
	Conversion diamondsToGoldenIngot = new Conversion(List.of(compressors_P1, compressOre_P2), 12, "Diamond", 64, "Golden Ingot", 2, "Diamond", 32);
	Conversion emeraldsToGoldenIngot = new Conversion(List.of(compressors_P1, compressOre_P2), 13, "Emerald", 64, "Golden Ingot", 3, "Emerald", 32);
	Conversion diamondToGoldenBlock = new Conversion(List.of(compressors_P1, compressOre_P2), 15, "Diamond", 64, "Golden Block", 1, "Diamond", 64);
	Conversion emeraldToGoldenBlock = new Conversion(List.of(compressors_P1, compressOre_P2), 16, "Emerald", 64, "Golden Block", 2, "Emerald", 64);

	// Decompress Ore Conversions
	Conversion goldenNuggetToCobblestone = new Conversion(List.of(compressors_P1, decompressOre_P2), 1, "Golden Nugget", 1, "Cobblestone", 64);
	Conversion goldenNuggetsToCoal = new Conversion(List.of(compressors_P1, decompressOre_P2), 2, "Golden Nugget", 2, "Coal", 64);
	Conversion goldenNuggetsToIron = new Conversion(List.of(compressors_P1, decompressOre_P2), 3, "Golden Nugget", 3, "Iron Ingot", 32);
	Conversion goldenNuggetsToDiamonds = new Conversion(List.of(compressors_P1, decompressOre_P2), 4, "Golden Nugget", 5, "Diamond", 12);
	Conversion goldenNuggetsToEmeralds = new Conversion(List.of(compressors_P1, decompressOre_P2), 5, "Golden Nugget", 6, "Emerald", 12);
	Conversion compressedGoldToIron = new Conversion(List.of(compressors_P1, decompressOre_P2), 7, "Compressed Raw Gold", 2, "Iron Ingot", 64, "Iron Ingot", 32);
	Conversion compressedRawGoldToDiamonds = new Conversion(List.of(compressors_P1, decompressOre_P2), 8, "Compressed Raw Gold", 3, "Diamond", 64);
	Conversion compressedRawGoldToEmeralds = new Conversion(List.of(compressors_P1, decompressOre_P2), 9, "Compressed Raw Gold", 6, "Emerald", 64);
	Conversion goldenIngotToIron = new Conversion(List.of(compressors_P1, decompressOre_P2), 11, "Golden Ingot", 1, "Iron Ingot", 64, "Iron Ingot", 64);
	Conversion goldenIngotToDiamonds = new Conversion(List.of(compressors_P1, decompressOre_P2), 12, "Golden Ingot", 2, "Diamond", 64, "Diamond", 32);
	Conversion goldenIngotToEmeralds = new Conversion(List.of(compressors_P1, decompressOre_P2), 13, "Golden Ingot", 3, "Emerald", 64, "Emerald", 32);
	Conversion goldenBlockToDiamond = new Conversion(List.of(compressors_P1, decompressOre_P2), 15, "Golden Block", 1, "Diamond", 64, "Diamond", 64);
	Conversion goldenBlockToEmerald = new Conversion(List.of(compressors_P1, decompressOre_P2), 16, "Golden Block", 2, "Emerald", 64, "Emerald", 64);



	// Declare the client
	private static final MinecraftClient client = MinecraftClient.getInstance();
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

	public void loadAllKeyBinds()
	{
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
		});
	}

	// ----------------------------- Custom Chat Commands -----------------------------
	// Container for custom chat commands and their corresponding functions
	private final Map<String, Consumer<String>> customCommands = new HashMap<>();

	// Register a new custom chat command and store it in the container
	private void registerCustomCommand(String command, Consumer<String> action) { customCommands.put(command, action); }

	// Handle incoming chat messages and trigger corresponding custom commands

	public void loadAllCustomCommands()
	{
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

	public void loadChatEvents()
	{
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
	public static int DELAY = 100;
	public static int SHOP_DELAY = 1000;
	public static int CONVERSION_DELAY = 50;

	public static void Sleep(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static int searchSlots(String name, boolean forceSearch) {
		int start = 0;
		MinecraftClient client = MinecraftClient.getInstance();
		DefaultedList<Slot> slots = null;

		if (client.currentScreen instanceof GenericContainerScreen) {
			slots = ((GenericContainerScreen) client.currentScreen).getScreenHandler().slots;
		} else if (client.currentScreen instanceof MerchantScreen) {
			slots = ((MerchantScreen) client.currentScreen).getScreenHandler().slots;
		} else {
			return -1;
		}

		// Search for the slot
		for (int i = start; i < slots.size(); i++) {
			if (name.equals(slots.get(i).getStack().getName().getString())) {
				return i;
			}
		}

		// If not found and force search is enabled, retry
		if (forceSearch) {
			Sleep(10);  // Small delay before retrying
			return searchSlots(name, true);  // Recursive call with forceSearch = true
		}

		// Return -1 if no slot is found and force search is not enabled
		return -1;
	}

//	public static void makeTrade(int offerIndex) {
//		MinecraftClient client = MinecraftClient.getInstance();
//		MerchantScreen merchantScreen;
//
//		// Wait until the current screen is MerchantScreen
//		while (!(client.currentScreen instanceof MerchantScreen)) {
//			Sleep(10);
//		}
//
//		Sleep(DELAY);
//		merchantScreen = (MerchantScreen) client.currentScreen;
//		TradeOffer offer = merchantScreen.getScreenHandler().getRecipes().get(offerIndex);
//
//		// Extract price details
//		String firstPriceName = offer.getOriginalFirstBuyItem().getName().getString();
//		int firstPriceCount = offer.getOriginalFirstBuyItem().getCount();
//		String secondPriceName = offer.getSecondBuyItem().getName().getString();
//		int secondPriceCount = offer.getSecondBuyItem().getCount();
//
//		// Log item prices
//		LOGGER.info("{}: {}", firstPriceName, firstPriceCount);
//		LOGGER.info("{}: {}", secondPriceName, secondPriceCount);
//
//		// Trade process
//		while (canTrade(firstPriceName, firstPriceCount, secondPriceName, secondPriceCount)) {
//			if (firstPriceName.equals(secondPriceName) && !canAffordCombinedPrice(firstPriceName, firstPriceCount, secondPriceCount)) {
//				LOGGER.info("Broken");
//				break;
//			}
//			client.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));
//			clickSlot(2);
//			Sleep(50);
//		}
//	}

	// Helper functions for trade validation
//	private static boolean canTrade(String firstPriceName, int firstPriceCount, String secondPriceName, int secondPriceCount) {
//		return countItemInAllSlots(firstPriceName) >= firstPriceCount &&
//				(secondPriceCount == 0 || countItemInAllSlots(secondPriceName) >= secondPriceCount) &&
//				(!getSlot(0).hasStack() || countEmptySlots(3) != 0);
//	}

//	private static boolean canAffordCombinedPrice(String itemName, int firstCount, int secondCount) {
//		return firstCount + secondCount <= countItemInAllSlots(itemName);
//	}


	public static void openCompressors() {
		openShop();
		clickSlot(searchSlots("ᴄᴏᴍᴘʀᴇssᴏʀs", true));
		waitForScreen(MerchantScreen.class);
		Sleep(SHOP_DELAY);
	}

	public static int countItemInAllSlots(String name, int start) {
		MinecraftClient client = MinecraftClient.getInstance();
		DefaultedList<Slot> slots = getCurrentScreenSlots(client);

		if (slots == null) {
			return 0;
		}

		int count = 0;
		for (int i = start; i < slots.size(); i++) {
			if (slots.get(i).getStack().getName().getString().equals(name)) {
				count += slots.get(i).getStack().getCount();
			}
		}
		return count;
	}

	// Helper function to get slots based on the current screen
	private static DefaultedList<Slot> getCurrentScreenSlots(MinecraftClient client) {
		if (client.currentScreen instanceof GenericContainerScreen) {
			return ((GenericContainerScreen) client.currentScreen).getScreenHandler().slots;
		} else if (client.currentScreen instanceof MerchantScreen) {
			return ((MerchantScreen) client.currentScreen).getScreenHandler().slots;
		} else {
			return null;
		}
	}

	public static void openCompressOne() {
		openCompressors();
		clickSlot(searchSlots("ᴄᴏᴍᴘʀᴇss ᴏʀᴇ", true));
		waitForScreen(MerchantScreen.class);
		Sleep(SHOP_DELAY);
	}

	public static void openDecompressOne() {
		openCompressors();
		clickSlot(searchSlots("ᴅᴇᴄᴏᴍᴘʀᴇss ᴏʀᴇ", true));
		waitForScreen(MerchantScreen.class);
		Sleep(SHOP_DELAY);
	}

	public static void clickSlot(int slotIndex) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.currentScreen == null) return;

		ScreenHandler screenHandler = null;

		if (client.currentScreen instanceof MerchantScreen) {
			screenHandler = ((MerchantScreen) client.currentScreen).getScreenHandler();
		} else if (client.currentScreen instanceof GenericContainerScreen) {
			screenHandler = ((GenericContainerScreen) client.currentScreen).getScreenHandler();
		}

		if (screenHandler != null) {
			client.interactionManager.clickSlot(
					screenHandler.syncId,
					slotIndex,
					0,
					SlotActionType.QUICK_MOVE,
					client.player
			);
		}
	}

	public static void waitForScreen(Class<?> screenClass) {
		MinecraftClient client = MinecraftClient.getInstance();
		while (!(screenClass.isInstance(client.currentScreen))) {
			Sleep(10);
		}
	}

	public static void printAllSlots(){
		DefaultedList<Slot> slots = null;
		if(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen){
			slots = ((GenericContainerScreen)(MinecraftClient.getInstance().currentScreen)).getScreenHandler().slots;
		}else if(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen){
			slots = ((MerchantScreen)(MinecraftClient.getInstance().currentScreen)).getScreenHandler().slots;
		}
		if(slots == null){
			return;
		}
		LOGGER.info("-------------------");
		for(int i = 0; i < slots.size(); i++){
			if(slots.get(i) != null) {
				LOGGER.info(""+i+": "+slots.get(i).getStack().getName().getString());
			}
		}
		LOGGER.info("-------------------");
	}
	public static void printAllTrades() {
		if (!(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen)) {
			return; // Exit if the current screen is not a villager trade screen
		}

		// Get the list of trade offers (recipes)
		List<TradeOffer> offers = ((MerchantScreen) MinecraftClient.getInstance().currentScreen)
				.getScreenHandler().getRecipes();

		if (offers == null || offers.isEmpty()) {
			LOGGER.info("No trades available.");
			return;
		}

		LOGGER.info("---------- Villager Trades ----------");

		// Loop through all available trades
		for (int i = 0; i < offers.size(); i++) {
			TradeOffer offer = offers.get(i);

			// Get the first buy item details
			int firstPriceCount = offer.getOriginalFirstBuyItem().getCount();
			String firstPriceName = offer.getOriginalFirstBuyItem().getName().getString();

			// Get the second buy item details (if present)
			int secondPriceCount = offer.getSecondBuyItem().isEmpty() ? 0 : offer.getSecondBuyItem().getCount();
			String secondPriceName = offer.getSecondBuyItem().isEmpty() ? "None" : offer.getSecondBuyItem().getName().getString();

			// Get the sell (output) item details
			int sellCount = offer.getSellItem().getCount();
			String sellName = offer.getSellItem().getName().getString();

			// Log all trade details
			LOGGER.info("Trade " + (i + 1) + ":");
			LOGGER.info("    First Item: " + firstPriceCount + " x " + firstPriceName);
			LOGGER.info("    Second Item: " + (secondPriceCount > 0 ? secondPriceCount + " x " + secondPriceName : "None"));
			LOGGER.info("    Result: " + sellCount + " x " + sellName);
			LOGGER.info("------------------------------------");
		}
	}



	// ----------------------------- Helper Functions -----------------------------
	// Make function to send command
	private static void sendCommand(String command) {  client.player.networkHandler.sendChatCommand(command); }


	// Make function to send message
	private static void sendChatMessage(String message) {  client.player.networkHandler.sendChatMessage(message); }

	// ----------------------------- Functions -----------------------------
	// Send a random chat message
	private static void sendRandomChatMessage(String unused) {
		String[] messages = { "Hello guys!", "What's up everyone?", "Hey there!", "How's it going?", "Good day, folks!" };
		String randomMessage = messages[new Random().nextInt(messages.length)];
		sendChatMessage(randomMessage);
	}

	// Send the /shop command

	private static void openShop() {
		sendCommand("shop");
		waitForScreen(GenericContainerScreen.class);
		Sleep(SHOP_DELAY);
	}
	private static void openShop(String unused) { sendCommand("shop"); }

	// Open PV 1
	private static void openPV1(String unused) { sendCommand("pv 1"); }

	// Test function to send a signed message
	private static void testFunction(String unused) {
		// Log last message receivedjj
		// sendChatMessage("Test");
		LOGGER.info("Test function called");
		// make  list contain all  p1

		List<String> p1 = Arrays.asList(compressors_P1, swords_P1, armors_P1, pickaxes_P1, axes_P1, misc_P1);

		List<String> g1 = Arrays.asList(compressOre_P2, decompressOre_P2);

		List<String> g2 = Arrays.asList(woodenSwords_P2, stoneSwords_P2, ironSwords_P2, diamondSwords_P2, netheriteSwords_P2);
		List<String> g3 = Arrays.asList(leatherArmors_P2, ironArmors_P2, diamondArmors_P2, netheriteArmors_P2);
		List<String> g4 = Arrays.asList(woodenPickaxes_P2, stonePickaxes_P2, ironPickaxes_P2, diamondPickaxes_P2, netheritePickaxes_P2);
		List<String> g5 = Arrays.asList(woodenAxes_P2, stoneAxes_P2, ironAxes_P2, diamondAxes_P2, netheriteAxes_P2);
		List<String> g6 = Arrays.asList(foods_P2, pvpUtilities_P2, shulkers_P2, air_P2, potions_P2, elytra_P2, blocks_P2);

		TreeNode root = buildTree();

		// show path

		List<String> pathWoodenAxes = root.search(g5.get(0)).getPathFromRoot();
		LOGGER.info("Path from root to " + g5.get(0) + ": " + pathWoodenAxes);
		for(int i = 0; i < pathWoodenAxes.size(); i++){
			LOGGER.info("Path from root to " + g5.get(0) + ": " + pathWoodenAxes.get(i));
		}


		Thread thread = new Thread(() -> {
			openShop();
		clickSlot(searchSlots(p1.get(0), true));
			Sleep(3000);
		clickSlot(searchSlots(g1.get(0), true));
			Sleep(3000);
			printAllTrades();
			//printAllSlots();
		});
		thread.start();


	}
}
