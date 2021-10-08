package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class KeyBindingManager {
    public static final KeyBinding KEY_BOLE_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            Keys.KEY_OPEN_BOLE_GUI, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, Keys.KEY_CATEGORY_BOLE
    ));

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KEY_BOLE_SCREEN.wasPressed()) {
                ClientPlayerEntity player = client.player;
                BoleClient.boleTarget = null;
                if (player == null) {
                    Bole.LOGGER.error("Client player is null. Fail to open the Bole Screen.");
                    return;
                }
                HitResult hit = client.crosshairTarget;
                if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
                    ClientNetworkManager.openBoleGui();
                }
                else {
                    Entity entity = ((EntityHitResult)hit).getEntity();
                    BoleClient.boleTarget = entity;
                    ClientNetworkManager.openBoleGui(entity);
                }
            }
        });
    }
}
