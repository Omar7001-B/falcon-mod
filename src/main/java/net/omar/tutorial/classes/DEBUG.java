package net.omar.tutorial.classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static net.omar.tutorial.Tutorial.LOGGER;

public class DEBUG {
    private static final boolean DISALBE = true;
    private static String currentScreen;
    public static void Screens(String message) {
        if(DISALBE) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Screens.txt", true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.warn("Failed to write to file: " + e.getMessage());
        }
    }
    public static void LogScreenChange(String newScreen) {
        if(DISALBE) return;
        if (!newScreen.equals(currentScreen)) {
            DEBUG.Screens("Screen changed from [" + currentScreen + "] --> [" + newScreen + "]");
            currentScreen = newScreen;
        }
    }

    public static void Chat(String message) {
        if(DISALBE) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Chat.txt", true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.warn("Failed to write to file: " + e.getMessage());
        }
    }

    public static void Slots(String message) {
        if(DISALBE) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Slots.txt", true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.warn("Failed to write to file: " + e.getMessage());
        }
    }

    public static void Shop(String message) {
        if(DISALBE) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Shop.txt", true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            LOGGER.warn("Failed to write to file: " + e.getMessage());
        }
    }
}
