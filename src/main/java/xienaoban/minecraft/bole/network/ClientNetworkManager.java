package xienaoban.minecraft.bole.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import xienaoban.minecraft.bole.gui.AbstractBoleScreenHandler;

@Environment(EnvType.CLIENT)
public class ClientNetworkManager {
    public static void init() {
        registerSendServerEntityData();
    }

    private static void registerSendServerEntityData() {
        ClientPlayNetworking.registerGlobalReceiver(Channels.SEND_SERVER_ENTITY_DATA, (client, handler, buf, responseSender) -> {
            if (client.player == null || !(client.player.currentScreenHandler instanceof AbstractBoleScreenHandler)) {
                return;
            }
            ((AbstractBoleScreenHandler<?>) client.player.currentScreenHandler).readServerEntityFromBuf(buf);
        });
    }


    public static void requestBoleScreen() {
        ClientPlayNetworking.send(Channels.REQUEST_BOLE_SCREEN, PacketByteBufs.empty());
    }

    public static void requestBoleScreen(Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getEntityId());
        ClientPlayNetworking.send(Channels.REQUEST_BOLE_SCREEN, buf);
    }

    public static void requestServerEntityData() {
        ClientPlayNetworking.send(Channels.REQUEST_SERVER_ENTITY_DATA, PacketByteBufs.empty());
    }

    public static void sendClientEntitySettings(PacketByteBuf buf) {
        ClientPlayNetworking.send(Channels.SEND_CLIENT_ENTITY_SETTINGS, buf);
    }
}
