package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.TameableShoulderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TameableShoulderEntity.class)
public interface IMixinTameableShoulderEntity {
    @Accessor("READY_TO_SIT_COOLDOWN")
    static int getReadyToSitCooldown() {
        throw new AssertionError();
    }

    @Accessor
    int getTicks();

    @Accessor
    void setTicks(int ticks);
}
