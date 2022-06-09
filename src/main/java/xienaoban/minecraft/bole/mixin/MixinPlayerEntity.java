package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xienaoban.minecraft.bole.config.Configs;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Redirect(method = "tickMovement()V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;fallDistance:F", opcode = Opcodes.GETFIELD))
    private float injected(PlayerEntity instance) {
        float time = Configs.getInstance().getFallDistanceToDropShoulderEntities();
        return instance.fallDistance / (Math.max(time, 0.001F) * 2);
    }
}
