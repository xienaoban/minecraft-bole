package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.entity.EntityLookup;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class HighlightManager {
    private final List<HighlightState> highlighted;
    private final List<BlockHighlightState> toMove;

    private int checkIndex;

    private HighlightState jobSiteHighlightState;

    public HighlightManager() {
        this.highlighted = new ArrayList<>();
        this.toMove = new ArrayList<>();
        this.checkIndex = 0;
        this.jobSiteHighlightState = null;
    }

    public void tick(int ticks) {
        if (this.highlighted.size() > 0) {
            if (this.toMove.size() > 0) {
                List<BlockHighlightState> list = this.toMove;
                for (int i = list.size() - 1; i >= 0; --i) {
                    BlockHighlightState highLightState = list.get(i);
                    if (highLightState.moveToRightPosition()) {
                        list.remove(i);
                    }
                }
            }
            // Checks only one HighlightState each tick. Because I don’t want it to affect game performance.
            List<HighlightState> list = this.highlighted;
            if (this.checkIndex >= list.size()) {
                this.checkIndex = 0;
            }
            HighlightState highLightState = list.get(this.checkIndex);
            highLightState.tick(ticks);
            if (highLightState.isStopped()) {
                list.remove(this.checkIndex);
            }
            ++this.checkIndex;
        }
    }

    public HighlightState highlightEntity(Entity entity, int ticks) {
        HighlightState highLightState = new HighlightState(entity, ticks);
        this.highlighted.add(highLightState);
        return highLightState;
    }

    public HighlightState highlightBlock(GlobalPos pos, int ticks) {
        BlockHighlightState highLightState = new BlockHighlightState(pos, ticks);
        this.toMove.add(highLightState);
        this.highlighted.add(highLightState);
        return highLightState;
    }

    public void setJobSiteOrBeehiveHighlightState(HighlightState jobSiteHighlightState) {
        HighlightState last = this.jobSiteHighlightState;
        if (last != null) {
            last.stop();
        }
        this.jobSiteHighlightState = jobSiteHighlightState;
    }

    /**
     * FallingBlockEntity doesn't render when the BlockState it displays is at its position (see MC-114286).
     * And FallingBlockEntity can't be invisible...
     * Solution: Spawn the FallingBlockEntity at any other position. Then set it to the right position after few ticks.
     */
    private static FallingBlockEntity createFallingBlockEntity(GlobalPos globalPos) {
        BlockPos pos = globalPos.getPos();
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null || !Objects.equals(world.getRegistryKey(), globalPos.getDimension())) {
            return null;
        }
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir() || blockState.getBlock() instanceof FluidBlock) {
            return null;
        }
        return new FallingBlockEntity(world, pos.getX() + 0.5, -123, pos.getZ() + 0.5, blockState);
    }

    private static int findAUsableEntityId(ClientWorld world) {
        ClientEntityManager<Entity> entityManager = MiscUtil.getFieldValue(world, ClientWorld.class, "entityManager");
        EntityLookup<Entity> lookup = entityManager.getLookup();
        int id;
        for (id = -66666666; id < -66660000; ++id) {
            if (lookup.get(id) == null) {
                break;
            }
        }
        if (lookup.get(id) != null) {
            throw new RuntimeException("Unable to find a empty entity id.");
        }
        return id;
    }

    public static class HighlightState {
        protected final Entity entity;
        private final int endTicks;
        private boolean stopped;

        public HighlightState(Entity entity, int ticks) {
            this.entity = entity;
            this.endTicks = BoleClient.getInstance().getTicks() + ticks;
            this.stopped = false;
            ((IMixinEntity) entity).callSetFlag(IMixinEntity.getGlowingFlagIndex(), true);
        }

        protected final void tick(int ticks) {
            if (!isStopped()) {
                // Check "this.entity.world != MinecraftClient.getInstance().world" to avoid memory leak.
                if (this.endTicks < ticks || this.entity.world != MinecraftClient.getInstance().world) {
                    stop();
                }
                else {
                    onTick();
                }
            }
        }

        public void stop() {
            if (!isStopped()) {
                this.stopped = true;
                onStop();
            }
        }

        protected void onTick() {}

        protected void onStop() {
            ((IMixinEntity) this.entity).callSetFlag(IMixinEntity.getGlowingFlagIndex(), false); // todo
        }

        public boolean isStopped() {
            return this.stopped;
        }

        public Entity getEntity() {
            return this.entity;
        }
    }

    /**
     * Create a fake entity and display it only on current client.
     *
     * @see net.minecraft.client.network.ClientPlayNetworkHandler#onEntitySpawn
     * @see net.minecraft.client.world.ClientWorld#addEntity
     * @see net.minecraft.client.world.ClientWorld#removeEntity
     */
    private static class FakeStationaryHighlightState extends HighlightState {

        public FakeStationaryHighlightState(Entity fakeEntity, int ticks) {
            super(fakeEntity, ticks);
            fakeEntity.setId(findAUsableEntityId((ClientWorld) fakeEntity.world));
            fakeEntity.setInvisible(true);
            fakeEntity.setNoGravity(true);
            if (fakeEntity instanceof MobEntity) {
                ((MobEntity) fakeEntity).setAiDisabled(true);
            }
            addEntity();
        }

        @Override
        protected void onStop() {
            removeEntity();
        }

        private void addEntity() {
            Entity entity = this.entity;
            ((ClientWorld) entity.world).addEntity(entity.getId(), entity);
        }

        private void removeEntity() {
            Entity entity = this.entity;
            ClientWorld world = ((ClientWorld) entity.world);
            if (world.getEntityById(entity.getId()) == entity) {
                world.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    private static class BlockHighlightState extends FakeStationaryHighlightState {
        protected final BlockPos pos;
        private int moveTicks;

        public BlockHighlightState(GlobalPos pos, int ticks) {
            super(createFallingBlockEntity(pos), ticks);
            this.pos = pos.getPos();
            this.moveTicks = 4;
        }

        @Override
        protected void onTick() {
            super.onTick();
            if (!Objects.equals(this.entity.world.getBlockState(pos).getBlock(), ((FallingBlockEntity) this.entity).getBlockState().getBlock())) {
                stop();
            }
        }

        protected final boolean moveToRightPosition() {
            if (isStopped()) {
                return true;
            }
            if (--this.moveTicks == 0) {
                entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                return true;
            }
            return false;
        }
    }
}
