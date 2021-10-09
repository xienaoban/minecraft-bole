package xienaoban.minecraft.bole.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public class BoleHorseEntityScreenHandler extends AbstractBoleScreenHandler<HorseEntity> {
    public static final ScreenHandlerType<BoleHorseEntityScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "horse_entity"), BoleHorseEntityScreenHandler::new);

    public BoleHorseEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId, playerInventory);
    }

    public BoleHorseEntityScreenHandler(int syncId, PlayerInventory playerInventor, Entity entity) {
        super(HANDLER, syncId, playerInventor, entity);
    }

    @Override
    protected void init() {}
}
