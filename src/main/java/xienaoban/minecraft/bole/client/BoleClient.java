package xienaoban.minecraft.bole.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import xienaoban.minecraft.bole.gui.ScreenRegistryManager;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.network.ClientNetworkManager;

@Environment(EnvType.CLIENT)
public class BoleClient implements ClientModInitializer {
    private static BoleClient instance;

    private boolean isScreenOpen;
    private Entity boleTarget;
    private int ticks;
    private int screenTicks;
    private PacketByteBuf handlerBufCache = null;
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
        this.highlightManager = new HighlightManager();
        ScreenRegistryManager.initClient();
        ClientNetworkManager.init();
        KeyBindingManager.init();
    }

    /**
     * @see xienaoban.minecraft.bole.mixin.MixinMinecraftClient#tick
     */
    public void clientTick() {
        ++this.ticks;
        if (this.isScreenOpen) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null && player.currentScreenHandler instanceof AbstractBoleScreenHandler) {
                ++screenTicks;
                ((AbstractBoleScreenHandler<?>) player.currentScreenHandler).clientTick(screenTicks);
            }
        }
        this.highlightManager.tick(this.ticks);
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
        return ticks;
    }

    public int getScreenTicks() {
        return screenTicks;
    }

    public PacketByteBuf getHandlerBufCache() {
        return handlerBufCache;
    }

    public void setHandlerBufCache(PacketByteBuf buf) {
        this.handlerBufCache = buf;
    }

    public HighlightManager getHighlightManager() {
        return highlightManager;
    }
}
