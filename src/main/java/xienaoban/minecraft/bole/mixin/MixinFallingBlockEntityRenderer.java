package xienaoban.minecraft.bole.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xienaoban.minecraft.bole.client.highlight.ClientHighlightBlockEntity;

@Environment(value= EnvType.CLIENT)
@Mixin(FallingBlockEntityRenderer.class)
public class MixinFallingBlockEntityRenderer {
    /**
     * Always render the ClientHighlightBlockEntity.
     * @see xienaoban.minecraft.bole.client.highlight.HighlightedBlockInstance
     */
    @Redirect(method = "render(Lnet/minecraft/entity/FallingBlockEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState render(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (fallingBlockEntity instanceof ClientHighlightBlockEntity) return null;
        return world.getBlockState(pos);
    }
}
