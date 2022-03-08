package xienaoban.minecraft.bole.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.core.BoleHandbookItem;
import xienaoban.minecraft.bole.gui.ScreenHandlerManager;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.gui.screen.misc.BeehiveScreenHandler;
import xienaoban.minecraft.bole.gui.screen.misc.MerchantInventoryScreenHandler;
import xienaoban.minecraft.bole.gui.screen.tree.BoleMerchantEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class ServerNetworkManager {
    public static void init() {
        registerRequestServerBoleConfigs();
        registerRequestBoleScreen();
        registerRequestBoleHandbook();
        registerRequestServerEntityData();
        registerSendClientEntitySettings();
        registerRequestServerEntitiesGlowing();
        registerSendHighlightEvent();
        registerRequestBeehiveScreen();
        registerRequestMerchantInventoryScreen();
    }

    private static void registerRequestServerBoleConfigs() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_SERVER_BOLE_CONFIGS, (server, player, handler, buf, responseSender) -> sendServerBoleConfigs(server, player));
    }

    private static void registerRequestBoleScreen() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_BOLE_SCREEN, (server, player, handler, buf, responseSender) -> {
            final Entity entity = buf.isReadable() ? player.world.getEntityById(buf.readInt()) : null;
            server.execute(() -> {
                if (!Bole.isDetached(player) && !Configs.getInstance().isAllowHotKeyToOpenBoleHandbookScreen()
                        && !Bole.isBoleHandbook(player.getMainHandStack()) && !Bole.isBoleHandbook(player.getOffHandStack())) {
                    player.sendMessage(new TranslatableText(Keys.TEXT_SERVER_BAN_HOTKEY).formatted(Formatting.GOLD), false);
                    return;
                }
                player.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return ScreenHandlerManager.getHandler(syncId, inv, entity);
                    }

                    @Override
                    public Text getDisplayName() {
                        String titleKey = entity == null ? Keys.BOLE_HANDBOOK_TITLE : entity.getType().getTranslationKey();
                        return new TranslatableText(titleKey);
                    }
                });
            });
        });
    }

    private static void registerRequestBoleHandbook() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_BOLE_HANDBOOK_ITEM, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (Bole.isGod(player)) {
                    player.getInventory().insertStack(BoleHandbookItem.createBook());
                }
            });
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
            server.execute(() -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 2 * 20));
                if (!(Bole.isDetached(player))) {
                    player.addExperience(-2);
                }
            });
        });
    }

    private static void registerRequestBeehiveScreen() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_BEEHIVE_SCREEN, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            BlockState blockState = player.world.getBlockState(pos);
            if (blockState.getBlock() instanceof BeehiveBlock) {
                server.execute(() -> {
                    player.openHandledScreen(new NamedScreenHandlerFactory() {
                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                            return new BeehiveScreenHandler(syncId, inv, pos);
                        }

                        @Override
                        public Text getDisplayName() {
                            return new TranslatableText(Keys.TEXT_INVENTORY_OF, new TranslatableText(blockState.getBlock().getTranslationKey()));
                        }
                    });
                });
            }
            else Bole.LOGGER.error("Cannot open BeehiveScreen.");
        });
    }

    private static void registerRequestMerchantInventoryScreen() {
        ServerPlayNetworking.registerGlobalReceiver(Channels.REQUEST_MERCHANT_INVENTORY_SCREEN, (server, player, handler, buf, responseSender) -> {
            if (player.world.getEntityById(buf.readInt()) instanceof MerchantEntity merchantEntity
                    && player.currentScreenHandler instanceof BoleMerchantEntityScreenHandler oldHandler) {
                server.execute(() -> {
                    if (oldHandler.trySpendItems(BoleMerchantEntityScreenHandler.OPEN_INVENTORY_COST)) {
                        player.openHandledScreen(new NamedScreenHandlerFactory() {
                            @Override
                            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                                return new MerchantInventoryScreenHandler(syncId, inv, merchantEntity.getInventory());
                            }

                            @Override
                            public Text getDisplayName() {
                                return new TranslatableText(Keys.TEXT_INVENTORY_OF, new TranslatableText(merchantEntity.getType().getTranslationKey()));
                            }
                        });
                    }
                });
            }
            else Bole.LOGGER.error("Cannot open BoleMerchantEntityScreen.");
        });
    }

    public static void sendServerBoleConfigs(MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String conf = gson.toJson(Configs.getInstance());
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(Bole.getModVersion());
            buf.writeString(conf);
            ServerPlayNetworking.send(player, Channels.SEND_SERVER_BOLE_CONFIGS, buf);
        });
    }

    public static void sendServerBoleConfigsToAllPlayers(MinecraftServer server) {
        server.execute(() -> {
            String version = Bole.getModVersion();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String conf = gson.toJson(Configs.getInstance());
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(version);
                buf.writeString(conf);
                ServerPlayNetworking.send(player, Channels.SEND_SERVER_BOLE_CONFIGS, buf);
            }
        });
    }

    public static void sendWanderingTraderSpawnMessageToAllPlayers(MinecraftServer server, ServerPlayerEntity target) {
        server.execute(() -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(target != null);
                if (target != null) buf.writeText(target.getName());
                ServerPlayNetworking.send(player, Channels.SEND_WANDERING_TRADER_SPAWN_MESSAGE, buf);
            }
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
