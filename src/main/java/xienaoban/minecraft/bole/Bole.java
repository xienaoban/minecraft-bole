package xienaoban.minecraft.bole;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
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

    @Override
    public void onInitialize() {
        ScreenHandlerManager.init();
        ServerNetworkManager.init();
        Configs.init();
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
}
