package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.gui.screen.BoleMerchantEntityScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinVillagerEntity;
import xienaoban.minecraft.bole.util.Keys;

public class BoleVillagerEntityScreenHandler<E extends VillagerEntity> extends BoleMerchantEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleVillagerEntityScreenHandler<VillagerEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "villager_entity"), BoleVillagerEntityScreenHandler::new);

    protected int entityRestocksToday = 0;

    public BoleVillagerEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleVillagerEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleVillagerEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleVillagerEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_RESTOCK, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                entity.restock();
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                ++entityRestocksToday;
            }
        });
    }

    @Override
    protected void initCustom() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
    }

    @Override
    public void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        buf.writeInt(((IMixinVillagerEntity)this.entity).getRestocksToday());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        this.entityRestocksToday = buf.readInt();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
