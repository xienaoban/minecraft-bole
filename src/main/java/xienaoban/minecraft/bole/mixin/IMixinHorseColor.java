package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.HorseColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseColor.class)
public interface IMixinHorseColor {
    @Accessor("VALUES")
    static HorseColor[] getValue() {
        throw new AssertionError();
    }
}
