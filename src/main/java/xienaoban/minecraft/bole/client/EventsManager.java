package xienaoban.minecraft.bole.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
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
import net.minecraft.util.math.Vec3f;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.screen.GenericHandledScreen;
import xienaoban.minecraft.bole.mixin.IMixinBlockItem;
import xienaoban.minecraft.bole.util.Keys;

import java.util.ArrayList;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class EventsManager {
    public static LeashFallFromPlayerEvent LEASH_FALL_FROM_PLAYER_EVENT = new LeashFallFromPlayerEvent();
    public static ShoulderEntityFirstPersonRenderer SHOULDER_ENTITY_FIRST_PERSON_RENDERER = new ShoulderEntityFirstPersonRenderer();

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
        private static final float HEAD_YAW_SPEED = 0.1F;

        private final NbtCompound[] oldNbts = new NbtCompound[2];
        private final LivingEntity[] entities = new LivingEntity[2];
        private final float[] nextHeadYaw = new float[2];
        private final long[] nextHeadYawTime = new long[2];
        private long lastTime;

        public void renderShoulderEntity(EntityRenderDispatcher entityRenderDispatcher, MatrixStack matrices, VertexConsumerProvider vertexConsumers, ClientPlayerEntity player, int light) {
            float rx, ry, rz;
            double tx, ty, tz;
            switch (Configs.getInstance().getShoulderCreatureHudPosition()) {
                case TOP:
                    rx = 60; ry = 0; rz = 180;
                    tx = 0.5; ty = 0.0; tz = -1.7;
                    break;
                case BOTTOM:
                    rx = 0; ry = 180; rz = 0;
                    tx = 0.5; ty = -1.4; tz = 1.3;
                    break;
                case SIDES:
                    rx = -10; ry = 180; rz = 0;
                    tx = 2.0; ty = -0.2; tz = 1.2;
                    break;
                default: return;
            }
            long diffTime = System.currentTimeMillis() - this.lastTime;
            this.lastTime += diffTime;
            NbtCompound[] newNbts = {player.getShoulderEntityLeft(), player.getShoulderEntityRight()};
            for (int i = 0; i < 2; ++i) {
                if (newNbts[i].isEmpty()) {
                    continue;
                }
                if (this.oldNbts[i] != newNbts[i]) {
                    this.oldNbts[i] = newNbts[i];
                    Optional<Entity> optionalEntity = EntityType.getEntityFromNbt(newNbts[i], player.world);
                    if (optionalEntity.isEmpty()) {
                        continue;
                    }
                    LivingEntity entity = (LivingEntity) optionalEntity.get();
                    entity.setYaw(0);
                    entity.setBodyYaw(0);
                    entity.setHeadYaw(0);
                    entity.prevHeadYaw = 0;
                    entity.setPitch(0);
                    this.entities[i] = entity;
                }
                long time = System.currentTimeMillis();
                LivingEntity entity = this.entities[i];
                if (lastTime > this.nextHeadYawTime[i]) {
                    this.nextHeadYawTime[i] = lastTime + 2000 + (long)(Math.random() * 8000);
                    this.nextHeadYaw[i] = (float) (Math.random() * 60 - 30);
                }
                entity.setHeadYaw(entity.getHeadYaw() + HEAD_YAW_SPEED * (this.nextHeadYaw[i] - entity.getHeadYaw()));
                int pos = i * -2 + 1;
                matrices.push();
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rx));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(ry));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rz));
                matrices.translate(pos * tx, ty, tz);
                entityRenderDispatcher.setRenderShadows(false);
                entityRenderDispatcher.render(entity, 0, 0, 0, 0, 1.0F, matrices, vertexConsumers, light);
                entityRenderDispatcher.setRenderShadows(true);
                matrices.pop();
            }
        }

        public void clearValues() {
            this.oldNbts[0] = this.oldNbts[1] = null;
            this.entities[0] = this.entities[1] = null;
        }
    }
}
