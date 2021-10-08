package xienaoban.minecraft.bole.screen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import xienaoban.minecraft.bole.BoleClient;

public abstract class AbstractBoleScreenHandler<E extends Entity> extends ScreenHandler {
    protected final PlayerEntity player;
    protected final E entity;

    public AbstractBoleScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId,
                                     PlayerInventory playerInventory) {
        this(type, syncId, playerInventory, BoleClient.boleTarget);
    }

    @SuppressWarnings("unchecked")
    public AbstractBoleScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId,
                                      PlayerInventory playerInventor, Entity entity) {
        super(type, syncId);
        this.player = playerInventor.player;
        this.entity = (E) entity;
        this.init();
    }

    protected abstract void init();

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
