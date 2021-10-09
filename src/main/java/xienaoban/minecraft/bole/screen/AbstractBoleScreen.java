package xienaoban.minecraft.bole.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import xienaoban.minecraft.bole.client.KeyBindingManager;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoleScreen<E extends Entity, T extends AbstractBoleScreenHandler<E>> extends HandledScreen<T> {
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    private static final int BOOK_TEXTURE_CUT = 29;

    protected final int bodyWidth, bodyHeight;
    protected int bodyLeft, bodyRight, bodyTop, bodyBottom;
    protected final int contentWidth, contentHeight, contentSpacingWidth;
    protected int[] contentLeft, contentRight;
    protected int contentTop, contentBottom;
    protected int entityLeft, entityRight, entityTop, entityBottom;
    protected int plan;

    public AbstractBoleScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.bodyWidth = (192 - BOOK_TEXTURE_CUT) * 2;
        this.bodyHeight = 192;
        this.contentWidth = 110;
        this.contentHeight = 140;
        this.contentSpacingWidth = 20;
        this.contentLeft = new int[2];
        this.contentRight = new int[2];
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindingManager.KEY_BOLE_SCREEN.matchesKey(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();
        this.bodyLeft = (this.width - this.bodyWidth) / 2;
        this.bodyRight = this.bodyLeft + this.bodyWidth;
        this.bodyTop = this.height / 2 - this.bodyHeight / 2 + 10;
        this.bodyBottom = this.bodyTop + this.bodyHeight;
        this.contentLeft[0] = (this.width - this.contentSpacingWidth) / 2 - this.contentWidth;
        this.contentRight[0] = this.contentLeft[0] + this.contentWidth;
        this.contentLeft[1] = (this.width + this.contentSpacingWidth) / 2;
        this.contentRight[1] = this.contentLeft[1] + this.contentWidth;
        this.contentTop = this.bodyTop + 15;
        this.contentBottom = this.contentTop + this.contentHeight;
        this.plan = this.calEntityDisplayRegion();
    }

    private int calEntityDisplayRegion() {
        if (this.handler.entity == null) return 0;
        Box box = this.handler.entity.getVisibilityBoundingBox();
        double x = box.getXLength(), y = box.getYLength();
        double area = x * y, ratio = y / x;
        int plan;
        if (ratio > 2.0) plan = 4;
        else if (ratio < 1 / 2.0) {
            if (area < 0.4) plan = 1;
            else plan = 3;
        }
        else {
            if (area < 0.5) plan = 1;
            else plan = 2;
        }
        switch (plan) {
            case 1:
                this.entityLeft = this.contentRight[0] - contentWidth / 2;
                this.entityTop = this.contentTop + 10;
                this.entityRight = this.contentRight[0];
                this.entityBottom = this.entityTop + this.contentHeight / 3;
                break;
            case 2:
                this.entityLeft = this.contentLeft[0];
                this.entityTop = this.contentTop + 10;
                this.entityRight = this.contentRight[0] - contentWidth / 2;
                this.entityBottom = this.entityTop + this.contentHeight / 2;
                break;
            case 3:
                this.entityLeft = this.contentLeft[0] + 25;
                this.entityTop = this.contentTop + 50;
                this.entityRight = this.contentRight[0] - 25;
                this.entityBottom = this.entityTop + (int) (this.contentHeight * 0.2F);
                break;
            case 4:
                this.entityLeft = this.contentLeft[0] + 5;
                this.entityTop = this.contentTop + 10;
                this.entityRight = this.contentLeft[0] + (int) (contentWidth * 0.3F);
                this.entityBottom = this.entityTop + (int) (this.contentHeight * 0.75F);
                break;
            default: break;
        }
        return plan;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        setTexture(BOOK_TEXTURE);
        int x0 = this.bodyLeft, x1 = this.bodyRight, y0 = this.bodyTop, y1 = this.bodyBottom;
        int u0 = BOOK_TEXTURE_CUT, u1 = u0 + this.bodyWidth / 2, v0 = 0, v1 = v0 + this.bodyHeight;
        drawTextureFlippedHorizontally(matrices, 256, 256, this.getZOffset(),
                x0, y0, this.width >> 1, y1, u0, v0, u1, v1);
        drawTextureNormally(matrices, 256, 256, this.getZOffset(),
                this.width >> 1, y0, x1, y1, u0, v0, u1, v1);
        if (mouseX < 5 && mouseY < 5) {
            drawRegionDebug(matrices);
        }
        this.textRenderer.drawWithShadow(matrices, this.title, this.contentLeft[0], this.contentTop, 0xff666666);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    private void drawRegionDebug(MatrixStack matrices) {
        final float r = 0.3F;
        int c = 0x66dd001b;
        drawHorizontalLine(matrices, c, r, getZOffset(), this.contentLeft[0], this.contentRight[0], this.contentTop);
        drawHorizontalLine(matrices, c, r, getZOffset(), this.contentLeft[0], this.contentRight[0], this.contentBottom);
        drawHorizontalLine(matrices, c, r, getZOffset(), this.contentLeft[1], this.contentRight[1], this.contentTop);
        drawHorizontalLine(matrices, c, r, getZOffset(), this.contentLeft[1], this.contentRight[1], this.contentBottom);
        drawVerticalLine(matrices, c, r, getZOffset(), this.contentLeft[0], this.contentTop, this.contentBottom);
        drawVerticalLine(matrices, c, r, getZOffset(), this.contentRight[0], this.contentTop, this.contentBottom);
        drawVerticalLine(matrices, c, r, getZOffset(), this.contentLeft[1], this.contentTop, this.contentBottom);
        drawVerticalLine(matrices, c, r, getZOffset(), this.contentRight[1], this.contentTop, this.contentBottom);

        c = 0x66287bde;
        drawHorizontalLine(matrices, c, r, getZOffset(), this.entityLeft, this.entityRight, this.entityTop);
        drawHorizontalLine(matrices, c, r, getZOffset(), this.entityLeft, this.entityRight, this.entityBottom);
        drawVerticalLine(matrices, c, r, getZOffset(), this.entityLeft, this.entityTop, this.entityBottom);
        drawVerticalLine(matrices, c, r, getZOffset(), this.entityRight, this.entityTop, this.entityBottom);
    }

    public void drawLivingEntityPlan(LivingEntity entity, int mouseX, int mouseY) {
        drawLivingEntityAuto(entity, this.entityLeft, this.entityTop, this.entityRight, this.entityBottom,
                mouseX / -33.0F, mouseY / -33.0F - 10);
    }

    public void drawLivingEntityAuto(LivingEntity entity, int x0, int y0, int x1, int y1, float mouseX, float mouseY) {
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
        InventoryScreen.drawEntity(x0 + x1 >> 1, y1, size, mouseX, mouseY, entity);
    }

    public void drawLivingEntityByWidth(LivingEntity entity, int x, int y, int width, float mouseX, float mouseY) {
        Box box = entity.getVisibilityBoundingBox();
        InventoryScreen.drawEntity(x, y, (int) (width / box.getXLength()), mouseX, mouseY, entity);
    }

    public void drawLivingEntityByHeight(LivingEntity entity, int x, int y, int height, float mouseX, float mouseY) {
        Box box = entity.getVisibilityBoundingBox();
        InventoryScreen.drawEntity(x, y, (int) (height / box.getYLength()), mouseX, mouseY, entity);
    }

    public void drawHorizontalLine(MatrixStack matrices, int color, float radius, float z, float x0, float x1, float y) {
        drawQuadrilateral(matrices, color, z, x0, y - radius, x1, y - radius, x0, y + radius, x1, y + radius);
    }

    public void drawVerticalLine(MatrixStack matrices, int color, float radius, float z, float x, float y0, float y1) {
        drawQuadrilateral(matrices, color, z, x - radius, y0, x + radius, y0, x - radius, y1, x + radius, y1);
    }

    public void drawQuadrilateral(MatrixStack matrices, int color, float z,
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
    public void drawTextureNormally(MatrixStack matrices, float tw, float th, float z,
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
    public void drawTextureFlippedHorizontally(MatrixStack matrices, float tw, float th, float z,
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
    public void drawTextureFlippedVertically(MatrixStack matrices, float tw, float th, float z,
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
    public void drawTextureRotated180(MatrixStack matrices, float tw, float th, float z,
                                             float x0, float y0, float x1, float y1,
                                             float u0, float v0, float u1, float v1) {
        drawTextureQuadrilateral(matrices, tw, th, z, x0, y0, x1, y0, x0, y1, x1, y1, u1, v1, u0, v1, u1, v0, u0, v0);
    }

    /**
     * @see net.minecraft.client.gui.DrawableHelper#drawTexturedQuad
     */
    public void drawTextureQuadrilateral(MatrixStack matrices, float textureWidth, float textureHeight, float z,
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

    public void setTexture(Identifier id) {
        assert this.client != null;
        this.client.getTextureManager().bindTexture(id);
    }
}
