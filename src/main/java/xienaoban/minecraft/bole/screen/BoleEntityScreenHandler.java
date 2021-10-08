package xienaoban.minecraft.bole.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public class BoleEntityScreenHandler extends AbstractBoleScreenHandler<Entity> {
    public static final ScreenHandlerType<BoleEntityScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "entity"), BoleEntityScreenHandler::new);

    public BoleEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId, playerInventory);
    }

    public BoleEntityScreenHandler(int syncId, PlayerInventory playerInventor, Entity entity) {
        super(HANDLER, syncId, playerInventor, entity);
    }

    @Override
    protected void init() {
    }
}
