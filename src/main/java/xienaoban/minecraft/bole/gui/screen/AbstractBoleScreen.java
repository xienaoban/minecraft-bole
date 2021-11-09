package xienaoban.minecraft.bole.gui.screen;

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
import xienaoban.minecraft.bole.gui.ElementBox;
import xienaoban.minecraft.bole.gui.ScreenElement;
import xienaoban.minecraft.bole.gui.Textures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoleScreen<E extends Entity, H extends AbstractBoleScreenHandler<E>> extends HandledScreen<H> {
    private static final int BOOK_TEXTURE_CUT = 29;

    public static final int BODY_WIDTH = (192 - BOOK_TEXTURE_CUT) * 2;
    public static final int BODY_HEIGHT = 192;
    public static final int CONTENT_WIDTH = 108;
    public static final int CONTENT_HEIGHT = 130;
    public static final int CONTENT_SPACING_WIDTH = 20;

    public static final int CONTENT_TEXT_COLOR = 0xd0121212;

    protected int bodyLeft, bodyRight, bodyTop, bodyBottom;
    protected int[] contentLeft, contentRight;
    protected int contentTop, contentBottom;

    protected final List<Page> pages;
    protected Page curLeftPage, curRightPage;

    protected boolean debugMode;

    public AbstractBoleScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.debugMode = false;
        this.contentLeft = new int[2];
        this.contentRight = new int[2];
        this.curLeftPage = new Page();
        this.curRightPage = new Page();
        this.pages = new ArrayList<>();
        this.pages.add(this.curLeftPage);
        this.pages.add(this.curRightPage);
        initCustom();
    }

    protected abstract void initCustom();

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
        BoleClient.getInstance().setScreenOpen(true);
    }

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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean success = false;
        if (mouseY >= this.contentTop && mouseY <= this.contentBottom) {
            if (mouseX >= this.contentLeft[0] && mouseX <= this.contentRight[0]) {
                success = this.curLeftPage.mouseClicked(mouseX, mouseY, button);
            }
            else if (mouseX >= this.contentLeft[1] && mouseX <= this.contentRight[1]) {
                success = this.curRightPage.mouseClicked(mouseX, mouseY, button);
            }
        }
        return success || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        this.handler.resetClientEntityServerProperties();
        super.onClose();
        BoleClient.getInstance().setScreenOpen(false);
    }

    public void setPageIndex(int pageIndex) {
        this.curLeftPage = this.pages.get(pageIndex);
        this.curRightPage = this.pages.get(pageIndex + 1);
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
            drawText(matrices, "Tick: " + BoleClient.getInstance().getTicks(), 0xbbffffff, 0.5F, 1, 10);
            if (this.handler.entity != null) {
                List<String> entitySuperclasses = new ArrayList<>();
                Class<?> clazz = this.handler.entity.getClass();
                while (!Entity.class.equals(clazz)) {
                    entitySuperclasses.add(clazz.getSimpleName());
                    clazz = clazz.getSuperclass();
                }
                entitySuperclasses.add(clazz.getSimpleName());
                Collections.reverse(entitySuperclasses);
                drawText(matrices, "Entity: " + String.join(" > ", entitySuperclasses), 0xbbffffff, 0.5F, 1, 15);
                drawText(matrices, "Screen: " + this.getClass().getSimpleName(), 0xbbffffff, 0.5F, 1, 20);
            }
        }
        drawText(matrices, this.title, 0x99888888, this.contentLeft[0] + 0.7F, this.contentTop - 12 + 0.7F);
        drawText(matrices, this.title, 0xff444444, this.contentLeft[0], this.contentTop - 12);
        drawLeftContent(matrices, delta, this.contentLeft[0], this.contentTop, mouseX, mouseY);
        drawRightContent(matrices, delta, this.contentLeft[1], this.contentTop, mouseX, mouseY);
    }

    /**
     * Draw the content of the left page.
     */
    protected abstract void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY);

    /**
     * Draw the content of the right page.
     */
    protected abstract void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY);

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
     * <p>2. The entity it renders is brighter (but it brings some lighting bugs...). </p>
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

    public void drawDebugBox(MatrixStack matrices, ElementBox box, int color) {
        if (!this.debugMode) {
            return;
        }
        drawRectangle(matrices, color, 0.3F, 0, box.left(), box.top(), box.right(), box.bottom());
    }

    public void setTexture(Identifier id) {
        assert this.client != null;
        this.client.getTextureManager().bindTexture(id);
    }

    /**
     * Manages all widgets on a page.
     */
    public class Page extends ScreenElement {
        public static final int CONTENT_WIDGET_MARGIN_WIDTH = 4;
        public static final int CONTENT_WIDGET_MARGIN_HEIGHT = 3;
        private static final int COLS = 4;
        public static final int CONTENT_WIDGET_WIDTH = (CONTENT_WIDTH - CONTENT_WIDGET_MARGIN_WIDTH * (COLS - 1)) / COLS;
        public static final int CONTENT_WIDGET_HEIGHT = 10;
        private static final int ROWS = CONTENT_HEIGHT / (CONTENT_WIDGET_HEIGHT + CONTENT_WIDGET_MARGIN_HEIGHT);

        private final List<List<AbstractContentWidget>> widgets;    // 10 * 2 widgets per page

        public Page() {
            super(CONTENT_WIDTH, CONTENT_HEIGHT);
            List<List<AbstractContentWidget>> l1 = new ArrayList<>();
            for (int i = COLS; i > 0; --i) {
                List<AbstractContentWidget> l2 = new ArrayList<>();
                for (int j = 0; j < ROWS; ++j) {
                    l2.add(null);
                }
                l1.add(l2);
            }
            this.widgets = l1;
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            super.draw(matrices, x, y, mouseX, mouseY);
            for (int i = 0; i < COLS; ++i) {
                for (int j = 0; j < ROWS; ++j) {
                    AbstractContentWidget w = this.widgets.get(i).get(j);
                    if (w != null) {
                        w.draw(matrices,
                                x + i * (CONTENT_WIDGET_WIDTH + CONTENT_WIDGET_MARGIN_WIDTH),
                                y + j * (CONTENT_WIDGET_HEIGHT + CONTENT_WIDGET_MARGIN_HEIGHT),
                                mouseX, mouseY);
                    }
                }
            }
            drawDebugBox(matrices, this.box, 0x66dd001b);
        }

        public boolean setSlot(int col, int row, AbstractContentWidget widget) {
            if (col + widget.getColSlots() > COLS || row + widget.getRowSlots() > ROWS) {
                Bole.LOGGER.error("Widget cannot be set here! " + widget.getColSlots() + ", " + widget.getRowSlots() + ", " + col + ", " + row);
                return false;
            }
            EmptyContentWidget empty = new EmptyContentWidget(1, 1, widget);
            for (int i = 0; i < widget.getColSlots(); ++i) {
                for (int j = 0; j < widget.getRowSlots(); ++j) {
                    this.widgets.get(col + i).set(row + j, empty);
                }
            }
            this.widgets.get(col).set(row, widget);
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
                            if (this.widgets.get(j + q).get(i + p) != null) {
                                continue BAD;
                            }
                        }
                    }
                    return setSlot(j, i, widget);
                }
            }
            Bole.LOGGER.error("Widget cannot be added here!");
            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int x = (int) mouseX - this.box.left(), y = (int) mouseY - this.box.top();
            int w = CONTENT_WIDGET_WIDTH + CONTENT_WIDGET_MARGIN_WIDTH, h = CONTENT_WIDGET_HEIGHT + CONTENT_WIDGET_MARGIN_HEIGHT;
            int col = x / w;
            int row = y / h;
            if (col >= COLS || row >= ROWS) {
                return false;
            }
            AbstractContentWidget widget = this.widgets.get(col).get(row);
            if (widget instanceof AbstractBoleScreen.EmptyContentWidget) {
                widget = ((EmptyContentWidget) widget).father;
            }
            if (widget == null || x > w * (widget.getColSlots() + col) - CONTENT_WIDGET_MARGIN_WIDTH || y > h * (widget.getRowSlots() + row) - CONTENT_WIDGET_MARGIN_HEIGHT) {
                return false;
            }
            return widget.mouseClicked(mouseX, mouseY, button);
        }
    }

    /**
     * Many widgets can be placed on one page.
     * Widgets can have different sizes.
     */
    public abstract class AbstractContentWidget extends ScreenElement {
        protected final int colSlots, rowSlots;

        public AbstractContentWidget(int colSlots, int rowSlots) {
            super(colSlots * (Page.CONTENT_WIDGET_WIDTH + Page.CONTENT_WIDGET_MARGIN_WIDTH) - Page.CONTENT_WIDGET_MARGIN_WIDTH,
                    rowSlots * (Page.CONTENT_WIDGET_HEIGHT + Page.CONTENT_WIDGET_MARGIN_HEIGHT) - Page.CONTENT_WIDGET_MARGIN_HEIGHT);
            this.colSlots = colSlots;
            this.rowSlots = rowSlots;
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            super.draw(matrices, x, y, mouseX, mouseY);
            drawContent(matrices, x, y, mouseX, mouseY);
            drawDebugBox(matrices, this.box, 0x88c55c2d);
        }

        protected abstract void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY);

        public final int getRowSlots() {
            return this.rowSlots;
        }

        public final int getColSlots() {
            return this.colSlots;
        }
    }

    /**
     * A widget that displays nothing.
     * It can also be used as a placeholder for large widgets.
     */
    public final class EmptyContentWidget extends AbstractContentWidget {
        private final AbstractContentWidget father;

        public EmptyContentWidget(int colSlots, int rowSlots) {
            this(colSlots, rowSlots, null);
        }

        public EmptyContentWidget(int colSlots, int rowSlots, AbstractContentWidget father) {
            super(colSlots, rowSlots);
            this.father = father;
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            this.box.position(x, y, true);
            if (this.father == null) {
                drawDebugBox(matrices, this.box, 0x88c55c2d);
            }
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {}

        public AbstractContentWidget getFather() {
            return this.father;
        }
    }

    /**
     * A widget that displays text in the center.
     */
    public final class CenteredTextContentWidget extends AbstractContentWidget {
        private static final int DEFAULT_LINE_HEIGHT = 8;
        private Text text;
        private int color;
        private float size;

        public CenteredTextContentWidget(int colSlots, int rowSlots, Text text, int color, float size) {
            super(colSlots, rowSlots);
            setText(text); setColor(color); setSize(size);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawText(matrices, text, color, size,
                    x + (this.box.width() - textRenderer.getWidth(text) >> 1),
                    y + (this.box.height() - (int)(DEFAULT_LINE_HEIGHT * this.size) >> 1));
        }

        public void setText(Text text) {
            this.text = text;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setSize(float size) {
            this.size = size;
        }
    }
}
