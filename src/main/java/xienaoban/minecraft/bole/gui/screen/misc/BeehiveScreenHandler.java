package xienaoban.minecraft.bole.gui.screen.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.util.Keys;

public class BeehiveScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<BeehiveScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "beehive"), BeehiveScreenHandler::new);

    public final BlockState blockState;
    public final BeehiveBlockEntity entity;

    public BeehiveScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, clientBlock());
    }

    public BeehiveScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(HANDLER, syncId);
        World world = playerInventory.player.getWorld();
        this.blockState = world.getBlockState(pos);
        this.entity = (BeehiveBlockEntity) world.getBlockEntity(pos);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    /**
     * Gets the block pos the client-side player is aiming at.
     */
    @Environment(EnvType.CLIENT)
    public static BlockPos clientBlock() {
        BlockPos pos = BoleClient.getInstance().getHitBlock();
        // Set BoleTarget to null to avoid memory leak.
        BoleClient.getInstance().setHitBlock(null);
        return pos;
    }
}
