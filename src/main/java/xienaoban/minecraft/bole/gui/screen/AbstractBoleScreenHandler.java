package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBoleScreenHandler<E extends Entity> extends ScreenHandler {
    public final E entity;
    public final PlayerEntity player;
    private final Map<String, EntitySettingsBufHandler> entitySettingsBufHandlers;

    public AbstractBoleScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId,
                                      PlayerInventory playerInventory, Entity entity) {
        super(type, syncId);
        this.entity = MiscUtil.cast(entity);
        this.player = playerInventory.player;
        this.entitySettingsBufHandlers = new HashMap<>();
        if (this.player instanceof ServerPlayerEntity) {
            initServer();
        }
        else if (this.player instanceof ClientPlayerEntity) {
            initClient();
        }
        initCustom();
    }

    protected void initServer() {
        ServerPlayerEntity p = (ServerPlayerEntity) this.player;
        ServerNetworkManager.sendServerEntityData(this, p.server, p);
    }

    @Environment(EnvType.CLIENT)
    protected void initClient() {
        PacketByteBuf buf = BoleClient.getInstance().getHandlerBufCache();
        if (buf != null) {
            tryReadServerEntityFromBuf(buf);
            BoleClient.getInstance().setHandlerBufCache(null);
        }
    }

    /**
     * Initializes some custom content (which should not be inherited by subclasses) of the handler. <br/>
     * So never invoke <code>super.initCustom()</code>.
     */
    protected abstract void initCustom();

    /**
     * Invoked at the beginning of each client tick.
     *
     * @param ticks tick count
     */
    @Environment(EnvType.CLIENT)
    public abstract void clientTick(int ticks);

    /**
     * Writes the server-side entity data to the buf. <br/>
     * Always invoke <code>super.writeServerEntityBuf()</code> on the first line.
     *
     * @param buf buf to send to the client
     */
    protected abstract void writeServerEntityToBuf(PacketByteBuf buf);

    /**
     * Reads the server-side entity data from the buf. <br/>
     * Always invoke <code>super.readServerEntityBuf()</code> on the first line.
     *
     * @param buf buf sent from the server
     */
    @Environment(EnvType.CLIENT)
    protected abstract void readServerEntityFromBuf(PacketByteBuf buf);

    /**
     * Reset some properties of the client-side entity when closing the screen. <br/>
     * It is because some properties of the entity only work on the server side, but we use them in the method
     * <code>writeServerEntityBuf()</code>. So it is better to reset them to their initial values when closing
     * the window (although these properties may not be accessed by other codes on the client side). <br/>
     * Always invoke <code>super.resetClientEntityServerState()</code> on the first line.
     */
    @Environment(EnvType.CLIENT)
    protected abstract void resetClientEntityServerProperties();

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    /**
     * Gets the entity the client-side player is aiming at.
     */
    @Environment(EnvType.CLIENT)
    protected static Entity clientEntity() {
        Entity entity = BoleClient.getInstance().getBoleTarget();
        // Set BoleTarget to null to avoid memory leak.
        BoleClient.getInstance().setBoleTarget(null);
        return entity;
    }

    public GameMode getGameMode() {
        if (this.player instanceof ClientPlayerEntity) {
            ClientPlayerInteractionManager manager = MinecraftClient.getInstance().interactionManager;
            return manager != null ? manager.getCurrentGameMode() : null;
        }
        return ((ServerPlayerEntity) this.player).interactionManager.getGameMode();
    }

    public boolean isGodMode() {
        return getGameMode() == GameMode.CREATIVE || getGameMode() == GameMode.SPECTATOR;
    }

    /**
     * Registers an EntitySettingsBufHandler to handle the specific buf sent from the client to the server.
     *
     * @param settingId Setting ID, to identity the buf
     */
    public final void registerEntitySettingsBufHandler(String settingId, EntitySettingsBufHandler bufHandler) {
        this.entitySettingsBufHandlers.put(settingId, bufHandler);
    }

    /**
     * Sends settings of the entity to the server.
     *
     * @param settingId Setting ID, to identity the buf
     */
    @Environment(EnvType.CLIENT)
    public final void sendClientEntitySettings(String settingId, Object... args) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(settingId);
        try {
            this.entitySettingsBufHandlers.get(settingId).writeToBuf(buf, args);
            ClientNetworkManager.sendClientEntitySettings(buf);
        }
        catch (Exception e) {
            Bole.LOGGER.error("No EntitySettingsBufHandler is registered for settingId \"" + settingId + "\"");
        }
    }

    /**
     * Receives settings of the entity from the client and sets to the server-side entity.
     *
     * @param buf buf sent from the client
     */
    public final void setServerEntitySettings(PacketByteBuf buf) {
        String settingId = buf.readString();
        try {
            this.entitySettingsBufHandlers.get(settingId).readFromBuf(buf);
        }
        catch (Exception e) {
            Bole.LOGGER.error("No EntitySettingsBufHandler is registered for settingId \"" + settingId + "\"");
        }
    }

    public final void tryWriteServerEntityFromBuf(PacketByteBuf buf) {
        try {
            writeServerEntityToBuf(buf);
        }
        catch (Exception e) {
            Bole.LOGGER.warn(e);
        }
    }

    public final void tryReadServerEntityFromBuf(PacketByteBuf buf) {
        try {
            readServerEntityFromBuf(buf);
        }
        catch (Exception e) {
            Bole.LOGGER.warn(e);
        }
    }

    public final boolean trySpendItems(ItemStack ...targetStacks) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack target : targetStacks) {
            Item item = target.getItem();
            int neededCount = target.getCount();
            int count = 0;
            for (int i = inventory.size() - 1; i >= 0; --i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.getItem().equals(item) || stack.hasNbt()) {
                    continue;
                }
                count += stack.getCount();
                if (count >= neededCount) {
                    break;
                }
            }
            if (count < neededCount) {
                return false;
            }
        }
        for (ItemStack target : targetStacks) {
            Item item = target.getItem();
            int leftCount = target.getCount();
            for (int i = inventory.size() - 1; i >= 0; --i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.getItem().equals(item) || stack.hasNbt()) {
                    continue;
                }
                int decrementCount = Math.min(stack.getCount(), leftCount);
                stack.decrement(decrementCount);
                leftCount -= decrementCount;
                if (leftCount == 0) {
                    break;
                }
            }
        }
        return true;
    }

    public interface EntitySettingsBufHandler {
        void readFromBuf(PacketByteBuf buf);
        void writeToBuf(PacketByteBuf buf, Object... args);
    }
}
