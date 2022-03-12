package xienaoban.minecraft.bole.mixin;

import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DyeColor.class)
public interface IMixinDyeColor {
    @Accessor("VALUES")
    static DyeColor[] getValues() {
        throw new AssertionError();
    }
}
