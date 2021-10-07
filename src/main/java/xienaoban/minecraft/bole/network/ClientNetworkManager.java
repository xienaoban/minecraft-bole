package xienaoban.minecraft.bole.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.network.PacketByteBuf;
import xienaoban.minecraft.bole.Bole;

@Environment(EnvType.CLIENT)
public class ClientNetworkManager {
    public static void openBoleHorseScreen(HorseEntity entity){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getEntityId());
        ClientPlayNetworking.send(Bole.BOLE_HORSE_PACKET, buf);
    }
}
