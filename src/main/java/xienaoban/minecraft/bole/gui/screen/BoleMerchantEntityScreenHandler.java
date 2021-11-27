package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xienaoban.minecraft.bole.util.Keys;

public class BoleMerchantEntityScreenHandler<E extends MerchantEntity> extends BolePassiveEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleMerchantEntityScreenHandler<MerchantEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "merchant_entity"), BoleMerchantEntityScreenHandler::new);

    protected SimpleInventory entityInventory = new SimpleInventory(8);

    public BoleMerchantEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleMerchantEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleMerchantEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleMerchantEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
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
        SimpleInventory inventory = this.entity.getInventory();
        int size = inventory.size();
        buf.writeInt(size);
        for (int i = 0; i < size; ++i) {
            ItemStack stack = inventory.getStack(i);
            buf.writeString(Registry.ITEM.getId(stack.getItem()).toString());
            buf.writeInt(stack.getCount());
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        int size = buf.readInt();
        SimpleInventory inventory = new SimpleInventory(size);
        for (int i = 0; i < size; ++i) {
            Item item = Registry.ITEM.get(new Identifier(buf.readString()));
            int count = buf.readInt();
            ItemStack stack = new ItemStack(item, count);
            inventory.setStack(i, stack);
        }
        this.entityInventory = inventory;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
