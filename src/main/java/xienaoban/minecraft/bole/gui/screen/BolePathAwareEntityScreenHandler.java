package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xienaoban.minecraft.bole.mixin.IMixinMobEntity;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class BolePathAwareEntityScreenHandler<E extends PathAwareEntity> extends BoleMobEntityScreenHandler<E> {
    public static final ScreenHandlerType<BolePathAwareEntityScreenHandler<PathAwareEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "path_aware_entity"), BolePathAwareEntityScreenHandler::new);

    @Environment(EnvType.CLIENT)
    protected Item[] entityAttractiveItems;

    public BolePathAwareEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BolePathAwareEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BolePathAwareEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BolePathAwareEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
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

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
    }

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        Item[] items = getEntityAttractiveItems();
        buf.writeInt(items.length);
        Arrays.stream(items).forEach(item -> buf.writeString(Registry.ITEM.getId(item).toString()));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        int len = buf.readInt();
        Item[] items = new Item[len];
        for (int i = 0; i < len; ++i) {
            items[i] = Registry.ITEM.get(new Identifier(buf.readString()));
        }
        this.entityAttractiveItems = items;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }

    /**
     * Only works on server side.
     */
    private Item[] getEntityAttractiveItems() {
        if (this.entityAttractiveItems != null) {
            return this.entityAttractiveItems;
        }
        Item[] items = null;
        GoalSelector goalSelector = ((IMixinMobEntity)this.entity).getGoalSelector();
        Set<PrioritizedGoal> goals = MiscUtil.getFieldValue(goalSelector, GoalSelector.class, "goals");
        for (PrioritizedGoal prioritizedGoal : goals) {
            Goal goal = prioritizedGoal.getGoal();
            if (!(goal instanceof TemptGoal)) {
                continue;
            }
            Ingredient foods = MiscUtil.getFieldValue(goal, TemptGoal.class, "food");
            ItemStack[] itemStacks = foods.getMatchingStacks();
            items = Arrays.stream(itemStacks).map(ItemStack::getItem)
                    .sorted(Comparator.comparing(Registry.ITEM::getId)).toArray(Item[]::new);
            break;
        }
        if (items == null) {
            items = new Item[0];
        }
        this.entityAttractiveItems = items;
        return items;
    }
}
