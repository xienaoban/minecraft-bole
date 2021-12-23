package xienaoban.minecraft.bole.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;

@Mixin(EatGrassGoal.class)
public interface IMixinEatGrassGoal {
    @Accessor("GRASS_PREDICATE")
    static Predicate<BlockState> getGrassPredicate() {
        throw new AssertionError();
    }

    @Accessor
    int getTimer();

    @Accessor
    void setTimer(int timer);
}
