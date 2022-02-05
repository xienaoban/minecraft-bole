package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allow polar bears to multiply.
 */
@Mixin(PolarBearEntity.class)
public abstract class MixinPolarBearEntity extends AnimalEntity implements Angerable {
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.TROPICAL_FISH);

    protected MixinPolarBearEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isBreedingItem(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void isBreedingItem(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
        callbackInfo.setReturnValue(BREEDING_INGREDIENT.test(stack));
    }

    @Inject(method = "initGoals()V", at = @At("TAIL"))
    private void initGoals(CallbackInfo callbackInfo) {
        this.goalSelector.add(2, new AnimalMateGoal((PolarBearEntity)(Object)this, 1.0D));
    }
}