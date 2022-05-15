package xienaoban.minecraft.bole.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.gui.ElementBox;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericHandledScreen<T extends GenericScreenHandler> extends HandledScreen<T> {
    /**
     * Compatibility with RoughlyEnoughItems: never show rei gui in bole screens.
     */
    protected static final boolean DO_NOT_SHOW_REI = true;

    protected boolean debugMode;

    public GenericHandledScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.debugMode = false;
        BoleClient.getInstance().setScreenOpen(true);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindingManager.KEY_BOLE_SCREEN.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            this.debugMode = !this.debugMode;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        BoleClient.getInstance().setScreenOpen(false);
        super.close();
    }

    public boolean isGod() {
        return Bole.isGod(this.handler.player);
    }

    public boolean isDetached() {
        return Bole.isDetached(this.handler.player);
    }

    public static <EE extends Entity> void copyEntityNbtForDisplay(EE from, EE to) {
        NbtCompound nbt = from.writeNbt(new NbtCompound());
        nbt.remove("Dimension");
        nbt.remove("Rotation");
        nbt.remove("CustomName");
        nbt.remove("CustomNameVisible");
        nbt.remove("AngryAt");
        nbt.remove("HurtTime");
        nbt.remove("Pos");
        try {
            to.readNbt(nbt);
        }
        catch (Exception e) {
            Bole.LOGGER.warn("Cannot copy nbt of [" + from.getType().getTranslationKey() + "]: " + e);
        }
    }

    public static void setTexture(Identifier id) {
        RenderSystem.setShaderTexture(0, id);
    }

    public int getTextWidth(Text text) {
        return this.textRenderer.getWidth(text);
    }

    public static MatrixStack matrixScaleOn(float x, float y, float z) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.scale(x, y, z);
        RenderSystem.applyModelViewMatrix();
        return matrixStack;
    }

    public static void matrixScaleOff(MatrixStack matrixStack) {
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    public void drawDebugBox(MatrixStack matrices, ElementBox box, int color) {
        if (!this.debugMode) {
            return;
        }
        drawRectangle(matrices, color, 0.3F, 0, box.left(), box.top(), box.right(), box.bottom());
    }

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
        drawEntityBrighter(entity, size, x0 + x1 >> 1, y1, mouseX, mouseY);
    }

    public static void drawEntityByWidth(Entity entity, int width, int x, int y, float mouseX, float mouseY) {
        Box box = entity.getVisibilityBoundingBox();
        drawEntityBrighter(entity, (int) (width / box.getXLength()), x, y, mouseX, mouseY);
    }

    public static void drawEntityByHeight(Entity entity, int height, int x, int y, float mouseX, float mouseY) {
        Box box = entity.getVisibilityBoundingBox();
        drawEntityBrighter(entity, (int) (height / box.getYLength()), x, y, mouseX, mouseY);
    }

    /**
     * Draw an entity.
     * This method is similar to InventoryScreen.drawEntity(), but there are some differences:
     * <p>1. It can draw any Entity, not only LivingEntity. </p>
     * <p>2. The entity it renders is brighter (but it brings some lighting bugs...). </p>
     * <p>3. It can't recognize the yaw of LivingEntity, so don’t use it to render a rotating LivingEntity. </p>
     * @see net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity
     */
    @SuppressWarnings("deprecation")
    public static void drawEntityBrighter(Entity entity, int size, int x, int y, float mouseX, float mouseY) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        float fSize = -size;
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0F);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0D, 0.0D, 1000.0D);
        matrixStack2.scale(fSize, fSize, fSize);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(0.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        Quaternion quaternion3 = Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getYaw(0.0F) - f * 40.0F);
        quaternion.hamiltonProduct(quaternion2);
        quaternion.hamiltonProduct(quaternion3);
        matrixStack2.multiply(quaternion);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 0x00F000F0));   // 0x00F000F0
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    /**
     * @see net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity
     */
    public static void drawEntityGeneric(Entity entity,float size, float x, float y, float rotateX, float rotateY, float rotateZ) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1010.0);
        matrixStack.scale(1.0f, 1.0f, -1.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0, 0.0, 1000.0);
        matrixStack2.scale(size, size, size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f + rotateZ);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(rotateX);
        Quaternion quaternion3 = Vec3f.POSITIVE_Y.getDegreesQuaternion(rotateY);
        quaternion.hamiltonProduct(quaternion2);
        quaternion.hamiltonProduct(quaternion3);
        matrixStack2.multiply(quaternion);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion3.conjugate();
        entityRenderDispatcher.setRotation(quaternion3);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrixStack2, immediate, 0xF000F0));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
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

    /**
     * @see net.minecraft.client.gui.DrawableHelper#fill(Matrix4f, int, int, int, int, int)
     */
    public static void drawQuadrilateral(MatrixStack matrices, int color, float z,
                                         float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        int a = color >> 24 & 255;
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
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

    public static void drawTextureFlippedHorizontally(MatrixStack matrices, float tw, float th, float w, float h, float z, float x, float y, float u, float v) {
        drawTextureQuadrilateral(matrices, tw, th, z, x, y, x + w, y, x, y + h, x + w, y + h, u + w, v, u, v, u + w, v + h, u, v + h);
    }

    /**
     * Draws a textured quadrilateral from a region in a horizontally flipped texture.
     */
    public static void drawTextureFlippedHorizontally(MatrixStack matrices, float tw, float th, float z,
                                                      float x0, float y0, float x1, float y1,
                                                      float u0, float v0, float u1, float v1) {
        drawTextureQuadrilateral(matrices, tw, th, z, x0, y0, x1, y0, x0, y1, x1, y1, u1, v0, u0, v0, u1, v1, u0, v1);
    }

    /**
     * Draws a textured quadrilateral from a region in a vertically flipped texture.
     */
    public static void drawTextureFlippedVertically(MatrixStack matrices, float tw, float th, float z,
                                                    float x0, float y0, float x1, float y1,
                                                    float u0, float v0, float u1, float v1) {
        drawTextureQuadrilateral(matrices, tw, th, z, x0, y0, x1, y0, x0, y1, x1, y1, u0, v1, u1, v1, u0, v0, u1, v0);
    }

    /**
     * Draws a textured quadrilateral from a region in a 180 rotated texture.
     */
    public static void drawTextureRotated180(MatrixStack matrices, float tw, float th, float w, float h, float z, float x, float y, float u, float v) {
        drawTextureQuadrilateral(matrices, tw, th, z, x, y, x + w, y, x, y + h, x + w, y + h, u + w, v + h, u, v + h, u + w, v, u, v);
    }

    /**
     * Draws a textured quadrilateral from a region in a 180 rotated texture.
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
        Matrix4f model = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(model, x0, y0, z).texture(u0, v0).next();      // The four vertices must
        bufferBuilder.vertex(model, x2, y2, z).texture(u2, v2).next();      // be added in clockwise
        bufferBuilder.vertex(model, x3, y3, z).texture(u3, v3).next();      // or counterclockwise
        bufferBuilder.vertex(model, x1, y1, z).texture(u1, v1).next();      // order.
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    public void drawText(MatrixStack matrices, String text, int color, float x, float y) {
        this.textRenderer.draw(matrices, text, x, y, color);
    }

    public void drawText(MatrixStack matrices, Text text, int color, float x, float y) {
        this.textRenderer.draw(matrices, text, x, y, color);
    }

    public void drawText(MatrixStack matrices, String text, int color, float size, float x, float y) {
        MatrixStack matrixStack = matrixScaleOn(size, size, size);
        this.textRenderer.draw(matrices, text, x / size, y / size, color);
        matrixScaleOff(matrixStack);
    }

    public void drawText(MatrixStack matrices, Text text, int color, float size, float x, float y) {
        MatrixStack matrixStack = matrixScaleOn(size, size, size);
        this.textRenderer.draw(matrices, text, x / size, y / size, color);
        matrixScaleOff(matrixStack);
    }

    public void drawTextCenteredX(MatrixStack matrices, Text text, int color, float xMid, float y) {
        int w2 = this.textRenderer.getWidth(text) >> 1;
        drawText(matrices, text, color, xMid - w2, y);
    }

    public void drawTextCenteredX(MatrixStack matrices, String text, int color, float size, float xMid, float y) {
        float w2 = (this.textRenderer.getWidth(text) >> 1) * size;
        drawText(matrices, text, color, size, xMid - w2, y);
    }

    public void drawTextCenteredX(MatrixStack matrices, Text text, int color, float size, float xMid, float y) {
        float w2 = (this.textRenderer.getWidth(text) >> 1) * size;
        drawText(matrices, text, color, size, xMid - w2, y);
    }

    /**
     * @see net.minecraft.client.gui.screen.Screen#renderTooltipFromComponents
     */
    public void renderTooltip(MatrixStack matrices, @NotNull List<OrderedText> lines, float size, int x, int y) {
        List<TooltipComponent> components = lines.stream().map(TooltipComponent::of).collect(Collectors.toList());
        TooltipComponent tooltipComponent2;
        int s;
        int k;
        if (components.isEmpty()) {
            return;
        }
        int i = 0;
        int j = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components) {
            k = tooltipComponent.getWidth(this.textRenderer);
            if (k > i) {
                i = k;
            }
            j += tooltipComponent.getHeight();
        }
        int xx = (int) ((x + 2) / size);
        int yy = (int) ((y + 2) / size);
        k = i;
        int m = j;
        int ww = (int) (this.width / size), hh = (int) (this.height / size);
        if (xx + i + 6 > ww) {
            xx = ww - i - 6;
        }
        MatrixStack matrixStack = matrixScaleOn(size, size, size);
        matrices.push();
        int n = -267386864;
        int o = 0x505000FF;
        int p = 1344798847;
        int q = 400;
        float f = this.itemRenderer.zOffset;
        this.itemRenderer.zOffset = q;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Screen.fillGradient(matrix4f, bufferBuilder, xx - 3, yy - 4, xx + k + 3, yy - 3, q, n, n);
        Screen.fillGradient(matrix4f, bufferBuilder, xx - 3, yy + m + 3, xx + k + 3, yy + m + 4, q, n, n);
        Screen.fillGradient(matrix4f, bufferBuilder, xx - 3, yy - 3, xx + k + 3, yy + m + 3, q, n, n);
        Screen.fillGradient(matrix4f, bufferBuilder, xx - 4, yy - 3, xx - 3, yy + m + 3, q, n, n);
        Screen.fillGradient(matrix4f, bufferBuilder, xx + k + 3, yy - 3, xx + k + 4, yy + m + 3, q, n, n);
        Screen.fillGradient(matrix4f, bufferBuilder, xx - 3, yy - 3 + 1, xx - 3 + 1, yy + m + 3 - 1, q, o, p);
        Screen.fillGradient(matrix4f, bufferBuilder, xx + k + 2, yy - 3 + 1, xx + k + 3, yy + m + 3 - 1, q, o, p);
        Screen.fillGradient(matrix4f, bufferBuilder, xx - 3, yy - 3, xx + k + 3, yy - 3 + 1, q, o, o);
        Screen.fillGradient(matrix4f, bufferBuilder, xx - 3, yy + m + 2, xx + k + 3, yy + m + 3, q, p, p);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, q);
        int r = yy;
        for (s = 0; s < components.size(); ++s) {
            tooltipComponent2 = components.get(s);
            tooltipComponent2.drawText(this.textRenderer, xx, r, matrix4f, immediate);
            r += tooltipComponent2.getHeight() + (s == 0 ? 2 : 0);
        }
        immediate.draw();
        matrices.pop();
        r = yy;
        for (s = 0; s < components.size(); ++s) {
            tooltipComponent2 = components.get(s);
            tooltipComponent2.drawItems(this.textRenderer, xx, r, matrices, this.itemRenderer, q);
            r += tooltipComponent2.getHeight() + (s == 0 ? 2 : 0);
        }
        this.itemRenderer.zOffset = f;
        matrixScaleOff(matrixStack);
    }

}
