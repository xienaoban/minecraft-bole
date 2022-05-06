package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoxEntity.Type.class)
public interface IMixinFoxEntityType {
    @Accessor("TYPES")
    static FoxEntity.Type[] getTypes() {
        throw new AssertionError();
    }
}
