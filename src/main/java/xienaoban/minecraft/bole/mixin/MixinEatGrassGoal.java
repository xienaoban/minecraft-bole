package xienaoban.minecraft.bole.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xienaoban.minecraft.bole.gui.screen.entity.BoleSheepEntityScreenHandler;

import java.util.function.Predicate;

@Mixin(EatGrassGoal.class)
public abstract class MixinEatGrassGoal extends Goal {
    @Shadow @Final private static Predicate<BlockState> GRASS_PREDICATE;
    @Shadow @Final private MobEntity mob;
    @Shadow @Final private World world;
    @Shadow private int timer;

    /**
     * Try to eat grass immediately if the timer is set to negative.
     * @see BoleSheepEntityScreenHandler#registerEntitySettingsBufHandlers
     */
    @Inject(method = "canStart()Z", at = @At("HEAD"), cancellable = true)
    private void canStart(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (this.timer < 0) {
            this.timer = 0;
            BlockPos blockPos = this.mob.getBlockPos();
            callbackInfo.setReturnValue(GRASS_PREDICATE.test(this.world.getBlockState(blockPos)) || this.world.getBlockState(blockPos.down()).isOf(Blocks.GRASS_BLOCK));
        }
    }
}
