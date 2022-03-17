package xienaoban.minecraft.bole.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.screen.GenericHandledScreen;
import xienaoban.minecraft.bole.mixin.IMixinBlockItem;
import xienaoban.minecraft.bole.util.Keys;

import java.util.ArrayList;
import java.util.Optional;

public class EventsManager {
    public static void init() {
        initTameableShoulderEntityInGameHud();
        initBeehiveTooltip();
    }

    private static void initTameableShoulderEntityInGameHud() {
        HudRenderCallback.EVENT.register(new HudRenderCallback() {
            private final NbtCompound[] oldNbts = new NbtCompound[2];
            private final LivingEntity[] entities = new LivingEntity[2];
            @Override
            public void onHudRender(MatrixStack matrixStack, float tickDelta) {
                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;
                if (player == null || player != client.cameraEntity) return;
                if (!client.options.getPerspective().isFirstPerson()) return;
                // todo render layer
                for (int i = 0; i < 2; ++i) {
                    NbtCompound entityNbt = i == 0 ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
                    if (this.oldNbts[i] == entityNbt) continue;
                    this.oldNbts[i] = entityNbt;
                    if (entityNbt.isEmpty()) {
                        this.entities[i] = null;
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
                        this.entities[i] = entity;
                    }
                    else this.entities[i] = null;
                }
                int w = client.getWindow().getScaledWidth() >> 1, h = client.getWindow().getScaledHeight() >> 1;
                float r = -16, flip = 0;
                float x, y;
                switch (Configs.getInstance().getShoulderCreatureHudPosition()) {
                    case TOP: x = w >> 1; y = -90; flip = 180; break;
                    case BOTTOM: x = w * 0.66F; y = (h << 1) + 90; break;
                    case SIDES: x = w + 15; y = h + 80; break;
                    default: return;
                }
                for (int i = 0; i < 2; ++i) {
                    if (this.entities[i] == null) continue;
                    GenericHandledScreen.drawEntityGeneric(this.entities[i], 160, w - x, y, flip, r, 0);
                    x = -x; r = -r;
                }
            }
        });
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
                    beeLines.add(new TranslatableText(Keys.TEXT_HONEY_LEVEL, honeyLevel + "/" + BeehiveBlock.FULL_HONEY_LEVEL).formatted(Formatting.YELLOW));
                }
            }
            if (nbt.contains(IMixinBlockItem.getBlockEntityTagKey(), NbtElement.COMPOUND_TYPE)) {
                NbtCompound entityNbt = nbt.getCompound(IMixinBlockItem.getBlockEntityTagKey());
                if (entityNbt.contains(BeehiveBlockEntity.BEES_KEY, NbtElement.LIST_TYPE)) {
                    NbtList bees = entityNbt.getList(BeehiveBlockEntity.BEES_KEY, NbtElement.COMPOUND_TYPE);
                    int beeCnt = bees.size();
                    beeLines.add(new TranslatableText(Keys.TEXT_BEE_COUNT, beeCnt + "/" + BeehiveBlockEntity.MAX_BEE_COUNT).formatted(Formatting.GOLD));
                    if (beeCnt > 0) {
                        beeLines.add(LiteralText.EMPTY);
                        beeLines.add(new TranslatableText(Keys.TEXT_BEE_INFO).formatted(Formatting.GRAY));
                    }
                    for (int i = 0; i < beeCnt; ++i) {
                        NbtCompound beeNbt = bees.getCompound(i).getCompound(BeehiveBlockEntity.ENTITY_DATA_KEY);
                        BeeEntity bee = EntityType.BEE.create(MinecraftClient.getInstance().world);
                        if (bee == null) {
                            beeLines.add(new LiteralText("[ERROR]").formatted(Formatting.RED));
                            continue;
                        }
                        bee.readNbt(beeNbt);
                        beeLines.add(bee.getName().copy().append(new TranslatableText(Keys.TEXT_COLON)).append(new TranslatableText(bee.isBaby() ? Keys.TEXT_MINOR : Keys.TEXT_ADULT)).formatted(Formatting.BLUE));
                    }
                }
            }
            lines.addAll(1, beeLines);
        });
    }
}
