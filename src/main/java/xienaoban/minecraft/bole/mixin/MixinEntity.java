package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.gui.screen.tree.BoleEntityScreenHandler;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(method = "tickNetherPortalCooldown()V", at = @At("HEAD"), cancellable = true)
    private void tickNetherPortalCooldown(CallbackInfo callbackInfo) {
        int cooldown = ((IMixinEntity) this).getNetherPortalCooldown();
        if (cooldown == BoleEntityScreenHandler.NETHER_PORTAL_LOCK) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "resetNetherPortalCooldown()V", at = @At("HEAD"), cancellable = true)
    private void resetNetherPortalCooldown(CallbackInfo callbackInfo) {
        int cooldown = ((IMixinEntity) this).getNetherPortalCooldown();
        if (cooldown == BoleEntityScreenHandler.NETHER_PORTAL_LOCK) {
            callbackInfo.cancel();
        }
    }
}
