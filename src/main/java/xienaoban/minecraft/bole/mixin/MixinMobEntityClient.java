package xienaoban.minecraft.bole.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xienaoban.minecraft.bole.client.EventsManager;
import xienaoban.minecraft.bole.config.Configs;

@Environment(value= EnvType.CLIENT)
@Mixin(MobEntity.class)
public abstract class MixinMobEntityClient extends LivingEntity {
    private static final EventsManager.LeashFallFromPlayerEvent fallEvent = new EventsManager.LeashFallFromPlayerEvent();

    @Shadow private Entity holdingEntity;
    @Shadow private int holdingEntityId;

    protected MixinMobEntityClient(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "detachLeash(ZZ)V", at = @At("HEAD"))
    private void detachLeash(boolean sendPacket, boolean dropItem, CallbackInfo ci) {
        Entity entity = this.holdingEntity;
        if (this.holdingEntityId == 0 && entity instanceof ClientPlayerEntity player
                && Configs.getInstance().isNotifyWhenLeashFallFromPlayer()
                && (!this.isAlive() || this.squaredDistanceTo(entity) > 100.0F)) {
            fallEvent.onFall(player);
        }
    }
}
