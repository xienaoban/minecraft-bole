package xienaoban.minecraft.bole.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.core.BoleHandbookItem;

@Mixin(WanderingTraderEntity.class)
public abstract class MixinWanderingTraderEntity extends MerchantEntity {

    public MixinWanderingTraderEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * The probability of bole handbook trade offer will decrease as the running time of the world becomes longer.
     * The probability will remain at 20% after 4 days.
     */
    @Inject(method = "fillRecipes()V", at = @At("TAIL"))
    private void addBoleHandbookTradeOffer(CallbackInfo ci) {
        if (this.getServer() != null) {
            final int maxTicks = 4 * 24 * 60 * 60 * 20;
            int r = this.random.nextInt(maxTicks + (maxTicks >> 2));
            int t = (int) Math.min(this.getWorld().getTime(), maxTicks);
            if (r > t) {
                TradeOffer tradeOffer = new TradeOffer(new ItemStack(Items.EMERALD, this.random.nextInt(41) + 24), BoleHandbookItem.createBook(), this.random.nextInt(5) + 4, 2, 0.05F);
                this.getOffers().add(tradeOffer);
            }
        }
    }
}
