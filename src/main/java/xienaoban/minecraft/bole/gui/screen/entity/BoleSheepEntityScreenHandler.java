package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.gui.screen.BoleAnimalEntityScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinEatGrassGoal;
import xienaoban.minecraft.bole.mixin.IMixinSheepEntity;
import xienaoban.minecraft.bole.util.Keys;

public class BoleSheepEntityScreenHandler<E extends SheepEntity> extends BoleAnimalEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleSheepEntityScreenHandler<SheepEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "sheep_entity"), BoleSheepEntityScreenHandler::new);

    public BoleSheepEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleSheepEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleSheepEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleSheepEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_EAT_GRASS, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                IMixinEatGrassGoal goal = (IMixinEatGrassGoal) ((IMixinSheepEntity) entity).getEatGrassGoal();
                if (goal.getTimer() == 0) {
                    goal.setTimer(-1);
                }
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
    protected void initCustom() {
    }

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
