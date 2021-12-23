package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.gui.screen.BoleTameableEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class BoleCatEntityScreenHandler<E extends CatEntity> extends BoleTameableEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleCatEntityScreenHandler<CatEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "cat_entity"), BoleCatEntityScreenHandler::new);

    public BoleCatEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleCatEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleCatEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleCatEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_CAT_VARIANT, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                if (isGodMode()) entity.setCatType(buf.readInt());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                int variant = (Integer) args[0];
                buf.writeInt(variant);
                entity.setCatType(variant);
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
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
