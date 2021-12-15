package xienaoban.minecraft.bole.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import xienaoban.minecraft.bole.client.EntityManager;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public final class BoleHandbookScreen extends AbstractBoleScreen<Entity, BoleHandbookScreenHandler> {
    public BoleHandbookScreen(BoleHandbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        initCatalog();
    }

    private void initCatalog() {
        EntityManager.getInstance().getTagGroup(Keys.TAG_GROUP_CLASS).dfsTags((root, depth) -> {
            int index = 0;
            while (!this.pages.get(index).addSlot(new CatalogItemPropertyWidget(depth, root.getName().substring(root.getName().lastIndexOf('.') + 1)))) {
                ++index;
                if (this.pages.size() == index) {
                    this.pages.add(new Page());
                }
            }
            return true;
        });
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

    public class CatalogItemPropertyWidget extends AbstractPropertyWidget {
        private final int sub;
        private final Text name;

        public CatalogItemPropertyWidget(int sub, String translationKey) {
            this(sub, new TranslatableText(translationKey));
        }

        public CatalogItemPropertyWidget(int sub, Text name) {
            super(4, 1);
            this.sub = sub;
            this.name = name;
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawText(matrices, this.name, DARK_TEXT_COLOR, 4.0F / (sub + 6), x + sub * 2 + 10, y + 3.25F);
        }

        @Override
        public void drawHovered(MatrixStack matrices, int mouseX, int mouseY) {
            drawText(matrices, this.name, 0xff000000, 4.0F / (sub + 6), this.box.left() + sub * 2 + 10, this.box.top() + 3.25F);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
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
            drawEntity();
            drawTextCenteredX(matrices, this.entityName, DARK_TEXT_COLOR, 0.5F, this.box.left() + (this.box.width() >> 1), this.box.bottom() - (Page.PROPERTY_WIDGET_HEIGHT >> 1));
        }

        /**
         * @see net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity
         */
        @SuppressWarnings("deprecation")
        private void drawEntity() {
            float size = entitySize;
            int t = (int) (System.currentTimeMillis() % 8000);
            t = t > 4000 ? 6000 - t : t - 2000;
            float f = (float) Math.atan(t / 420.0F) * 6F;
            float g = -45;
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(this.box.left() + (this.box.width() >> 1), this.box.bottom() - Page.PROPERTY_WIDGET_MARGIN_HEIGHT - size * (float) this.entity.getBoundingBox().getXLength(), 1050.0F);
            matrixStack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            MatrixStack matrixStack2 = new MatrixStack();
            matrixStack2.translate(0.0, 0.0, 1000.0);
            matrixStack2.scale(size, size, size);
            Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g);
            quaternion.hamiltonProduct(quaternion2);
            matrixStack2.multiply(quaternion);
            entity.bodyYaw = 180.0F + f * 2;
            entity.setYaw(180.0F - f);
            entity.setPitch(g);
            entity.headYaw = entity.getYaw();
            entity.prevHeadYaw = entity.getYaw();
            DiffuseLighting.method_34742();
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            quaternion2.conjugate();
            entityRenderDispatcher.setRotation(quaternion2);
            entityRenderDispatcher.setRenderShadows(false);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrixStack2, immediate, 0xF000F0));
            immediate.draw();
            entityRenderDispatcher.setRenderShadows(true);
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
            DiffuseLighting.enableGuiDepthLighting();
        }
    }
}
