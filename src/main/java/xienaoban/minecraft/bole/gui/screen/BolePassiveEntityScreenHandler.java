package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.mixin.IMixinPassiveEntity;
import xienaoban.minecraft.bole.util.Keys;

import java.lang.reflect.Field;

public class BolePassiveEntityScreenHandler<E extends PassiveEntity> extends BolePathAwareEntityScreenHandler<E> {
    public static final ScreenHandlerType<BolePassiveEntityScreenHandler<PassiveEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "passive_entity"), BolePassiveEntityScreenHandler::new);

    private static final Field breedingAgeField;

    static {
        try {
            breedingAgeField = PassiveEntity.class.getDeclaredField("breedingAge");
            breedingAgeField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public BolePassiveEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BolePassiveEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BolePassiveEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BolePassiveEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_BABY, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                entity.setBreedingAge(buf.readInt());   // use setBreedingAge on the server side
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                int age = (Integer) args[0];
                ((IMixinPassiveEntity)entity).setBreedingAgeValue(age);
                buf.writeInt(age);
            }
        });
    }

    @Override
    protected void initCustom() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
        calculateClientEntityBreedingAge();
    }

    @Override
    public void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        buf.writeInt(((IMixinPassiveEntity)this.entity).getBreedingAgeValue());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        ((IMixinPassiveEntity)this.entity).setBreedingAgeValue(buf.readInt());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
        ((IMixinPassiveEntity)this.entity).setBreedingAgeValue(0);
    }

    @Environment(EnvType.CLIENT)
    private void calculateClientEntityBreedingAge() {
        IMixinPassiveEntity entity = (IMixinPassiveEntity)this.entity;
        int age = entity.getBreedingAgeValue();
        if (age == 0x80000000 || age >= 0) {
            return;
        }
        entity.setBreedingAgeValue(age + 1);
    }
}
