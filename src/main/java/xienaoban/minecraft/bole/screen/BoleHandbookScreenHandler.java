package xienaoban.minecraft.bole.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public class BoleHandbookScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<BoleHandbookScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "handbook"), BoleHandbookScreenHandler::new);

    public BoleHandbookScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
