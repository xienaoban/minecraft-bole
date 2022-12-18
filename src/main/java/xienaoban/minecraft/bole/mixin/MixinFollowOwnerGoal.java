package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xienaoban.minecraft.bole.config.Configs;

@Mixin(FollowOwnerGoal.class)
public class MixinFollowOwnerGoal {
    @Shadow @Final private boolean leavesAllowed;

    @Redirect(method = "canTeleportTo(Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/LandPathNodeMaker;getLandNodeType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos$Mutable;)Lnet/minecraft/entity/ai/pathing/PathNodeType;"))
    private PathNodeType getTargetPlayer(BlockView world, BlockPos.Mutable pos) {
        if (Configs.getInstance().isPetsCanTeleportToMoreBlocks()) {
            if (world.getFluidState(pos.down()).isIn(FluidTags.WATER) && (world.getFluidState(pos).isEmpty() || world.getFluidState(pos.up()).isEmpty())) return PathNodeType.WALKABLE;
        }
        return LandPathNodeMaker.getLandNodeType(world, pos);
    }

    @Redirect(method = "canTeleportTo(Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/goal/FollowOwnerGoal;leavesAllowed:Z", opcode = Opcodes.GETFIELD))
    private boolean getTargetPlayer(FollowOwnerGoal instance) {
        return leavesAllowed || Configs.getInstance().isPetsCanTeleportToMoreBlocks();
    }
}
