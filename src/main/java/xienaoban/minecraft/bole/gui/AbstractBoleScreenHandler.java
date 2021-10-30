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

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    /**
     * Initialize some custom content (which should not be inherited by subclasses) of the handler. <br/>
     * So never invoke <code>super.initCustom()</code>.
     */
    protected abstract void initCustom();

    /**
     * Write the server-side entity data to the buf. <br/>
     * Always invoke <code>super.writeServerEntityBuf()</code> on the first line.
     */
    public abstract void writeServerEntityToBuf(PacketByteBuf buf);

    /**
     * Read the server-side entity data from the buf. <br/>
     * Always invoke <code>super.readServerEntityBuf()</code> on the first line.
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

    @Environment(EnvType.CLIENT)
    public abstract void clientTick(int ticks);

    public final void registerEntitySettingsBufHandler(String attr, EntitySettingsBufHandler bufHandler) {
        this.entitySettingsBufHandlers.put(attr, bufHandler);
    }

    @Environment(EnvType.CLIENT)
    public final void sendClientEntitySettings(String attr) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(attr);
        try {
            this.entitySettingsBufHandlers.get(attr).writeToBuf(buf);
            ClientNetworkManager.sendClientEntitySettings(buf);
        }
        catch (Exception e) {
            Bole.LOGGER.error("No EntitySettingsBufHandler is registered for attr \"" + attr + "\"");
        }
    }

    /**
     * Handle the buf sent by the client.
     * @param buf buf sent by the client
     */
    public final void setServerEntitySettings(PacketByteBuf buf) {
        String attr = buf.readString();
        try {
            this.entitySettingsBufHandlers.get(attr).readFromBuf(buf);
        }
        catch (Exception e) {
            Bole.LOGGER.error("No EntitySettingsBufHandler is registered for attr \"" + attr + "\"");
        }
    }

    public interface EntitySettingsBufHandler {
        void readFromBuf(PacketByteBuf buf);
        void writeToBuf(PacketByteBuf buf);
    }

    @Environment(EnvType.CLIENT)
    protected static Entity clientEntity() {
        return BoleClient.getInstance().getBoleTarget();
    }
}
