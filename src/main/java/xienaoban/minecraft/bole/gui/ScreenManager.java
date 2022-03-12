package xienaoban.minecraft.bole.gui;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.*;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.screen.entity.*;
import xienaoban.minecraft.bole.gui.screen.homepage.BoleHomepageScreen;
import xienaoban.minecraft.bole.gui.screen.homepage.BoleHomepageScreenHandler;
import xienaoban.minecraft.bole.gui.screen.misc.BeehiveScreen;
import xienaoban.minecraft.bole.gui.screen.misc.BeehiveScreenHandler;
import xienaoban.minecraft.bole.gui.screen.misc.MerchantInventoryScreen;
import xienaoban.minecraft.bole.gui.screen.misc.MerchantInventoryScreenHandler;
import xienaoban.minecraft.bole.gui.screen.tree.*;

@Environment(EnvType.CLIENT)
public class ScreenManager {
    public static Screen getConfigScreen(Screen parent) {
        return getAutoConfigScreen(parent);
    }

    private static Screen getAutoConfigScreen(Screen parent) {
        return AutoConfig.getConfigScreen(Configs.class, parent).get();
    }

    public static void init() {
        // package: homepage
        ScreenRegistry.register(BoleHomepageScreenHandler.HANDLER, BoleHomepageScreen::new);

        // package: misc
        ScreenRegistry.register(BeehiveScreenHandler.HANDLER, BeehiveScreen::new);
        ScreenRegistry.register(MerchantInventoryScreenHandler.HANDLER, MerchantInventoryScreen::new);

        // package: tree & entity
        ScreenRegistry.register(BoleEntityScreenHandler.HANDLER, BoleEntityScreen<Entity, BoleEntityScreenHandler<Entity>>::new);
        ScreenRegistry.register(BoleLivingEntityScreenHandler.HANDLER, BoleLivingEntityScreen<LivingEntity, BoleLivingEntityScreenHandler<LivingEntity>>::new);
        ScreenRegistry.register(BoleMobEntityScreenHandler.HANDLER, BoleMobEntityScreen<MobEntity, BoleMobEntityScreenHandler<MobEntity>>::new);
        ScreenRegistry.register(BolePathAwareEntityScreenHandler.HANDLER, BolePathAwareEntityScreen<PathAwareEntity, BolePathAwareEntityScreenHandler<PathAwareEntity>>::new);
        ScreenRegistry.register(BolePassiveEntityScreenHandler.HANDLER, BolePassiveEntityScreen<PassiveEntity, BolePassiveEntityScreenHandler<PassiveEntity>>::new);
        ScreenRegistry.register(BoleAnimalEntityScreenHandler.HANDLER, BoleAnimalEntityScreen<AnimalEntity, BoleAnimalEntityScreenHandler<AnimalEntity>>::new);
        ScreenRegistry.register(BoleHorseBaseEntityScreenHandler.HANDLER, BoleHorseBaseEntityScreen<HorseBaseEntity, BoleHorseBaseEntityScreenHandler<HorseBaseEntity>>::new);
        ScreenRegistry.register(BoleAbstractDonkeyEntityScreenHandler.HANDLER, BoleAbstractDonkeyEntityScreen<AbstractDonkeyEntity, BoleAbstractDonkeyEntityScreenHandler<AbstractDonkeyEntity>>::new);
        ScreenRegistry.register(BoleLlamaEntityScreenHandler.HANDLER, BoleLlamaEntityScreen<LlamaEntity, BoleLlamaEntityScreenHandler<LlamaEntity>>::new);
        ScreenRegistry.register(BoleMerchantEntityScreenHandler.HANDLER, BoleMerchantEntityScreen<MerchantEntity, BoleMerchantEntityScreenHandler<MerchantEntity>>::new);
        ScreenRegistry.register(BoleVillagerEntityScreenHandler.HANDLER, BoleVillagerEntityScreen<VillagerEntity, BoleVillagerEntityScreenHandler<VillagerEntity>>::new);
        ScreenRegistry.register(BoleSheepEntityScreenHandler.HANDLER, BoleSheepEntityScreen<SheepEntity, BoleSheepEntityScreenHandler<SheepEntity>>::new);
        ScreenRegistry.register(BoleRabbitEntityScreenHandler.HANDLER, BoleRabbitEntityScreen<RabbitEntity, BoleRabbitEntityScreenHandler<RabbitEntity>>::new);
        ScreenRegistry.register(BoleBeeEntityScreenHandler.HANDLER, BoleBeeEntityScreen<BeeEntity, BoleBeeEntityScreenHandler<BeeEntity>>::new);
        ScreenRegistry.register(BoleTameableEntityScreenHandler.HANDLER, BoleTameableEntityScreen<TameableEntity, BoleTameableEntityScreenHandler<TameableEntity>>::new);
        ScreenRegistry.register(BoleTameableShoulderEntityScreenHandler.HANDLER, BoleTameableShoulderEntityScreen<TameableShoulderEntity, BoleTameableShoulderEntityScreenHandler<TameableShoulderEntity>>::new);
        ScreenRegistry.register(BoleParrotEntityScreenHandler.HANDLER, BoleParrotEntityScreen<ParrotEntity, BoleParrotEntityScreenHandler<ParrotEntity>>::new);
        ScreenRegistry.register(BoleCatEntityScreenHandler.HANDLER, BoleCatEntityScreen<CatEntity, BoleCatEntityScreenHandler<CatEntity>>::new);
        ScreenRegistry.register(BolePandaEntityScreenHandler.HANDLER, BolePandaEntityScreen<PandaEntity, BolePandaEntityScreenHandler<PandaEntity>>::new);
        ScreenRegistry.register(BoleAxolotlEntityScreenHandler.HANDLER, BoleAxolotlEntityScreen<AxolotlEntity, BoleAxolotlEntityScreenHandler<AxolotlEntity>>::new);
        ScreenRegistry.register(BoleHorseEntityScreenHandler.HANDLER, BoleHorseEntityScreen<HorseEntity, BoleHorseEntityScreenHandler<HorseEntity>>::new);
        ScreenRegistry.register(BoleWanderingTraderEntityScreenHandler.HANDLER, BoleWanderingTraderEntityScreen<WanderingTraderEntity, BoleWanderingTraderEntityScreenHandler<WanderingTraderEntity>>::new);
        ScreenRegistry.register(BoleWaterCreatureEntityScreenHandler.HANDLER, BoleWaterCreatureEntityScreen<WaterCreatureEntity, BoleWaterCreatureEntityScreenHandler<WaterCreatureEntity>>::new);
        ScreenRegistry.register(BoleFishEntityScreenHandler.HANDLER, BoleFishEntityScreen<FishEntity, BoleFishEntityScreenHandler<FishEntity>>::new);
        ScreenRegistry.register(BoleSchoolingFishEntityScreenHandler.HANDLER, BoleSchoolingFishEntityScreen<SchoolingFishEntity, BoleSchoolingFishEntityScreenHandler<SchoolingFishEntity>>::new);
        ScreenRegistry.register(BoleTropicalFishEntityScreenHandler.HANDLER, BoleTropicalFishEntityScreen<TropicalFishEntity, BoleTropicalFishEntityScreenHandler<TropicalFishEntity>>::new);
        ScreenRegistry.register(BoleDolphinEntityScreenHandler.HANDLER, BoleDolphinEntityScreen<DolphinEntity, BoleDolphinEntityScreenHandler<DolphinEntity>>::new);
    }
}
