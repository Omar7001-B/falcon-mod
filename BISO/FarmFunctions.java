package net.name.farm;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FarmFunctions {
    public static final Logger LOGGER = LoggerFactory.getLogger("FarmFunctions");
    public static final int DELAY = 300;
    public static void openShop(){
        closeScreen();
        while(MinecraftClient.getInstance().currentScreen != null){
            sleep(10);
        }
        MinecraftClient.getInstance().player.networkHandler.sendChatCommand("shop");
    }
    public static void closeScreen(){
        MinecraftClient.getInstance().execute(()->{
            if(MinecraftClient.getInstance().currentScreen != null){
                MinecraftClient.getInstance().currentScreen.close();
            }
        });
    }
    public static void openPv(){
        while(MinecraftClient.getInstance().currentScreen != null){
            sleep(10);
        }
        sleep(DELAY);
        MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("pv 1");
    }
    public static void openCompressors(){
        openShop();
        while(!(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen)){
            sleep(10);
        }
        int slot = searchSlots("ᴄᴏᴍᴘʀᴇssᴏʀs");
        while(slot == -1){
            sleep(10);
            slot = searchSlots("ᴄᴏᴍᴘʀᴇssᴏʀs");
        }
        clickSlot(getSlot(searchSlots("ᴄᴏᴍᴘʀᴇssᴏʀs")).getIndex());

    }


    public static void openCompressOne(){
        openCompressors();

        // ᴄᴏᴍᴘʀᴇss ᴏʀᴇ
        // ᴅᴇᴄᴏᴍᴘʀᴇss ᴏʀᴇ
        while(!(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen)){
            sleep(10);
        }
        int slot = searchSlots("ᴄᴏᴍᴘʀᴇss ᴏʀᴇ");
        while(slot == -1){
            sleep(10);
            slot = searchSlots("ᴄᴏᴍᴘʀᴇss ᴏʀᴇ");
        }
        sleep(DELAY);
        clickSlot(slot);
    }
    public static void openDecompressOne(){
        openCompressors();

        // ᴄᴏᴍᴘʀᴇss ᴏʀᴇ
        // ᴅᴇᴄᴏᴍᴘʀᴇss ᴏʀᴇ
        while(!(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen)){
            sleep(10);
        }
        int slot = searchSlots("ᴅᴇᴄᴏᴍᴘʀᴇss ᴏʀᴇ");
        while(slot == -1){
            sleep(10);
            slot = searchSlots("ᴅᴇᴄᴏᴍᴘʀᴇss ᴏʀᴇ");
        }
        sleep(DELAY);
        clickSlot(getSlot(slot).getIndex());
    }

    public static void diamondToGoldNuggets(){
        openCompressOne();
        makeTrade(3);
    }
    public static void goldenNuggetsToEmerald(){
        openDecompressOne();
        makeTrade(4);
    }
    public static void emeraldToCompressedRowGold(){
        openCompressOne();
        makeTrade(8);
    }
    public static void compressedRowGoldToDiamond(){
        openDecompressOne();
        makeTrade(7);
    }
    public static void rawGoldToBlockGold(){
        closeScreen();
        sleep(DELAY);
        openPv();
        sleep(DELAY);
        int raw = searchSlots("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ");
        if(raw==-1){
            sendSystemMessage("You don't have raw gold :(");
            closeScreen();
            return;
        }
        while(getSlot(raw).getStack().getCount()<=32){
            raw = searchSlots("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ", raw+1);
            if(raw==-1){
                sendSystemMessage("You don't have raw gold :(");
                closeScreen();
                return;
            }
        }
        clickSlot(raw);
        closeScreen();
        openDecompressOne();
        makeTrade(8);
        openCompressOne();
        makeTrade(15);
    }

    public static void goldIngotToBlockGold(){
        closeScreen();
        sleep(DELAY);
        openPv();
        sleep(DELAY);
        int raw = searchSlots("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ");
        if(raw==-1){
            sendSystemMessage("You don't have raw gold :(");
            closeScreen();
            return;
        }
        while(getSlot(raw).getStack().getCount()<=32){
            raw = searchSlots("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ", raw+1);
            if(raw==-1){
                sendSystemMessage("You don't have raw gold :(");
                closeScreen();
                return;
            }
        }
        clickSlot(raw);
        closeScreen();
        openDecompressOne();
        makeTrade(8);
        openCompressOne();
        makeTrade(12);
    }

    public static void putOneStackInPv(){
        closeScreen();
        openPv();
        sleep(DELAY);
        int raw = searchSlots("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ", 54);
        if(raw == -1){
            sendSystemMessage("I didn't find a stack raw gold less than 64.");
            closeScreen();
            return;
        }
        while(getSlot(raw).getStack().getCount() == 64){
            raw = searchSlots("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ", raw+1);
            if(raw == -1){
                sendSystemMessage("I didn't find a stack raw gold less than 64.");
                closeScreen();
                return;
            }
        }
        clickSlot(raw);
        closeScreen();

        printAllSlots();
    }

    public static void makeTrade(int offerIndex){
        while(!(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen)){
            sleep(10);
        }
        sleep(DELAY);
        TradeOffer offer = ((MerchantScreen)MinecraftClient.getInstance().currentScreen).getScreenHandler().getRecipes().get(offerIndex);

        int firstPriceCount = offer.getOriginalFirstBuyItem().getCount();
        String firstPriceName = offer.getOriginalFirstBuyItem().getName().getString();

        int secondPriceCount = offer.getSecondBuyItem().getCount();
        String secondPriceName = offer.getSecondBuyItem().getName().getString();

        LOGGER.info(firstPriceName + ": " + firstPriceCount);
        LOGGER.info(secondPriceName + ": " + secondPriceCount);

        while(countItemInAllSlots(firstPriceName) >= firstPriceCount && ((secondPriceCount == 0 || countItemInAllSlots(secondPriceName) >= (secondPriceCount)) && (!getSlot(0).hasStack() || countEmptySlots(3) != 0))){
            if(firstPriceName.equals(secondPriceName) && firstPriceCount+secondPriceCount > countItemInAllSlots(firstPriceName)){
                LOGGER.info("Broken");
                break;
            }
            MinecraftClient.getInstance().player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(offerIndex));
            clickSlot(2);
            sleep(50);
        }
    }


    public static int searchSlots(String name, int start){
        DefaultedList<Slot> slots = null;
        if(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen){
            slots = ((GenericContainerScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().slots;
        }else if(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen){
            slots = ((MerchantScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().slots;
        }else{
            return -1;
        }

        for(int i = start; i < slots.size(); i++){
            if(slots.get(i).getStack().getName().getString().equals(name)){
                return i;
            }
        }
        return -1;
    }
    public static int searchSlots(String name){
        return searchSlots(name, 0);
    }

    public static Slot getSlot(int index){
        if(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen){
            return ((GenericContainerScreen)MinecraftClient.getInstance().currentScreen).getScreenHandler().slots.get(index);
        }else if(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen){
            return ((MerchantScreen)MinecraftClient.getInstance().currentScreen).getScreenHandler().slots.get(index);
        }
        return null;
    }

    public static int countItemInAllSlots(String name, int start){
        DefaultedList<Slot> slots = null;
        int count = 0;
        if(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen){
            slots = ((GenericContainerScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().slots;
        }else if(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen){
            slots = ((MerchantScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().slots;
        }else{
            return 0;
        }

        for(int i = start; i < slots.size(); i++){
            if(slots.get(i).getStack().getName().getString().equals(name)){
                count+=slots.get(i).getStack().getCount();
            }
        }
        return count;
    }

    public static int countEmptySlots(int start){
        DefaultedList<Slot> slots = null;
        int count = 0;
        if(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen){
            slots = ((GenericContainerScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().slots;
        }else if(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen){
            slots = ((MerchantScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler().slots;
        }else{
            return 0;
        }

        for(int i = start; i < slots.size(); i++){
            if(!slots.get(i).hasStack()){
                count++;
            }
        }
        return count;
    }
    public static int countEmptySlots(){
        return countEmptySlots(0);
    }

    public static int countItemInAllSlots(String name){
        return countItemInAllSlots(name, 0);
    }

    public static void clickSlot(int slotIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client.currentScreen instanceof MerchantScreen) {
            MerchantScreen containerScreen = (MerchantScreen) client.currentScreen;
            if (client.player != null) {
                client.interactionManager.clickSlot(
                        containerScreen.getScreenHandler().syncId, // The sync ID of the container
                        slotIndex, // The slot index to click on
                        0, // The mouse button (0 = left click, 1 = right click)
                        SlotActionType.QUICK_MOVE, // The action type (PICKUP = standard click)
                        client.player // The player performing the action
                );
            }
        }else{
            GenericContainerScreen containerScreen = (GenericContainerScreen) client.currentScreen;
            if (client.player != null) {
                client.interactionManager.clickSlot(
                        containerScreen.getScreenHandler().syncId, // The sync ID of the container
                        slotIndex, // The slot index to click on
                        0, // The mouse button (0 = left click, 1 = right click)
                        SlotActionType.QUICK_MOVE, // The action type (PICKUP = standard click)
                        client.player // The player performing the action
                );
            }
        }
    }
    public static void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    public static void sendSystemMessage(String message){
        MinecraftClient.getInstance().player.sendMessage(Text.literal(message));
    }
}
