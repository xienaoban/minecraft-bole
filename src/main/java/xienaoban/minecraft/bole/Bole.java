package xienaoban.minecraft.bole;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.core.BoleHandbookItem;
import xienaoban.minecraft.bole.gui.ScreenManager;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

public class Bole implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(Keys.BOLE);

    @Override
    public void onInitialize() {
        ScreenManager.initServer();
        ServerNetworkManager.init();
        Configs.init();
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
