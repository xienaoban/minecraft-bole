package xienaoban.minecraft.bole;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.core.BoleHandbookItem;
import xienaoban.minecraft.bole.gui.ScreenHandlerManager;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Optional;

public class Bole implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Keys.BOLE);

    private static String modVersion = null;

    private static Bole instance;

    // These two values are only used on the client side.
    // But in order to prevent ClassNotFoundException on the dedicated server side, I can't define them in BoleClient.
    @Environment(EnvType.CLIENT) private String serverVersion;
    @Environment(EnvType.CLIENT) private Configs serverConfigs;

    public static Bole getInstance() {
        return instance;
    }

    public static String getModVersion() {
        if (modVersion != null) return modVersion;
        String version = "<unknown>";
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Keys.BOLE);
        if (modContainer.isPresent()) version = modContainer.get().getMetadata().getVersion().toString();
        return modVersion = version;
    }

    public static boolean isGod(PlayerEntity player) {
        return player.isCreative();
    }

    /**
     * I don't know how to translate "脱离尘世，超然象外", maybe "detached"?
     */
    public static boolean isDetached(PlayerEntity player) {
        return player.isCreative() || player.isSpectator();
    }

    public static boolean isBoleHandbook(ItemStack stack) {
        if (stack == null || !stack.isOf(Items.WRITABLE_BOOK) || !stack.hasNbt()) return false;
        assert stack.getNbt() != null;
        return stack.getNbt().contains(BoleHandbookItem.ID, NbtElement.STRING_TYPE);
    }

    public static boolean isMonster(Entity entity) {
        if (entity == null) return false;
        return entity.getType().getSpawnGroup() == SpawnGroup.MONSTER;
    }

    @Override
    public void onInitialize() {
        instance = this;
        ScreenHandlerManager.init();
        ServerNetworkManager.init();
        Configs.init();
    }

    @Environment(EnvType.CLIENT)
    public String getServerVersion() {
        return serverVersion;
    }

    @Environment(EnvType.CLIENT)
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    @Environment(EnvType.CLIENT)
    public Configs getServerConfigs() {
        return this.serverConfigs;
    }

    @Environment(EnvType.CLIENT)
    public void setServerConfigs(Configs serverConfigs) {
        this.serverConfigs = serverConfigs;
    }
}
