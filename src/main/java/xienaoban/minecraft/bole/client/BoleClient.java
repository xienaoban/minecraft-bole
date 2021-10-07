package xienaoban.minecraft.bole.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.BoleHorseScreen;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.TranslationKey;

@Environment(EnvType.CLIENT)
public class BoleClient implements ClientModInitializer {
    public static HorseEntity currentHorse;

    @Override
    public void onInitializeClient() {
        registerScreens();
        bindKeys();
    }

    private void registerScreens() {
        ScreenRegistry.register(Bole.BOLE_HORSE_SCREEN_HANDLER, BoleHorseScreen::new);
    }

    private void bindKeys() {
        KeyBinding keyBoleGui = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                TranslationKey.KEY_OPEN_GUI, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL,
                TranslationKey.KEY_CATEGORY));
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
            if (entity instanceof HorseEntity) {
                HorseEntity horse = (HorseEntity)entity;
                currentHorse = horse;
                double jumpStrength = horse.getJumpStrength();
                double jumpHeight = -0.1817584952f * Math.pow(jumpStrength, 3) + 3.689713992f * Math.pow(jumpStrength, 2) + 2.128599134f * jumpStrength - 0.343930367f;
                player.sendMessage(new LiteralText(String.valueOf(jumpHeight)), false);
                ClientNetworkManager.openBoleHorseScreen(horse);
            }
        }
    }
}
