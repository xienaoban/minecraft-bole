package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.TameableShoulderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.util.Keys;

@Mixin(TameableShoulderEntity.class)
public class MixinTameableShoulderEntity {
    @Shadow private int ticks;

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo callbackInfo) {
        if (this.ticks == Keys.SIT_ON_PLAYER_LOCK) --this.ticks;
    }
}
