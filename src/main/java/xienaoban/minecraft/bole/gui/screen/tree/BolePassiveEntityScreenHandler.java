package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.mixin.IMixinPassiveEntity;
import xienaoban.minecraft.bole.util.Keys;

public class BolePassiveEntityScreenHandler<E extends PassiveEntity> extends BolePathAwareEntityScreenHandler<E> {
    public static final ScreenHandlerType<BolePassiveEntityScreenHandler<PassiveEntity>> HANDLER
            = register(new Identifier(Keys.NAMESPACE, "passive_entity"), BolePassiveEntityScreenHandler::new);

    public static final int BABY_LOCK = -0x70000000;    // Do not use 0x80000000, as it may overflow into positive.

    @Environment(EnvType.CLIENT)
    protected int entityBreedingAge;

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
                entityBreedingAge = age;
                buf.writeInt(age);
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
        calculateClientEntityBreedingAge();
    }

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        buf.writeInt(((IMixinPassiveEntity)this.entity).getBreedingAgeValue());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        this.entityBreedingAge = buf.readInt();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }

    @Environment(EnvType.CLIENT)
    private void calculateClientEntityBreedingAge() {
        int age = this.entityBreedingAge;
        if (age == 0x80000000 || age >= 0) {
            return;
        }
        this.entityBreedingAge = age + 1;
    }
}
