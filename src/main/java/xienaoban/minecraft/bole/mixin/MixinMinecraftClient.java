package xienaoban.minecraft.bole.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.core.BoleHandbookItem;
import xienaoban.minecraft.bole.core.BoleHandler;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Unique private ItemStack handItem;

    @Inject(method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V", at = @At("TAIL"))
    private void joinWorld(CallbackInfo callbackInfo) {
        // BoleClient.getInstance().onJoin();
    }

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
        return this.handItem = player.getStackInHand(hand);
    }

    @Inject(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private void onUseBoleHandbook(CallbackInfo ci) {
        ItemStack book = this.handItem;
        if (book != null && book.isOf(Items.WRITABLE_BOOK) && book.hasNbt()) {
            assert book.getNbt() != null;
            if (book.getNbt().contains(BoleHandbookItem.ID, NbtElement.STRING_TYPE)) {
                BoleHandler.tryOpenBoleScreen((MinecraftClient)(Object) this);
                ci.cancel();
            }
        }
    }
}
