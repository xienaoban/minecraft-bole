package xienaoban.minecraft.bole.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.ScreenRegistryManager;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class ServerNetworkManager {
    public static void init() {
        registerRequestBoleScreen();
        registerRequestServerEntityData();
        registerSendClientEntitySettings();
    }

    private static void registerRequestBoleScreen() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_BOLE_SCREEN, (server, player, handler, buf, responseSender) -> {
            final Entity entity = buf.isReadable() ? player.world.getEntityById(buf.readInt()) : null;
            server.execute(() -> player.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return ScreenRegistryManager.getHandler(syncId, inv, entity);
                }

                @Override
                public Text getDisplayName() {
                    String titleKey = entity == null ? Keys.TITLE_BOLE_OVERVIEW : entity.getType().getTranslationKey();
                    return new TranslatableText(titleKey);
                }
            }));
        });
    }

    private static void registerRequestServerEntityData() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_SERVER_ENTITY_DATA, (server, player, handler, buf, responseSender) -> {
            AbstractBoleScreenHandler<?> boleScreenHandler = getBoleScreenHandler(player);
            if (boleScreenHandler == null) {
                return;
            }
            PacketByteBuf entityBuf = PacketByteBufs.create();
            boleScreenHandler.writeServerEntityToBuf(entityBuf);
            server.execute(() -> ServerPlayNetworking.send(player, Channels.SEND_SERVER_ENTITY_DATA, entityBuf));
        });
    }

    private static void registerSendClientEntitySettings() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.SEND_CLIENT_ENTITY_SETTINGS, (server, player, handler, buf, responseSender) -> {
            AbstractBoleScreenHandler<?> boleScreenHandler = getBoleScreenHandler(player);
            if (boleScreenHandler == null) {
                return;
            }
            // The buf will expire in server.execute(). So I make a copy.
            PacketByteBuf bufCopy = PacketByteBufs.copy(buf);
            server.execute(() -> boleScreenHandler.setServerEntitySettings(bufCopy));
        });
    }

    public static AbstractBoleScreenHandler<?> getBoleScreenHandler(ServerPlayerEntity player) {
        if (!(player.currentScreenHandler instanceof AbstractBoleScreenHandler)) {
            Bole.LOGGER.warn("The bole screen may have been closed. Buf ignored.");
            return null;
        }
        return (AbstractBoleScreenHandler<?>) player.currentScreenHandler;
    }
}
