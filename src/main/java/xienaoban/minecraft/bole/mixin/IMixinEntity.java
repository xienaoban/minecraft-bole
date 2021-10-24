package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IMixinEntity {
    @Accessor
    boolean getInNetherPortal();

    @Accessor
    int getNetherPortalCooldown();

    @Accessor
    void setNetherPortalCooldown(int netherPortalCooldown);

    @Invoker
    void callTickNetherPortalCooldown();
}
