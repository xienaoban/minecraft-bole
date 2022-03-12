package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import xienaoban.minecraft.bole.Bole;

public abstract class GenericScreenHandler extends ScreenHandler {
    protected final boolean isServer;
    public final PlayerEntity player;

    protected GenericScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory) {
        super(type, syncId);
        this.player = playerInventory.player;
        this.isServer = this.player instanceof ServerPlayerEntity;
        initCustom();
    }

    /**
     * Initializes some custom content (which should not be inherited by subclasses) of the handler. <br/>
     * So never invoke <code>super.initCustom()</code>.
     */
    protected abstract void initCustom();

    /**
     * Invoked at the beginning of each client tick.
     *
     * @param ticks tick count
     */
    @Environment(EnvType.CLIENT)
    public abstract void clientTick(int ticks);

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public boolean isGod() {
        return Bole.isGod(this.player);
    }

    public boolean isDetached() {
        return Bole.isDetached(this.player);
    }

    public final boolean trySpendItems(ItemStack...targetStacks) {
        if (!hasEnoughItems(targetStacks)) return false;
        PlayerInventory inventory = player.getInventory();
        for (ItemStack target : targetStacks) {
            Item item = target.getItem();
            int leftCount = target.getCount();
            for (int i = inventory.size() - 1; i >= 0; --i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.getItem().equals(item) || stack.hasNbt()) continue;
                int decrementCount = Math.min(stack.getCount(), leftCount);
                stack.decrement(decrementCount);
                leftCount -= decrementCount;
                if (leftCount == 0) break;
            }
        }
        return true;
    }

    public final boolean trySpendBuckets(ItemStack ...targetStacks) {
        if (!hasEnoughItems(targetStacks)) return false;
        PlayerInventory inventory = player.getInventory();
        for (ItemStack target : targetStacks) {
            BucketItem item = (BucketItem) target.getItem();
            int leftCount = target.getCount();
            for (int i = inventory.size() - 1; i >= 0; --i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.getItem().equals(item) || stack.hasNbt()) continue;
                if (stack.getCount() == 1) {    // only supports unstackable buckets
                    inventory.setStack(i, new ItemStack(Items.BUCKET, 1));
                    leftCount -= 1;
                    if (leftCount == 0) break;
                }
            }
        }
        return true;
    }

    public final boolean hasEnoughItems(ItemStack ...targetStacks) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack target : targetStacks) {
            Item item = target.getItem();
            int neededCount = target.getCount();
            int count = 0;
            for (int i = inventory.size() - 1; i >= 0; --i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.getItem().equals(item) || stack.hasNbt()) continue;
                count += stack.getCount();
                if (count >= neededCount) break;
            }
            if (count < neededCount) return false;
        }
        return true;
    }

}
