package xienaoban.minecraft.bole.client.highlight;

import net.minecraft.entity.Entity;
import net.minecraft.util.dynamic.GlobalPos;
import xienaoban.minecraft.bole.config.ClientConfig;

import java.util.*;

public class HighlightManager {
    private final PriorityQueue<HighlightedInstance> highlightedQue;
    private final Map<Entity, HighlightedInstance> highlightedMap;
    private final List<HighlightedBlockInstance> highlightedBlockList;

    private HighlightedBlockInstance onlyHighlighted;

    public HighlightManager() {
        this.highlightedQue = new PriorityQueue<>();
        this.highlightedMap = new HashMap<>();
        this.highlightedBlockList = new ArrayList<>();
        this.onlyHighlighted = null;
    }

    public void tick() {
        if (!this.highlightedBlockList.isEmpty()) {
            this.highlightedBlockList.removeIf(HighlightedInstance::check);
        }
        if (!this.highlightedQue.isEmpty()) {
            PriorityQueue<HighlightedInstance> que = this.highlightedQue;
            HighlightedInstance ins;
            while ((ins = que.peek()) != null && ins.check()) {
                que.poll();
                this.highlightedMap.remove(ins.getEntity());
                if (ClientConfig.lazilyUnhighlight) break;
            }
        }
    }

    public HighlightedInstance highlight(Entity entity, int ticks) {
        PriorityQueue<HighlightedInstance> que = this.highlightedQue;
        Map<Entity, HighlightedInstance> map = this.highlightedMap;
        HighlightedInstance old = map.getOrDefault(entity, null);
        if (old != null) {
            old.stop();
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

    public void setOnlyHighlighted(HighlightedBlockInstance onlyHighlighted) {
        HighlightedBlockInstance old = this.onlyHighlighted;
        if (old != null) old.stop();
        this.onlyHighlighted = onlyHighlighted;
    }

    public void clear() {
        this.highlightedQue.clear();
        this.highlightedMap.clear();
        this.highlightedBlockList.forEach(HighlightedBlockInstance::stop); // actually it's ok not to call "stop" here
        this.highlightedBlockList.clear();
        this.onlyHighlighted = null;
    }
}
