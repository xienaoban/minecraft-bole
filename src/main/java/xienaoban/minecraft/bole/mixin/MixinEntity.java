package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.gui.screen.tree.BoleEntityScreenHandler;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(method = "tickPortalCooldown()V", at = @At("HEAD"), cancellable = true)
    private void tickPortalCooldown(CallbackInfo callbackInfo) {
        int cooldown = ((IMixinEntity) this).getPortalCooldown();
        if (cooldown == BoleEntityScreenHandler.NETHER_PORTAL_LOCK) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "resetPortalCooldown()V", at = @At("HEAD"), cancellable = true)
    private void resetPortalCooldown(CallbackInfo callbackInfo) {
        int cooldown = ((IMixinEntity) this).getPortalCooldown();
        if (cooldown == BoleEntityScreenHandler.NETHER_PORTAL_LOCK) {
            callbackInfo.cancel();
        }
    }
}
