package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.world.entity.EntityLookup;
import xienaoban.minecraft.bole.mixin.IMixinWorld;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Environment(EnvType.CLIENT)
public class PlayerDataCacheManager {
    private static final PlayerDataCacheManager INSTANCE = new PlayerDataCacheManager();

    public static final Text TRYING = Text.translatable(Keys.TEXT_REQUESTING_MOJANG_API);
    public static final Text FAILED = Text.translatable(Keys.TEXT_FAIL_TO_REQUEST_MOJANG_API);
    public static final Text NOT_GENUINE_PLAYER = Text.translatable(Keys.TEXT_NOT_GENUINE_PLAYER);

    public static PlayerDataCacheManager getInstance() {
        return INSTANCE;
    }

    public static boolean isNoPlayerData(Text text) {
        return text == null || text == TRYING || text == FAILED || text == NOT_GENUINE_PLAYER;
    }

    private final ExecutorService pool;

    private final Map<UUID, Text> uuidToName;

    private PlayerDataCacheManager() {
        this.pool = Executors.newCachedThreadPool();
        this.uuidToName = new ConcurrentHashMap<>();
    }

    /**
     * First we try to get the players' name from the client EntityLookup.
     * Then we try to request api.mojang.com to get the name if not found.
     * @return Keys.TEXT_NOT_GENUINE_PLAYER, Keys.TEXT_FAIL_TO_REQUEST_MOJANG_API or the real name
     */
    public Text getPlayerName(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world != null) {
            EntityLookup<Entity> lookup = ((IMixinWorld) MinecraftClient.getInstance().world).callGetEntityLookup();
            Entity e = lookup.get(uuid);
            if (e != null) {
                this.uuidToName.put(uuid, e.getName());
                return e.getName();
            }
        }
        ClientPlayerEntity player = client.player;
        if (player != null) {
            PlayerListEntry entry = client.player.networkHandler.getPlayerListEntry(uuid);
            if (entry != null) {
                Text name = Text.literal(entry.getProfile().getName());
                this.uuidToName.put(uuid, name);
                return name;
            }
        }
        Text name = this.uuidToName.get(uuid);
         if (name == null) {
             requestMojangApiToGetPlayerName(uuid);
             return TRYING;
         }
         return name;
    }

    private void requestMojangApiToGetPlayerName(final UUID uuid) {
        if (this.uuidToName.put(uuid, TRYING) == TRYING) {
            return;
        }
        this.pool.execute(() -> {
            String name = MiscUtil.requestMojangApiGetPlayerName(uuid);
            Text text;
            // Here I really need "==" not "equals"!!!
            if (name == Keys.TEXT_FAIL_TO_REQUEST_MOJANG_API) {
                text = FAILED;
            }
            else if (name == Keys.TEXT_NOT_GENUINE_PLAYER) {
                text = NOT_GENUINE_PLAYER;
            }
            else {
                text = Text.literal(name);
            }
            this.uuidToName.put(uuid, text);
        });
    }

    public void debugClear() {
        this.uuidToName.clear();
    }
}
