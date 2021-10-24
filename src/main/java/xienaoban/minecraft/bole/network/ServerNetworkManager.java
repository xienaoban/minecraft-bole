package xienaoban.minecraft.bole.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.gui.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.gui.BoleEntityScreenHandler;
import xienaoban.minecraft.bole.gui.BoleHandbookScreenHandler;
import xienaoban.minecraft.bole.gui.BoleMobEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class ServerNetworkManager {
    public static void init() {
        registerRequestBoleScreen();
        registerRequestServerEntityData();
    }

    private static void registerRequestBoleScreen() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_BOLE_SCREEN, (server, player, handler, buf, responseSender) -> {
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

    private static void registerRequestServerEntityData() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_SERVER_ENTITY_DATA, (server, player, handler, buf, responseSender) -> {
            if (!(player.currentScreenHandler instanceof AbstractBoleScreenHandler)) {
                return;
            }
            PacketByteBuf entityBuf = PacketByteBufs.create();
            ((AbstractBoleScreenHandler<?>) player.currentScreenHandler).writeServerEntityToBuf(entityBuf);
            server.execute(() -> ServerPlayNetworking.send(player, Channels.SEND_SERVER_ENTITY_DATA, entityBuf));
        });
    }
}
