package xienaoban.minecraft.bole.client.highlight;

import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.entity.EntityLookup;
import xienaoban.minecraft.bole.util.MiscUtil;

/**
 * Create a fake entity and display it only on current client.
 *
 * @see net.minecraft.client.network.ClientPlayNetworkHandler#onEntitySpawn
 * @see net.minecraft.client.world.ClientWorld#addEntity
 * @see net.minecraft.client.world.ClientWorld#removeEntity
 */
public class HighlightedFakeInstance extends HighlightedInstance {
    public HighlightedFakeInstance(Entity fakeEntity, int ticks) {
        super(fakeEntity, ticks);
        fakeEntity.setId(findAUsableEntityId());
        fakeEntity.setInvisible(true);
        fakeEntity.setNoGravity(true);
        if (fakeEntity instanceof MobEntity) {
            ((MobEntity) fakeEntity).setAiDisabled(true);
        }
        addEntity();
    }

    @Override
    protected void onStop() {
        // No need to call super.onStop() here.
        removeEntity();
    }

    private int findAUsableEntityId() {
        ClientEntityManager<Entity> entityManager = MiscUtil.getFieldValue(this.entity.world, ClientWorld.class, "entityManager");
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

    private void addEntity() {
        Entity entity = this.entity;
        ((ClientWorld) entity.world).addEntity(entity.getId(), entity);
    }

    private void removeEntity() {
        Entity entity = this.entity;
        ClientWorld world = ((ClientWorld) entity.world);
        // Make sure both entities are the same. This "if" prevents accidental deletion caused by overwritten of EntityLookup.
        if (world.getEntityById(entity.getId()) == entity) {
            world.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
        }
    }
}
