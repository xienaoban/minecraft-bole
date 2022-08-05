package xienaoban.minecraft.bole.gui.screen.homepage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.client.entity.EntityManager;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public final class BoleHomepageScreenHandler extends AbstractBoleScreenHandler<Entity> {
    public static final ScreenHandlerType<BoleHomepageScreenHandler> HANDLER
            = register(new Identifier(Keys.NAMESPACE, "homepage"), BoleHomepageScreenHandler::new);

    public static final int HIGHLIGHT_EXPERIENCE_COST = 2;

    EntityManager entityManager;

    public BoleHomepageScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId, playerInventory, null);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_OFFER_OR_DROP_GOD_MODE_ONLY, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                if (isGod()) player.getInventory().offerOrDrop(buf.readItemStack());
            }

            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                ItemStack stack = (ItemStack) args[0];
                buf.writeItemStack(stack);
                player.getInventory().offerOrDrop(stack);
                player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }
        });
    }

    @Override
    protected void initServer() {
        super.initServer();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void initClient() {
        super.initClient();
        this.entityManager = EntityManager.getInstance();
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {}

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {}

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {}
}
