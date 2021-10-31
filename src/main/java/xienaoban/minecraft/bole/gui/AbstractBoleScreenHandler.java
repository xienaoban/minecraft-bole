package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.network.ClientNetworkManager;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBoleScreenHandler<E extends Entity> extends ScreenHandler {
    protected final E entity;
    protected final PlayerEntity player;
    private final Map<String, EntitySettingsBufHandler> entitySettingsBufHandlers;

    @SuppressWarnings("unchecked")
    public AbstractBoleScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId,
                                      PlayerInventory playerInventor, Entity entity) {
        super(type, syncId);
        this.entity = (E) entity;
        this.player = playerInventor.player;
        this.entitySettingsBufHandlers = new HashMap<>();
        initCustom();
    }

    /**
     * Initializes some custom content (which should not be inherited by subclasses) of the handler. <br/>
     * So never invoke <code>super.initCustom()</code>.
     */
    protected abstract void initCustom();

    /**
     * Writes the server-side entity data to the buf. <br/>
     * Always invoke <code>super.writeServerEntityBuf()</code> on the first line.
     *
     * @param buf buf to send to the client
     */
    public abstract void writeServerEntityToBuf(PacketByteBuf buf);

    /**
     * Reads the server-side entity data from the buf. <br/>
     * Always invoke <code>super.readServerEntityBuf()</code> on the first line.
     *
     * @param buf buf sent from the server
     */
    @Environment(EnvType.CLIENT)
    public abstract void readServerEntityFromBuf(PacketByteBuf buf);

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
     * Invoked at the beginning of each client tick.
     *
     * @param ticks tick count
     */
    @Environment(EnvType.CLIENT)
    public abstract void clientTick(int ticks);

    /**
     * Gets the entity the client-side player is aiming at.
     */
    @Environment(EnvType.CLIENT)
    protected static Entity clientEntity() {
        return BoleClient.getInstance().getBoleTarget();
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
    public final void sendClientEntitySettings(String settingId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(settingId);
        try {
            this.entitySettingsBufHandlers.get(settingId).writeToBuf(buf);
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

    public interface EntitySettingsBufHandler {
        void readFromBuf(PacketByteBuf buf);
        void writeToBuf(PacketByteBuf buf);
    }
}
