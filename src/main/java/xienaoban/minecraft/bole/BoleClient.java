package xienaoban.minecraft.bole;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import xienaoban.minecraft.bole.client.EntityManager;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.ScreenManager;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.gui.screen.handbook.BoleHandbookScreenState;
import xienaoban.minecraft.bole.network.ClientNetworkManager;

@Environment(EnvType.CLIENT)
public class BoleClient implements ClientModInitializer {
    private static BoleClient instance;

    private boolean isScreenOpen;
    private Entity boleTarget;
    private int ticks;
    private int screenTicks;
    private boolean inWorld;
    private Configs serverConfigs;
    private PacketByteBuf handlerBufCache = null;
    private BoleHandbookScreenState handbookState;
    private HighlightManager highlightManager;

    public static BoleClient getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        this.isScreenOpen = false;
        this.ticks = -1;
        this.screenTicks = -1;
        this.inWorld = false;
        setServerConfigs(Configs.getInstance());
        this.highlightManager = new HighlightManager();
        ScreenManager.initClient();
        ClientNetworkManager.init();
        KeyBindingManager.init();
    }

    public void onJoin() {
        ClientWorld world = MinecraftClient.getInstance().world;
        preventMemoryLeak();
        if (world != null) {
            EntityManager.getInstance();
            ClientNetworkManager.requestServerBoleConfigs();
            Bole.LOGGER.info("Joining the world: " + world.getRegistryKey().getValue());
        }
        else Bole.LOGGER.info("Joining the world: null?!");
        this.inWorld = world != null;
    }

    public void onDisconnect() {
        this.inWorld = false;
        setServerConfigs(Configs.getInstance());
        preventMemoryLeak();
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) Bole.LOGGER.info("Disconnecting from the world: " + world.getRegistryKey().getValue());
        else Bole.LOGGER.info("Disconnecting from the world: null");
    }

    public void clientTick() {
        if (!inWorld) return;
        if (!MinecraftClient.getInstance().isPaused()) ++this.ticks;
        if (this.isScreenOpen) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null && player.currentScreenHandler instanceof AbstractBoleScreenHandler boleScreenHandler) {
                ++screenTicks;
                boleScreenHandler.clientTick(screenTicks);
            }
        }
        this.highlightManager.tick();
    }

    public boolean isHost() {
        return MinecraftClient.getInstance().getServer() != null;
    }

    public Entity getBoleTarget() {
        return this.boleTarget;
    }

    public void setScreenOpen(boolean isScreenOpen) {
        this.isScreenOpen = isScreenOpen;
        this.screenTicks = -1;
    }

    public void setBoleTarget(Entity boleTarget) {
        this.boleTarget = boleTarget;
    }

    public int getTicks() {
        return this.ticks;
    }

    public int getScreenTicks() {
        return this.screenTicks;
    }

    public boolean isInWorld() {
        return inWorld;
    }

    public Configs getServerConfigs() {
        return serverConfigs;
    }

    public void setServerConfigs(Configs serverConfigs) {
        this.serverConfigs = serverConfigs;
    }

    public PacketByteBuf getHandlerBufCache() {
        return this.handlerBufCache;
    }

    public void setHandlerBufCache(PacketByteBuf buf) {
        this.handlerBufCache = buf;
    }

    public BoleHandbookScreenState getHandbookState() {
        return this.handbookState;
    }

    public void setHandbookState(BoleHandbookScreenState handbookState) {
        this.handbookState = handbookState;
    }

    public HighlightManager getHighlightManager() {
        return highlightManager;
    }

    private void preventMemoryLeak() {
        this.boleTarget = null;
        this.handlerBufCache = null;
        this.highlightManager.clear();
    }
}
