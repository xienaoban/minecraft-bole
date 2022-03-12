package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.DolphinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DolphinEntity.class)
public interface IMixinDolphinEntity {
    @Accessor("MAX_MOISTNESS")
    static int getMaxMoistness() {
        throw new AssertionError();
    }
}
