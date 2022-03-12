package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBoleScreenHandler<E extends Entity> extends GenericScreenHandler {
    public final E entity;
    private final Map<String, EntitySettingsBufHandler> entitySettingsBufHandlers;

    public AbstractBoleScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId,
                                      PlayerInventory playerInventory, Entity entity) {
        super(type, syncId, playerInventory);
        this.entity = MiscUtil.cast(entity);
        this.entitySettingsBufHandlers = new HashMap<>();
        if (this.isServer) initServer();
        else initClient();
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

    /**
     * Gets the entity the client-side player is aiming at.
     */
    @Environment(EnvType.CLIENT)
    public static Entity clientEntity() {
        Entity entity = BoleClient.getInstance().getHitEntity();
        // Set BoleTarget to null to avoid memory leak.
        BoleClient.getInstance().setHitEntity(null);
        return entity;
    }

    public void sendOverlayMessage(Text text) {
        ServerPlayerEntity player = (ServerPlayerEntity) this.player;
        ServerNetworkManager.sendOverlayMessage(text, player.server, player);
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

    public interface EntitySettingsBufHandler {
        void readFromBuf(PacketByteBuf buf);
        void writeToBuf(PacketByteBuf buf, Object... args);
    }
}
