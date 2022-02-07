package xienaoban.minecraft.bole.gui;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerInventory;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.gui.screen.entity.*;
import xienaoban.minecraft.bole.gui.screen.homepage.BoleHomepageScreenHandler;
import xienaoban.minecraft.bole.gui.screen.tree.*;

import java.util.HashMap;
import java.util.Map;

public class ScreenHandlerManager {
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
        registerEntityToHandler(Entity.class, BoleEntityScreenHandler::new);
        registerEntityToHandler(LivingEntity.class, BoleLivingEntityScreenHandler::new);
        registerEntityToHandler(MobEntity.class, BoleMobEntityScreenHandler::new);
        registerEntityToHandler(PathAwareEntity.class, BolePathAwareEntityScreenHandler::new);
        registerEntityToHandler(PassiveEntity.class, BolePassiveEntityScreenHandler::new);
        registerEntityToHandler(AnimalEntity.class, BoleAnimalEntityScreenHandler::new);
        registerEntityToHandler(HorseBaseEntity.class, BoleHorseBaseEntityScreenHandler::new);
        registerEntityToHandler(AbstractDonkeyEntity.class, BoleAbstractDonkeyEntityScreenHandler::new);
        registerEntityToHandler(LlamaEntity.class, BoleLlamaEntityScreenHandler::new);
        registerEntityToHandler(MerchantEntity.class, BoleMerchantEntityScreenHandler::new);
        registerEntityToHandler(VillagerEntity.class, BoleVillagerEntityScreenHandler::new);
        registerEntityToHandler(SheepEntity.class, BoleSheepEntityScreenHandler::new);
        registerEntityToHandler(BeeEntity.class, BoleBeeEntityScreenHandler::new);
        registerEntityToHandler(TameableEntity.class, BoleTameableEntityScreenHandler::new);
        registerEntityToHandler(TameableShoulderEntity.class, BoleTameableShoulderEntityScreenHandler::new);
        registerEntityToHandler(ParrotEntity.class, BoleParrotEntityScreenHandler::new);
        registerEntityToHandler(CatEntity.class, BoleCatEntityScreenHandler::new);
        registerEntityToHandler(PandaEntity.class, BolePandaEntityScreenHandler::new);
        registerEntityToHandler(AxolotlEntity.class, BoleAxolotlEntityScreenHandler::new);
        registerEntityToHandler(HorseEntity.class, BoleHorseEntityScreenHandler::new);
        registerEntityToHandler(WanderingTraderEntity.class, BoleWanderingTraderEntityScreenHandler::new);
    }
}
