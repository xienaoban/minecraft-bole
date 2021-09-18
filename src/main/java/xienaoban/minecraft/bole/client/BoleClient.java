package xienaoban.minecraft.bole.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import static xienaoban.minecraft.bole.util.TranslationKey.KEY_CATEGORY;
import static xienaoban.minecraft.bole.util.TranslationKey.KEY_OPEN_GUI;


@Environment(EnvType.CLIENT)
public class BoleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        bindKey();
    }

    private void bindKey() {
        KeyBinding keyBoleGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_GUI, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL, KEY_CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBoleGui.wasPressed()) { selectScreen(client); }
        });
    }

    private void selectScreen(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) { throw new RuntimeException("Player is null?!"); }
        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
            player.sendMessage(new LiteralText("-nothing-"), false);
        }
        else {
            Entity entity = ((EntityHitResult)hit).getEntity();
            player.sendMessage(new LiteralText(entity.toString()), false);
        }
    }
}
