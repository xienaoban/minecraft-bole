package xienaoban.minecraft.bole.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.gui.ScreenRegistryManager;
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
        this.highlightManager = new HighlightManager();
        ScreenRegistryManager.initClient();
        ClientNetworkManager.init();
        KeyBindingManager.init();
    }

    public void onJoinWorld() {
        ClientWorld world = MinecraftClient.getInstance().world;
        preventMemoryLeak();
        if (world != null) {
            EntityManager.getInstance();
            Bole.LOGGER.info("Joining the world: " + world.getRegistryKey().getValue());
        }
        else Bole.LOGGER.info("Joining the world: null?!");
        this.inWorld = world != null;
    }

    public void onDisconnect() {
        this.inWorld = false;
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
            if (player != null && player.currentScreenHandler instanceof AbstractBoleScreenHandler) {
                ++screenTicks;
                ((AbstractBoleScreenHandler<?>) player.currentScreenHandler).clientTick(screenTicks);
            }
        }
        this.highlightManager.tick();
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
