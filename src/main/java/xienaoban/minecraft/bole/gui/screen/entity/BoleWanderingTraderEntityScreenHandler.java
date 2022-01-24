package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.screen.BoleMerchantEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class BoleWanderingTraderEntityScreenHandler<E extends WanderingTraderEntity> extends BoleMerchantEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleWanderingTraderEntityScreenHandler<WanderingTraderEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "wandering_trader_entity"), BoleWanderingTraderEntityScreenHandler::new);

    private final int addDespawnDelayTicks = 2 * 60 * 20;
    protected final ItemStack addDespawnDelayCost = new ItemStack(Items.WATER_BUCKET, 1);

    @Environment(EnvType.CLIENT)
    protected int entityDespawnDelay;

    public BoleWanderingTraderEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleWanderingTraderEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleWanderingTraderEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleWanderingTraderEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_ADD_WANDERING_TIME, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                if (trySpendBuckets(addDespawnDelayCost)) {
                    entity.setDespawnDelay(entity.getDespawnDelay() + addDespawnDelayTicks);
                    entity.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.6F, 0.8F);
                }
                else Bole.LOGGER.error("The player inventory data on the client and server are inconsistent.");
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                entityDespawnDelay += addDespawnDelayTicks;
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
    }

    @Override
    protected void initCustom() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
    }

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        buf.writeInt(this.entity.getDespawnDelay());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        this.entityDespawnDelay = buf.readInt();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
