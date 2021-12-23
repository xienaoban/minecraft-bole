package xienaoban.minecraft.bole.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.client.BoleClient;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("TAIL"))
    private void joinWorld(CallbackInfo callbackInfo) {
        BoleClient.getInstance().onJoinWorld();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void disconnect(CallbackInfo callbackInfo) {
        BoleClient.getInstance().onDisconnect();
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo callbackInfo) {
        BoleClient.getInstance().clientTick();
    }
}
