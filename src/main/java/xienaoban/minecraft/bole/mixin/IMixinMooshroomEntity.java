package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.MooshroomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MooshroomEntity.class)
public interface IMixinMooshroomEntity {
    @Invoker
    void callSetType(MooshroomEntity.Type type);

    @Mixin(MooshroomEntity.Type.class)
    interface IMixinMooshroomEntityType {
        @Accessor
        String getName();

        @Invoker
        static MooshroomEntity.Type callFromName(String name) {
            throw new AssertionError();
        }
    }
}
