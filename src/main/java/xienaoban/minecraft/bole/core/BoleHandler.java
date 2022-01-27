package xienaoban.minecraft.bole.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.network.ClientNetworkManager;

public class BoleHandler {
    @Environment(EnvType.CLIENT)
    public static void tryOpenBoleScreen(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        BoleClient.getInstance().setBoleTarget(null);
        if (player == null) {
            Bole.LOGGER.error("Client player is null. Fail to open the Bole Screen.");
            return;
        }
        Entity target;
        double y = player.getRotationVec(0).getY();
        HitResult hit = client.crosshairTarget;
        if (y > 0.998) target = null;
        else if (y < -0.998) target = player;
        else if (y < -0.886 && player.hasVehicle()) target = player.getVehicle();
        else if (hit == null || hit.getType() != HitResult.Type.ENTITY) target = null;
        else target = ((EntityHitResult) hit).getEntity();

        if (target == null) ClientNetworkManager.requestBoleScreen();
        else {
            BoleClient.getInstance().setBoleTarget(target);
            ClientNetworkManager.requestBoleScreen(target);
        }
    }
}
