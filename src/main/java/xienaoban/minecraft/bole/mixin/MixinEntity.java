package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface MixinEntity {
    @Accessor
    int getNetherPortalCooldown();
}
