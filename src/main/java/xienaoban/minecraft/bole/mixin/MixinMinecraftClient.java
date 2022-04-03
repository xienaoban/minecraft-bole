package xienaoban.minecraft.bole.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.core.BoleHandler;

@Environment(value= EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Unique private ClientPlayerEntity player;
    @Unique private Hand hand;
    @Unique private ItemStack handItem;

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void disconnect(CallbackInfo callbackInfo) {
        BoleClient.getInstance().onDisconnect();
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo callbackInfo) {
        BoleClient.getInstance().clientTick();
    }

    @Redirect(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack getHandItem(ClientPlayerEntity player, Hand hand) {
        this.player = player;
        this.hand = hand;
        return this.handItem = player.getStackInHand(hand);
    }

    /**
     * The logic of using the bole handbook item.
     */
    @Inject(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private void onUseBoleHandbook(CallbackInfo ci) {
        if (Bole.isBoleHandbook(this.handItem)) {
            this.player.swingHand(this.hand);
            BoleHandler.tryOpenBoleScreen((MinecraftClient)(Object) this);
            ci.cancel();
        }
    }
}
