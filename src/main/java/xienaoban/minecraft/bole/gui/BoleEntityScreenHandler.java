package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

public class BoleEntityScreenHandler<E extends Entity> extends AbstractBoleScreenHandler<E> {
    public static final ScreenHandlerType<BoleEntityScreenHandler<Entity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "entity"), BoleEntityScreenHandler::new);

    public BoleEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
    }

    @Override
    protected void initCustom() {}

    @Override
    public void writeServerEntityToBuf(PacketByteBuf buf) {
        buf.writeInt(((IMixinEntity)this.entity).getNetherPortalCooldown());
    }

    @Override
    public void readServerEntityFromBuf(PacketByteBuf buf) {
        ((IMixinEntity)this.entity).setNetherPortalCooldown(buf.readInt());
    }

    @Override
    public void clientTick(int ticks) {
        if (ticks % 20 == 0) {
            ClientNetworkManager.requestServerEntityData();
        }
        calculateClientEntityNetherPortalCooldown();
    }

    @Environment(EnvType.CLIENT)
    private void calculateClientEntityNetherPortalCooldown() {
        if (isClientEntityInNetherPortal()) {
            this.entity.resetNetherPortalCooldown();
        }
        else {
            ((IMixinEntity) this.entity).callTickNetherPortalCooldown();
        }
    }

    @Environment(EnvType.CLIENT)
    private boolean isClientEntityInNetherPortal() {
        World world = MinecraftClient.getInstance().world;
        if (world == null || world.getEntityById(this.entity.getEntityId()) == null) {
            return true;
        }
        Vec3d pos = this.entity.getPos();
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        final double r = 0.25;
        return world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x - r, y, z)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x + r, y, z)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x, y, z - r)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x, y, z + r)).getBlock() == Blocks.NETHER_PORTAL;
    }
}
