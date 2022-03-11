package xienaoban.minecraft.bole.gui.screen.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.gui.screen.GenericScreenHandler;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.network.ServerNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

public class BeehiveScreenHandler extends GenericScreenHandler {
    public static final ScreenHandlerType<BeehiveScreenHandler> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "beehive"), BeehiveScreenHandler::new);

    private final BlockPos pos;
    private final World world;
    private final BeehiveBlockEntity entity;

    protected final BeeInfo[] bees;
    protected int blockBeeCnt = 0;
    protected int blockHoneyCnt = 0;

    public BeehiveScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, clientBlock());
    }

    public BeehiveScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(HANDLER, syncId, playerInventory);
        this.bees = new BeeInfo[BeehiveBlockEntity.MAX_BEE_COUNT];
        for (int i = 0; i < this.bees.length; ++i) {
            this.bees[i] = new BeeInfo(this.player);
        }
        this.pos = pos;
        this.world = this.player.getWorld();
        this.entity = (BeehiveBlockEntity) this.world.getBlockEntity(pos);
        if (this.player instanceof ServerPlayerEntity serverPlayer) {
            ServerNetworkManager.sendServerBeehiveInfo(this, serverPlayer.server, serverPlayer);
        }
        else {
            PacketByteBuf buf = BoleClient.getInstance().getHandlerBufCache();
            if (buf != null) {
                readBeehiveInfo(buf);
                BoleClient.getInstance().setHandlerBufCache(null);
            }
        }
    }

    @Override
    protected void initCustom() {}

    /**
     * Invoked at the beginning of each client tick.
     *
     * @param ticks tick count
     */
    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        if (ticks % 10 == 5) {
            ClientNetworkManager.requestBeehiveInfo();
        }
    }

    public void writeBeehiveInfo(PacketByteBuf buf) {
        buf.writeInt(this.syncId);
        int honeyCnt = BeehiveBlockEntity.getHoneyLevel(this.world.getBlockState(pos));
        buf.writeInt(honeyCnt);
        NbtList beeList = this.entity.getBees();
        int beeCnt = beeList.size();
        buf.writeInt(beeCnt);
        for (int i = 0; i < beeCnt; ++i) {
            buf.writeNbt(beeList.getCompound(i));
        }
    }

    public void readBeehiveInfo(PacketByteBuf buf) {
        if (buf.readInt() != this.syncId) {
            Bole.LOGGER.warn("BeehiveScreenHandler#readBeehiveInfo(): wrong syncId.");
            return;
        }
        this.blockHoneyCnt = buf.readInt();
        this.blockBeeCnt = buf.readInt();
        for (int i = 0; i < this.blockBeeCnt; ++i) {
            NbtCompound nbt = buf.readNbt();
            assert nbt != null;
            BeeInfo bee = this.bees[i];
            bee.entity.setCustomName(null);
            bee.entity.readNbt(nbt.getCompound(BeehiveBlockEntity.ENTITY_DATA_KEY));
            bee.ticksInHive = nbt.getInt(BeehiveBlockEntity.TICKS_IN_HIVE_KEY);
            bee.minOccupationTicks = nbt.getInt(BeehiveBlockEntity.MIN_OCCUPATION_TICKS_KEY);
        }
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

    public static class BeeInfo {
        public final BeeEntity entity;
        public int ticksInHive;
        public int minOccupationTicks;

        public BeeInfo(PlayerEntity player) {
            this.entity = EntityType.BEE.create(player.getWorld());
            this.ticksInHive = -1;
            this.minOccupationTicks = -1;
        }
    }
}
