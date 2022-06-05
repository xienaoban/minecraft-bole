package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import xienaoban.minecraft.bole.client.PlayerDataCacheManager;
import xienaoban.minecraft.bole.gui.screen.tree.BolePathAwareEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class BoleAllayEntityScreenHandler<E extends AllayEntity> extends BolePathAwareEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleAllayEntityScreenHandler<AllayEntity>> HANDLER
            = register(new Identifier(Keys.NAMESPACE, "allay_entity"), BoleAllayEntityScreenHandler::new);

    @Environment(EnvType.CLIENT)
    protected Text likedPlayerName;

    @Environment(EnvType.CLIENT)
    protected GlobalPos likedNoteBlockPosition;

    public BoleAllayEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleAllayEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleAllayEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleAllayEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
    }

    @Override
    protected void initServer() {
        super.initServer();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void initClient() {
        super.initClient();
    }

    @Override
    protected void initCustom() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
    }

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        Brain<AllayEntity> brain = this.entity.getBrain();
        buf.writeOptional(brain.getOptionalMemory(MemoryModuleType.LIKED_PLAYER), PacketByteBuf::writeUuid);
        buf.writeOptional(brain.getOptionalMemory(MemoryModuleType.LIKED_NOTEBLOCK), PacketByteBuf::writeGlobalPos);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        buf.readOptional(PacketByteBuf::readUuid).ifPresentOrElse(uuid -> {
            this.likedPlayerName = PlayerDataCacheManager.getInstance().getPlayerName(uuid);
        }, () -> this.likedPlayerName = null);
        buf.readOptional(PacketByteBuf::readGlobalPos).ifPresentOrElse(
                globalPos -> this.likedNoteBlockPosition = globalPos,
                () -> this.likedNoteBlockPosition = null);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
