package xienaoban.minecraft.bole.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FallingBlockEntity.class)
public interface IMixinFallingBlockEntity {
    @Accessor
    void setBlock(BlockState blockState);
}
