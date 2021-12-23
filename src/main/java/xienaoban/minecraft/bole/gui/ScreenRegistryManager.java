package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerInventory;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.screen.*;
import xienaoban.minecraft.bole.gui.screen.entity.*;
import xienaoban.minecraft.bole.gui.screen.handbook.BoleHandbookScreen;
import xienaoban.minecraft.bole.gui.screen.handbook.BoleHandbookScreenHandler;

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
        registerEntityToHandler(MerchantEntity.class, BoleMerchantEntityScreenHandler::new);
        registerEntityToHandler(VillagerEntity.class, BoleVillagerEntityScreenHandler::new);
        registerEntityToHandler(SheepEntity.class, BoleSheepEntityScreenHandler::new);
        registerEntityToHandler(BeeEntity.class, BoleBeeEntityScreenHandler::new);
        registerEntityToHandler(TameableEntity.class, BoleTameableEntityScreenHandler::new);
        registerEntityToHandler(TameableShoulderEntity.class, BoleTameableShoulderEntityScreenHandler::new);
        registerEntityToHandler(ParrotEntity.class, BoleParrotEntityScreenHandler::new);
        registerEntityToHandler(CatEntity.class, BoleCatEntityScreenHandler::new);
        registerEntityToHandler(PandaEntity.class, BolePandaEntityScreenHandler::new);
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
        ScreenRegistry.register(BoleMerchantEntityScreenHandler.HANDLER, BoleMerchantEntityScreen<MerchantEntity, BoleMerchantEntityScreenHandler<MerchantEntity>>::new);
        ScreenRegistry.register(BoleVillagerEntityScreenHandler.HANDLER, BoleVillagerEntityScreen<VillagerEntity, BoleVillagerEntityScreenHandler<VillagerEntity>>::new);
        ScreenRegistry.register(BoleSheepEntityScreenHandler.HANDLER, BoleSheepEntityScreen<SheepEntity, BoleSheepEntityScreenHandler<SheepEntity>>::new);
        ScreenRegistry.register(BoleBeeEntityScreenHandler.HANDLER, BoleBeeEntityScreen<BeeEntity, BoleBeeEntityScreenHandler<BeeEntity>>::new);
        ScreenRegistry.register(BoleTameableEntityScreenHandler.HANDLER, BoleTameableEntityScreen<TameableEntity, BoleTameableEntityScreenHandler<TameableEntity>>::new);
        ScreenRegistry.register(BoleTameableShoulderEntityScreenHandler.HANDLER, BoleTameableShoulderEntityScreen<TameableShoulderEntity, BoleTameableShoulderEntityScreenHandler<TameableShoulderEntity>>::new);
        ScreenRegistry.register(BoleParrotEntityScreenHandler.HANDLER, BoleParrotEntityScreen<ParrotEntity, BoleParrotEntityScreenHandler<ParrotEntity>>::new);
        ScreenRegistry.register(BoleCatEntityScreenHandler.HANDLER, BoleCatEntityScreen<CatEntity, BoleCatEntityScreenHandler<CatEntity>>::new);
        ScreenRegistry.register(BolePandaEntityScreenHandler.HANDLER, BolePandaEntityScreen<PandaEntity, BolePandaEntityScreenHandler<PandaEntity>>::new);
    }
}
