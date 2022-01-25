package xienaoban.minecraft.bole.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WanderingTraderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.network.ServerNetworkManager;

@Mixin(WanderingTraderManager.class)
public class MixinWanderingTraderManager {
    private ServerPlayerEntity playerEntity;

    @Shadow private int spawnTimer;
    @Shadow private int spawnDelay;
    @Shadow private int spawnChance;

    @Inject(method = "spawn(Lnet/minecraft/server/world/ServerWorld;ZZ)I", at = @At(value = "HEAD"))
    private void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir) {
        this.spawnTimer = Math.min(100, this.spawnTimer);
        this.spawnDelay = Math.min(100, this.spawnDelay);
        this.spawnChance = 100;
    }

    // @ModifyVariable(method = "trySpawn(Lnet/minecraft/server/world/ServerWorld;)Z", at = @At(value = "STORE", ordinal = 0))
    // private ServerPlayerEntity injected(ServerPlayerEntity playerEntity) {
    //     System.out.println(playerEntity.getEntityName());
    //     this.playerEntity = playerEntity;
    //     return playerEntity;
    // }

    @Inject(method = "trySpawn(Lnet/minecraft/server/world/ServerWorld;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;setWanderingTraderId(Ljava/util/UUID;)V", shift = At.Shift.AFTER))
    private void spawnBroadcast(ServerWorld world, CallbackInfoReturnable<Boolean> cir) {
        if (Configs.getInstance().isBroadcastWhenWanderingTraderSpawn()) {
            ServerNetworkManager.sendWanderingTraderSpawnMessageToAllPlayers(world.getServer(), this.playerEntity);
        }
    }
}
