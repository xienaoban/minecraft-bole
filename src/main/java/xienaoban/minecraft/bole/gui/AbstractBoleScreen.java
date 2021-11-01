package xienaoban.minecraft.bole.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.client.KeyBindingManager;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoleScreen<E extends Entity, H extends AbstractBoleScreenHandler<E>> extends HandledScreen<H> {
    private static final int BOOK_TEXTURE_CUT = 29;

    public static final int BODY_WIDTH = (192 - BOOK_TEXTURE_CUT) * 2;
    public static final int BODY_HEIGHT = 192;
    public static final int CONTENT_WIDTH = 110;
    public static final int CONTENT_HEIGHT = 130;
    public static final int CONTENT_SPACING_WIDTH = 20;

    public static final int CONTENT_TEXT_COLOR = 0xd0121212;

    protected int bodyLeft, bodyRight, bodyTop, bodyBottom;
    protected int[] contentLeft, contentRight;
    protected int contentTop, contentBottom;

    protected final List<ContentWidgets> pages;
    protected ContentWidgets curLeftPage, curRightPage;

    protected boolean debugMode;

    public AbstractBoleScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.debugMode = false;
        this.contentLeft = new int[2];
        this.contentRight = new int[2];
        this.curLeftPage = new ContentWidgets();
        this.curRightPage = new ContentWidgets();
        this.pages = new ArrayList<>();
        this.pages.add(this.curLeftPage);
        this.pages.add(this.curRightPage);
        initCustom();
    }

    protected abstract void initCustom();

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindingManager.KEY_BOLE_SCREEN.matchesKey(keyCode, scanCode)) {
            onClose();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            this.debugMode = !this.debugMode;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();
        this.bodyLeft = (this.width - BODY_WIDTH) / 2;
        this.bodyRight = this.bodyLeft + BODY_WIDTH;
        this.bodyTop = this.height / 2 - BODY_HEIGHT / 2 + 10;
        this.bodyBottom = this.bodyTop + BODY_HEIGHT;
        this.contentLeft[0] = (this.width - CONTENT_SPACING_WIDTH) / 2 - CONTENT_WIDTH;
        this.contentRight[0] = this.contentLeft[0] + CONTENT_WIDTH;
        this.contentLeft[1] = (this.width + CONTENT_SPACING_WIDTH) / 2;
        this.contentRight[1] = this.contentLeft[1] + CONTENT_WIDTH;
        this.contentTop = this.bodyTop + 25;
        this.contentBottom = this.contentTop + CONTENT_HEIGHT;
    }

    @Override
    public void onClose() {
        this.handler.resetClientEntityServerProperties();
        super.onClose();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        setTexture(Textures.BOOK);
        int x0 = this.bodyLeft, x1 = this.bodyRight, y0 = this.bodyTop, y1 = this.bodyBottom;
        int u0 = BOOK_TEXTURE_CUT, u1 = u0 + BODY_WIDTH / 2, v0 = 0, v1 = v0 + BODY_HEIGHT;
        drawTextureFlippedHorizontally(matrices, 256, 256, this.getZOffset(),
                x0, y0, this.width >> 1, y1, u0, v0, u1, v1);
        drawTextureNormally(matrices, 256, 256, this.getZOffset(),
                this.width >> 1, y0, x1, y1, u0, v0, u1, v1);
        if (this.debugMode) {
            final float r = 0.3F;
            final int c = 0x66dd001b;
            drawRectangle(matrices, c, r, getZOffset(), this.contentLeft[0], this.contentTop, this.contentRight[0], this.contentBottom);
            drawRectangle(matrices, c, r, getZOffset(), this.contentLeft[1], this.contentTop, this.contentRight[1], this.contentBottom);
            drawText(matrices, String.valueOf(BoleClient.getInstance().getTicks()), 0xbbffffff, 0.5F, 1, this.height - 5);
        }
        drawText(matrices, this.title, 0x99888888, this.contentLeft[0] + 0.7F, this.contentTop - 12 + 0.7F);
        drawText(matrices, this.title, 0xff444444, this.contentLeft[0], this.contentTop - 12);
        RenderSystem.translatef(this.contentLeft[0], this.contentTop, 0.0F);
        drawLeftContent(matrices, delta, mouseX, mouseY);
        RenderSystem.translatef(this.contentLeft[1] - this.contentLeft[0], 0, 0.0F);
        drawRightContent(matrices, delta, mouseX, mouseY);
        RenderSystem.translatef(-this.contentLeft[1], -this.contentTop, 0.0F);
    }

    /**
     * Draw the content of the left page.
     */
    protected abstract void drawLeftContent(MatrixStack matrices, float delta, int mouseX, int mouseY);

    /**
     * Draw the content of the right page.
     */
    protected abstract void drawRightContent(MatrixStack matrices, float delta, int mouseX, int mouseY);

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    public static void drawEntityAuto(Entity entity, int x0, int y0, int x1, int y1, float mouseX, float mouseY) {
        Box box = entity.getVisibilityBoundingBox();
        double ew = box.getXLength(), eh = box.getYLength();
        if (ew > eh) {
            ew = Math.max(ew, 1);
        }
        else {
            eh = Math.max(eh, 1);
        }
        int rw = x1 - x0, rh = y1 - y0;
        int size = (int) (Math.min(rw / ew, rh / eh) * 0.85);
        drawEntity(entity, size, x0 + x1 >> 1, y1, mouseX, mouseY);
    }

    public static void drawEntityByWidth(Entity entity, int width, int x, int y, float mouseX, float mouseY) {
        Box box = entity.getVisibilityBoundingBox();
        drawEntity(entity, (int) (width / box.getXLength()), x, y, mouseX, mouseY);
    }

    public static void drawEntityByHeight(Entity entity, int height, int x, int y, float mouseX, float mouseY) {
        Box box = entity.getVisibilityBoundingBox();
        drawEntity(entity, (int) (height / box.getYLength()), x, y, mouseX, mouseY);
    }

    /**
     * Draw an entity.
     * This method is similar to InventoryScreen.drawEntity(), but there are some differences:
     * <p>1. It can draw any Entity, not only LivingEntity. </p>
     * <p>2. The entity it renders is brighter (but it brings some bugs...). </p>
     * <p>3. It can't recognize the yaw of LivingEntity, so don’t use it to render a rotating LivingEntity. </p>
     * @see net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity
     */
    public static void drawEntity(Entity entity, int size, int x, int y, float mouseX, float mouseY) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        float fSize = -size;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x, (float)y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale(fSize, fSize, fSize);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(0.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        Quaternion quaternion3 = Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getYaw(0.0F) - f * 40.0F);
        quaternion.hamiltonProduct(quaternion2);
        quaternion.hamiltonProduct(quaternion3);
        matrixStack.multiply(quaternion);
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion3.conjugate();
        entityRenderDispatcher.setRotation(quaternion3);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 180.0F, 1.0F, matrixStack, immediate, 0x00F000F0));   // 0x00F000F0
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        RenderSystem.popMatrix();
    }

    public static void drawRectangle(MatrixStack matrices, int color, float z, float x0, float y0, float x1, float y1) {
        drawQuadrilateral(matrices, color, z, x0, y0, x1, y0, x0, y1, x1, y1);
    }

    public static void drawRectangle(MatrixStack matrices, int color, float radius, float z, float x0, float y0, float x1, float y1) {
        drawHorizontalLine(matrices, color, radius, z, x0, x1, y0);
        drawHorizontalLine(matrices, color, radius, z, x0, x1, y1);
        drawVerticalLine(matrices, color, radius, z, x0, y0, y1);
        drawVerticalLine(matrices, color, radius, z, x1, y0, y1);
    }

    public static void drawHorizontalLine(MatrixStack matrices, int color, float radius, float z, float x0, float x1, float y) {
        drawQuadrilateral(matrices, color, z, x0, y - radius, x1, y - radius, x0, y + radius, x1, y + radius);
    }

    public static void drawVerticalLine(MatrixStack matrices, int color, float radius, float z, float x, float y0, float y1) {
        drawQuadrilateral(matrices, color, z, x - radius, y0, x + radius, y0, x - radius, y1, x + radius, y1);
    }

    public static void drawQuadrilateral(MatrixStack matrices, int color, float z,
                                          float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        Matrix4f model = matrices.peek().getModel();
        int a = color >> 24 & 255;
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);     // mode 7 means "Quadrilateral"
        bufferBuilder.vertex(model, x0, y0, z).color(r, g, b, a).next();    // The four vertices must
        bufferBuilder.vertex(model, x2, y2, z).color(r, g, b, a).next();    // be added in clockwise
        bufferBuilder.vertex(model, x3, y3, z).color(r, g, b, a).next();    // or counterclockwise
        bufferBuilder.vertex(model, x1, y1, z).color(r, g, b, a).next();    // order.
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Draws a textured quadrilateral from a region in a texture.
     *
     * @param matrices the matrix stack used for rendering
     * @param tw the width of the entire texture
     * @param th the height of the entire texture
     * @param w the width of the quadrilateral and the texture region
     * @param h the height of the quadrilateral and the texture region
     * @param z the Z coordinate of the quadrilateral
     * @param x the left-most coordinate of the quadrilateral
     * @param y the top-most coordinate of the quadrilateral
     * @param u the left-most coordinate of the texture region
     * @param v the top-most coordinate of the texture region
     */
    public static void drawTextureNormally(MatrixStack matrices, float tw, float th, float w, float h, float z, float x, float y, float u, float v) {
        drawTextureQuadrilateral(matrices, tw, th, z, x, y, x + w, y, x, y + h, x + w, y + h, u, v, u + w, v, u, v + h, u + w, v + h);
    }

    /**
     * Draws a textured quadrilateral from a region in a texture.
     *
     * @param matrices the matrix stack used for rendering
     * @param tw the width of the entire texture
     * @param th the height of the entire texture
     * @param z the Z coordinate of the quadrilateral
     * @param x0 the left-most coordinate of the quadrilateral
     * @param y0 the top-most coordinate of the quadrilateral
     * @param x1 the right-most coordinate of the quadrilateral
     * @param y1 the bottom-most coordinate of the quadrilateral
     * @param u0 the left-most coordinate of the texture region
     * @param v0 the top-most coordinate of the texture region
     * @param u1 the right-most coordinate of the texture region
     * @param v1 the bottom-most coordinate of the texture region
     */
    public static void drawTextureNormally(MatrixStack matrices, float tw, float th, float z,
                                    float x0, float y0, float x1, float y1,
                                    float u0, float v0, float u1, float v1) {
        drawTextureQuadrilateral(matrices, tw, th, z, x0, y0, x1, y0, x0, y1, x1, y1, u0, v0, u1, v0, u0, v1, u1, v1);
    }

    /**
     * Draws a textured quadrilateral from a region in a horizontally flipped texture.
     *
     * @param matrices the matrix stack used for rendering
     * @param tw the width of the entire texture
     * @param th the height of the entire texture
     * @param z the Z coordinate of the quadrilateral
     * @param x0 the left-most coordinate of the quadrilateral
     * @param y0 the top-most coordinate of the quadrilateral
     * @param x1 the right-most coordinate of the quadrilateral
     * @param y1 the bottom-most coordinate of the quadrilateral
     * @param u0 the left-most coordinate of the texture region
     * @param v0 the top-most coordinate of the texture region
     * @param u1 the right-most coordinate of the texture region
     * @param v1 the bottom-most coordinate of the texture region
     */
    public static void drawTextureFlippedHorizontally(MatrixStack matrices, float tw, float th, float z,
                                                      float x0, float y0, float x1, float y1,
                                                      float u0, float v0, float u1, float v1) {
        drawTextureQuadrilateral(matrices, tw, th, z, x0, y0, x1, y0, x0, y1, x1, y1, u1, v0, u0, v0, u1, v1, u0, v1);
    }

    /**
     * Draws a textured quadrilateral from a region in a vertically flipped texture.
     *
     * @param matrices the matrix stack used for rendering
     * @param tw the width of the entire texture
     * @param th the height of the entire texture
     * @param z the Z coordinate of the quadrilateral
     * @param x0 the left-most coordinate of the quadrilateral
     * @param y0 the top-most coordinate of the quadrilateral
     * @param x1 the right-most coordinate of the quadrilateral
     * @param y1 the bottom-most coordinate of the quadrilateral
     * @param u0 the left-most coordinate of the texture region
     * @param v0 the top-most coordinate of the texture region
     * @param u1 the right-most coordinate of the texture region
     * @param v1 the bottom-most coordinate of the texture region
     */
    public static void drawTextureFlippedVertically(MatrixStack matrices, float tw, float th, float z,
                                                    float x0, float y0, float x1, float y1,
                                                    float u0, float v0, float u1, float v1) {
        drawTextureQuadrilateral(matrices, tw, th, z, x0, y0, x1, y0, x0, y1, x1, y1, u0, v1, u1, v1, u0, v0, u1, v0);
    }

    /**
     * Draws a textured quadrilateral from a region in a 180 rotated texture.
     *
     * @param matrices the matrix stack used for rendering
     * @param tw the width of the entire texture
     * @param th the height of the entire texture
     * @param z the Z coordinate of the quadrilateral
     * @param x0 the left-most coordinate of the quadrilateral
     * @param y0 the top-most coordinate of the quadrilateral
     * @param x1 the right-most coordinate of the quadrilateral
     * @param y1 the bottom-most coordinate of the quadrilateral
     * @param u0 the left-most coordinate of the texture region
     * @param v0 the top-most coordinate of the texture region
     * @param u1 the right-most coordinate of the texture region
     * @param v1 the bottom-most coordinate of the texture region
     */
    public static void drawTextureRotated180(MatrixStack matrices, float tw, float th, float z,
                                             float x0, float y0, float x1, float y1,
                                             float u0, float v0, float u1, float v1) {
        drawTextureQuadrilateral(matrices, tw, th, z, x0, y0, x1, y0, x0, y1, x1, y1, u1, v1, u0, v1, u1, v0, u0, v0);
    }

    /**
     * @see net.minecraft.client.gui.DrawableHelper#drawTexturedQuad
     */
    public static void drawTextureQuadrilateral(MatrixStack matrices, float textureWidth, float textureHeight, float z,
                                         float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3,
                                         float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3) {
        u0 /= textureWidth; v0 /= textureHeight;
        u1 /= textureWidth; v1 /= textureHeight;
        u2 /= textureWidth; v2 /= textureHeight;
        u3 /= textureWidth; v3 /= textureHeight;
        Matrix4f model = matrices.peek().getModel();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);     // mode 7 means "Quadrilateral"
        bufferBuilder.vertex(model, x0, y0, z).texture(u0, v0).next();      // The four vertices must
        bufferBuilder.vertex(model, x2, y2, z).texture(u2, v2).next();      // be added in clockwise
        bufferBuilder.vertex(model, x3, y3, z).texture(u3, v3).next();      // or counterclockwise
        bufferBuilder.vertex(model, x1, y1, z).texture(u1, v1).next();      // order.
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    public void drawText(MatrixStack matrices, String text, int color, float x, float y) {
        this.textRenderer.draw(matrices, text, x, y, color);
    }

    public void drawText(MatrixStack matrices, Text text, int color, float x, float y) {
        this.textRenderer.draw(matrices, text, x, y, color);
    }

    public void drawText(MatrixStack matrices, String text, int color, float size, float x, float y) {
        RenderSystem.pushMatrix();
        RenderSystem.scalef(size, size, size);
        this.textRenderer.draw(matrices, text, x / size, y / size, color);
        RenderSystem.popMatrix();
    }

    public void drawText(MatrixStack matrices, Text text, int color, float size, float x, float y) {
        RenderSystem.pushMatrix();
        RenderSystem.scalef(size, size, size);
        this.textRenderer.draw(matrices, text, x / size, y / size, color);
        RenderSystem.popMatrix();
    }

    public void setTexture(Identifier id) {
        assert this.client != null;
        this.client.getTextureManager().bindTexture(id);
    }

    /**
     * Manages all widgets on a page.
     */
    public class ContentWidgets {
        public static final int CONTENT_WIDGET_MARGIN_WIDTH = 4;
        public static final int CONTENT_WIDGET_MARGIN_HEIGHT = 3;
        public static final int CONTENT_WIDGET_WIDTH = (CONTENT_WIDTH - CONTENT_WIDGET_MARGIN_WIDTH >> 1);
        public static final int CONTENT_WIDGET_HEIGHT = 10;
        private static final int ROWS = CONTENT_HEIGHT / (CONTENT_WIDGET_HEIGHT + CONTENT_WIDGET_MARGIN_HEIGHT);
        private static final int COLS = 2;
        private final List<List<AbstractContentWidget>> widgets;

        public ContentWidgets() {
            List<List<AbstractContentWidget>> l1 = new ArrayList<>();
            for (int i = ROWS; i > 0; --i) {
                List<AbstractContentWidget> l2 = new ArrayList<>();
                for (int j = 0; j < COLS; ++j) {
                    l2.add(null);
                }
                l1.add(l2);
            }
            this.widgets = l1;
        }

        public void draw(MatrixStack matrices, int mouseX, int mouseY) {
            for (int i = 0; i < ROWS; ++i) {
                for (int j = 0; j < COLS; ++j) {
                    AbstractContentWidget w = this.widgets.get(i).get(j);
                    if (w != null) {
                        w.draw(matrices,
                                j * (CONTENT_WIDGET_WIDTH + CONTENT_WIDGET_MARGIN_WIDTH),
                                i * (CONTENT_WIDGET_HEIGHT + CONTENT_WIDGET_MARGIN_HEIGHT),
                                mouseX, mouseY);
                    }
                }
            }
        }

        public boolean setSlot(AbstractContentWidget widget, int row, int col) {
            if (row + widget.getRowSlots() > ROWS || col + widget.getColSlots() > COLS) {
                Bole.LOGGER.error("Widget cannot be set here! " + widget.getRowSlots() + ", " + widget.getColSlots() + ", " + row + ", " + col);
                return false;
            }
            EmptyContentWidget empty = new EmptyContentWidget(1, 1, widget);
            for (int i = 0; i < widget.getRowSlots(); ++i) {
                for (int j = 0; j < widget.getColSlots(); ++j) {
                    this.widgets.get(row + i).set(col + j, empty);
                }
            }
            this.widgets.get(row).set(col, widget);
            return true;
        }

        public boolean addSlot(AbstractContentWidget widget) {
            for (int i = 0; i < ROWS; ++i) {
                if (i + widget.getRowSlots() > ROWS) break;
                BAD:
                for (int j = 0; j < COLS; ++j) {
                    if (j + widget.getColSlots() > COLS) break;
                    for (int p = 0; p < widget.getRowSlots(); ++p) {
                        for (int q = 0; q < widget.getColSlots(); ++q) {
                            if (this.widgets.get(i + p).get(j + q) != null) {
                                continue BAD;
                            }
                        }
                    }
                    return setSlot(widget, i, j);
                }
            }
            Bole.LOGGER.error("Widget cannot be added here!");
            return false;
        }
    }

    public abstract class AbstractContentWidget {
        protected final int rowSlots, colSlots;
        protected final int widgetWidth, widgetHeight;

        public AbstractContentWidget(int rowSlots, int colSlots) {
            if (colSlots != 1 && colSlots != 2) {
                throw new RuntimeException("`colSlots` should be 1 or 2.");
            }
            this.rowSlots = rowSlots;
            this.colSlots = colSlots;
            this.widgetWidth = colSlots == 1 ? ContentWidgets.CONTENT_WIDGET_WIDTH : CONTENT_WIDTH;
            this.widgetHeight = rowSlots * (ContentWidgets.CONTENT_WIDGET_HEIGHT + ContentWidgets.CONTENT_WIDGET_MARGIN_HEIGHT) - ContentWidgets.CONTENT_WIDGET_MARGIN_HEIGHT;
        }

        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawContent(matrices, x, y, mouseX, mouseY);
            if (debugMode) {
                drawRectangle(matrices, 0x66c55c2d, 0.3F, 0, x, y, x + widgetWidth, y + widgetHeight);
            }
        }

        protected abstract void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY);

        public final int getRowSlots() {
            return this.rowSlots;
        }

        public final int getColSlots() {
            return this.colSlots;
        }
    }

    public class EmptyContentWidget extends AbstractContentWidget {
        private final AbstractContentWidget father;

        public EmptyContentWidget(int rowSlots, int colSlots) {
            this(rowSlots, colSlots, null);
        }

        public EmptyContentWidget(int widthSlots, int heightSlots, AbstractContentWidget father) {
            super(widthSlots, heightSlots);
            this.father = father;
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (debugMode && this.father == null) {
                drawRectangle(matrices, 0x66c55c2d, 0.3F, getZOffset(), x, y, x + widgetWidth, y + widgetHeight);
            }
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {}

        public AbstractContentWidget getFather() {
            return this.father;
        }
    }

    public class DisplayedEntityContentWidget extends AbstractContentWidget {
        private Entity displayedEntity, targetEntity;

        public DisplayedEntityContentWidget(int rowSlots, int colSlots, Entity targetEntity) {
            super(rowSlots, colSlots);
            setTargetEntity(targetEntity);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawEntityAuto(this.displayedEntity, x + 2, y, x + this.widgetWidth - 2, y + this.widgetHeight - 4,
                    (mouseX) / 33.0F + 0.0001F, (mouseY) / 53.0F + 5.0F);
        }

        public void updateDisplayedEntity() {
            NbtCompound nbt = this.targetEntity.writeNbt(new NbtCompound());
            nbt.remove("Dimension");
            nbt.remove("Rotation");
            nbt.remove("CustomName");
            nbt.remove("CustomNameVisible");
            nbt.remove("AngryAt");
            try {
                this.displayedEntity.readNbt(nbt);
            }
            catch (Exception e) {
                Bole.LOGGER.warn(e);
            }
        }

        public void setTargetEntity(Entity targetEntity) {
            this.targetEntity = targetEntity;
            this.displayedEntity = targetEntity.getType().create(MinecraftClient.getInstance().world);
            updateDisplayedEntity();
        }
    }
}
