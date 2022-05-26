package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FoxEntity.class)
public interface IMixinFoxEntity {
    @Invoker
    void callSetType(FoxEntity.Type type);

    @Mixin(FoxEntity.Type.class)
    interface IMixinFoxEntityType {
        @Accessor("TYPES")
        static FoxEntity.Type[] getTypes() {
            throw new AssertionError();
        }
    }
}
