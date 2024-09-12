package net.omar.tutorial.classes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


public class ModValidator {
    private static final String API_URL = "https://api.jsonbin.io/v3/b/66e070dde41b4d34e42d206f"; // Replace with your actual jsonbin.io URL
    private static final String CURRENT_VERSION = "1.0.0";

    // Static variables to store validation results and the Discord link
    public static boolean isUserValid = false;
    public static boolean isModUpToDate = false;
    public static String discordLink = ""; // To store the Discord link
    public static LocalDateTime userExpiryDate; // To store the expiry date

    // Fetch data from the API
    private static JsonObject fetchJsonData() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            return gson.fromJson(response.toString(), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Validate if the user is in the list and not expired
    private static boolean validateUser(String username) {
        JsonObject data = fetchJsonData();
        if (data == null) return false;

        // Get the "record" object from the root
        JsonObject record = data.getAsJsonObject("record");
        if (record == null) {
            System.out.println("No record found in the JSON response.");
            return false;
        }

        // Now, get the "users" array from the "record" object
        JsonArray users = record.getAsJsonArray("users");
        if (users == null) {
            System.out.println("No users found in the record.");
            return false;
        }

        // Iterate through users
        for (JsonElement userElement : users) {
            JsonObject user = userElement.getAsJsonObject();
            String storedUsername = user.get("username").getAsString();
            String expirationDate = user.get("expirationDate").getAsString();

            if (storedUsername.equalsIgnoreCase(username)) {
                if (isExpired(expirationDate)) {
                    System.out.println("User " + username + " has expired access.");
                    return false;
                } else {
                    // Set the expiry date
                    userExpiryDate = LocalDate.parse(expirationDate).atStartOfDay();
                    return true;
                }
            }
        }

        System.out.println("User " + username + " not found.");
        return false;
    }

    // Validate if the mod is updated to the latest version and fetch the discord link
    private static boolean validateModVersion() {
        JsonObject data = fetchJsonData();
        if (data == null) return false;

        // Get the "record" object from the root
        JsonObject record = data.getAsJsonObject("record");
        if (record == null) {
            System.out.println("No record found in the JSON response.");
            return false;
        }

        // Now, get the "modInfo" object from the "record" object
        JsonObject modInfo = record.getAsJsonObject("modInfo");
        if (modInfo == null) {
            System.out.println("No modInfo found in the record.");
            return false;
        }

        // Fetch the necessary fields
        String latestVersion = modInfo.get("version").getAsString();
        boolean updateAvailable = modInfo.get("updateAvailable").getAsBoolean();

        // Save the Discord link
        if (modInfo.has("discordLink")) {
            discordLink = modInfo.get("discordLink").getAsString();
        }

        if (!CURRENT_VERSION.equals(latestVersion)) {
            System.out.println("An update is available: " + latestVersion);
            return false;
        }
        if (updateAvailable) {
            System.out.println("Update flagged as available.");
        }
        return true;
    }

    // Helper function to check if the user's expiration date has passed
    private static boolean isExpired(String expirationDate) {
        // Parse the expiration date (assumes yyyy-mm-dd format)
        LocalDate expiration = LocalDate.parse(expirationDate);
        return LocalDate.now().isAfter(expiration);
    }

    // Initialize the validation (called once at the start of the mod)
    public static void initializeValidation(String username) {
        isUserValid = validateUser(username);
        isModUpToDate = validateModVersion();

        if (!isUserValid) {
            System.out.println("User validation failed for: " + username);
        }
        if (!isModUpToDate) {
            System.out.println("Mod is out of date.");
        }
    }

    // Function to get the number of days or hours left
    public static long getDaysLeft() {
        if (userExpiryDate == null) {
            System.out.println("Expiry date is not set.");
            return -1;
        }

        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.DAYS.between(now, userExpiryDate);
    }

    public static long getHoursLeft() {
        if (userExpiryDate == null) {
            System.out.println("Expiry date is not set.");
            return -1;
        }

        LocalDateTime now = LocalDateTime.now();
        return Duration.between(now, userExpiryDate).toHours();
    }

    public static void main(String[] args) {
        // Call validation at the start
        String username = "Benjamin007"; // Example username to validate
        initializeValidation(username);

        // Example usage of static boolean values in screens or later code
        if (isUserValid) {
            System.out.println("Access granted for user: " + username);
        } else {
            System.out.println("Access denied for user: " + username);
        }

        if (isModUpToDate) {
            System.out.println("Mod is up to date.");
        } else {
            System.out.println("Please update the mod.");
        }

        // Display the Discord link if it's available
        if (!discordLink.isEmpty()) {
            System.out.println("Join our Discord: " + discordLink);
        }

        // Display the days or hours left
        long daysLeft = getDaysLeft();
        if (daysLeft >= 0) {
            System.out.println("Days left until expiry: " + daysLeft);
        }

        long hoursLeft = getHoursLeft();
        if (hoursLeft >= 0) {
            System.out.println("Hours left until expiry: " + hoursLeft);
        }
    }
}
