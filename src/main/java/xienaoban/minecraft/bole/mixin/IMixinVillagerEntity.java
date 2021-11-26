package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerEntity.class)
public interface IMixinVillagerEntity {
    @Accessor
    int getRestocksToday();
}
