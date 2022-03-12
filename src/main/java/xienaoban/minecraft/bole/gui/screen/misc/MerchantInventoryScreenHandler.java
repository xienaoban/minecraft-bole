package xienaoban.minecraft.bole.gui.screen.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.gui.screen.GenericScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class MerchantInventoryScreenHandler extends GenericScreenHandler {
    public static final ScreenHandlerType<MerchantInventoryScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "merchant_inventory"), MerchantInventoryScreenHandler::new);

    protected final Inventory inventory;

    public MerchantInventoryScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(8));
    }

    public MerchantInventoryScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(HANDLER, syncId, playerInventory);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        int i, j;
        for (i = 0; i < this.inventory.size(); ++i) {
            this.addSlot(new Slot(inventory, i, 80 + (i % 4) * 18, 18 + (i / 4) * 18 + 18));
        }
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    protected void initCustom() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {}

    /**
     * @see net.minecraft.screen.HorseScreenHandler#transferSlot
     * @see net.minecraft.screen.HopperScreenHandler#transferSlot
     */
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < this.inventory.size() ? !this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}
