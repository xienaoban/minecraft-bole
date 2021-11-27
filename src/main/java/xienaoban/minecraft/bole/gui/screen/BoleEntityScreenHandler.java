package xienaoban.minecraft.bole.gui.screen;

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

    // I didn't define a local variable "protected int entityNetherPortalCooldown" in this handler,
    // because the calculation logic of this value is inside Entity (and my mixin).

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
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_NETHER_PORTAL_COOLDOWN, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                ((IMixinEntity)entity).setNetherPortalCooldown(buf.readInt());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                int cooldown = (Integer) args[0];
                ((IMixinEntity)entity).setNetherPortalCooldown(cooldown);
                buf.writeInt(cooldown);
            }
        });
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_CUSTOM_NAME_VISIBLE, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                entity.setCustomNameVisible(buf.readBoolean());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                boolean visible = (Boolean) args[0];
                entity.setCustomNameVisible(visible);
                buf.writeBoolean(visible);
            }
        });
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_SILENT, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                entity.setSilent(buf.readBoolean());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                boolean silent = (Boolean) args[0];
                buf.writeBoolean(silent);
            }
        });
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        buf.writeString(this.entity.getUuidAsString());
        buf.writeInt(((IMixinEntity)this.entity).getNetherPortalCooldown());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        String uuid = buf.readString();
        if (!this.entity.getUuidAsString().equals(uuid)) {
            throw new RuntimeException("Expired package of the server entity.");
        }
        ((IMixinEntity)this.entity).setNetherPortalCooldown(buf.readInt());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        ((IMixinEntity)this.entity).setNetherPortalCooldown(0);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        if (ticks % 20 == 0) {
            ClientNetworkManager.requestServerEntityData();
        }
        calculateClientEntityNetherPortalCooldown();
    }

    @Environment(EnvType.CLIENT)
    private void calculateClientEntityNetherPortalCooldown() {
        switch (isClientEntityInNetherPortal()) {
            case -1: ((IMixinEntity) this.entity).callTickNetherPortalCooldown(); break;
            case 1: this.entity.resetNetherPortalCooldown(); break;
            default: break;
        }
    }

    /**
     * @return 1: in portal; -1: not in portal; 0: I don't know
     */
    @Environment(EnvType.CLIENT)
    private int isClientEntityInNetherPortal() {
        World world = MinecraftClient.getInstance().world;
        if (world == null || world.getEntityById(this.entity.getEntityId()) == null) {
            return 0;
        }
        Vec3d pos = this.entity.getPos();
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        final double r = 0.3;
        return world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x - r, y, z)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x + r, y, z)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x, y, z - r)).getBlock() == Blocks.NETHER_PORTAL
                || world.getBlockState(new BlockPos(x, y, z + r)).getBlock() == Blocks.NETHER_PORTAL
                ? 1 : -1;
    }
}
