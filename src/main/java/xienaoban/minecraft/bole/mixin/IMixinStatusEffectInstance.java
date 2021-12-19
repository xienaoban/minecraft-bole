package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatusEffectInstance.class)
public interface IMixinStatusEffectInstance {
    @Accessor
    void setDuration(int duration);
}
