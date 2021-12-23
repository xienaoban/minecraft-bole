package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.gui.screen.BoleAnimalEntityScreenHandler;
import xienaoban.minecraft.bole.util.Keys;

public class BolePandaEntityScreenHandler<E extends PandaEntity> extends BoleAnimalEntityScreenHandler<E> {
    public static final ScreenHandlerType<BolePandaEntityScreenHandler<PandaEntity>> HANDLER = ScreenHandlerRegistry.registerSimple(
            new Identifier(Keys.NAMESPACE, "panda_entity"), BolePandaEntityScreenHandler::new);

    public BolePandaEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BolePandaEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BolePandaEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BolePandaEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_PANDA_VARIANT, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                if (isGodMode()) {
                    boolean isMainGene = buf.readBoolean();
                    PandaEntity.Gene gene = PandaEntity.Gene.byId(buf.readInt());
                    if (isMainGene) entity.setMainGene(gene);
                    else entity.setHiddenGene(gene);
                }
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                boolean isMainGene = (Boolean) args[0];
                PandaEntity.Gene gene = (PandaEntity.Gene) args[1];
                buf.writeBoolean(isMainGene);
                buf.writeInt(gene.getId());
                if (isMainGene) entity.setMainGene(gene);
                else entity.setHiddenGene(gene);
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
    protected void initCustom() {
    }

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
