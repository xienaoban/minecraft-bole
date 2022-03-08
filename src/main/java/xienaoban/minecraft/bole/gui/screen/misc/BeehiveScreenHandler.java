package xienaoban.minecraft.bole.gui.screen.misc;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public class BeehiveScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<BeehiveScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "beehive"), BeehiveScreenHandler::new);

    public BeehiveScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(HANDLER, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
