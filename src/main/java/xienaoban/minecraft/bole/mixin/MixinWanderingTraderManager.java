package xienaoban.minecraft.bole.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WanderingTraderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.network.ServerNetworkManager;

@Mixin(WanderingTraderManager.class)
public class MixinWanderingTraderManager {
    @Unique private ServerPlayerEntity targetPlayer;

    // FOR DEBUG >
    // @Shadow private int spawnTimer;
    // @Shadow private int spawnDelay;
    // @Shadow private int spawnChance;
    //
    // @Inject(method = "spawn(Lnet/minecraft/server/world/ServerWorld;ZZ)I", at = @At(value = "HEAD"))
    // private void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir) {
    //     this.spawnTimer = Math.min(60, this.spawnTimer);
    //     this.spawnDelay = Math.min(60, this.spawnDelay);
    //     this.spawnChance = 100;
    // }
    // < FOR DEBUG

    @Redirect(method = "trySpawn(Lnet/minecraft/server/world/ServerWorld;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getRandomAlivePlayer()Lnet/minecraft/server/network/ServerPlayerEntity;"))
    private ServerPlayerEntity getTargetPlayer(ServerWorld world) {
        return this.targetPlayer = world.getRandomAlivePlayer();
    }

    @Inject(method = "trySpawn(Lnet/minecraft/server/world/ServerWorld;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;setWanderingTraderId(Ljava/util/UUID;)V", shift = At.Shift.AFTER))
    private void spawnBroadcast(ServerWorld world, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.getInstance().isBroadcastWhenWanderingTraderSpawn()) {
            ServerNetworkManager.sendWanderingTraderSpawnMessageToAllPlayers(world.getServer(), this.targetPlayer);
        }
        this.targetPlayer = null;
    }
}
