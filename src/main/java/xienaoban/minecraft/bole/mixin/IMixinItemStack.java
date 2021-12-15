package xienaoban.minecraft.bole.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStack.class)
public interface IMixinItemStack {
    @Accessor("item")
    Item getRealItem();
}
