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
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerType;
import xienaoban.minecraft.bole.gui.screen.BoleMerchantEntityScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinVillagerEntity;
import xienaoban.minecraft.bole.util.Keys;

public class BoleVillagerEntityScreenHandler<E extends VillagerEntity> extends BoleMerchantEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleVillagerEntityScreenHandler<VillagerEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "villager_entity"), BoleVillagerEntityScreenHandler::new);

    @Environment(EnvType.CLIENT)
    protected int entityRestocksToday;

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
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_CLOTHING, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                VillagerType type = Registry.VILLAGER_TYPE.get(Identifier.tryParse(buf.readString()));
                entity.setVillagerData(entity.getVillagerData().withType(type));
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                VillagerType type = (VillagerType) args[0];
                buf.writeString(type.toString());
                entity.setVillagerData(entity.getVillagerData().withType(type));
            }
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
        buf.writeInt(((IMixinVillagerEntity)this.entity).getRestocksToday());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        this.entityRestocksToday = buf.readInt();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
