package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreen;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

public class BoleEntityScreenHandler<E extends Entity> extends AbstractBoleScreenHandler<E> {
    public static final ScreenHandlerType<BoleEntityScreenHandler<Entity>> HANDLER
            = register(new Identifier(Keys.NAMESPACE, "entity"), BoleEntityScreenHandler::new);

    public static final int NETHER_PORTAL_LOCK = 1200;  // don't set it too big to ensure compatibility with the vanilla
    private static final int CLOSE_SCREEN_DISTANCE = 10;

    protected final boolean isMonster;

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
        this.isMonster = Bole.isMonster(this.entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_NETHER_PORTAL_COOLDOWN, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                if (isOtherPlayer() && Configs.getInstance().isForbidToSetNetherPortalCooldownOfOtherPlayers()) {
                    sendOverlayMessage(Text.translatable(Keys.HINT_TEXT_FORBID_TO_SET_NETHER_PORTAL_COOLDOWN_OF_OTHER_PLAYERS));
                    return;
                }
                ((IMixinEntity)entity).setPortalCooldown(buf.readInt());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                int cooldown = (Integer) args[0];
                ((IMixinEntity) entity).setPortalCooldown(cooldown);
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
                entity.setSilent(true);
            }
        });
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_INVULNERABLE, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                if (isGod()) entity.setInvulnerable(buf.readBoolean());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                boolean invulnerable = (Boolean) args[0];
                buf.writeBoolean(invulnerable);
                entity.setInvulnerable(invulnerable);
            }
        });
    }

    @Override
    protected void initServer() {
        super.initServer();
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void initClient() {
        super.initClient();
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        buf.writeInt(this.syncId);
        buf.writeInt(((IMixinEntity)this.entity).getPortalCooldown());
        buf.writeBoolean(this.entity.isInvulnerable());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        int id = buf.readInt();
        if (this.syncId != id) {
            throw new RuntimeException("Expired packet of the server entity.");
        }
        ((IMixinEntity)this.entity).setPortalCooldown(buf.readInt());
        this.entity.setInvulnerable(buf.readBoolean());
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        ((IMixinEntity)this.entity).setPortalCooldown(0);
        this.entity.setInvulnerable(false);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        if (ticks % 20 == 0) {
            if (this.player.squaredDistanceTo(this.entity) > CLOSE_SCREEN_DISTANCE * CLOSE_SCREEN_DISTANCE) {
                if (MinecraftClient.getInstance().currentScreen instanceof AbstractBoleScreen screen) {
                    BoleClient.getInstance().getHighlightManager().highlight(this.entity, 3 * 20);
                    screen.close();
                    player.sendMessage(Text.translatable(Keys.TEXT_TARGET_ENTITY_TOO_FAR).formatted(Formatting.YELLOW), true);
                }
            }
            ClientNetworkManager.requestServerEntityData();
        }
        calculateClientEntityNetherPortalCooldown();
    }

    protected boolean isOtherPlayer() {
        return this.entity instanceof PlayerEntity && this.entity != this.player;
    }

    @Environment(EnvType.CLIENT)
    private void calculateClientEntityNetherPortalCooldown() {
        switch (isClientEntityInNetherPortal()) {
            case -1 -> ((IMixinEntity) this.entity).callTickPortalCooldown();
            case 1 -> this.entity.resetPortalCooldown();
            default -> {}
        }
    }

    /**
     * @return 1: in portal; -1: not in portal; 0: I don't know
     */
    @Environment(EnvType.CLIENT)
    private int isClientEntityInNetherPortal() {
        World world = MinecraftClient.getInstance().world;
        if (world == null || world.getEntityById(this.entity.getId()) == null) return 0;
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
