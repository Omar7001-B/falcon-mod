package net.omar.tutorial.Handlers;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.omar.tutorial.Managers.Screening;
import net.omar.tutorial.Managers.Trading;
import net.omar.tutorial.Tutorial;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KeyBindingHandler {
    // ----------------------------- Maps ------------------------------
    public static final Map<KeyBinding, Consumer<String>> keyBindings = new HashMap<>();
    private static Thread thread;

    public static void loadAllKeyBinds() {
//        registerKeyBinding("Random Message", "Chat", GLFW.GLFW_KEY_R, Tutorial::sendRandomChatMessage);
        registerKeyBinding(keyBindings, "Falcon Farm", "Falcon", GLFW.GLFW_KEY_Z, Screening::openFalconFarmrScreen);
        registerKeyBinding(keyBindings, "Shop", "Falcon", GLFW.GLFW_KEY_KP_MULTIPLY, Trading::openShop);
        registerKeyBinding(keyBindings, "PV", "Falcon", GLFW.GLFW_KEY_KP_DIVIDE, Screening::openPV1);
        //registerKeyBinding(keyBindings, "Show Current User", "DEBUG", GLFW.GLFW_KEY_KP_7, Tutorial::showCurrentUser);
        // make capslock keybinding
//        registerKeyBinding("Testing Armopr", "Debug", GLFW.GLFW_KEY_X, Tutorial::buyFullArmors);

    }

    private static void registerKeyBinding(Map<KeyBinding, Consumer<String>> keyBindings, String translationKey, String categoryName, int keyCode, Consumer<String> action) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey, InputUtil.Type.KEYSYM, keyCode, categoryName));
        keyBindings.put(keyBinding, action);
    }

    public static void onClientTick() {
        keyBindings.forEach((keyBinding, action) -> {
            if (keyBinding.wasPressed()) {
                if (thread == null || !thread.isAlive()) {
                    thread = new Thread(() -> action.accept(""));
                    thread.start();
                }
            }
        });
    }
}
