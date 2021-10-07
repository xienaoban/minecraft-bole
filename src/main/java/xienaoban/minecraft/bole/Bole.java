package xienaoban.minecraft.bole;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xienaoban.minecraft.bole.gui.BoleHorseScreenHandler;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.TranslationKey;

public class Bole implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(TranslationKey.MOD_NAME);
    public static final Identifier BOLE_HORSE_PACKET = new Identifier("xienaoban.bole", "horse_packet");
    public static final Identifier BOLE_HORSE = new Identifier("xienaoban.bole", "horse");
    public static final ScreenHandlerType<BoleHorseScreenHandler> BOLE_HORSE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(BOLE_HORSE, BoleHorseScreenHandler::new);

    @Override
    public void onInitialize() {
        ServerNetworkManager.registerReceivers();
    }
}
