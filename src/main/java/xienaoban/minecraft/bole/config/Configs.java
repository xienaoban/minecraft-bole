package xienaoban.minecraft.bole.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ActionResult;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

@Config(name = Keys.MOD_NAME)
public class Configs implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static final String CLIENT = "default", SERVER = "server";

    @ConfigEntry.Gui.Excluded
    private static ConfigHolder<Configs> holder;

    public static void init() {
        AutoConfig.register(Configs.class, GsonConfigSerializer::new);
        holder = AutoConfig.getConfigHolder(Configs.class);
        holder.registerSaveListener((configHolder, configs) -> {
            // method "holder.save" should only be used on the client side.
            IntegratedServer server = MinecraftClient.getInstance().getServer();
            if (server != null) {
                ServerNetworkManager.sendServerBoleConfigsToAllPlayers(server);
            }
            return ActionResult.SUCCESS;
        });
    }

    public static Configs getInstance() {
        return holder.getConfig();
    }

    ///////// Configs /////////

    @ConfigEntry.Category(CLIENT)
    @ConfigEntry.Gui.Tooltip()
    boolean receiveWanderingTraderSpawnBroadcasts = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean allowHotKeyToOpenBoleHandbookScreen = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean invulnerablePets = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean forbidToSetNetherPortalCooldownOfOtherPlayers = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean broadcastWhenWanderingTraderSpawn = false;

    ///////// Getters /////////

    public boolean isReceiveWanderingTraderSpawnBroadcasts() {
        return receiveWanderingTraderSpawnBroadcasts;
    }

    public boolean isAllowHotKeyToOpenBoleHandbookScreen() {
        return allowHotKeyToOpenBoleHandbookScreen;
    }

    public boolean isInvulnerablePets() {
        return invulnerablePets;
    }

    public boolean isForbidToSetNetherPortalCooldownOfOtherPlayers() {
        return forbidToSetNetherPortalCooldownOfOtherPlayers;
    }

    public boolean isBroadcastWhenWanderingTraderSpawn() {
        return broadcastWhenWanderingTraderSpawn;
    }
}
