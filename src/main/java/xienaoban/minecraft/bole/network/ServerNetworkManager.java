package xienaoban.minecraft.bole.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.ScreenManager;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class ServerNetworkManager {
    public static void init() {
        registerRequestServerBoleConfigs();
        registerRequestBoleScreen();
        registerRequestServerEntityData();
        registerSendClientEntitySettings();
        registerRequestServerEntitiesGlowing();
        registerSendHighlightEvent();
    }

    private static void registerRequestServerBoleConfigs() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_SERVER_BOLE_CONFIGS, (server, player, handler, buf, responseSender) -> sendServerBoleConfigs(server, player));
    }

    private static void registerRequestBoleScreen() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_BOLE_SCREEN, (server, player, handler, buf, responseSender) -> {
            final Entity entity = buf.isReadable() ? player.world.getEntityById(buf.readInt()) : null;
            server.execute(() -> player.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return ScreenManager.getHandler(syncId, inv, entity);
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
            sendServerEntityData(boleScreenHandler, server, player);
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

    private static void registerRequestServerEntitiesGlowing() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_SERVER_ENTITIES_GLOWING, (server, player, handler, buf, responseSender) -> {
            int size = buf.readInt();
            Identifier worldId = buf.readIdentifier();
            World world = server.getWorld(RegistryKey.of(Registry.WORLD_KEY, worldId));
            if (world == null) return;
            int[] entityIds = new int[size];
            for (int i = 0; i < size; ++i) entityIds[i] = buf.readInt();
            server.execute(() -> {
                PacketByteBuf res = PacketByteBufs.create();
                res.writeInt(size);
                res.writeIdentifier(worldId);
                for (int i = 0; i < size; ++i) {
                    int entityId = entityIds[i];
                    Entity entity = world.getEntityById(entityId);
                    res.writeInt(entityId);
                    res.writeBoolean(entity != null && entity.isGlowing());
                }
                ServerPlayNetworking.send(player, Channels.SEND_SERVER_ENTITIES_GLOWING, res);
            });
        });
    }

    private static void registerSendHighlightEvent() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.SEND_HIGHLIGHT_EVENT, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 2 * 20)));
        });
    }

    public static void sendServerBoleConfigs(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String str = gson.toJson(Configs.getInstance());
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(str);
            ServerPlayNetworking.send(player, Channels.SEND_SERVER_BOLE_CONFIGS, buf);
        });
    }

    public static void sendServerEntityData(AbstractBoleScreenHandler<?> boleScreenHandler, MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            PacketByteBuf entityBuf = PacketByteBufs.create();
            boleScreenHandler.tryWriteServerEntityFromBuf(entityBuf);
            ServerPlayNetworking.send(player, Channels.SEND_SERVER_ENTITY_DATA, entityBuf);
        });
    }

    public static void sendOverlayMessage(Text text, MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeText(text);
            ServerPlayNetworking.send(player, Channels.SEND_OVERLAY_MESSAGE, buf);
        });
    }

    private static AbstractBoleScreenHandler<?> getBoleScreenHandler(ServerPlayerEntity player) {
        if (!(player.currentScreenHandler instanceof AbstractBoleScreenHandler)) {
            Bole.LOGGER.warn("The bole screen may have been closed. Buf ignored.");
            return null;
        }
        return (AbstractBoleScreenHandler<?>) player.currentScreenHandler;
    }
}
