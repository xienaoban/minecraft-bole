package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xienaoban.minecraft.bole.Bole;

@Mixin(AnimalEntity.class)
public class MixinAnimalEntity {
    @ModifyVariable(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float damage(float amount, DamageSource source) {
        AnimalEntity that = (AnimalEntity)(Object) this;
        if (((that instanceof TameableEntity te && te.isTamed()) || (that instanceof HorseBaseEntity hbe && hbe.isTame()))) {
            if (source == DamageSource.DROWN  || source == DamageSource.DRYOUT || source == DamageSource.ANVIL) return amount;
            return Bole.getInstance().getServerConfigs().isInvulnerablePets() ? 0.0F : amount;
        }
        return amount;
    }
}
