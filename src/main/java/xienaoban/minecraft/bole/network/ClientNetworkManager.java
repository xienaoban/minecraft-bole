package xienaoban.minecraft.bole.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreen;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Objects;
import java.util.Queue;

@Environment(EnvType.CLIENT)
public class ClientNetworkManager {
    public static void init() {
        registerSendServerBoleConfigs();
        registerSendServerEntityData();
        registerSendServerEntitiesGlowing();
        registerSendWanderingTraderSpawnMessage();
        registerSendOverlayMessage();
    }

    private static void registerSendServerBoleConfigs() {
        ClientPlayNetworking.registerGlobalReceiver(Channels.SEND_SERVER_BOLE_CONFIGS, (client, handler, buf, responseSender) -> {
            String version = buf.readString();
            String conf = buf.readString();
            Bole.LOGGER.info("New Bole configs from the server: " + conf);
            client.execute(() -> {
                Bole bole = Bole.getInstance();
                bole.setServerVersion(version);
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Configs configs = gson.fromJson(conf, Configs.class);
                    bole.setServerConfigs(configs);
                } catch (Exception e) {
                    Bole.LOGGER.error("The mod version of the client does not match the mod version of the server!");
                    if (client.player != null) {
                        client.player.sendMessage(new TranslatableText(Keys.ERROR_TEXT_CLIENT_SERVER_MOD_VERSION_NOT_MATCH), false);
                    }
                }
            });
        });
    }

    private static void registerSendServerEntityData() {
        ClientPlayNetworking.registerGlobalReceiver(Channels.SEND_SERVER_ENTITY_DATA, (client, handler, buf, responseSender) -> {
            PacketByteBuf bufCopy = PacketByteBufs.copy(buf);
            AbstractBoleScreenHandler<?> boleScreenHandler = getBoleScreenHandler(client);
            if (boleScreenHandler == null) {
                BoleClient.getInstance().setHandlerBufCache(bufCopy);
                return;
            }
            // The buf will expire in server.execute(). So I make a copy.
            client.execute(() -> boleScreenHandler.tryReadServerEntityFromBuf(bufCopy));
        });
    }

    private static void registerSendServerEntitiesGlowing() {
        ClientPlayNetworking.registerGlobalReceiver(Channels.SEND_SERVER_ENTITIES_GLOWING, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            Identifier worldId = buf.readIdentifier();
            World world = client.world;
            if (world == null || !Objects.equals(world.getRegistryKey().getValue(), worldId)) return;
            int[] entityIds = new int[size];
            boolean[] entityGlowing = new boolean[size];
            for (int i = 0; i < size; ++i) {
                entityIds[i] = buf.readInt();
                entityGlowing[i] = buf.readBoolean();
            }
            client.execute(() -> {
                Bole.LOGGER.debug("Request entity glowing size: " + size);
                for (int i = 0; i < size; ++i) {
                    Entity entity = world.getEntityById(entityIds[i]);
                    if (entity != null) {
                        ((IMixinEntity) entity).callSetFlag(IMixinEntity.getGlowingFlagIndex(), entityGlowing[i]);
                        Bole.LOGGER.debug(entity.getType().getTranslationKey() + ", " + entityGlowing[i]);
                    }
                }
            });
        });
    }

    private static void registerSendWanderingTraderSpawnMessage() {
        ClientPlayNetworking.registerGlobalReceiver(Channels.SEND_WANDERING_TRADER_SPAWN_MESSAGE, (client, handler, buf, responseSender) -> {
            Text playerName;
            if (buf.readBoolean()) playerName = buf.readText();
            else playerName = new TranslatableText(Keys.TEXT_UNKNOWN_PLAYER);
            client.execute(() -> {
                assert client.player != null;
                if (Configs.getInstance().isReceiveWanderingTraderSpawnBroadcasts()) {
                    client.player.sendMessage(new TranslatableText(Keys.TEXT_WANDERING_TRADER_SPAWN_MESSAGE, playerName).formatted(Formatting.GRAY), false);
                }
            });
        });
    }

    private static void registerSendOverlayMessage() {
        ClientPlayNetworking.registerGlobalReceiver(Channels.SEND_OVERLAY_MESSAGE, (client, handler, buf, responseSender) -> {
            Text text = buf.readText();
            client.execute(() -> {
                if (client.currentScreen instanceof AbstractBoleScreen boleScreen) {
                    boleScreen.showOverlayMessage(text);
                }
            });
        });
    }

    public static void requestServerBoleConfigs() {
        ClientPlayNetworking.send(Channels.REQUEST_SERVER_BOLE_CONFIGS, PacketByteBufs.empty());
    }

    public static void requestBoleScreen() {
        ClientPlayNetworking.send(Channels.REQUEST_BOLE_SCREEN, PacketByteBufs.empty());
    }

    public static void requestBoleScreen(Entity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entity.getId());
        ClientPlayNetworking.send(Channels.REQUEST_BOLE_SCREEN, buf);
    }

    public static void requestBoleHandbook() {
        ClientPlayNetworking.send(Channels.REQUEST_BOLE_HANDBOOK_ITEM, PacketByteBufs.empty());
    }

    public static void requestServerEntityData() {
        ClientPlayNetworking.send(Channels.REQUEST_SERVER_ENTITY_DATA, PacketByteBufs.empty());
    }

    public static void sendClientEntitySettings(PacketByteBuf buf) {
        ClientPlayNetworking.send(Channels.SEND_CLIENT_ENTITY_SETTINGS, buf);
    }

    public static void requestServerEntitiesGlowing(Queue<Entity> que) {
        if (que.isEmpty()) return;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(que.size());
        assert que.peek() != null;
        buf.writeIdentifier(que.peek().world.getRegistryKey().getValue());
        Entity entity;
        while ((entity = que.poll()) != null) {
            buf.writeInt(entity.getId());
        }
        ClientPlayNetworking.send(Channels.REQUEST_SERVER_ENTITIES_GLOWING, buf);
    }

    public static void sendHighlightEvent() {
        ClientPlayNetworking.send(Channels.SEND_HIGHLIGHT_EVENT, PacketByteBufs.empty());
    }

    public static void requestBeehiveScreen(BlockPos beehivePos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(beehivePos);
        ClientPlayNetworking.send(Channels.REQUEST_BEEHIVE_SCREEN, buf);
    }

    public static void requestMerchantInventoryScreen(MerchantEntity merchantEntity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(merchantEntity.getId());
        ClientPlayNetworking.send(Channels.REQUEST_MERCHANT_INVENTORY_SCREEN, buf);
    }

    public static AbstractBoleScreenHandler<?> getBoleScreenHandler(MinecraftClient client) {
        if (client.player != null && client.player.currentScreenHandler instanceof AbstractBoleScreenHandler boleScreenHandler) {
            return boleScreenHandler;
        }
        return null;
    }
}
