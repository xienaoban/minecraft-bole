package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.screen.GenericHandledScreen;
import xienaoban.minecraft.bole.mixin.IMixinBlockItem;
import xienaoban.minecraft.bole.util.Keys;

import java.util.ArrayList;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class EventsManager {
    public static void init() {
        initBeehiveTooltip();
    }

    private static void initBeehiveTooltip() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (!(Items.BEEHIVE.equals(stack.getItem()) || Items.BEE_NEST.equals(stack.getItem()))) return;
            NbtCompound nbt = stack.getNbt();
            if (nbt == null) return;
            ArrayList<Text> beeLines = new ArrayList<>();
            if (nbt.contains(BlockItem.BLOCK_STATE_TAG_KEY, NbtElement.COMPOUND_TYPE)) {
                NbtCompound blockNbt = nbt.getCompound(BlockItem.BLOCK_STATE_TAG_KEY);
                if (blockNbt.contains(Properties.HONEY_LEVEL.getName(), NbtElement.INT_TYPE)) {
                    int honeyLevel = blockNbt.getInt(Properties.HONEY_LEVEL.getName());
                    beeLines.add(Text.translatable(Keys.TEXT_HONEY_LEVEL, honeyLevel + "/" + BeehiveBlock.FULL_HONEY_LEVEL).formatted(Formatting.YELLOW));
                }
            }
            if (nbt.contains(IMixinBlockItem.getBlockEntityTagKey(), NbtElement.COMPOUND_TYPE)) {
                NbtCompound entityNbt = nbt.getCompound(IMixinBlockItem.getBlockEntityTagKey());
                if (entityNbt.contains(BeehiveBlockEntity.BEES_KEY, NbtElement.LIST_TYPE)) {
                    NbtList bees = entityNbt.getList(BeehiveBlockEntity.BEES_KEY, NbtElement.COMPOUND_TYPE);
                    int beeCnt = bees.size();
                    beeLines.add(Text.translatable(Keys.TEXT_BEE_COUNT, beeCnt + "/" + BeehiveBlockEntity.MAX_BEE_COUNT).formatted(Formatting.GOLD));
                    if (beeCnt > 0) {
                        beeLines.add(Text.empty());
                        beeLines.add(Text.translatable(Keys.TEXT_BEE_INFO).formatted(Formatting.GRAY));
                    }
                    for (int i = 0; i < beeCnt; ++i) {
                        NbtCompound beeNbt = bees.getCompound(i).getCompound(BeehiveBlockEntity.ENTITY_DATA_KEY);
                        BeeEntity bee = EntityType.BEE.create(MinecraftClient.getInstance().world);
                        if (bee == null) {
                            beeLines.add(Text.literal("[ERROR]").formatted(Formatting.RED));
                            continue;
                        }
                        bee.readNbt(beeNbt);
                        beeLines.add(bee.getName().copy().append(Text.translatable(Keys.TEXT_COLON)).append(Text.translatable(bee.isBaby() ? Keys.TEXT_MINOR : Keys.TEXT_ADULT)).formatted(Formatting.BLUE));
                    }
                }
            }
            lines.addAll(1, beeLines);
        });
    }

    public static class LeashFallFromPlayerEvent {
        long lastFallTime = 0;
        int times = 0;

        public void onFall(ClientPlayerEntity player) {
            long curTime = System.currentTimeMillis();
            if (curTime - this.lastFallTime > 3000) this.times = 1;
            else ++this.times;
            this.lastFallTime = curTime;
            GenericHandledScreen.playScreenSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 0.4F, 1.0F);
            player.sendMessage(Text.translatable(Keys.TEXT_LEASH_FALL, this.times).formatted(Formatting.GOLD), true);
        }
    }

    public static class ShoulderEntityFirstPersonRenderer {
        private final NbtCompound[] oldNbts = new NbtCompound[2];
        private final LivingEntity[] entities = new LivingEntity[2];
        private long lastTime = 0;
        private float rotateX = 0, rotateY = 0, entityX = 0, entityY = 0;

        public void renderShoulderEntity(MinecraftClient client) {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                // prevent memory leak
                if (this.oldNbts[0] != null || this.oldNbts[1] != null || this.entities[0] != null || this.entities[1] != null) {
                    this.oldNbts[0] = this.oldNbts[1] = null;
                    this.entities[0] = this.entities[1] = null;
                }
                return;
            }
            if (!client.options.getPerspective().isFirstPerson() || player != client.cameraEntity) return;

            int w = client.getWindow().getScaledWidth() >> 1, h = client.getWindow().getScaledHeight() >> 1;
            float r = -18, z = -40;
            float x, y, flip;
            switch (Configs.getInstance().getShoulderCreatureHudPosition()) {
                case TOP: x = w >> 1; y = -96; flip = 140; z = -z; break;
                case BOTTOM: x = w * 0.7F; y = (h << 1) + 70; flip = 5; break;
                case SIDES: x = w + 28.5F; y = h + 70; flip = 15; break;
                default: return;
            }
            long diffTime = System.currentTimeMillis() - this.lastTime;
            float rx = (player.getYaw() - this.rotateX) / diffTime * 0.05F;
            float ry = (player.getPitch() - this.rotateY) / diffTime * 0.05F;
            this.lastTime += diffTime;
            this.rotateX = player.getYaw();
            this.rotateY = player.getPitch();
            this.entityX = this.entityX - this.entityX * 0.01F * diffTime + rx;
            this.entityY = this.entityY - this.entityY * 0.01F * diffTime + ry;
            for (int i = 0; i < 2; ++i) {
                NbtCompound entityNbt;
                if (i == 0) {
                    entityNbt = player.getShoulderEntityLeft();
                }
                else {
                    entityNbt = player.getShoulderEntityRight();
                    x = -x;
                    r = -r;
                }
                if (oldNbts[i] != entityNbt) {
                    oldNbts[i] = entityNbt;
                    if (entityNbt.isEmpty()) {
                        entities[i] = null;
                        continue;
                    }
                    Optional<Entity> optionalEntity = EntityType.getEntityFromNbt(entityNbt, player.world);
                    if (optionalEntity.isPresent()) {
                        LivingEntity entity = (LivingEntity) optionalEntity.get();
                        entity.setYaw(0);
                        entity.setBodyYaw(0);
                        entity.setHeadYaw(0);
                        entity.prevHeadYaw = 0;
                        entity.setPitch(0);
                        entities[i] = entity;
                    } else {
                        entities[i] = null;
                        continue;
                    }
                }
                if (entities[i] == null) continue;
                GenericHandledScreen.drawEntityGeneric(entities[i], 160, w - x, y - (float)Math.atan(this.entityY) * 100, flip, r, (float)Math.atan(this.entityX) * z);
            }
        }
    }
}
