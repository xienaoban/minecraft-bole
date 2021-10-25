package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import xienaoban.minecraft.bole.client.BoleClient;

public abstract class AbstractBoleScreenHandler<E extends Entity> extends ScreenHandler {
    protected final PlayerEntity player;
    protected final E entity;

    @SuppressWarnings("unchecked")
    public AbstractBoleScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId,
                                      PlayerInventory playerInventor, Entity entity) {
        super(type, syncId);
        this.player = playerInventor.player;
        this.entity = (E) entity;
        initCustom();
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

    @Environment(EnvType.CLIENT)
    protected static Entity clientEntity() {
        return BoleClient.getInstance().getBoleTarget();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
