package xienaoban.minecraft.bole.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.util.Keys;

public class BoleHorseScreenHandler extends AbstractBoleScreenHandler<HorseEntity> {
    public static final ScreenHandlerType<BoleHorseScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "horse"), BoleHorseScreenHandler::new);

    public BoleHorseScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId, playerInventory);
    }

    public BoleHorseScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        super(HANDLER, syncId, playerInventory, entity);
    }

    @Override
    protected void init() {
        PlayerInventory playerInventory = this.player.inventory;
        int i, j;

        ItemStack stack = new ItemStack(Items.EMERALD, 4);
        int ccc = 0;
        for(i = playerInventory.size() - 1; i >= 0; --i) {
            ItemStack cur = playerInventory.getStack(i);
            if (!cur.isEmpty() && cur.getItem() == stack.getItem()) {
                ccc += cur.getCount();
            }
        }
        Bole.LOGGER.info("物品栏中的绿宝石: " + ccc);

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18 - 18));
            }
        }
        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        if (entity.hasCustomName()) {
            entity.setCustomName(null);
        }
        else{
            entity.setCustomName(new LiteralText("Dinnerbone"));
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
