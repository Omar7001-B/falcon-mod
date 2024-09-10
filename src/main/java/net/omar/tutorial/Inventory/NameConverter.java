package net.omar.tutorial.Inventory;

import java.util.HashMap;
import java.util.Map;

public class NameConverter {

    private static final Map<String, String> wordsMap = new HashMap<>();
    private static final String[] NON_STACKABLE_KEYWORDS = {"cap", "tunic", "pants", "boots", "totem" ,"bow", "sword", "pickaxe", "axe", "helmet", "chestplate", "leggings", "boots", "shulker", "elytra"};

    static {
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ", "Raw Gold");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ɪɴɢᴏᴛ", "Gold Ingot");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ʙʟᴏᴄᴋ", "Gold Block");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ɴᴜɢɢᴇᴛ", "Gold Nugget");
        // Add other mappings here...
    }

    public static String offerNamesToInventoryNames(String input) {
        //LOGGER.info("Converting: " + input);
        for (Map.Entry<String, String> entry : wordsMap.entrySet()) {
            if(input.contains(entry.getKey())) {
                //LOGGER.info("Converted: " + entry.getValue());
                return entry.getValue();
            }
        }
        return input;
    }

    public static boolean isStackedItem(String name) {
        for (String keyword : NON_STACKABLE_KEYWORDS)
            if (name.toLowerCase().contains(keyword)) return false;
        return true;
    }
}
