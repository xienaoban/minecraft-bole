package xienaoban.minecraft.bole.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.BoleHorseScreenHandler;

public class ServerNetworkManager {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(Bole.BOLE_HORSE_PACKET, (server, player, handler, buf, responseSender) -> {
            HorseEntity horseEntity = (HorseEntity) player.world.getEntityById(buf.readInt());
            server.execute(() -> {
                player.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new BoleHorseScreenHandler(syncId, inv, horseEntity);
                    }

                    @Override
                    public Text getDisplayName() {
                        return new TranslatableText("entity.minecraft.horse");
                    }
                });
            });
        });
    }
}
