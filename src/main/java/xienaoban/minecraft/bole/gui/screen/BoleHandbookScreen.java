package xienaoban.minecraft.bole.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.entity.EntityLookup;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.client.EntityManager;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public final class BoleHandbookScreen extends AbstractBoleScreen<Entity, BoleHandbookScreenHandler> {
    public BoleHandbookScreen(BoleHandbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        initCatalog(EntityManager.getInstance().getTagGroups().get(0));
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void initButtons() {
        super.initButtons();
        int cnt = 0;
        for (EntityManager.TagGroup tags : EntityManager.getInstance().getTagGroups()) {
            addDrawableChild(new TagGroupButtonWidget(this.contentLeft[0] - 30 - 10 + (cnt % 3), this.contentTop - 5 + cnt * 14, cnt, tags.getText(), (button -> initCatalog(tags))));
            ++cnt;
        }
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    private void initCatalog(EntityManager.TagGroup group) {
        resetPages();
        this.pages.get(0).addSlot(new LeftTextPropertyWidget(4, 1, new TranslatableText(group.getName() + ".description"), DARK_TEXT_COLOR, 0.5F));
        group.dfsTags((root, depth) -> {
            int index = 0;
            while (!this.pages.get(index).addSlot(new TagItemPropertyWidget(depth, root))) {
                ++index;
                if (this.pages.size() == index) {
                    this.pages.add(new Page());
                }
            }
            return true;
        });
        setPageIndex(0);
    }

    public class TagItemPropertyWidget extends AbstractPropertyWidget {
        private static final int TAB = 5;
        private final int sub;
        private final EntityManager.Tag tag;
        private final Text text;

        public TagItemPropertyWidget(int sub, EntityManager.Tag tag) {
            super(4, 1);
            this.sub = sub;
            this.tag = tag;
            this.text = new TranslatableText(tag.getName()).append(" (" + tag.getEntities().size() + ")");
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 40, 10, getZOffset(), x + this.sub * TAB, y, 0, 240);
            drawName(matrices, 0xb0222222);
        }

        @Override
        public void drawHovered(MatrixStack matrices, int mouseX, int mouseY) {
            drawName(matrices, 0xff000000);
        }

        private void drawName(MatrixStack matrices, int color) {
            if (this.sub < 0) { // impossible (sub always >= 0)
                drawText(matrices, this.text, color, 1.0F, this.box.left() + 12 - this.sub * TAB, this.box.top() + 1);
            }
            else {
                drawText(matrices, this.text, color, 0.5F, this.box.left() + 12 + this.sub * TAB, this.box.top() + 3.25F);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            resetPages();
            int index = 0;
            for (EntityManager.EntityInfo entityInfo : this.tag.getEntities()) {
                while (!pages.get(index).addSlot(new LivingEntityPropertyWidget(entityInfo.getType()))) {
                    ++index;
                    if (pages.size() == index) {
                        pages.add(new Page());
                    }
                }
            }
            setPageIndex(0);
            return true;
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

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            EntityType<?> entityType = this.entity.getType();
            HighlightManager hl = BoleClient.getInstance().getHighlightManager();
            ClientEntityManager<Entity> entityManager = MiscUtil.getFieldValue(MinecraftClient.getInstance().world, ClientWorld.class, "entityManager");
            EntityLookup<Entity> lookup = entityManager.getLookup();
            AtomicInteger cnt = new AtomicInteger();
            lookup.forEach(entityType, entity -> {
                if (entity.distanceTo(handler.player) < 66 * 66) {
                    hl.highlight(entity, 8 * 20);
                    cnt.incrementAndGet();
                }
            });
            handler.player.sendMessage(new LiteralText(String.valueOf(cnt.get())).append(new TranslatableText(entityType.getTranslationKey())).append(new TranslatableText(Keys.TEXT_HIGHLIGHT)).formatted(Formatting.GOLD), false);
            onClose();
            return true;
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
