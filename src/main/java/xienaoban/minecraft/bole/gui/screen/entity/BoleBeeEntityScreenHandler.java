package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.screen.BoleAnimalEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Objects;

public class BoleBeeEntityScreenHandler<E extends BeeEntity> extends BoleAnimalEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleBeeEntityScreenHandler<BeeEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "bee_entity"), BoleBeeEntityScreenHandler::new);

    @Environment(EnvType.CLIENT)
    protected BlockPos entityBeehivePosition;

    public BoleBeeEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleBeeEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleBeeEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleBeeEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_RESET_BEEHIVE, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                MiscUtil.setFieldValue(entity, BeeEntity.class, "hivePos", null);
            }

            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {}
        });
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
        BlockPos pos = this.entity.getHivePos();
        buf.writeBoolean(pos != null);
        if (pos != null) {
            buf.writeBlockPos(pos);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        if (buf.readBoolean()) {
            BlockPos pos = buf.readBlockPos();
            if (Objects.equals(this.entity.world.getBlockState(pos).getBlock(), Blocks.BEE_NEST)
                    || Objects.equals(this.entity.world.getBlockState(pos).getBlock(), Blocks.BEEHIVE)) {
                this.entityBeehivePosition = pos;
            }
            else {
                this.entityBeehivePosition = null;
                Bole.LOGGER.error("Invalid beehive position.");
            }
        }
        else {
            this.entityBeehivePosition = null;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
