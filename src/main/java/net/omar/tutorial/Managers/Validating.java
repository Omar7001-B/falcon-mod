package net.omar.tutorial.Managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.omar.tutorial.GUI.GearScreen;

public class Validating {
    // Flag to check if the data is parsed
    public static boolean isParsed = false;

    private static final String API_URL = "https://rest-api-back.vercel.app/mod/subscriber/";
    private static final String CURRENT_VERSION = "1.1.0";
    private static String current_user =  MinecraftClient.getInstance().getSession().getUsername();

    // User validation data
    public static String status = "";
    public static LocalDateTime expirationDate;

    // Mod management
    public static String version = "";
    public static String discordLink = "";
    public static boolean disableAllUsers = false;
    public static boolean enableAllUsers = false;

    // Feature toggles
    public static boolean enableFarming = false;
    public static boolean enableBuyItems = false;
    public static boolean enableBuyGears = false;
    public static boolean enableStatistics = false;
    public static boolean enableSaveInventory = false;
    public static boolean enableRecoverInventory = false;
    public static boolean enableSendInventory = false;
    public static boolean enableCompleteInventory = false;

    // Data sync options
    public static boolean syncOnGameOpenClose = false;
    public static boolean syncOnModScreenOpen = false;
    public static boolean syncAfterCycle = false;
    public static boolean syncAfterOperation = false;

    // User validation options
    public static boolean validateOnGameStart = false;
    public static boolean validateOnScreenOpen = false;
    public static boolean validateAfterCycle = false;
    public static boolean validateAfterOperation = false;

