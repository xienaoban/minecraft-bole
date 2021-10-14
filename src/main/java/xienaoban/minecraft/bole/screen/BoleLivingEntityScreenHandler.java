package xienaoban.minecraft.bole.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.util.Keys;

public class BoleLivingEntityScreenHandler<E extends LivingEntity> extends BoleEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleLivingEntityScreenHandler<LivingEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "living_entity"), BoleLivingEntityScreenHandler::new);

    public BoleLivingEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleLivingEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleLivingEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleLivingEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        Bole.LOGGER.info("name: " + this.entity.getEntityName());
        Bole.LOGGER.info("name: " + this.entity.getCustomName());
        Bole.LOGGER.info("box: " + this.entity.getBoundingBox().getXLength() + ", " + this.entity.getBoundingBox().getYLength() + ", " + this.entity.getBoundingBox().getZLength());
        Bole.LOGGER.info("age: " + this.entity.age);
        Bole.LOGGER.info("death: " + this.entity.deathTime);
        Bole.LOGGER.info("yam: " + this.entity.bodyYaw);
        Bole.LOGGER.info("max health: " + this.entity.getMaxHealth());
        Bole.LOGGER.info("health: " + this.entity.getHealth());
    }
}
