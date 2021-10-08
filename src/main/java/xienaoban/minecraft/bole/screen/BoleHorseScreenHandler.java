package xienaoban.minecraft.bole.screen;

import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;

public class BoleHorseScreenHandler extends ScreenHandler {
    private final HorseEntity entity;

    public BoleHorseScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, BoleClient.currentHorse);
    }

    public BoleHorseScreenHandler(int syncId, PlayerInventory playerInventory, HorseEntity entity) {
        super(Bole.BOLE_HORSE_SCREEN_HANDLER, syncId);
        this.entity = entity;
        int i, j;

        ItemStack stack = new ItemStack(Items.EMERALD, 4);
        int ccc = 0;
        for(i = playerInventory.size() - 1; i >= 0; --i) {
            ItemStack cur = playerInventory.getStack(i);
            if (!cur.isEmpty() && cur.getItem() == stack.getItem()) {
                ccc += cur.getCount();
            }
        }
        Bole.LOGGER.info(ccc);

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

    public HorseEntity getEntity() {
        return entity;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