    private static JsonObject fetchJsonData(String username) {
        try {
            URL url = new URL(API_URL + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            // Check if the response code is 200 (HTTP OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Debugging.Validation("Response: " + response.toString());
                return new Gson().fromJson(response.toString(), JsonObject.class);
            } else {
                // Handle the error response
                System.err.println("Error: " + responseCode + " - " + connection.getResponseMessage());
                return null; // Or handle the error appropriately
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle exceptions
        }
    }

    private static void parseData(JsonObject data) {
        // Parse subscriber data
        JsonObject subscriber = data.getAsJsonObject("subscriber");
        if (subscriber != null) {
            status = subscriber.get("status").getAsString();
            expirationDate = LocalDateTime.parse(subscriber.get("expirationDate").getAsString().replace("Z", ""));
        }

        // Parse mod info data
        JsonObject modInfo = data.getAsJsonObject("modInfo");
        if (modInfo != null) {
            version = modInfo.get("version").getAsString();
            discordLink = modInfo.has("discordLink") ? modInfo.get("discordLink").getAsString() : "";

            JsonObject featureToggles = modInfo.getAsJsonObject("featureToggles");
            enableFarming = featureToggles.get("farming").getAsBoolean();
            enableBuyItems = featureToggles.get("buyItems").getAsBoolean();
            enableBuyGears = featureToggles.get("buyGear").getAsBoolean();
            enableStatistics = featureToggles.get("statistics").getAsBoolean();
            enableSaveInventory = featureToggles.get("saveInventory").getAsBoolean();
            enableRecoverInventory = featureToggles.get("recoverInventory").getAsBoolean();
            enableSendInventory = featureToggles.get("sendInventory").getAsBoolean();
            enableCompleteInventory = featureToggles.get("completeInventory").getAsBoolean();

            JsonObject dataSyncOptions = modInfo.getAsJsonObject("dataSyncOptions");
            syncOnGameOpenClose = dataSyncOptions.get("onGameOpenClose").getAsBoolean();
            syncOnModScreenOpen = dataSyncOptions.get("onModScreenOpen").getAsBoolean();
            syncAfterCycle = dataSyncOptions.get("afterCycle").getAsBoolean();
            syncAfterOperation = dataSyncOptions.get("afterOperation").getAsBoolean();

            JsonObject userValidationOptions = modInfo.getAsJsonObject("userValidationOptions");
            validateOnGameStart = userValidationOptions.get("onGameStart").getAsBoolean();
            validateOnScreenOpen = userValidationOptions.get("onScreenOpen").getAsBoolean();
            validateAfterCycle = userValidationOptions.get("afterCycle").getAsBoolean();
            validateAfterOperation = userValidationOptions.get("afterOperation").getAsBoolean();

            disableAllUsers = modInfo.get("disableAllUsers").getAsBoolean();
            enableAllUsers = modInfo.get("enableAllUsers").getAsBoolean();

            isParsed = true;
        }
    }

    private static void updateData(boolean forceUpdate) {
        current_user =  MinecraftClient.getInstance().getSession().getUsername();
        if (forceUpdate || !isParsed) {
            JsonObject data = fetchJsonData(current_user);
            if (data != null) {
                parseData(data);
            }
        }
        debugAllData();
    }

    // Method to check if sync is required based on context
    public static boolean shouldSyncData(String context) {
        return switch (context) {
            case "gameStart" -> syncOnGameOpenClose;
            case "gameEnd" -> syncOnGameOpenClose;
            case "modScreen" -> syncOnModScreenOpen;
            case "cycle" -> syncAfterCycle;
            case "operation" -> syncAfterOperation;
            default -> false;
        };
    }


    private static Thread thread;
    // Unified method to handle conditional updating
    public static void updateValidatingData(String context) {
        boolean shouldValidate = switch (context) {
            case "gameStart" -> validateOnGameStart;
            case "modScreen" -> validateOnScreenOpen;
            case "cycle" -> validateAfterCycle;
            case "operation" -> validateAfterOperation;
            case "force" -> true;
            default -> false;
        };

        if(thread == null || !thread.isAlive())
        {
            thread = new Thread(() -> updateData(shouldValidate));
            thread.start();
        }
    }

    public static boolean isUserEnabled() {
        return isParsed && ((status.equals("Active") || enableAllUsers) && !disableAllUsers);
    }

    public static long getTimeLeft(ChronoUnit unit) {
        return isParsed && ChronoUnit.SECONDS.between(LocalDateTime.now(), expirationDate) > 0
                ? LocalDateTime.now().until(expirationDate, unit)
                : -1;
    }

    public static boolean isModUpToDate() {
        return isParsed && CURRENT_VERSION.equals(version);
    }

    // fucntion to debug  all  data  to  Debugging.validation
    public static void debugAllData(){
        Debugging.Validation("----------------- User Data -----------------");
        Debugging.Validation("User Data: --");
        Debugging.Validation("User: " + current_user);
        Debugging.Validation("User status: " + status);
        Debugging.Validation("Expiration Date: " + expirationDate);

        Debugging.Validation("Mod Data: --");
        Debugging.Validation("Join our Discord: " + discordLink);
        Debugging.Validation("Version: " + version);
        Debugging.Validation("Disable All Users: " + disableAllUsers);
        Debugging.Validation("Enable All Users: " + enableAllUsers);

        Debugging.Validation("Feature Toggles: --");
        Debugging.Validation("Enable Farming: " + enableFarming);
        Debugging.Validation("Enable Buy Items: " + enableBuyItems);
        Debugging.Validation("Enable Buy Gears: " + enableBuyGears);
        Debugging.Validation("Enable Statistics: " + enableStatistics);
        Debugging.Validation("Enable Save Inventory: " + enableSaveInventory);
        Debugging.Validation("Enable Recover Inventory: " + enableRecoverInventory);
        Debugging.Validation("Enable Send Inventory: " + enableSendInventory);
        Debugging.Validation("Enable Complete Inventory: " + enableCompleteInventory);


        Debugging.Validation("Data Sync Options: --");
        Debugging.Validation("Sync On Game Open/Close: " + syncOnGameOpenClose);
        Debugging.Validation("Sync On Mod Screen Open: " + syncOnModScreenOpen);
        Debugging.Validation("Sync After Cycle: " + syncAfterCycle);
        Debugging.Validation("Sync After Operation: " + syncAfterOperation);

        Debugging.Validation("User Validation Options: --");
        Debugging.Validation("Validate On Game Start: " + validateOnGameStart);
        Debugging.Validation("Validate On Screen Open: " + validateOnScreenOpen);
        Debugging.Validation("Validate After Cycle: " + validateAfterCycle);
        Debugging.Validation("Validate After Operation: " + validateAfterOperation);

        Debugging.Validation("---------------------------------------------");
    }

    public static void main(String[] args) {
    }
}
