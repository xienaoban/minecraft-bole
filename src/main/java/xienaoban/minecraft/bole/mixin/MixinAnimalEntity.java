package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.config.Configs;

import java.util.Objects;

@Mixin(AnimalEntity.class)
public class MixinAnimalEntity {
    /**
     * Modify the damage amount to zero if source is hostile mobs or anvils.
     */
    @ModifyVariable(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float damage(float amount, DamageSource source) {
        AnimalEntity that = (AnimalEntity)(Object) this;
        if (((that instanceof TameableEntity te && te.isTamed()) || (that instanceof AbstractHorseEntity hbe && hbe.isTame()))) {
            Configs configs = that.world instanceof ServerWorld ? Configs.getInstance() : Bole.getInstance().getServerConfigs();
            if (!configs.isBlockAccidentalInjuryToPets()) return amount;
            if (source instanceof EntityDamageSource entityDamageSource) {
                Entity attacker;
                if (source instanceof ProjectileDamageSource projectileDamageSource) attacker = projectileDamageSource.getAttacker();
                else attacker = entityDamageSource.getSource();
                return Bole.isMonster(attacker) || isOtherPlayer(that, attacker) ? amount : 0.0F;
            }
            return source == DamageSource.CRAMMING || source == DamageSource.OUT_OF_WORLD ? amount : 0.0F;
        }
        return amount;
    }

    private static boolean isOtherPlayer(AnimalEntity that, Entity attacker) {
        if (attacker instanceof PlayerEntity) {
             if (that instanceof TameableEntity t) return !Objects.equals(t.getOwnerUuid(), attacker.getUuid());
            if (that instanceof AbstractHorseEntity t) return !Objects.equals(t.getOwnerUuid(), attacker.getUuid());
        }
        return false;
    }
}
