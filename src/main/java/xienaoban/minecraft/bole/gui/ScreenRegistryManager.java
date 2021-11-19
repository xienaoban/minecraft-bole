package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerInventory;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.screen.*;

import java.util.HashMap;
import java.util.Map;

public class ScreenRegistryManager {
    private static final Map<Class<? extends Entity>, BoleScreenHandlerFactory<?, ?>> ENTITY_TO_HANDLER = new HashMap<>();

    public static <E extends Entity, H extends AbstractBoleScreenHandler<E>> void registerEntityToHandler(
            Class<? extends E> entityClazz, BoleScreenHandlerFactory<E, H> factory) {
        BoleScreenHandlerFactory<?, ?> previous = ENTITY_TO_HANDLER.put(entityClazz, factory);
        if (previous != null) {
            Bole.LOGGER.info("A new bole screen handler for " + entityClazz.getSimpleName() + " replaces the previous one.");
        }
    }

    public static AbstractBoleScreenHandler<?> getHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        if (entity == null) {
            return new BoleHandbookScreenHandler(syncId, playerInventory);
        }
        Class<?> clazz = entity.getClass();
        BoleScreenHandlerFactory<?, ?> factory;
        while ((factory = ENTITY_TO_HANDLER.get(clazz)) == null) {
            clazz = clazz.getSuperclass();
            if (clazz == null) {
                throw new RuntimeException("No BoleScreenHandler registered. ENTITY_TO_HANDLER size: " + ENTITY_TO_HANDLER.size());
            }
        }
        return factory.create(syncId, playerInventory, entity);
    }

    /**
     * To wrap <code>AbstractBoleScreenHandler::new</>.
     */
    @FunctionalInterface
    public interface BoleScreenHandlerFactory<E extends Entity, H extends AbstractBoleScreenHandler<E>> {
        /**
         * Creates a new bole screen handler.
         */
        H create(int syncId, PlayerInventory playerInventory, Entity entity);
    }

    public static void initServer() {
        registerEntityToHandler(Entity.class, BoleEntityScreenHandler::new);
        registerEntityToHandler(LivingEntity.class, BoleLivingEntityScreenHandler::new);
        registerEntityToHandler(MobEntity.class, BoleMobEntityScreenHandler::new);
        registerEntityToHandler(PathAwareEntity.class, BolePathAwareEntityScreenHandler::new);
        registerEntityToHandler(PassiveEntity.class, BolePassiveEntityScreenHandler::new);
        registerEntityToHandler(AnimalEntity.class, BoleAnimalEntityScreenHandler::new);
        registerEntityToHandler(HorseBaseEntity.class, BoleHorseBaseEntityScreenHandler::new);
        registerEntityToHandler(AbstractDonkeyEntity.class, BoleAbstractDonkeyEntityScreenHandler::new);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ScreenRegistry.register(BoleHandbookScreenHandler.HANDLER, BoleHandbookScreen::new);

        ScreenRegistry.register(BoleEntityScreenHandler.HANDLER, BoleEntityScreen<Entity, BoleEntityScreenHandler<Entity>>::new);
        ScreenRegistry.register(BoleLivingEntityScreenHandler.HANDLER, BoleLivingEntityScreen<LivingEntity, BoleLivingEntityScreenHandler<LivingEntity>>::new);
        ScreenRegistry.register(BoleMobEntityScreenHandler.HANDLER, BoleMobEntityScreen<MobEntity, BoleMobEntityScreenHandler<MobEntity>>::new);
        ScreenRegistry.register(BolePathAwareEntityScreenHandler.HANDLER, BolePathAwareEntityScreen<PathAwareEntity, BolePathAwareEntityScreenHandler<PathAwareEntity>>::new);
        ScreenRegistry.register(BolePassiveEntityScreenHandler.HANDLER, BolePassiveEntityScreen<PassiveEntity, BolePassiveEntityScreenHandler<PassiveEntity>>::new);
        ScreenRegistry.register(BoleAnimalEntityScreenHandler.HANDLER, BoleAnimalEntityScreen<AnimalEntity, BoleAnimalEntityScreenHandler<AnimalEntity>>::new);
        ScreenRegistry.register(BoleHorseBaseEntityScreenHandler.HANDLER, BoleHorseBaseEntityScreen<HorseBaseEntity, BoleHorseBaseEntityScreenHandler<HorseBaseEntity>>::new);
        ScreenRegistry.register(BoleAbstractDonkeyEntityScreenHandler.HANDLER, BoleAbstractDonkeyEntityScreen<AbstractDonkeyEntity, BoleAbstractDonkeyEntityScreenHandler<AbstractDonkeyEntity>>::new);
    }
}
