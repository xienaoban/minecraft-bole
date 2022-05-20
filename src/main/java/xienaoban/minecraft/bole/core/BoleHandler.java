package xienaoban.minecraft.bole.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.gui.screen.GenericHandledScreen;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

public class BoleHandler {
    @Environment(EnvType.CLIENT)
    public static void tryOpenBoleScreen(MinecraftClient client) {
        Bole bole = Bole.getInstance();
        BoleClient boleClient = BoleClient.getInstance();
        ClientPlayerEntity player = client.player;
        boleClient.setHitEntity(null);
        boleClient.setHitBlock(null);
        if (player == null) {
            Bole.LOGGER.error("Client player is null. Fail to open the Bole Screen.");
            return;
        }
        if (!Bole.isDetached(player) && !bole.getServerConfigs().isAllowHotKeyToOpenBoleHandbookScreen()
                && !Bole.isBoleHandbook(player.getMainHandStack()) && !Bole.isBoleHandbook(player.getOffHandStack())) {
            player.sendMessage(Text.translatable(Keys.TEXT_SERVER_BAN_HOTKEY).formatted(Formatting.GOLD), false);
            return;
        }
        Entity target;
        double y = player.getRotationVec(0).getY();
        HitResult hit = client.crosshairTarget;
        if (y > 0.998) target = null;
        else if (y < -0.998) target = player;
        else if (y < -0.886 && player.hasVehicle()) target = player.getVehicle();
        else if (hit == null) target = null;
        else if (hit.getType() != HitResult.Type.ENTITY) {
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hit).getBlockPos();
                boleClient.setHitBlock(pos);
                BlockState blockState = player.world.getBlockState(pos);
                if (blockState.getBlock() instanceof BeehiveBlock) {
                    ClientNetworkManager.requestBeehiveScreen(pos);
                    return;
                }
            }
            target = null;
        }
        else target = ((EntityHitResult) hit).getEntity();

        if (target == null) ClientNetworkManager.requestBoleScreen();
        else {
            boleClient.setHitEntity(target);
            ClientNetworkManager.requestBoleScreen(target);
        }
        GenericHandledScreen.playScreenSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F, 0.8F);
    }
}
