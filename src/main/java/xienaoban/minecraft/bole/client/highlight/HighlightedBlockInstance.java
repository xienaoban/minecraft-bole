package xienaoban.minecraft.bole.client.highlight;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

/**
 * Uses FallingBlockEntity to achieve highlighting effects of blocks.
 *
 * FallingBlockEntity doesn't render when the BlockState it displays is at its position (see MC-114286).
 * Solution:
 * Don't set the FallingBlockEntity to the position it should be immediately.
 * Initialize its position to anywhere else instead {@link #createFallingBlockEntity},
 * and move it to where it should be after some ticks {@link #tryToMoveToRightPosition}.
 */
public class HighlightedBlockInstance extends HighlightedFakeInstance {
    protected final BlockPos pos;
    private final int moveTicks;

    public HighlightedBlockInstance(GlobalPos pos, int ticks) {
        super(createFallingBlockEntity(pos), ticks);
        this.pos = pos.getPos();
        this.moveTicks = getCurrentTicks() + 3;
    }

    @Override
    protected boolean shouldStop() {
        tryToMoveToRightPosition();
        return super.shouldStop() || !isBlockStillHere();
    }

    private void tryToMoveToRightPosition() {
        if (this.moveTicks == getCurrentTicks()) {
            entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }
    }

    private boolean isBlockStillHere() {
        return Objects.equals(this.entity.world.getBlockState(pos).getBlock(), ((FallingBlockEntity) this.entity).getBlockState().getBlock());
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
        return new FallingBlockEntity(world, pos.getX() + 0.5, -123, pos.getZ() + 0.5, blockState);
    }
}
