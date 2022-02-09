package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import xienaoban.minecraft.bole.util.Keys;

public class BoleMobEntityScreenHandler<E extends MobEntity> extends BoleLivingEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleMobEntityScreenHandler<MobEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "mob_entity"), BoleMobEntityScreenHandler::new);

    public BoleMobEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleMobEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleMobEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleMobEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_NO_AI, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                set(buf.readBoolean());
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                boolean disabled = (Boolean) args[0];
                buf.writeBoolean(disabled);
                set(disabled);
            }
            private void set(boolean disabled) {
                entity.setAiDisabled(disabled);
                int healthAndSatiety = isMonster ? 12 : 6;
                if (disabled && !isGod()) {
                    player.damage(DamageSource.mob(entity), entity.world.getDifficulty() == Difficulty.HARD ? healthAndSatiety / 1.5F : healthAndSatiety);
                    player.getHungerManager().add(-healthAndSatiety, 0);
                }
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

    @Environment(EnvType.CLIENT)
    @Override
    public void clientTick(int ticks) {
        super.clientTick(ticks);
    }

    @Override
    protected void writeServerEntityToBuf(PacketByteBuf buf) {
        super.writeServerEntityToBuf(buf);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }
}
