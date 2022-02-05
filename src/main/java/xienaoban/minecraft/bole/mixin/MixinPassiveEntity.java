package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.gui.screen.tree.BolePassiveEntityScreenHandler;

@Mixin(PassiveEntity.class)
public class MixinPassiveEntity {
    /**
     * Lock breeding age
     */
    @Inject(method = "tickMovement()V", at = @At("TAIL"))
    private void tickMovement(CallbackInfo callbackInfo) {
        IMixinPassiveEntity entity = (IMixinPassiveEntity) this;
        int age = entity.getBreedingAgeValue();
        if (age < PassiveEntity.BABY_AGE) {
            entity.setBreedingAgeValue(BolePassiveEntityScreenHandler.BABY_LOCK);
        }
    }
}
