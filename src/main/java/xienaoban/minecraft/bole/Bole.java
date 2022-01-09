package xienaoban.minecraft.bole;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.ScreenManager;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

public class Bole implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Keys.BOLE);

    @Override
    public void onInitialize() {
        ScreenManager.initServer();
        ServerNetworkManager.init();
        Configs.init();
    }
}
