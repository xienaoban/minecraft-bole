package xienaoban.minecraft.bole;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xienaoban.minecraft.bole.client.EntityManager;
import xienaoban.minecraft.bole.client.EventsManager;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.ScreenManager;
import xienaoban.minecraft.bole.gui.screen.GenericScreenHandler;
import xienaoban.minecraft.bole.gui.screen.homepage.BoleHomepageScreenState;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.network.ServerNetworkManager;

@Environment(EnvType.CLIENT)
public class BoleClient implements ClientModInitializer {
    private static BoleClient instance;

    private boolean isScreenOpen;
    private Entity hitEntity;
    private BlockPos hitBlock;
    private int ticks;
    private int screenTicks;
    private boolean inWorld;
    private PacketByteBuf handlerBufCache = null;
    private BoleHomepageScreenState screenState;
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
        ScreenManager.init();
        ClientNetworkManager.init();
        KeyBindingManager.init();
        initConfigsSaveListener();
        EventsManager.init();
        EntityManager.init();
        Bole.getInstance().setServerVersion("<unknown>");
        Bole.getInstance().setServerConfigs(Configs.getInstance());
    }

    /**
     * Broadcasts new configs to the entire server after saving configs by the host via cloth-config screen.
     * For DedicatedServer, there's no way to update configs in the game yet.
     */
    private static void initConfigsSaveListener() {
        Configs.getHolder().registerSaveListener((configHolder, configs) -> {
            IntegratedServer server = MinecraftClient.getInstance().getServer();
            if (server != null) {
                ServerNetworkManager.sendServerBoleConfigsToAllPlayers(server);
            }
            return ActionResult.PASS;
        });
    }

    public void onJoin() {
        ClientWorld world = MinecraftClient.getInstance().world;
        preventMemoryLeak();
        if (world != null) {
            EntityManager.getInstance();
            ClientNetworkManager.requestServerBoleConfigs();
        }
        else Bole.LOGGER.info("Joining the world: null?!");
        this.inWorld = world != null;
    }

    public void onDisconnect() {
        this.inWorld = false;
        Bole.getInstance().setServerVersion("<unknown>");
        Bole.getInstance().setServerConfigs(Configs.getInstance());
        preventMemoryLeak();
        ClientWorld world = MinecraftClient.getInstance().world;
    }

    public void clientTick() {
        if (!this.inWorld) return;
        if (!MinecraftClient.getInstance().isPaused()) ++this.ticks;
        if (this.isScreenOpen) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null && player.currentScreenHandler instanceof GenericScreenHandler handler) {
                ++this.screenTicks;
                handler.clientTick(this.screenTicks);
            }
        }
        this.highlightManager.tick();
    }

    public boolean isHost() {
        return MinecraftClient.getInstance().getServer() != null;
    }

    public void setScreenOpen(boolean isScreenOpen) {
        this.isScreenOpen = isScreenOpen;
        this.screenTicks = -1;
    }

    public Entity getHitEntity() {
        return this.hitEntity;
    }

    public void setHitEntity(Entity hitEntity) {
        this.hitEntity = hitEntity;
    }

    public BlockPos getHitBlock() {
        return this.hitBlock;
    }

    public void setHitBlock(BlockPos hitBlock) {
        this.hitBlock = hitBlock;
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

    public BoleHomepageScreenState getHomepageScreenState() {
        return this.screenState;
    }

    public void setHomepageScreenState(BoleHomepageScreenState screenState) {
        this.screenState = screenState;
    }

    public HighlightManager getHighlightManager() {
        return highlightManager;
    }

    private void preventMemoryLeak() {
        setHitEntity(null);
        setHitBlock(null);
        setHandlerBufCache(null);
        setHomepageScreenState(null);
        this.highlightManager.clear();
    }
}
