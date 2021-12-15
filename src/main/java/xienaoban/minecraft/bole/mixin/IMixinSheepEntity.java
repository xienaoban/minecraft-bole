package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SheepEntity.class)
public interface IMixinSheepEntity {
    @Accessor
    int getEatGrassTimer();
}
