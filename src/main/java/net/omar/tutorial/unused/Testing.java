package net.omar.tutorial.unused;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;

import java.util.List;
import static net.omar.tutorial.Tutorial.LOGGER;


/*
public class Testing {
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

}
*/

/*
public static void bigAllTrades()
{
    if(false) return;
    List<TreeNode> g1 = Arrays.asList(Market.compressOre_P2, Market.decompressOre_P2);
    List<TreeNode> g2 = Arrays.asList(Market.woodenSwords_P2, Market.stoneSwords_P2, Market.ironSwords_P2, Market.diamondSwords_P2, Market.netheriteSwords_P2);
    List<TreeNode> g3 = Arrays.asList(Market.leatherArmors_P2, Market.ironArmors_P2, Market.diamondArmors_P2, Market.netheriteArmors_P2);
    List<TreeNode> g4 = Arrays.asList(Market.woodenPickaxes_P2, Market.stonePickaxes_P2, Market.ironPickaxes_P2, Market.diamondPickaxes_P2, Market.netheritePickaxes_P2);
    List<TreeNode> g5 = Arrays.asList(Market.woodenAxes_P2, Market.stoneAxes_P2, Market.ironAxes_P2, Market.diamondAxes_P2, Market.netheriteAxes_P2);
    List<TreeNode> g6 = Arrays.asList(Market.foods_P2, Market.pvpUtilitys_P2, Market.shulkers_P2,  Market.potions_P2, Market.elytra_P2, Market.blocks_P2);

    List<TreeNode> All = Arrays.asList(Market.compressOre_P2, Market.decompressOre_P2, Market.woodenSwords_P2, Market.stoneSwords_P2, Market.ironSwords_P2, Market.diamondSwords_P2, Market.netheriteSwords_P2, Market.leatherArmors_P2, Market.ironArmors_P2, Market.diamondArmors_P2, Market.netheriteArmors_P2, Market.woodenPickaxes_P2, Market.stonePickaxes_P2, Market.ironPickaxes_P2, Market.diamondPickaxes_P2, Market.netheritePickaxes_P2, Market.woodenAxes_P2, Market.stoneAxes_P2, Market.ironAxes_P2, Market.diamondAxes_P2, Market.netheriteAxes_P2, Market.foods_P2, Market.pvpUtilitys_P2, Market.shulkers_P2,  Market.potions_P2, Market.elytra_P2, Market.blocks_P2);

    Thread thread = new Thread(() -> {
        List<TreeNode> turn = All;
        for(int i = 0; i < turn.size(); i++){
            List<String> path = turn.get(i).getPathFromRoot();
            LOGGER.info("Path of" + turn.get(i) + " is " + path);
            openShop();
            clickSlot(searchSlots(path.get(1)));
            Sleep(1000);
            clickSlot(searchSlots(path.get(2)));
            Sleep(1000);
            LOGGER.info("Trades of" + turn.get(i) );
            printAllTrades(path.get(2) +".txt");
            closeScreen();
            Sleep(1000);
        }
    });
    thread.start();
}
 */

/*
public static void printAllTrades(String fileName) {
    if (!(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen)) {
        return; // Exit if the current screen is not a villager trade screen
    }

    // Get the list of trade offers (recipes)
    List<TradeOffer> offers = ((MerchantScreen) MinecraftClient.getInstance().currentScreen)
            .getScreenHandler().getRecipes();

    if (offers == null || offers.isEmpty()) {
        log("No trades available.", fileName);
        return;
    }

    log("---------- Villager Trades ----------", fileName);

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
        log("Trade " + (i + 1) + ":", fileName);
        log("    First Item: " + firstPriceCount + " x " + firstPriceName, fileName);
        log("    Second Item: " + (secondPriceCount > 0 ? secondPriceCount + " x " + secondPriceName : "None"), fileName);
        log("    Result: " + sellCount + " x " + sellName, fileName);
        log("------------------------------------", fileName);
    }
}


    private static void log(String message, String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.write(message);
                writer.newLine();
            } catch (IOException e) {
                LOGGER.warn("Failed to write to file: " + e.getMessage());
            }
        } else {
            LOGGER.info(message);
        }
    }


    public static void waitForScreen(Class<?> screenClass) {
        MinecraftClient client = MinecraftClient.getInstance();
        while (!(screenClass.isInstance(client.currentScreen))) {
            Sleep(10);
        }
    }
 */


/*
// Helper function to get slots based on the current screen
private static DefaultedList<Slot> getCurrentScreenSlots() {
    if (client.currentScreen == null) return null;
    return ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
}
 */


/*
    public static int countEmptySlots(int start) {
        DefaultedList<Slot> slots = ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots;
        int count = 0;
        for (int i = start; i < slots.size(); i++)
            count += (slots.get(i).hasStack() ? 0 : 1);
        return count;
    }
 */
/*
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
 */
