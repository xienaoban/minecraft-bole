package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.HorseMarking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseMarking.class)
public interface IMixinHorseMarking {
    @Accessor("VALUES")
    static HorseMarking[] getValue() {
        throw new AssertionError();
    }
}
