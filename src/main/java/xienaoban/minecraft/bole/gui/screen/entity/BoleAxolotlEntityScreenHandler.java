package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAnimalEntityScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinAxolotlEntity;
import xienaoban.minecraft.bole.util.Keys;

public class BoleAxolotlEntityScreenHandler<E extends AxolotlEntity> extends BoleAnimalEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleAxolotlEntityScreenHandler<AxolotlEntity>> HANDLER
            = register(new Identifier(Keys.NAMESPACE, "axolotl_entity"), BoleAxolotlEntityScreenHandler::new);

    public BoleAxolotlEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleAxolotlEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleAxolotlEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleAxolotlEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_AXOLOTL_VARIANT, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                AxolotlEntity.Variant variant = AxolotlEntity.Variant.VARIANTS[buf.readInt()];
                if (isGod()) ((IMixinAxolotlEntity) entity).callSetVariant(variant);
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                AxolotlEntity.Variant variant = (AxolotlEntity.Variant) args[0];
                buf.writeInt(variant.getId());
                ((IMixinAxolotlEntity) entity).callSetVariant(variant);
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
