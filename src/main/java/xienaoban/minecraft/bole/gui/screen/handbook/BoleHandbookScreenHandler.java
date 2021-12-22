package xienaoban.minecraft.bole.gui.screen.handbook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.client.EntityManager;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public final class BoleHandbookScreenHandler extends AbstractBoleScreenHandler<Entity> {
    public static final ScreenHandlerType<BoleHandbookScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "handbook"), BoleHandbookScreenHandler::new);

    EntityManager entityManager;

    public BoleHandbookScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId, playerInventory, null);
    }

    @Override
    protected void initServer() {
        super.initServer();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void initClient() {
        super.initClient();
        this.entityManager = EntityManager.getInstance();
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {}

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {}

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {}
}
