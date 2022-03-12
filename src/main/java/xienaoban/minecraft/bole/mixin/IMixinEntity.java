package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IMixinEntity {
    @Accessor("GLOWING_FLAG_INDEX")
    static int getGlowingFlagIndex() {
        throw new AssertionError();
    }

    @Accessor
    boolean getInNetherPortal();

    @Accessor
    int getNetherPortalCooldown();

    @Accessor
    void setNetherPortalCooldown(int netherPortalCooldown);

    @Accessor
    void setTouchingWater(boolean touchingWater);

    @Invoker
    void callSetFlag(int index, boolean value);

    @Invoker
    void callTickNetherPortalCooldown();
}
