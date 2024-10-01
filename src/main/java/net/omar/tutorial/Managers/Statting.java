package net.omar.tutorial.Managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Statting {
    private static final String USERNAME = MinecraftClient.getInstance().getSession().getUsername();
    private static final String FILENAME = "stats/falcon_stats_" + USERNAME + ".json";

    // Static maps for storing stats
    private static Map<String, Integer> farmingStats = new HashMap<>();
    private static Map<String, Integer> itemStats = new HashMap<>();
    private static Map<String, Integer> gearStats = new HashMap<>();

    static {
        loadFromFile(); // Load existing stats when the class is loaded
    }

    // Function to add farming statistics
    public static void addFarmingStat(String itemName, int count) {
        farmingStats.put(itemName, farmingStats.getOrDefault(itemName, 0) + count);
        saveToFile(); // Automatically save after adding
    }

    // Function to add item statistics
    public static void addItemStat(String itemName, int count) {
        itemStats.put(itemName, itemStats.getOrDefault(itemName, 0) + count);
        saveToFile(); // Automatically save after adding
    }

    // Function to add gear statistics
    public static void addGearStat(String itemName, int count) {
        gearStats.put(itemName, gearStats.getOrDefault(itemName, 0) + count);
        saveToFile(); // Automatically save after adding
    }

    // Function to save stats to a file
    private static void saveToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File statsFile = new File(FILENAME);
        statsFile.getParentFile().mkdirs(); // Create the directory if it doesn't exist

        try (FileWriter writer = new FileWriter(statsFile)) {
            // Create a temporary object to hold the map data along with the username
            Map<String, Object> dataToSave = new HashMap<>();
            dataToSave.put("username", USERNAME);
            dataToSave.put("farmingStats", farmingStats);
            dataToSave.put("itemStats", itemStats);
            dataToSave.put("gearStats", gearStats);

            // Serialize the map data to JSON
            gson.toJson(dataToSave, writer);
            System.out.println("Statistics saved to " + FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// Function to load stats from a file
    private static void loadFromFile() {
        Gson gson = new Gson();
        File statsFile = new File(FILENAME);

        // Create the file and the parent directories if they don't exist
        try {
            if (!statsFile.exists()) {
                statsFile.getParentFile().mkdirs(); // Create the directory if it doesn't exist
                statsFile.createNewFile(); // Create the file
                System.out.println("Statistics file created: " + FILENAME);
            }

            // Now try to read from the file
            try (FileReader reader = new FileReader(statsFile)) {
                Map<String, Object> loadedData = gson.fromJson(reader, Map.class);
                if (loadedData != null) {
                    // Retrieve the username and stats
                    String loadedUsername = (String) loadedData.get("username");

                    // Load stats and convert to Integer maps
                    farmingStats = convertToIntegerMap((Map<String, Object>) loadedData.get("farmingStats"));
                    itemStats = convertToIntegerMap((Map<String, Object>) loadedData.get("itemStats"));
                    gearStats = convertToIntegerMap((Map<String, Object>) loadedData.get("gearStats"));

                    System.out.println("Statistics loaded from " + FILENAME + " for user: " + loadedUsername);
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to convert Map<String, Object> to Map<String, Integer>
    private static Map<String, Integer> convertToIntegerMap(Map<String, Object> input) {
        Map<String, Integer> output = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if (entry.getValue() instanceof Number) {
                output.put(entry.getKey(), ((Number) entry.getValue()).intValue()); // Convert to Integer
            }
        }
        return output;
    }

    // Optional: Getters for accessing stats if needed
    public static Map<String, Integer> getFarmingStats() {
        return farmingStats;
    }

    public static Map<String, Integer> getItemStats() {
        return itemStats;
    }

    public static Map<String, Integer> getGearStats() {
        return gearStats;
    }
}
