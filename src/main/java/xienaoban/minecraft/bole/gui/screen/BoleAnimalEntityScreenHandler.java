package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public class BoleAnimalEntityScreenHandler<E extends AnimalEntity> extends BolePassiveEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleAnimalEntityScreenHandler<AnimalEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "animal_entity"), BoleAnimalEntityScreenHandler::new);

    public BoleAnimalEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleAnimalEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleAnimalEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleAnimalEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
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
    public void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
