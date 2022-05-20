package xienaoban.minecraft.bole.gui;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.gui.screen.entity.*;
import xienaoban.minecraft.bole.gui.screen.homepage.BoleHomepageScreenHandler;
import xienaoban.minecraft.bole.gui.screen.misc.BeehiveScreenHandler;
import xienaoban.minecraft.bole.gui.screen.misc.MerchantInventoryScreenHandler;
import xienaoban.minecraft.bole.gui.screen.tree.*;

import java.util.HashMap;
import java.util.Map;

public class ScreenHandlerManager {
    private static final Map<Class<? extends Entity>, BoleScreenHandlerFactory<?, ?>> ENTITY_TO_HANDLER = new HashMap<>();

    /**
     * Just to trigger the initialization of the <code>public static final ScreenHandlerType HANDLER</code>
     * in the screen handler classes.
     * The code below has no effect. But without this code, `cinit` of the screen handler classes will not be
     * executed on the dedicated server side, which will cause the handlers to not be registered.
     */
    public static void registerHandler(ScreenHandlerType<?> handlerType) {
        if (Registry.SCREEN_HANDLER.getId(handlerType) == null) throw new RuntimeException("ScreenHandlerType not found.");
    }

    /**
     * Matches entities with corresponding screen handlers.
     */
    public static <E extends Entity, H extends AbstractBoleScreenHandler<E>> void registerHandler(
            ScreenHandlerType<H> handlerType, Class<? extends E> entityClazz, BoleScreenHandlerFactory<E, H> factory) {
        registerHandler(handlerType);
        BoleScreenHandlerFactory<?, ?> previous = ENTITY_TO_HANDLER.put(entityClazz, factory);
        if (previous != null) {
            Bole.LOGGER.info("A new bole screen handler for " + entityClazz.getSimpleName() + " replaces the previous one.");
        }
    }

    public static AbstractBoleScreenHandler<?> getHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        if (entity == null) {
            return new BoleHomepageScreenHandler(syncId, playerInventory);
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

    public static void init() {
        // package: homepage
        registerHandler(BoleHomepageScreenHandler.HANDLER);

        // package: misc
        registerHandler(BeehiveScreenHandler.HANDLER);
        registerHandler(MerchantInventoryScreenHandler.HANDLER);

        // package: tree
        registerHandler(BoleEntityScreenHandler.HANDLER, Entity.class, BoleEntityScreenHandler::new);
        registerHandler(BoleLivingEntityScreenHandler.HANDLER, LivingEntity.class, BoleLivingEntityScreenHandler::new);
        registerHandler(BoleMobEntityScreenHandler.HANDLER, MobEntity.class, BoleMobEntityScreenHandler::new);
        registerHandler(BolePathAwareEntityScreenHandler.HANDLER, PathAwareEntity.class, BolePathAwareEntityScreenHandler::new);
        registerHandler(BolePassiveEntityScreenHandler.HANDLER, PassiveEntity.class, BolePassiveEntityScreenHandler::new);
        registerHandler(BoleAnimalEntityScreenHandler.HANDLER, AnimalEntity.class, BoleAnimalEntityScreenHandler::new);
        registerHandler(BoleAbstractHorseEntityScreenHandler.HANDLER, AbstractHorseEntity.class, BoleAbstractHorseEntityScreenHandler::new);
        registerHandler(BoleAbstractDonkeyEntityScreenHandler.HANDLER, AbstractDonkeyEntity.class, BoleAbstractDonkeyEntityScreenHandler::new);
        registerHandler(BoleMerchantEntityScreenHandler.HANDLER, MerchantEntity.class, BoleMerchantEntityScreenHandler::new);
        registerHandler(BoleTameableEntityScreenHandler.HANDLER, TameableEntity.class, BoleTameableEntityScreenHandler::new);
        registerHandler(BoleTameableShoulderEntityScreenHandler.HANDLER, TameableShoulderEntity.class, BoleTameableShoulderEntityScreenHandler::new);
        registerHandler(BoleWaterCreatureEntityScreenHandler.HANDLER, WaterCreatureEntity.class, BoleWaterCreatureEntityScreenHandler::new);
        registerHandler(BoleFishEntityScreenHandler.HANDLER, FishEntity.class, BoleFishEntityScreenHandler::new);
        registerHandler(BoleSchoolingFishEntityScreenHandler.HANDLER, SchoolingFishEntity.class, BoleSchoolingFishEntityScreenHandler::new);

        // package: entity
        registerHandler(BoleLlamaEntityScreenHandler.HANDLER, LlamaEntity.class, BoleLlamaEntityScreenHandler::new);
        registerHandler(BoleVillagerEntityScreenHandler.HANDLER, VillagerEntity.class, BoleVillagerEntityScreenHandler::new);
        registerHandler(BoleSheepEntityScreenHandler.HANDLER, SheepEntity.class, BoleSheepEntityScreenHandler::new);
        registerHandler(BoleRabbitEntityScreenHandler.HANDLER, RabbitEntity.class, BoleRabbitEntityScreenHandler::new);
        registerHandler(BoleBeeEntityScreenHandler.HANDLER, BeeEntity.class, BoleBeeEntityScreenHandler::new);
        registerHandler(BoleParrotEntityScreenHandler.HANDLER, ParrotEntity.class, BoleParrotEntityScreenHandler::new);
        registerHandler(BoleCatEntityScreenHandler.HANDLER, CatEntity.class, BoleCatEntityScreenHandler::new);
        registerHandler(BolePandaEntityScreenHandler.HANDLER, PandaEntity.class, BolePandaEntityScreenHandler::new);
        registerHandler(BoleAxolotlEntityScreenHandler.HANDLER, AxolotlEntity.class, BoleAxolotlEntityScreenHandler::new);
        registerHandler(BoleHorseEntityScreenHandler.HANDLER, HorseEntity.class, BoleHorseEntityScreenHandler::new);
        registerHandler(BoleWanderingTraderEntityScreenHandler.HANDLER, WanderingTraderEntity.class, BoleWanderingTraderEntityScreenHandler::new);
        registerHandler(BoleTropicalFishEntityScreenHandler.HANDLER, TropicalFishEntity.class, BoleTropicalFishEntityScreenHandler::new);
        registerHandler(BoleDolphinEntityScreenHandler.HANDLER, DolphinEntity.class, BoleDolphinEntityScreenHandler::new);
        registerHandler(BoleFoxEntityScreenHandler.HANDLER, FoxEntity.class, BoleFoxEntityScreenHandler::new);
        registerHandler(BoleGoatEntityScreenHandler.HANDLER, GoatEntity.class, BoleGoatEntityScreenHandler::new);
    }
}
