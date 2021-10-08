package xienaoban.minecraft.bole;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.screen.ScreenRegistryManager;

@Environment(EnvType.CLIENT)
public class BoleClient implements ClientModInitializer {
    public static Entity boleTarget;

    @Override
    public void onInitializeClient() {
        ScreenRegistryManager.init();
        KeyBindingManager.init();
    }
}
