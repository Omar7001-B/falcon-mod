package net.omar.tutorial.Managers;

import java.util.HashMap;
import java.util.Map;

public class Naming {

    private static final Map<String, String> wordsMap = new HashMap<>();
    private static final String[] NON_STACKABLE_KEYWORDS = {"potion", "cap", "tunic", "pants", "boots", "totem" ,"bow", "sword", "pickaxe", "axe", "helmet", "chestplate", "leggings", "boots", "shulker", "elytra"};

    static {
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɢᴏʟᴅ", "Raw Gold");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ɪɴɢᴏᴛ", "Gold Ingot");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ʙʟᴏᴄᴋ", "Gold Block");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɢᴏʟᴅ ɴᴜɢɢᴇᴛ", "Gold Nugget");
        // Add other mappings here...

        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ʀᴀᴡ ɪʀᴏɴ", "Raw Iron");
        wordsMap.put("ᴄᴏᴍᴘʀᴇssᴇᴅ ɪʀᴏɴ ɪɴɢᴏᴛ", "Iron Ingot");
        wordsMap.put("arrow", "arrow");
        wordsMap.put("cobweb", "cobweb");
        wordsMap.put("potion", "potion");
        wordsMap.put("totem", "totem");
        wordsMap.put("apple", "apple");
        wordsMap.put("bow", "bow");
        wordsMap.put("shears", "shears");
        wordsMap.put("firework", "firework");
        wordsMap.put("obsidian", "obsidian");
    }

    public static String determineMaterialType(Map<String, Integer> materialMap) {
        boolean hasElytra = false;
        boolean hasBow = false;
        boolean hasShears = false;
        boolean hasFullArmorSet = false;
        boolean hasSword = false;
        boolean hasPickaxe = false;
        boolean hasAxe = false;

        // Check the contents of the map
        for (String materialName : materialMap.keySet()) {
            String lowerCaseMaterial = materialName.toLowerCase();

            // Check for specific items
            if (lowerCaseMaterial.contains("elytra")) {
                hasElytra = true;
            } else if (lowerCaseMaterial.contains("bow")) {
                hasBow = true;
            } else if (lowerCaseMaterial.contains("shears")) {
                hasShears = true;
            } else if (lowerCaseMaterial.contains("helmet") ||
                    lowerCaseMaterial.contains("chestplate") ||
                    lowerCaseMaterial.contains("leggings") ||
                    lowerCaseMaterial.contains("boots") ||
                    lowerCaseMaterial.contains("tunic") ||
                    lowerCaseMaterial.contains("cap") ||
                    lowerCaseMaterial.contains("hood") ||
                    lowerCaseMaterial.contains("robe")) {
                hasFullArmorSet = true;
            } else if (lowerCaseMaterial.contains("sword")) {
                hasSword = true;
            } else if (lowerCaseMaterial.contains("pickaxe")) {
                hasPickaxe = true;
            } else if (lowerCaseMaterial.contains("axe")) {
                hasAxe = true;
            }
        }

        // Determine the return value based on what was found
        if (hasElytra) {
            return "Elytra";
        } else if (hasBow) {
            return "Bow";
        } else if (hasShears) {
            return "Shears";
        } else if (hasFullArmorSet) {
            return "Full Armor Set";
        } else if (hasSword) {
            return "Sword";
        } else if (hasPickaxe) {
            return "Pickaxe";
        } else if (hasAxe) {
            return "Axe";
        }

        // Default return value if none of the conditions are met
        return "No specific item found";
    }

    public static Map<String,Integer> fixMapMaterial(Map<String,Integer> materialMap){
        Map<String,Integer> fixedMap = new HashMap<>();
        return fixedMap;
    }

    public static String offerNamesToInventoryNames(String input) {
        //LOGGER.info("Converting: " + input);
        for (Map.Entry<String, String> entry : wordsMap.entrySet()) {
            if(input.toLowerCase().contains(entry.getKey()) || input.contains(entry.getKey())) {
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

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;
        //LOGGER.info("Checking if " + str + " contains " + searchStr);

        // if searchString is longer than str, swap
        if (searchStr.length() > str.length()) {
            String temp = str;
            str = searchStr;
            searchStr = temp;
        }

        // Split the string between each lowercase and uppercase letter
        //DEBUG.Store("Original String: " + searchStr);
        searchStr = searchStr.replaceAll("([a-z])([A-Z])", "$1 $2");
        //DEBUG.Store("Modified String: " + searchStr);

        // Convert both strings to lowercase and split the search string into words
        String[] wordsToSearch = searchStr.toLowerCase().split("\\s+");
        String lowerStr = str.toLowerCase();

        // Check if each word in the search string is contained in the main string
        String debugString = "Words to search: ";
        //for (String word : wordsToSearch) debugString += word + " ";
        //debugString += " In: " + lowerStr;
        //DEBUG.Shulker(debugString);
        for (String word : wordsToSearch) {
            //LOGGER.info("Checking for word: " + word);
            if (!lowerStr.contains(word)) {
                //DEBUG.Shulker("Word not found: " + word);
                return false;
            }
        }
        //DEBUG.Shulker("Word found");
        return true;
    }
}
