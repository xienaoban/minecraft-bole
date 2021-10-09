package xienaoban.minecraft.bole.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.util.Keys;

public class BoleVillagerEntityScreenHandler extends AbstractBoleScreenHandler<VillagerEntity> {
    public static final ScreenHandlerType<BoleVillagerEntityScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "villager_entity"), BoleVillagerEntityScreenHandler::new);

    public BoleVillagerEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId, playerInventory);
    }

    public BoleVillagerEntityScreenHandler(int syncId, PlayerInventory playerInventor, Entity entity) {
        super(HANDLER, syncId, playerInventor, entity);
    }

    @Override
    protected void init() {
        PlayerInventory playerInventory = this.player.inventory;
        ItemStack stack = new ItemStack(Items.EMERALD, 4);
        int ccc = 0;
        for(int i = playerInventory.size() - 1; i >= 0; --i) {
            ItemStack cur = playerInventory.getStack(i);
            if (!cur.isEmpty() && cur.getItem() == stack.getItem()) {
                ccc += cur.getCount();
            }
        }
        Bole.LOGGER.info("物品栏中的绿宝石: " + ccc);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
