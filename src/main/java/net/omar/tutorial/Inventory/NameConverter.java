package net.omar.tutorial.Inventory;
import net.omar.tutorial.classes.DEBUG;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NameConverter {

    private static final Map<String, String> wordsMap = new HashMap<>();

    static {
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ", "Raw Gold");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ɪɴɢᴏᴛ", "Gold Ingot");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ʙʟᴏᴄᴋ", "Gold Block");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ɴᴜɢɢᴇᴛ", "Gold Nugget");
        /*
        wordsMap.put("ɴᴜɢɢᴇᴛ", "Nugget");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ", "");
        wordsMap.put("Shulker Box", "Box");
        wordsMap.put("Golden", "Gold");
        wordsMap.put("ʀᴀᴡ", "Raw");
        wordsMap.put("ɢᴏʟᴅ", "Gold");
        wordsMap.put("ʙʟᴏᴄᴋ", "Block");
        wordsMap.put("ɪɴɢᴏᴛ", "Ingot");
        wordsMap.put("sʜᴜʟᴋᴇʀ", "Shulker");
        wordsMap.put("sᴛᴏɴᴇ", "Stone");
        wordsMap.put("sᴡᴏʀᴅ", "Sword");
        wordsMap.put("ᴘɪᴄᴋᴀxᴇ", "Pickaxe");
        wordsMap.put("ᴀxᴇ", "Axe");
        wordsMap.put("ɪʀᴏɴ", "Iron");
        wordsMap.put("ᴀʀᴍᴏʀ", "Armor");
        wordsMap.put("ɴᴇᴛʜᴇʀɪᴛᴇ", "Netherite");
        wordsMap.put("ʟᴇᴀᴛʜᴇʀ", "Leather");
        wordsMap.put("ғᴏᴏᴅ", "Food");
        wordsMap.put("ᴄᴏᴍᴘʀᴇss", "Compressed");
        wordsMap.put("ᴅᴇᴄᴏᴍᴘʀᴇss", "Decompressed");
        wordsMap.put("ᴅɪᴀᴍᴏɴᴅ", "Diamond");
        wordsMap.put("ᴇʟʏᴛʀᴀ", "Elytra");
        wordsMap.put("ᴇʟʏᴛʀᴀ_java", "Elytra Java");
        wordsMap.put("ᴘᴏᴛɪᴏɴ", "Potion");
        wordsMap.put("ᴘᴠᴘ", "PVP");
        wordsMap.put("ᴜᴛɪʟɪᴛɪᴇ", "Utility");
        wordsMap.put("ᴡᴏᴏᴅᴇɴ", "Wooden");
         */
    }

    public static String offerNamesToInventoryNames(String input) {
        DEBUG.Shop("Converting: " + input);
        for (Map.Entry<String, String> entry : wordsMap.entrySet()) {
            if(input.contains(entry.getKey())) {
                DEBUG.Shop("Converted: " + entry.getValue());
                return entry.getValue();
            }
        }
        return input;
    }
}
