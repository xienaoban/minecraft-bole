package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.gui.screen.tree.BoleMerchantEntityScreenHandler;
import xienaoban.minecraft.bole.mixin.IMixinVillagerEntity;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Optional;

public class BoleVillagerEntityScreenHandler<E extends VillagerEntity> extends BoleMerchantEntityScreenHandler<E> {
    public static final ScreenHandlerType<BoleVillagerEntityScreenHandler<VillagerEntity>> HANDLER
            = register(new Identifier(Keys.NAMESPACE, "villager_entity"), BoleVillagerEntityScreenHandler::new);

    protected static final ItemStack CHANGE_CLOTH_COST = new ItemStack(Items.LEATHER, 5 + 8 + 7 + 4);

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
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_RESET_VILLAGER_JOB, new EntitySettingsBufHandler() {
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
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_VILLAGER_RESTOCK, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                int cnt = Math.max(0, ((IMixinVillagerEntity)entity).getRestocksToday() - 3 + 1) * 2;
                ItemStack overTime = new ItemStack(Items.EMERALD, cnt);
                if (isGod() || trySpendItems(overTime)) {
                    entity.playWorkSound();
                    entity.restock();
                }
                else {
                    sendOverlayMessage(Text.translatable(Keys.HINT_TEXT_NOT_ENOUGH_ITEMS));
                }
            }
            @Override public void writeToBuf(PacketByteBuf buf, Object... args) {
                ++entityRestocksToday;
            }
        });
        registerEntitySettingsBufHandler(Keys.ENTITY_SETTING_VILLAGER_CLOTHING, new EntitySettingsBufHandler() {
            @Override public void readFromBuf(PacketByteBuf buf) {
                if (!isGod() && !trySpendItems(CHANGE_CLOTH_COST)) {
                    sendOverlayMessage(Text.translatable(Keys.HINT_TEXT_NOT_ENOUGH_ITEMS));
                    return;
                }
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
