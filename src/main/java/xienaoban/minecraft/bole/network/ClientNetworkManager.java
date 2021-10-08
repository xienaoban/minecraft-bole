package xienaoban.minecraft.bole.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class ClientNetworkManager {
    public static void openBoleGui(){
        ClientPlayNetworking.send(Channels.OPEN_BOLE_GUI, PacketByteBufs.empty());
    }

    public static void openBoleGui(Entity entity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getEntityId());
        ClientPlayNetworking.send(Channels.OPEN_BOLE_GUI, buf);
    }
}
