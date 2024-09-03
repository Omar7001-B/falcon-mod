package net.omar.tutorial.Inventory;
import net.omar.tutorial.classes.DEBUG;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static net.omar.tutorial.Tutorial.LOGGER;

public class NameConverter {

    private static final Map<String, String> wordsMap = new HashMap<>();
    private static final String[] STACKABLE_KEYWORDS = {"sword", "pickaxe", "axe", "helmet", "chestplate", "leggings", "boots", "shulker"};

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
        //LOGGER.info("Checking if " + name + " is stackable");
        for (String keyword : STACKABLE_KEYWORDS) {
            if (name.toLowerCase().contains(keyword)) {
                //LOGGER.info("Yes, " + name + " is stackable");
                return false;
            }
        }
        return true;
    }
}
