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

    private static Bole instance;

    private ServerConfigsGetter serverConfigsGetter;

    public static Bole getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        System.out.println(1);
        instance = this;
        ScreenManager.initServer();
        ServerNetworkManager.init();
        Configs.init();
        setServerConfigsOnServer();
    }

    public Configs getServerConfigs() {
        return this.serverConfigsGetter.get();
    }

    public void setServerConfigsOnClient(Configs serverConfigs) {
        this.serverConfigsGetter = () -> serverConfigs;
    }

    public void setServerConfigsOnServer() {
        this.serverConfigsGetter = Configs::getInstance;
    }

    @FunctionalInterface
    public interface ServerConfigsGetter {
        Configs get();
    }
}
