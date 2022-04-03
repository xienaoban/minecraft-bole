package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.gui.screen.tree.BoleMerchantEntityScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinVillagerEntity;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Optional;

public class BoleVillagerEntityScreenHandler<E extends VillagerEntity> extends BoleMerchantEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleVillagerEntityScreenHandler<VillagerEntity>> HANDLER
            = register(new Identifier(Keys.NAMESPACE, "villager_entity"), BoleVillagerEntityScreenHandler::new);

    @Environment(EnvType.CLIENT)
    protected int entityRestocksToday;

    @Environment(EnvType.CLIENT)
    protected GlobalPos entityJobSitePos;

    public BoleVillagerEntityScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(HANDLER, syncId, playerInventory);
    }

    public BoleVillagerEntityScreenHandler(int syncId, PlayerInventory playerInventory, Entity entity) {
        this(HANDLER, syncId, playerInventory, entity);
    }

    public BoleVillagerEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory) {
        this(handler, syncId, playerInventory, clientEntity());
    }

    public BoleVillagerEntityScreenHandler(ScreenHandlerType<?> handler, int syncId, PlayerInventory playerInventory, Entity entity) {
        super(handler, syncId, playerInventory, entity);
        registerEntitySettingsBufHandlers();
    }

    private void registerEntitySettingsBufHandlers() {
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_RESET_JOB, new EntitySettingsBufHandler() {
            /**
             * @see net.minecraft.entity.ai.brain.task.LoseJobOnSiteLossTask#shouldRun
             */
            @Override public void readFromBuf(PacketByteBuf buf) {
                entity.setOffers(null);
                entity.setExperience(0);
                entity.setVillagerData(entity.getVillagerData().withLevel(1));
                entity.reinitializeBrain((ServerWorld) entity.world);
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {}
        });
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_RESTOCK, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                ItemStack overTime = buf.readItemStack();
                if (trySpendItems(overTime)) {
                    entity.playWorkSound();
                    entity.restock();
                }
                else Bole.LOGGER.error("The player inventory data on the client and server are inconsistent.");
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                ItemStack overTime = (ItemStack) args[0];
                buf.writeItemStack(overTime);
                ++entityRestocksToday;
            }
        });
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_VILLAGER_CLOTHING, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                VillagerType type = Registry.VILLAGER_TYPE.get(Identifier.tryParse(buf.readString()));
                entity.setVillagerData(entity.getVillagerData().withType(type));
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                VillagerType type = (VillagerType) args[0];
                buf.writeString(type.toString());
                entity.setVillagerData(entity.getVillagerData().withType(type));
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
        buf.writeInt(((IMixinVillagerEntity)this.entity).getRestocksToday());

        Optional<GlobalPos> jobSite = this.entity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        buf.writeBoolean(jobSite.isPresent());
        if (jobSite.isPresent()) {
            GlobalPos pos = jobSite.get();
            buf.writeIdentifier(pos.getDimension().getValue());
            buf.writeBlockPos(pos.getPos());
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void readServerEntityFromBuf(PacketByteBuf buf) {
        super.readServerEntityFromBuf(buf);
        this.entityRestocksToday = buf.readInt();

        if (buf.readBoolean()) {
            RegistryKey<World> dimension = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
            BlockPos blockPos = new BlockPos(buf.readBlockPos());
            this.entityJobSitePos = GlobalPos.create(dimension, blockPos);
        }
        else {
            this.entityJobSitePos = null;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void resetClientEntityServerProperties() {
        super.resetClientEntityServerProperties();
    }

    protected boolean hasJob() {
        return this.entity.getVillagerData().getProfession() != VillagerProfession.NONE;
    }
}
