package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoleLivingEntityScreenHandler<E extends LivingEntity> extends BoleEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleLivingEntityScreenHandler<LivingEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "living_entity"), BoleLivingEntityScreenHandler::new);

    private static final Field EFFECT_DURATION_EFFECT = MiscUtil.getField(StatusEffectInstance.class, "duration");

    protected List<StatusEffectInstance> entityStatusEffects;

    public BoleLivingEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleLivingEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleLivingEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleLivingEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        this.entityStatusEffects = new ArrayList<>();
    }

    @Override
    protected void initCustom() {}

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
        calStatusEffectsDuration();
    }

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
        Collection<StatusEffectInstance> effects = this.entity.getStatusEffects();
        buf.writeInt(effects.size());
        for (StatusEffectInstance effect : effects) {
            buf.writeInt(StatusEffect.getRawId(effect.getEffectType()));
            buf.writeInt(effect.getDuration());
            buf.writeInt(effect.getAmplifier());
            buf.writeBoolean(effect.isAmbient());
            buf.writeBoolean(effect.shouldShowParticles());
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        int size = buf.readInt();
        List<StatusEffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            StatusEffectInstance effect = new StatusEffectInstance(StatusEffect.byRawId(buf.readInt()), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean());
            effects.add(effect);
        }
        this.entityStatusEffects = effects;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }

    @Environment(EnvType.CLIENT)
    private void calStatusEffectsDuration() {
        for (StatusEffectInstance effect : this.entityStatusEffects) {
            MiscUtil.setFieldValue(effect, EFFECT_DURATION_EFFECT, Math.max(effect.getDuration() - 1, 0));
        }
    }
}
