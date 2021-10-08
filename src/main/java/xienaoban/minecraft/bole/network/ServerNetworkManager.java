package xienaoban.minecraft.bole.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.screen.BoleHandbookScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class ServerNetworkManager {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.OPEN_BOLE_GUI, (server, player, handler, buf, responseSender) -> {
            if (!buf.isReadable()) {
                server.execute(() -> {
                    player.openHandledScreen(new NamedScreenHandlerFactory() {
                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                            return new BoleHandbookScreenHandler(syncId, inv);
                        }

                        @Override
                        public Text getDisplayName() {
                            return new TranslatableText(Keys.TITLE_BOLE_OVERVIEW);
                        }
                    });
                });
            }
            else {
                Entity entity = player.world.getEntityById(buf.readInt());
                // server.execute(() -> {
                //     player.openHandledScreen(new NamedScreenHandlerFactory() {
                //         @Override
                //         public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                //             return new BoleHorseScreenHandler(syncId, inv, entity);
                //         }
                //
                //         @Override
                //         public Text getDisplayName() {
                //             return new TranslatableText("entity.minecraft.horse");
                //         }
                //     });
                // });
            }
        });
    }
}
