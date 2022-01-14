package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.gui.screen.BoleHorseBaseEntityScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinHorseEntity;
import xienaoban.minecraft.bole.util.Keys;

public class BoleHorseEntityScreenHandler<E extends HorseEntity> extends BoleHorseBaseEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleHorseEntityScreenHandler<HorseEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "horse_entity"), BoleHorseEntityScreenHandler::new);

    public BoleHorseEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleHorseEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleHorseEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleHorseEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_HORSE_COLOR_VARIANT, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                HorseColor color = HorseColor.byIndex(buf.readInt());
                if (isGodMode()) ((IMixinHorseEntity) entity).callSetVariant(color, entity.getMarking());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                HorseColor color = (HorseColor) args[0];
                buf.writeInt(color.getIndex());
                ((IMixinHorseEntity) entity).callSetVariant(color, entity.getMarking());
            }
        });
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_HORSE_MARKING_VARIANT, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                HorseMarking marking = HorseMarking.byIndex(buf.readInt());
                if (isGodMode()) ((IMixinHorseEntity) entity).callSetVariant(entity.getColor(), marking);
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                HorseMarking marking = (HorseMarking) args[0];
                buf.writeInt(marking.getIndex());
                ((IMixinHorseEntity) entity).callSetVariant(entity.getColor(), marking);
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
