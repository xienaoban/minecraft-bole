package xienaoban.minecraft.bole.mixin;

import net.minecraft.item.BlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockItem.class)
public interface IMixinBlockItem {
    @Accessor("BLOCK_ENTITY_TAG_KEY")
    static String getBlockEntityTagKey() { throw new AssertionError(); }
}
