package xienaoban.minecraft.bole.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public final class BoleHandbookScreen extends AbstractBoleScreen<Entity, BoleHandbookScreenHandler> {
    public BoleHandbookScreen(BoleHandbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        this.pages.get(0).addSlotLazy(new LivingEntityPropertyWidget(EntityType.HORSE))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.SHEEP))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.COW))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.TURTLE))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.ZOMBIE))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.VILLAGER))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.RABBIT))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.ENDERMAN))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.POLAR_BEAR))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.IRON_GOLEM))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.GHAST))
                .addSlotLazy(new LivingEntityPropertyWidget(EntityType.PARROT));
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    public class LivingEntityPropertyWidget extends AbstractPropertyWidget {
        private final LivingEntity entity;
        private final Text entityName;
        private final float entitySize;

        public LivingEntityPropertyWidget(EntityType<?> entityType) {
            super(1, 3);
            this.entity = (LivingEntity) entityType.create(MinecraftClient.getInstance().world);
            this.entityName = entityType.getName();
            this.entitySize = calEntitySize();
        }

        private float calEntitySize() {
            Box bound = this.entity.getBoundingBox();
            float maxH = (float) ((bound.getZLength() + bound.getYLength())) / 1.414F;
            float maxW = (float) ((bound.getXLength() + bound.getZLength())) / 1.414F;
            float scale = Math.max(maxH / (this.box.height() - 4) * this.box.width(), maxW);
            return 16.0F / scale * ( 0.7F + 0.5F / (1 + (float)Math.pow(2.71828, -(scale - 1) * 3)));
        }

        @Override
        protected void initTooltipLines() {

        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawEntity(matrices);
            drawTextCenteredX(matrices, this.entityName, DARK_TEXT_COLOR, 0.5F, this.box.left() + (this.box.width() >> 1), this.box.bottom() - (Page.PROPERTY_WIDGET_HEIGHT >> 1));
        }

        private void drawEntity(MatrixStack matrices) {
            float size = entitySize;
            int t = ((int) System.currentTimeMillis()) % 8000;
            t = t > 4000 ? 6000 - t : t - 2000;
            float f = (float)Math.atan(t / 420.0F) * 6F;
            float g = -45;
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.box.left() + (this.box.width() >> 1), this.box.bottom() - Page.PROPERTY_WIDGET_MARGIN_HEIGHT - size * (float) this.entity.getBoundingBox().getXLength(), 1050.0F);
            RenderSystem.scalef(1.0F, 1.0F, -1.0F);
            RenderSystem.disableLighting();
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.translate(0.0D, 0.0D, 1000.0D);
            matrixStack.scale(size, size, size);
            Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g);
            quaternion.hamiltonProduct(quaternion2);
            matrixStack.multiply(quaternion);
            this.entity.bodyYaw = 180.0F + f * 2;
            this.entity.yaw = 180.0F - f;
            this.entity.pitch = g;
            this.entity.headYaw = this.entity.yaw;
            this.entity.prevHeadYaw = this.entity.yaw;
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            quaternion2.conjugate();
            entityRenderDispatcher.setRotation(quaternion2);
            entityRenderDispatcher.setRenderShadows(false);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            RenderSystem.runAsFancy(() -> {
                entityRenderDispatcher.render(this.entity, 0.0D, 0.0D, getZOffset(), 0.0F, 1.0F, matrixStack, immediate, 15728880);
            });
            immediate.draw();
            entityRenderDispatcher.setRenderShadows(true);
            RenderSystem.popMatrix();

        }
    }
}
