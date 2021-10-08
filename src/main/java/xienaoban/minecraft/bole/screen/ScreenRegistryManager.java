package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class ScreenRegistryManager {
    public static void init() {
        ScreenRegistry.register(BoleHandbookScreenHandler.HANDLER, BoleHandbookScreen::new);
        ScreenRegistry.register(BoleEntityScreenHandler.HANDLER, BoleEntityScreen::new);
        ScreenRegistry.register(BoleHorseScreenHandler.HANDLER, BoleHorseScreen::new);
    }
}
