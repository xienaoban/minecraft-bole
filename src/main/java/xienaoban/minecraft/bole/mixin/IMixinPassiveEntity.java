package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.passive.PassiveEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PassiveEntity.class)
public interface IMixinPassiveEntity {
    @Accessor("breedingAge")
    int getBreedingAgeValue();

    @Accessor("breedingAge")
    void setBreedingAgeValue(int breedingAge);
}
