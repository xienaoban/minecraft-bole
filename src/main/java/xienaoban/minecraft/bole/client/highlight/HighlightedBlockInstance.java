package xienaoban.minecraft.bole.client.highlight;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import xienaoban.minecraft.bole.mixin.MixinFallingBlockEntityRenderer;

import java.util.Objects;

/**
 * Uses FallingBlockEntity to achieve highlighting effects of blocks.
 *
 * The renderer {@link FallingBlockEntityRenderer#render} doesn't render the entity when it is in the same position
 * as the corresponding block. So let's mixin.
 * @see MixinFallingBlockEntityRenderer#render
 */
public class HighlightedBlockInstance extends HighlightedFakeInstance {
    protected final BlockPos pos;

    public HighlightedBlockInstance(GlobalPos pos, int ticks) {
        super(createFallingBlockEntity(pos), ticks);
        this.pos = pos.getPos();
        this.entity.setInvisible(true);
    }

    @Override
    protected boolean shouldStop() {
        return !isBlockStillHere() || super.shouldStop();
    }

    private boolean isBlockStillHere() {
        return Objects.equals(this.entity.world.getBlockState(pos), ((FallingBlockEntity) this.entity).getBlockState());
    }

    private static FallingBlockEntity createFallingBlockEntity(GlobalPos globalPos) {
        BlockPos pos = globalPos.getPos();
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null || !Objects.equals(world.getRegistryKey(), globalPos.getDimension())) {
            return null;
        }
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir() || blockState.getBlock() instanceof FluidBlock) {
            return null;
        }
        return new ClientHighlightBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState);
    }
}
