package net.omar.tutorial.classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static net.omar.tutorial.Tutorial.LOGGER;

public class DEBUG {
    private static final boolean DISALBE = false;
    private static String currentScreen;
    public static final long MAX_FILE_SIZE = 1024 * 1024; // 1 MB limit

    private static void writeToFile(String fileName, String message) {
        if(DISALBE) return;
        File file = new File(fileName);

        try {
            if (file.length() > MAX_FILE_SIZE) {
                // Delete the file if it exceeds the limit
                if (!file.delete()) {
                    LOGGER.warn("Failed to delete the file: " + fileName);
                }
            }

            // Write the new message
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to write to file: " + e.getMessage());
        }
    }

    public static void Screens(String message) {
        writeToFile("Screens.txt", message);
    }

    public static void LogScreenChange(String newScreen) {
        if(DISALBE) return;
        if (!newScreen.equals(currentScreen)) {
            DEBUG.Screens("Screen changed from [" + currentScreen + "] --> [" + newScreen + "]");
            currentScreen = newScreen;
        }
    }

    public static void Chat(String message) {
        writeToFile("Chat.txt", message);
    }

    public static void Slots(String message) {
        writeToFile("Slots.txt", message);
    }

    public static void Shop(String message) {
        writeToFile("Shop.txt", message);
    }

    public static void Store(String message) {
        writeToFile("Store.txt", message);
    }
}
