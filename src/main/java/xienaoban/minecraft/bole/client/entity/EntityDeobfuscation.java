package xienaoban.minecraft.bole.client.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.village.Merchant;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.event.listener.VibrationListener;
import xienaoban.minecraft.bole.Bole;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class EntityDeobfuscation {
    /**
     * Generates deobfuscation code of <code>buildDeobfuscationMap()</code>.
     * This method can only be invoked in the debug environment of the project  (or you will get obfuscated classes
     * like "net.minecraft.entity.class_12345").
     * @see #buildDeobfuscationMap
     */
    static void __generateDeobfuscationMapCode(EntityManager entityManager) {
        final Set<Class<?>> interfazes = new HashSet<>();
        Bole.LOGGER.info("// classes");
        entityManager.dfsEntityTree(false, (cur, depth) -> {
            Class<?> clazz = cur.getClazz();
            Bole.LOGGER.info("/*" + " ".repeat(depth * 2) + "*/"
                    + "map.put(" + clazz.getSimpleName() + ".class, \""
                    + clazz.getName() + "\");");
            for (Class<?> interfaze : clazz.getInterfaces()) {
                if (interfaze.getPackageName().indexOf("net.minecraft") != 0) continue;
                interfazes.add(interfaze);
            }
            return true;
        });
        Bole.LOGGER.info("// interfaces");
        interfazes.stream().sorted(Comparator.comparing(Class::getName)).forEach(clazz ->
                Bole.LOGGER.info("map.put(" + clazz.getSimpleName() + ".class, \"" + clazz.getName() + "\");")
        );
        throw new NullPointerException("DeobfuscationMapCode generated successfully.");
    }

    /**
     * When invoking <code>AnyClass.class.getName()</code> in the real game, you will get strings like
     * "net.minecraft.entity.class_12345" instead of "net.minecraft.entity.AnyClass".
     * @param map This map is to get real names of the entity classes.
     */
    static void buildDeobfuscationMap(Map<Class<?>, String> map) {
        // classes
        /**/map.put(Entity.class, "net.minecraft.entity.Entity");
        /*  */map.put(LivingEntity.class, "net.minecraft.entity.LivingEntity");
        /*    */map.put(MobEntity.class, "net.minecraft.entity.mob.MobEntity");
        /*      */map.put(PathAwareEntity.class, "net.minecraft.entity.mob.PathAwareEntity");
        /*        */map.put(AllayEntity.class, "net.minecraft.entity.passive.AllayEntity");
        /*        */map.put(PassiveEntity.class, "net.minecraft.entity.passive.PassiveEntity");
        /*          */map.put(AnimalEntity.class, "net.minecraft.entity.passive.AnimalEntity");
        /*            */map.put(AxolotlEntity.class, "net.minecraft.entity.passive.AxolotlEntity");
        /*            */map.put(BeeEntity.class, "net.minecraft.entity.passive.BeeEntity");
        /*            */map.put(TameableEntity.class, "net.minecraft.entity.passive.TameableEntity");
        /*              */map.put(CatEntity.class, "net.minecraft.entity.passive.CatEntity");
        /*              */map.put(TameableShoulderEntity.class, "net.minecraft.entity.passive.TameableShoulderEntity");
        /*                */map.put(ParrotEntity.class, "net.minecraft.entity.passive.ParrotEntity");
        /*              */map.put(WolfEntity.class, "net.minecraft.entity.passive.WolfEntity");
        /*            */map.put(ChickenEntity.class, "net.minecraft.entity.passive.ChickenEntity");
        /*            */map.put(CowEntity.class, "net.minecraft.entity.passive.CowEntity");
        /*              */map.put(MooshroomEntity.class, "net.minecraft.entity.passive.MooshroomEntity");
        /*            */map.put(AbstractHorseEntity.class, "net.minecraft.entity.passive.AbstractHorseEntity");
        /*              */map.put(AbstractDonkeyEntity.class, "net.minecraft.entity.passive.AbstractDonkeyEntity");
        /*                */map.put(DonkeyEntity.class, "net.minecraft.entity.passive.DonkeyEntity");
        /*                */map.put(LlamaEntity.class, "net.minecraft.entity.passive.LlamaEntity");
        /*                  */map.put(TraderLlamaEntity.class, "net.minecraft.entity.passive.TraderLlamaEntity");
        /*                */map.put(MuleEntity.class, "net.minecraft.entity.passive.MuleEntity");
        /*              */map.put(HorseEntity.class, "net.minecraft.entity.passive.HorseEntity");
        /*              */map.put(SkeletonHorseEntity.class, "net.minecraft.entity.mob.SkeletonHorseEntity");
        /*              */map.put(ZombieHorseEntity.class, "net.minecraft.entity.mob.ZombieHorseEntity");
        /*            */map.put(FoxEntity.class, "net.minecraft.entity.passive.FoxEntity");
        /*            */map.put(FrogEntity.class, "net.minecraft.entity.passive.FrogEntity");
        /*            */map.put(GoatEntity.class, "net.minecraft.entity.passive.GoatEntity");
        /*            */map.put(HoglinEntity.class, "net.minecraft.entity.mob.HoglinEntity");
        /*            */map.put(OcelotEntity.class, "net.minecraft.entity.passive.OcelotEntity");
        /*            */map.put(PandaEntity.class, "net.minecraft.entity.passive.PandaEntity");
        /*            */map.put(PigEntity.class, "net.minecraft.entity.passive.PigEntity");
        /*            */map.put(PolarBearEntity.class, "net.minecraft.entity.passive.PolarBearEntity");
        /*            */map.put(RabbitEntity.class, "net.minecraft.entity.passive.RabbitEntity");
        /*            */map.put(SheepEntity.class, "net.minecraft.entity.passive.SheepEntity");
        /*            */map.put(StriderEntity.class, "net.minecraft.entity.passive.StriderEntity");
        /*            */map.put(TurtleEntity.class, "net.minecraft.entity.passive.TurtleEntity");
        /*          */map.put(MerchantEntity.class, "net.minecraft.entity.passive.MerchantEntity");
        /*            */map.put(VillagerEntity.class, "net.minecraft.entity.passive.VillagerEntity");
        /*            */map.put(WanderingTraderEntity.class, "net.minecraft.entity.passive.WanderingTraderEntity");
        /*        */map.put(HostileEntity.class, "net.minecraft.entity.mob.HostileEntity");
        /*          */map.put(BlazeEntity.class, "net.minecraft.entity.mob.BlazeEntity");
        /*          */map.put(SpiderEntity.class, "net.minecraft.entity.mob.SpiderEntity");
        /*            */map.put(CaveSpiderEntity.class, "net.minecraft.entity.mob.CaveSpiderEntity");
        /*          */map.put(CreeperEntity.class, "net.minecraft.entity.mob.CreeperEntity");
        /*          */map.put(ZombieEntity.class, "net.minecraft.entity.mob.ZombieEntity");
        /*            */map.put(DrownedEntity.class, "net.minecraft.entity.mob.DrownedEntity");
        /*            */map.put(HuskEntity.class, "net.minecraft.entity.mob.HuskEntity");
        /*            */map.put(ZombieVillagerEntity.class, "net.minecraft.entity.mob.ZombieVillagerEntity");
        /*            */map.put(ZombifiedPiglinEntity.class, "net.minecraft.entity.mob.ZombifiedPiglinEntity");
        /*          */map.put(GuardianEntity.class, "net.minecraft.entity.mob.GuardianEntity");
        /*            */map.put(ElderGuardianEntity.class, "net.minecraft.entity.mob.ElderGuardianEntity");
        /*          */map.put(EndermanEntity.class, "net.minecraft.entity.mob.EndermanEntity");
        /*          */map.put(EndermiteEntity.class, "net.minecraft.entity.mob.EndermiteEntity");
        /*          */map.put(PatrolEntity.class, "net.minecraft.entity.mob.PatrolEntity");
        /*            */map.put(RaiderEntity.class, "net.minecraft.entity.raid.RaiderEntity");
        /*              */map.put(IllagerEntity.class, "net.minecraft.entity.mob.IllagerEntity");
        /*                */map.put(SpellcastingIllagerEntity.class, "net.minecraft.entity.mob.SpellcastingIllagerEntity");
        /*                  */map.put(EvokerEntity.class, "net.minecraft.entity.mob.EvokerEntity");
        /*                  */map.put(IllusionerEntity.class, "net.minecraft.entity.mob.IllusionerEntity");
        /*                */map.put(PillagerEntity.class, "net.minecraft.entity.mob.PillagerEntity");
        /*                */map.put(VindicatorEntity.class, "net.minecraft.entity.mob.VindicatorEntity");
        /*              */map.put(RavagerEntity.class, "net.minecraft.entity.mob.RavagerEntity");
        /*              */map.put(WitchEntity.class, "net.minecraft.entity.mob.WitchEntity");
        /*          */map.put(GiantEntity.class, "net.minecraft.entity.mob.GiantEntity");
        /*          */map.put(AbstractPiglinEntity.class, "net.minecraft.entity.mob.AbstractPiglinEntity");
        /*            */map.put(PiglinEntity.class, "net.minecraft.entity.mob.PiglinEntity");
        /*            */map.put(PiglinBruteEntity.class, "net.minecraft.entity.mob.PiglinBruteEntity");
        /*          */map.put(SilverfishEntity.class, "net.minecraft.entity.mob.SilverfishEntity");
        /*          */map.put(AbstractSkeletonEntity.class, "net.minecraft.entity.mob.AbstractSkeletonEntity");
        /*            */map.put(SkeletonEntity.class, "net.minecraft.entity.mob.SkeletonEntity");
        /*            */map.put(StrayEntity.class, "net.minecraft.entity.mob.StrayEntity");
        /*            */map.put(WitherSkeletonEntity.class, "net.minecraft.entity.mob.WitherSkeletonEntity");
        /*          */map.put(VexEntity.class, "net.minecraft.entity.mob.VexEntity");
        /*          */map.put(WardenEntity.class, "net.minecraft.entity.mob.WardenEntity");
        /*          */map.put(WitherEntity.class, "net.minecraft.entity.boss.WitherEntity");
        /*          */map.put(ZoglinEntity.class, "net.minecraft.entity.mob.ZoglinEntity");
        /*        */map.put(WaterCreatureEntity.class, "net.minecraft.entity.mob.WaterCreatureEntity");
        /*          */map.put(FishEntity.class, "net.minecraft.entity.passive.FishEntity");
        /*            */map.put(SchoolingFishEntity.class, "net.minecraft.entity.passive.SchoolingFishEntity");
        /*              */map.put(CodEntity.class, "net.minecraft.entity.passive.CodEntity");
        /*              */map.put(SalmonEntity.class, "net.minecraft.entity.passive.SalmonEntity");
        /*              */map.put(TropicalFishEntity.class, "net.minecraft.entity.passive.TropicalFishEntity");
        /*            */map.put(PufferfishEntity.class, "net.minecraft.entity.passive.PufferfishEntity");
        /*            */map.put(TadpoleEntity.class, "net.minecraft.entity.passive.TadpoleEntity");
        /*          */map.put(DolphinEntity.class, "net.minecraft.entity.passive.DolphinEntity");
        /*          */map.put(SquidEntity.class, "net.minecraft.entity.passive.SquidEntity");
        /*            */map.put(GlowSquidEntity.class, "net.minecraft.entity.passive.GlowSquidEntity");
        /*        */map.put(GolemEntity.class, "net.minecraft.entity.passive.GolemEntity");
        /*          */map.put(IronGolemEntity.class, "net.minecraft.entity.passive.IronGolemEntity");
        /*          */map.put(ShulkerEntity.class, "net.minecraft.entity.mob.ShulkerEntity");
        /*          */map.put(SnowGolemEntity.class, "net.minecraft.entity.passive.SnowGolemEntity");
        /*      */map.put(AmbientEntity.class, "net.minecraft.entity.mob.AmbientEntity");
        /*        */map.put(BatEntity.class, "net.minecraft.entity.passive.BatEntity");
        /*      */map.put(EnderDragonEntity.class, "net.minecraft.entity.boss.dragon.EnderDragonEntity");
        /*      */map.put(FlyingEntity.class, "net.minecraft.entity.mob.FlyingEntity");
        /*        */map.put(GhastEntity.class, "net.minecraft.entity.mob.GhastEntity");
        /*        */map.put(PhantomEntity.class, "net.minecraft.entity.mob.PhantomEntity");
        /*      */map.put(SlimeEntity.class, "net.minecraft.entity.mob.SlimeEntity");
        /*        */map.put(MagmaCubeEntity.class, "net.minecraft.entity.mob.MagmaCubeEntity");
        /*    */map.put(ArmorStandEntity.class, "net.minecraft.entity.decoration.ArmorStandEntity");

        // interfaces
        map.put(SkinOverlayOwner.class, "net.minecraft.client.render.entity.feature.SkinOverlayOwner");
        map.put(AngledModelEntity.class, "net.minecraft.entity.AngledModelEntity");
        map.put(Bucketable.class, "net.minecraft.entity.Bucketable");
        map.put(CrossbowUser.class, "net.minecraft.entity.CrossbowUser");
        map.put(Flutterer.class, "net.minecraft.entity.Flutterer");
        map.put(InteractionObserver.class, "net.minecraft.entity.InteractionObserver");
        map.put(InventoryOwner.class, "net.minecraft.entity.InventoryOwner");
        map.put(ItemSteerable.class, "net.minecraft.entity.ItemSteerable");
        map.put(JumpingMount.class, "net.minecraft.entity.JumpingMount");
        map.put(Npc.class, "net.minecraft.entity.Npc");
        map.put(RideableInventory.class, "net.minecraft.entity.RideableInventory");
        map.put(Saddleable.class, "net.minecraft.entity.Saddleable");
        map.put(Shearable.class, "net.minecraft.entity.Shearable");
        map.put(Tameable.class, "net.minecraft.entity.Tameable");
        map.put(RangedAttackMob.class, "net.minecraft.entity.ai.RangedAttackMob");
        map.put(Angerable.class, "net.minecraft.entity.mob.Angerable");
        map.put(Hoglin.class, "net.minecraft.entity.mob.Hoglin");
        map.put(Monster.class, "net.minecraft.entity.mob.Monster");
        map.put(InventoryChangedListener.class, "net.minecraft.inventory.InventoryChangedListener");
        map.put(CommandOutput.class, "net.minecraft.server.command.CommandOutput");
        map.put(Nameable.class, "net.minecraft.util.Nameable");
        map.put(Merchant.class, "net.minecraft.village.Merchant");
        map.put(VillagerDataContainer.class, "net.minecraft.village.VillagerDataContainer");
        map.put(EntityLike.class, "net.minecraft.world.entity.EntityLike");
        map.put(VibrationListener.Callback.class, "net.minecraft.world.event.listener.VibrationListener$Callback");
    }
}
