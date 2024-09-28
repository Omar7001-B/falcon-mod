package net.omar.tutorial.Handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class KeyPressingHandler {
    public static final Map<Integer, Consumer<String>> keyPressBindings = new HashMap<>();

    public static void loadAllKeyPressBinds() {
        //registerKeyPressBinding(GLFW.GLFW_KEY_X, (String s) -> SlotOperations.showAllSlots(null));
//        registerKeyPressBinding(GLFW.GLFW_KEY_Y, (String s) -> {
//            LOGGER.info("Y key pressed");
//            TradeManager.getMaterialNeeded(Market.armors_P1);
//            TradeManager.getMaterialNeeded(Market.swords_P1);
//            TradeManager.getMaterialNeeded(Market.pickaxes_P1);
//            TradeManager.getMaterialNeeded(Market.axes_P1);
//        });
    }

    public static void registerKeyPressBinding(int keyCode, Consumer<String> action) {
        keyPressBindings.put(keyCode, action);
    }

    public static void onClientTick() {
        keyPressBindings.forEach((keyCode, action) -> {
            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyCode)) {
                Thread thread = new Thread(() -> {
                    action.accept("");
                });
                thread.start();
            }
        });
    }
}
