package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeeEntity.class)
public interface IMixinBeeEntity {
    @Accessor
    void setHivePos(BlockPos hivePos);
}
