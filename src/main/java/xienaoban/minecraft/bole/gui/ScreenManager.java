package xienaoban.minecraft.bole.gui;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
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
        HandledScreens.register(BoleHomepageScreenHandler.HANDLER, BoleHomepageScreen::new);

        // package: misc
        HandledScreens.register(BeehiveScreenHandler.HANDLER, BeehiveScreen::new);
        HandledScreens.register(MerchantInventoryScreenHandler.HANDLER, MerchantInventoryScreen::new);

        // package: tree
        HandledScreens.register(BoleEntityScreenHandler.HANDLER, BoleEntityScreen<Entity, BoleEntityScreenHandler<Entity>>::new);
        HandledScreens.register(BoleLivingEntityScreenHandler.HANDLER, BoleLivingEntityScreen<LivingEntity, BoleLivingEntityScreenHandler<LivingEntity>>::new);
        HandledScreens.register(BoleMobEntityScreenHandler.HANDLER, BoleMobEntityScreen<MobEntity, BoleMobEntityScreenHandler<MobEntity>>::new);
        HandledScreens.register(BolePathAwareEntityScreenHandler.HANDLER, BolePathAwareEntityScreen<PathAwareEntity, BolePathAwareEntityScreenHandler<PathAwareEntity>>::new);
        HandledScreens.register(BolePassiveEntityScreenHandler.HANDLER, BolePassiveEntityScreen<PassiveEntity, BolePassiveEntityScreenHandler<PassiveEntity>>::new);
        HandledScreens.register(BoleAnimalEntityScreenHandler.HANDLER, BoleAnimalEntityScreen<AnimalEntity, BoleAnimalEntityScreenHandler<AnimalEntity>>::new);
        HandledScreens.register(BoleAbstractHorseEntityScreenHandler.HANDLER, BoleAbstractHorseEntityScreen<AbstractHorseEntity, BoleAbstractHorseEntityScreenHandler<AbstractHorseEntity>>::new);
        HandledScreens.register(BoleAbstractDonkeyEntityScreenHandler.HANDLER, BoleAbstractDonkeyEntityScreen<AbstractDonkeyEntity, BoleAbstractDonkeyEntityScreenHandler<AbstractDonkeyEntity>>::new);
        HandledScreens.register(BoleMerchantEntityScreenHandler.HANDLER, BoleMerchantEntityScreen<MerchantEntity, BoleMerchantEntityScreenHandler<MerchantEntity>>::new);
        HandledScreens.register(BoleTameableEntityScreenHandler.HANDLER, BoleTameableEntityScreen<TameableEntity, BoleTameableEntityScreenHandler<TameableEntity>>::new);
        HandledScreens.register(BoleTameableShoulderEntityScreenHandler.HANDLER, BoleTameableShoulderEntityScreen<TameableShoulderEntity, BoleTameableShoulderEntityScreenHandler<TameableShoulderEntity>>::new);
        HandledScreens.register(BoleWaterCreatureEntityScreenHandler.HANDLER, BoleWaterCreatureEntityScreen<WaterCreatureEntity, BoleWaterCreatureEntityScreenHandler<WaterCreatureEntity>>::new);
        HandledScreens.register(BoleFishEntityScreenHandler.HANDLER, BoleFishEntityScreen<FishEntity, BoleFishEntityScreenHandler<FishEntity>>::new);
        HandledScreens.register(BoleSchoolingFishEntityScreenHandler.HANDLER, BoleSchoolingFishEntityScreen<SchoolingFishEntity, BoleSchoolingFishEntityScreenHandler<SchoolingFishEntity>>::new);

        // package: entity
        HandledScreens.register(BoleLlamaEntityScreenHandler.HANDLER, BoleLlamaEntityScreen<LlamaEntity, BoleLlamaEntityScreenHandler<LlamaEntity>>::new);
        HandledScreens.register(BoleVillagerEntityScreenHandler.HANDLER, BoleVillagerEntityScreen<VillagerEntity, BoleVillagerEntityScreenHandler<VillagerEntity>>::new);
        HandledScreens.register(BoleSheepEntityScreenHandler.HANDLER, BoleSheepEntityScreen<SheepEntity, BoleSheepEntityScreenHandler<SheepEntity>>::new);
        HandledScreens.register(BoleRabbitEntityScreenHandler.HANDLER, BoleRabbitEntityScreen<RabbitEntity, BoleRabbitEntityScreenHandler<RabbitEntity>>::new);
        HandledScreens.register(BoleBeeEntityScreenHandler.HANDLER, BoleBeeEntityScreen<BeeEntity, BoleBeeEntityScreenHandler<BeeEntity>>::new);
        HandledScreens.register(BoleParrotEntityScreenHandler.HANDLER, BoleParrotEntityScreen<ParrotEntity, BoleParrotEntityScreenHandler<ParrotEntity>>::new);
        HandledScreens.register(BoleCatEntityScreenHandler.HANDLER, BoleCatEntityScreen<CatEntity, BoleCatEntityScreenHandler<CatEntity>>::new);
        HandledScreens.register(BolePandaEntityScreenHandler.HANDLER, BolePandaEntityScreen<PandaEntity, BolePandaEntityScreenHandler<PandaEntity>>::new);
        HandledScreens.register(BoleAxolotlEntityScreenHandler.HANDLER, BoleAxolotlEntityScreen<AxolotlEntity, BoleAxolotlEntityScreenHandler<AxolotlEntity>>::new);
        HandledScreens.register(BoleHorseEntityScreenHandler.HANDLER, BoleHorseEntityScreen<HorseEntity, BoleHorseEntityScreenHandler<HorseEntity>>::new);
        HandledScreens.register(BoleWanderingTraderEntityScreenHandler.HANDLER, BoleWanderingTraderEntityScreen<WanderingTraderEntity, BoleWanderingTraderEntityScreenHandler<WanderingTraderEntity>>::new);
        HandledScreens.register(BoleTropicalFishEntityScreenHandler.HANDLER, BoleTropicalFishEntityScreen<TropicalFishEntity, BoleTropicalFishEntityScreenHandler<TropicalFishEntity>>::new);
        HandledScreens.register(BoleDolphinEntityScreenHandler.HANDLER, BoleDolphinEntityScreen<DolphinEntity, BoleDolphinEntityScreenHandler<DolphinEntity>>::new);
        HandledScreens.register(BoleFoxEntityScreenHandler.HANDLER, BoleFoxEntityScreen<FoxEntity, BoleFoxEntityScreenHandler<FoxEntity>>::new);
        HandledScreens.register(BoleGoatEntityScreenHandler.HANDLER, BoleGoatEntityScreen<GoatEntity, BoleGoatEntityScreenHandler<GoatEntity>>::new);
    }
}
