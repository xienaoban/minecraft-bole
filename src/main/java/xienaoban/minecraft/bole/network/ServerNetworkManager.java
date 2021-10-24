package xienaoban.minecraft.bole.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.screen.BoleEntityScreenHandler;
import xienaoban.minecraft.bole.screen.BoleHandbookScreenHandler;
import xienaoban.minecraft.bole.screen.BoleMobEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class ServerNetworkManager {
    public static void init() {
        registerOpenBoleGui();
    }

    private static void registerOpenBoleGui() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.OPEN_BOLE_GUI, (server, player, handler, buf, responseSender) -> {
            final Entity entity = buf.isReadable() ? player.world.getEntityById(buf.readInt()) : null;
            if (entity == null) {
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
                server.execute(() -> {
                    player.openHandledScreen(new NamedScreenHandlerFactory() {
                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                            if (entity instanceof MobEntity) return new BoleMobEntityScreenHandler<>(syncId, inv, entity);
                            return new BoleEntityScreenHandler<>(syncId, inv, entity);
                        }

                        @Override
                        public Text getDisplayName() {
                            return new TranslatableText(entity.getType().getTranslationKey());
                        }
                    });
                });
            }
        });
    }
}
