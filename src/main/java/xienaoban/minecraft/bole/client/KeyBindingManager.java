package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.core.BoleHandler;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class KeyBindingManager {
    public static final KeyBinding KEY_BOLE_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            Keys.KEY_OPEN_BOLE_GUI, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, Keys.KEY_CATEGORY_BOLE
    ));

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KEY_BOLE_SCREEN.wasPressed()) BoleHandler.tryOpenBoleScreen(client);
        });
    }
}
