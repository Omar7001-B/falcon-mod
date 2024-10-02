package net.omar.tutorial.Managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Statting {
    private static String USERNAME = MinecraftClient.getInstance().getSession().getUsername();
    private static String FILENAME = "stats/falcon_stats_" + USERNAME + ".json";
    private static String SERVER_URL = "https://rest-api-back.vercel.app/mod/stats/" + USERNAME; // Replace with your server URL

    // Static maps for storing stats
    private static Map<String, Integer> farmingStats = new HashMap<>();
    private static Map<String, Integer> itemStats = new HashMap<>();
    private static Map<String, Integer> gearStats = new HashMap<>();

    // Assuming isSynced is a class-level variable; initialize it here
    private static boolean isSynced = false;

    static {
        loadFromFile(); // Load existing stats when the class is loaded
    }

    // Function to add farming statistics
    public static void addFarmingStat(String itemName, int count) {
        Debugging.Statting("Adding farming stat: " + itemName + " x" + count);
        farmingStats.put(itemName, farmingStats.getOrDefault(itemName, 0) + count);
        Debugging.Statting("Farming stats after: " + farmingStats);
        saveToFile(); // Automatically save after adding
    }

    // Function to add item statistics
    public static void addItemStat(String itemName, int count) {
        Debugging.Statting("Adding item stat: " + itemName + " x" + count);
        Debugging.Statting("Item stats before: " + itemStats);
        itemStats.put(itemName, itemStats.getOrDefault(itemName, 0) + count);
        Debugging.Statting("Item stats after: " + itemStats);
        saveToFile(); // Automatically save after adding
    }

    // Function to add gear statistics
    public static void addGearStat(String itemName, int count) {
        Debugging.Statting("Adding gear stat: " + itemName + " x" + count);
        gearStats.put(itemName, gearStats.getOrDefault(itemName, 0) + count);
        Debugging.Statting("Gear stats after: " + gearStats);
        saveToFile(); // Automatically save after adding
    }

    // Function to save stats to a file
    private static void saveToFile() {
        USERNAME = MinecraftClient.getInstance().getSession().getUsername();
        FILENAME = "stats/falcon_stats_" + USERNAME + ".json";
        SERVER_URL = "https://rest-api-back.vercel.app/mod/stats/" + USERNAME; // Update server URL
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
            isSynced = false; // Set isSynced to false after saving
            Debugging.Statting("Statistics saved to " + FILENAME);
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
                Debugging.Statting("Statistics file created: " + FILENAME);
                isSynced = false;
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

                    Debugging.Statting("Statistics loaded from " + FILENAME + " for user: " + loadedUsername);
                    isSynced = false; // Set isSynced to false after loading
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

    private static void saveStatsToServer() {
        Gson gson = new Gson();
        Map<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("username", USERNAME);
        dataToSend.put("farmingStats", farmingStats);
        dataToSend.put("itemStats", itemStats);
        dataToSend.put("gearStats", gearStats);

        try {
            // Create URL and open connection
            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send JSON data
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = gson.toJson(dataToSend).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Debugging.Statting("Statistics successfully sent to the server.");
                isSynced = true; // Set isSynced to true after successful sync
            } else {
                Debugging.Statting("Failed to send statistics. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Thread thread;

    // Function to send stats to the server
    public static void saveStatsToServer(String context) {
        // Ensure that isSynced is updated based on the context
        if (Validating.shouldSyncData(context) && !isSynced) {
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(Statting::saveStatsToServer);
                thread.start();
            }
        }
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
