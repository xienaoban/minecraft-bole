package xienaoban.minecraft.bole.client.highlight;

import net.minecraft.entity.Entity;
import net.minecraft.util.dynamic.GlobalPos;
import xienaoban.minecraft.bole.network.ClientNetworkManager;

import java.util.*;

public class HighlightManager {
    private final PriorityQueue<HighlightedInstance> highlightedQue;
    private final Map<Entity, HighlightedInstance> highlightedMap;
    private final List<HighlightedBlockInstance> highlightedBlockList;

    private HighlightedBlockInstance highlightedJobSiteOrBeehive;

    private final Queue<Entity> toCheckGlowingFromServer;

    public HighlightManager() {
        this.highlightedQue = new PriorityQueue<>();
        this.highlightedMap = new HashMap<>();
        this.highlightedBlockList = new ArrayList<>();
        this.highlightedJobSiteOrBeehive = null;
        this.toCheckGlowingFromServer = new ArrayDeque<>(123);
    }

    public void tick() {
        if (!this.highlightedBlockList.isEmpty()) {
            // highlighting of blocks should be checked every tick (to check if the block is broken)
            this.highlightedBlockList.removeIf(HighlightedInstance::check);
            if (this.highlightedJobSiteOrBeehive != null && this.highlightedJobSiteOrBeehive.isStopped()) {
                this.highlightedJobSiteOrBeehive = null;
            }
        }
        if (!this.highlightedQue.isEmpty()) {
            PriorityQueue<HighlightedInstance> que = this.highlightedQue;
            HighlightedInstance ins;
            while ((ins = que.peek()) != null && ins.check()) {
                que.poll();
                this.highlightedMap.remove(ins.getEntity());
            }
            ClientNetworkManager.requestServerEntitiesGlowing(this.toCheckGlowingFromServer);
        }
    }

    public HighlightedInstance highlight(Entity entity, int ticks) {
        PriorityQueue<HighlightedInstance> que = this.highlightedQue;
        Map<Entity, HighlightedInstance> map = this.highlightedMap;
        HighlightedInstance old = map.getOrDefault(entity, null);
        if (old != null) {
            // old.stop();  // don't call "stop" as it will check server glowing state
            que.remove(old);
        }
        HighlightedInstance ins = new HighlightedInstance(entity, ticks);
        map.put(entity, ins);
        que.add(ins);
        return ins;
    }
    
    public HighlightedBlockInstance highlight(GlobalPos pos, int ticks) {
        HighlightedBlockInstance ins = new HighlightedBlockInstance(pos, ticks);
        this.highlightedBlockList.add(ins);
        return ins;
    }

    public void setHighlightedJobSiteOrBeehive(HighlightedBlockInstance highlighted) {
        HighlightedBlockInstance old = this.highlightedJobSiteOrBeehive;
        if (old != null) old.stop();
        this.highlightedJobSiteOrBeehive = highlighted;
    }

    public void clear() {
        this.highlightedQue.clear();
        this.highlightedMap.clear();
        this.highlightedBlockList.forEach(HighlightedBlockInstance::stop); // actually it's ok not to call "stop" here
        this.highlightedBlockList.clear();
        this.highlightedJobSiteOrBeehive = null;
    }

    public void checkServerGlowing(Entity entity) {
        this.toCheckGlowingFromServer.add(entity);
    }

    @Override
    public String toString() {
        return "highlightedQue(" + this.highlightedQue.size() +
                "), highlightedMap(" + this.highlightedMap.size() +
                "), highlightedBlockList(" + this.highlightedBlockList.size() +
                "), highlightedJobSiteOrBeehive(" + (this.highlightedJobSiteOrBeehive != null ? 1 : 0) +
                // "), toCheckGlowingFromServer(" + this.toCheckGlowingFromServer.size() +
                ")";
    }
}
