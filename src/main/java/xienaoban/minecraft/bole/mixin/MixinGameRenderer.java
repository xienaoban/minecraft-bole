package xienaoban.minecraft.bole.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.client.EventsManager;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow @Final private MinecraftClient client;

    private final EventsManager.ShoulderEntityFirstPersonRenderer renderer = new EventsManager.ShoulderEntityFirstPersonRenderer();

    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;", shift = At.Shift.BEFORE, ordinal = 0))
    private void renderShoulderEntity(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        this.renderer.renderShoulderEntity(client);
    }
}
