package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.AxolotlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AxolotlEntity.class)
public interface IMixinAxolotlEntity {
    @Invoker
    void callSetVariant(AxolotlEntity.Variant variant);
}
