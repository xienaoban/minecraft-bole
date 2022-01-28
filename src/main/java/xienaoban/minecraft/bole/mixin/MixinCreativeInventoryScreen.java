package xienaoban.minecraft.bole.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.core.BoleHandbookItem;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    /**
     * Add the bole handbook item to the misc item group.
     * I didn't define a new item called "bole handbook", instead I just created a writable book with custom NBT.
     * So the item can't be registered in the traditional way.
     */
    @Inject(method = "setSelectedTab(Lnet/minecraft/item/ItemGroup;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;appendStacks(Lnet/minecraft/util/collection/DefaultedList;)V", shift = At.Shift.AFTER))
    private void addBoleHandbook(ItemGroup group, CallbackInfo ci) {
        if (group == ItemGroup.MISC) this.handler.itemList.add(BoleHandbookItem.createBook());
    }
}
