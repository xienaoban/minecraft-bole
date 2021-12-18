package xienaoban.minecraft.bole.client.highlight;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.mixin.IMixinEntity;

public class HighlightedInstance implements Comparable<HighlightedInstance> {
    protected final Entity entity;
    private final int endTicks;
    private boolean stopped;

    public HighlightedInstance(Entity entity, int ticks) {
        this.entity = entity;
        this.endTicks = getCurrentTicks() + ticks;
        this.stopped = false;
        ((IMixinEntity) entity).callSetFlag(IMixinEntity.getGlowingFlagIndex(), true);
    }

    /**
     * Check if the entity should continue to highlight.
     * @return true to stop highlighting, false to keep highlighting.
     */
    protected final boolean check() {
        if (isStopped()) return true;
        if (this.endTicks < getCurrentTicks() || shouldStop()) {
            stop();
            return true;
        }
        return false;
    }

    public final void stop() {
        if (isStopped()) return;
        this.stopped = true;
        onStop();
    }

    public final boolean isStopped() {
        return stopped;
    }

    protected boolean shouldStop() {
        return false;
    }

    protected void onStop() {
        ((IMixinEntity) this.entity).callSetFlag(IMixinEntity.getGlowingFlagIndex(), false);
        BoleClient.getInstance().getHighlightManager().checkServerGlowing(this.entity);
    }

    public Entity getEntity() {
        return entity;
    }

    protected int getCurrentTicks() {
        return BoleClient.getInstance().getTicks();
    }

    @Override
    public int compareTo(@NotNull HighlightedInstance that) {
        return this.endTicks - that.endTicks;
    }
}
