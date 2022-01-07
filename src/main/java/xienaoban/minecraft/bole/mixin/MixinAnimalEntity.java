package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AnimalEntity.class)
public class MixinAnimalEntity {
    @ModifyVariable(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float damage(float amount, DamageSource source) {
        AnimalEntity that = (AnimalEntity)(Object) this;
        if (that instanceof TameableEntity && ((TameableEntity) that).isTamed()
                || that instanceof HorseBaseEntity && ((HorseBaseEntity) that).isTame()) {
            if (source == DamageSource.DROWN || source == DamageSource.ANVIL) return amount;
            return 0.0F;
        }
        return amount;
    }
}
