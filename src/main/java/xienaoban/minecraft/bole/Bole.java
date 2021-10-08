package xienaoban.minecraft.bole;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xienaoban.minecraft.bole.screen.BoleHorseScreenHandler;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

public class Bole implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Keys.BOLE);
    public static final ScreenHandlerType<BoleHorseScreenHandler> BOLE_HORSE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("xienaoban.bole", "horse"), BoleHorseScreenHandler::new);

    @Override
    public void onInitialize() {
        ServerNetworkManager.init();
    }
}
