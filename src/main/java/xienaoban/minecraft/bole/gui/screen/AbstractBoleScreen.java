package xienaoban.minecraft.bole.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.gui.ElementBox;
import xienaoban.minecraft.bole.gui.ScreenElement;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.mixin.IMixinItemStack;
import xienaoban.minecraft.bole.util.Keys;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoleScreen<E extends Entity, H extends AbstractBoleScreenHandler<E>> extends HandledScreen<H> {
    private static final int BOOK_TEXTURE_CUT = 29;

    public static final int BODY_WIDTH = (192 - BOOK_TEXTURE_CUT) * 2;
    public static final int BODY_HEIGHT = 192;
    public static final int CONTENT_WIDTH = 108;
    public static final int CONTENT_HEIGHT = 130;
    public static final int CONTENT_SPACING_WIDTH = 20;

    public static final int DARK_TEXT_COLOR = 0xc0121212;
    public static final int LIGHT_TEXT_COLOR = 0xbbffffff;

    private final Page emptyPage;

    // private Element focused; (in AbstractParentElement)
    private ScreenElement hovered;
    private final OverlayMessageHud overlayMessageHud;

    protected int bodyLeft, bodyRight, bodyTop, bodyBottom;
    protected int[] contentLeft, contentRight;
    protected int contentTop, contentBottom;

    protected final Map<Integer, ButtonWidget> bookmarks;

    protected ScreenElement popup;

    protected final List<Page> pages;
    protected Page curLeftPage, curRightPage;
    protected int pageIndex;

    protected boolean debugMode;

    public AbstractBoleScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.debugMode = false;
        this.contentLeft = new int[2];
        this.contentRight = new int[2];
        this.overlayMessageHud = new OverlayMessageHud();
        this.bookmarks = new HashMap<>();
        this.emptyPage = new Page();
        this.emptyPage.setSlot(0, 4, new CenteredTextPropertyWidget(4, 2, new TranslatableText(Keys.TEXT_EMPTY_WITH_BRACKETS), 0xaa666666, 1.0F));
        this.pages = new ArrayList<>();
        this.pages.add(new Page());
        this.pages.add(new Page());
        setPageIndex(0);
        initPages();
        for (Page page : this.pages) {
            page.addSlotsFromLazyList();
        }
        initCustom();
        BoleClient.getInstance().setScreenOpen(true);
    }

    protected abstract void initPages();

    protected abstract void initCustom();

    @Override
    protected void init() {
        super.init();
        this.bodyLeft = (this.width - BODY_WIDTH) / 2;
        this.bodyRight = this.bodyLeft + BODY_WIDTH;
        this.bodyTop = this.height / 2 - BODY_HEIGHT / 2 - 10;
        this.bodyBottom = this.bodyTop + BODY_HEIGHT;
        this.contentLeft[0] = (this.width - CONTENT_SPACING_WIDTH) / 2 - CONTENT_WIDTH;
        this.contentRight[0] = this.contentLeft[0] + CONTENT_WIDTH;
        this.contentLeft[1] = (this.width + CONTENT_SPACING_WIDTH) / 2;
        this.contentRight[1] = this.contentLeft[1] + CONTENT_WIDTH;
        this.contentTop = this.bodyTop + 25;
        this.contentBottom = this.contentTop + CONTENT_HEIGHT;
        initButtons();
    }

    protected void initButtons() {
        addDrawableChild(new PageTurnWidget(this.contentLeft[0] + 1, this.contentBottom, false, (buttonWidget) -> this.setPageIndex(this.pageIndex - 2), true));
        addDrawableChild(new PageTurnWidget(this.contentRight[1] - 25, this.contentBottom, true, (buttonWidget) -> this.setPageIndex(this.pageIndex + 2), true));
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
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        ScreenElement pre = null, cur = getScreenElement(mouseX, mouseY);
        while (cur != null) {
            pre = cur;
            cur = cur.getSubScreenElement(mouseX, mouseY);
        }
        setHovered(pre);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean success;
        ScreenElement ele = getScreenElement(mouseX, mouseY);
        if (ele != null) {
            success = ele.mouseClicked(mouseX, mouseY, button);
        }
        else {
            success = false;
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

    public ScreenElement getScreenElement(double mouseX, double mouseY) {
        if (this.popup != null) return this.popup;
        if (mouseY >= this.contentTop && mouseY <= this.contentBottom) {
            if (mouseX >= this.contentLeft[0] && mouseX <= this.contentRight[0]) {
                return this.curLeftPage;
            }
            else if (mouseX >= this.contentLeft[1] && mouseX <= this.contentRight[1]) {
                return this.curRightPage;
            }
        }
        return null;
    }

    @Override
    public void onClose() {
        this.handler.resetClientEntityServerProperties();
        super.onClose();
        BoleClient.getInstance().setScreenOpen(false);
    }

    public void setPageIndex(int pageIndex) {
        pageIndex = pageIndex & 0xfffffffe;
        int size = this.pages.size() - 1;
        if (pageIndex < 0) {
            pageIndex = 0;
        }
        else if (pageIndex > size) {
            pageIndex = size & 0xfffffffe;
        }
        this.curLeftPage = pageIndex <= size ? this.pages.get(pageIndex) : this.emptyPage;
        this.curRightPage = pageIndex + 1 <= size ? this.pages.get(pageIndex + 1) : this.emptyPage;
        this.pageIndex = pageIndex;
    }

    /**
     * Don't forget to setPageIndex(0) after reset pages.
     */
    public void resetPages() {
        this.pages.clear();
        this.pages.add(new Page());
        setHovered(null);
    }

    public void addBookmark(int index, Text title, ButtonWidget.PressAction onPress) {
        BookmarkButtonWidget bookmark = new BookmarkButtonWidget(this.contentLeft[0] - 30 - 10 + (index % 3), this.contentTop - 5 + index * 14, index, title, onPress);
        addDrawableChild(bookmark);
        this.bookmarks.put(index, bookmark);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        setTexture(Textures.BOOK);
        int x0 = this.bodyLeft, x1 = this.bodyRight, y0 = this.bodyTop, y1 = this.bodyBottom;
        int u0 = BOOK_TEXTURE_CUT, u1 = u0 + BODY_WIDTH / 2, v0 = 0, v1 = v0 + BODY_HEIGHT;
        drawTextureFlippedHorizontally(matrices, 256, 256, this.getZOffset(),
                x0, y0, this.width >> 1, y1, u0, v0, u1, v1);
        drawTextureNormally(matrices, 256, 256, this.getZOffset(),
                this.width >> 1, y0, x1, y1, u0, v0, u1, v1);
        if (this.debugMode) {
            drawText(matrices, "Ticks: " + BoleClient.getInstance().getScreenTicks(), LIGHT_TEXT_COLOR, 0.5F, 1, 10);
            if (this.handler.entity != null) {
                List<String> entitySuperclasses = new ArrayList<>();
                Class<?> clazz = this.handler.entity.getClass();
                while (!Entity.class.equals(clazz)) {
                    entitySuperclasses.add(clazz.getSimpleName());
                    clazz = clazz.getSuperclass();
                }
                entitySuperclasses.add(clazz.getSimpleName());
                Collections.reverse(entitySuperclasses);
                drawText(matrices, "Entity: " + String.join(" > ", entitySuperclasses), LIGHT_TEXT_COLOR, 0.5F, 1, 15);
                drawText(matrices, "Screen: " + this.getClass().getSimpleName(), LIGHT_TEXT_COLOR, 0.5F, 1, 20);
            }
            drawText(matrices, "HighlightManager: " + BoleClient.getInstance().getHighlightManager(), LIGHT_TEXT_COLOR, 0.5F, 1, this.height - 5);
        }
        drawText(matrices, this.title, 0x99888888, this.contentLeft[0] + 0.7F, this.contentTop - 12 + 0.7F);
        drawText(matrices, this.title, 0xff444444, this.contentLeft[0], this.contentTop - 12);
        drawLeftContent(matrices, delta, this.contentLeft[0], this.contentTop, mouseX, mouseY);
        drawRightContent(matrices, delta, this.contentLeft[1], this.contentTop, mouseX, mouseY);
        int sz = this.pages.size();
        if (this.pageIndex < sz) {
            drawTextCenteredX(matrices, "- " + (this.pageIndex + 1) + "/" + sz + " -", 0x80000000, 0.75F, this.contentLeft[0] + this.contentRight[0] >> 1, this.contentBottom + 4);
        }
        if (this.pageIndex + 1 < sz) {
            drawTextCenteredX(matrices, "- " + (this.pageIndex + 2) + "/" + sz + " -", 0x80000000, 0.75F, this.contentLeft[1] + this.contentRight[1] >> 1, this.contentBottom + 4);
        }
        this.overlayMessageHud.draw(matrices, this.width >> 1, this.bodyBottom - 4, mouseX, mouseY);
    }

    /**
     * Draw the content of the left page.
     */
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curLeftPage.draw(matrices, x, y, mouseX, mouseY);
    }

    /**
     * Draw the content of the right page.
     */
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curRightPage.draw(matrices, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.translate(-super.x, -super.y, 0.0F);
        RenderSystem.applyModelViewMatrix();
        if (this.popup != null) this.popup.draw(matrices, this.width - this.popup.box.width() >> 1, this.height - this.popup.box.height() - 32 >> 1, mouseX, mouseY);
        if (this.hovered != null) {
            this.hovered.drawHovered(matrices, mouseX, mouseY);
        }
        matrixStack.translate(super.x, super.y, 0.0F);
        RenderSystem.applyModelViewMatrix();
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
    @SuppressWarnings("deprecation")
    public static void drawEntity(Entity entity, int size, int x, int y, float mouseX, float mouseY) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        float fSize = -size;
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate((float)x, (float)y, 1050.0F);
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
        quaternion3.conjugate();
        entityRenderDispatcher.setRotation(quaternion3);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 180.0F, 1.0F, matrixStack2, immediate, 0x00F000F0));   // 0x00F000F0
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

    public void drawDebugBox(MatrixStack matrices, ElementBox box, int color) {
        if (!this.debugMode) {
            return;
        }
        drawRectangle(matrices, color, 0.3F, 0, box.left(), box.top(), box.right(), box.bottom());
    }

    public void setTexture(Identifier id) {
        RenderSystem.setShaderTexture(0, id);
    }

    public MatrixStack matrixScaleOn(float x, float y, float z) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.scale(x, y, z);
        RenderSystem.applyModelViewMatrix();
        return matrixStack;
    }

    public void matrixScaleOff(MatrixStack matrixStack) {
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
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

    public void showOverlayMessage(String translationKey) {
        this.overlayMessageHud.showMessage(new TranslatableText(translationKey));
    }

    public void showOverlayMessage(Text text) {
        this.overlayMessageHud.showMessage(text);
    }

    public ScreenElement getHovered() {
        return hovered;
    }

    public void setHovered(ScreenElement hovered) {
        this.hovered = hovered;
    }

    public void setPopup(ScreenElement popup) {
        this.popup = popup;
        this.setHovered(null);
    }

    public GameMode getGameMode() {
        ClientPlayerInteractionManager manager = MinecraftClient.getInstance().interactionManager;
        return manager != null ? manager.getCurrentGameMode() : null;
    }

    public boolean isGodMode() {
        return getGameMode() == GameMode.CREATIVE;
    }

    public final class OverlayMessageHud extends ScreenElement {
        private Text overlayMessage;
        private int overlayTicksTo;

        public OverlayMessageHud() {
            super(0, 0);
            this.overlayMessage = null;
            this.overlayTicksTo = -1;
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int ticks = this.overlayTicksTo - BoleClient.getInstance().getScreenTicks();
            if (this.overlayMessage == null || ticks < 0) {
                return;
            }
            if (ticks == 0) {
                clearMessage();
                return;
            }
            if (ticks > 63) {
                ticks = 63;
            }
            drawTextCenteredX(matrices, this.overlayMessage, 0x00FFFFAA | (ticks << 26), x, y);
        }

        public void showMessage(Text text) {
            this.overlayMessage = text;
            this.overlayTicksTo = BoleClient.getInstance().getScreenTicks() + 100;
        }

        public void clearMessage() {
            this.overlayMessage = null;
            this.overlayTicksTo = -1;
        }

    }

    public class BookmarkButtonWidget extends ButtonWidget {
        private final int color;
        private final Text title;

        public BookmarkButtonWidget(int x, int y, int color, Text title, PressAction onPress) {
            super(x, y, 30, 10, LiteralText.EMPTY, onPress);
            this.color = color % 4;
            this.title = title;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            setTexture(Textures.ICONS);
            int u = 220;
            int v = 240 - 10 * this.color;
            if (this.isHovered()) {
                u -= 30;
            }
            this.drawTexture(matrices, this.x, this.y, u, v, 30, 10);
            drawText(matrices, this.title, LIGHT_TEXT_COLOR, 0.5F, this.x + 28 - textRenderer.getWidth(this.title) * 0.5F, this.y + 3);
        }
    }

    public class PopUpConfirmWindow extends ScreenElement {
        private final Text title;
        private final List<OrderedText> lines;
        private final PopUpButton confirm, cancel;

        public PopUpConfirmWindow(Text text, Runnable onConfirm) {
            this(text, onConfirm, () -> {});
        }

        public PopUpConfirmWindow(Text text, Runnable onConfirm, Runnable onCancel) {
            super(180, 120);
            this.title = new TranslatableText(Keys.TEXT_ANTI_MISTOUCH_WARNING);
            this.lines = textRenderer.wrapLines(text, this.box.width() - 24);
            this.confirm = new PopUpButton(new TranslatableText(Keys.GUI_OK), onConfirm);
            this.cancel = new PopUpButton(new TranslatableText(Keys.GUI_CANCEL), onCancel);
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            super.draw(matrices, x, y, mouseX, mouseY);
            renderBackground(matrices);
            setTexture(Textures.BOOK);
            final int bookRight = 160, bookTop = 7, bookBottom = 175, bookWidth = (this.box.width() >> 1) + 1, bookHeight = (this.box.height() >> 1) + 1;
            drawTextureNormally(matrices, 256, 256, bookWidth, bookHeight, getZOffset(), this.box.right() - bookWidth, this.box.top(), bookRight - bookWidth, bookTop);
            drawTextureNormally(matrices, 256, 256, bookWidth, bookHeight, getZOffset(), this.box.right() - bookWidth, this.box.bottom() - bookHeight, bookRight - bookWidth, bookBottom - bookHeight);
            drawTextureFlippedHorizontally(matrices, 256, 256, bookWidth, bookHeight, getZOffset(), this.box.left(), this.box.top(), bookRight - bookWidth, bookTop);
            drawTextureFlippedHorizontally(matrices, 256, 256, bookWidth, bookHeight, getZOffset(), this.box.left(), this.box.bottom() - bookHeight, bookRight - bookWidth, bookBottom - bookHeight);
            textRenderer.draw(matrices, this.title, x + 12.5F, y + 8.7F, 0x66e67417);
            textRenderer.draw(matrices, this.title, x + 12, y + 8, 0xff612f08);
            for (int i = 0; i < lines.size(); ++i) {
                textRenderer.draw(matrices, lines.get(i), x + 12, y + 22 + i * 10, DARK_TEXT_COLOR);
            }
            this.confirm.draw(matrices, this.box.right() - this.confirm.box.width() - this.cancel.box.width() - 25, this.box.bottom() - this.confirm.box.height() - 12, mouseX, mouseY);
            this.cancel.draw(matrices, this.box.right() - this.cancel.box.width() - 20, this.box.bottom() - this.cancel.box.height() - 12, mouseX, mouseY);
            drawDebugBox(matrices, this.box, 0x66dd001b);
        }

        @Override
        public ScreenElement getSubScreenElement(double mouseX, double mouseY) {
            if (confirm.isMouseOver(mouseX, mouseY)) return confirm;
            if (cancel.isMouseOver(mouseX, mouseY)) return cancel;
            return null;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ScreenElement sub = getSubScreenElement(mouseX, mouseY);
            if (sub != null) sub.mouseClicked(mouseX, mouseY, button);
            else if (!isMouseOver(mouseX, mouseY)) {
                cancel.mouseClicked(mouseX, mouseY, button);
            }
            return true;
        }
    }

    public class PopUpButton extends ScreenElement {
        private final Text title;
        private final Runnable onClick;

        public PopUpButton(Text title, Runnable onClick) {
            super(40, 20);
            this.title = title;
            this.onClick = onClick;
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            super.draw(matrices, x, y, mouseX, mouseY);
            setTexture(Textures.POPUP);
            drawTextureNormally(matrices, 256, 256, this.box.width(), this.box.height(), getZOffset(), x, y, getHovered() == this ? this.box.width() : 0, 200);
            drawTextCenteredX(matrices, this.title, DARK_TEXT_COLOR, x + (this.box.width() >> 1), y + (this.box.height() - 7 >> 1));
            drawDebugBox(matrices, this.box, getHovered() == this ? 0x88b9ac67 : 0x88c55c2d);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            setPopup(null);
            this.onClick.run();
            return true;
        }
    }

    /**
     * Manages all widgets on a page.
     */
    public class Page extends ScreenElement {
        public static final int PROPERTY_WIDGET_MARGIN_WIDTH = 4;
        public static final int PROPERTY_WIDGET_MARGIN_HEIGHT = 3;
        private static final int COLS = 4;
        public static final int PROPERTY_WIDGET_WIDTH = (CONTENT_WIDTH - PROPERTY_WIDGET_MARGIN_WIDTH * (COLS - 1)) / COLS;
        public static final int PROPERTY_WIDGET_HEIGHT = 10;
        private static final int ROWS = CONTENT_HEIGHT / (PROPERTY_WIDGET_HEIGHT + PROPERTY_WIDGET_MARGIN_HEIGHT);

        private final List<List<AbstractPropertyWidget>> widgets;    // 10 * 2 widgets per page
        private List<AbstractPropertyWidget> lazyList;

        public Page() {
            super(CONTENT_WIDTH, CONTENT_HEIGHT);
            List<List<AbstractPropertyWidget>> l1 = new ArrayList<>();
            for (int i = COLS; i > 0; --i) {
                List<AbstractPropertyWidget> l2 = new ArrayList<>();
                for (int j = 0; j < ROWS; ++j) {
                    l2.add(null);
                }
                l1.add(l2);
            }
            this.widgets = l1;
            this.lazyList = new ArrayList<>();
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            super.draw(matrices, x, y, mouseX, mouseY);
            if (popup == null) {
                for (int i = 0; i < COLS; ++i) {
                    for (int j = 0; j < ROWS; ++j) {
                        AbstractPropertyWidget w = this.widgets.get(i).get(j);
                        if (w != null) {
                            w.draw(matrices,
                                    x + i * (PROPERTY_WIDGET_WIDTH + PROPERTY_WIDGET_MARGIN_WIDTH),
                                    y + j * (PROPERTY_WIDGET_HEIGHT + PROPERTY_WIDGET_MARGIN_HEIGHT),
                                    mouseX, mouseY);
                        }
                    }
                }
            }
            drawDebugBox(matrices, this.box, 0x66dd001b);
        }

        public boolean setSlot(int col, int row, AbstractPropertyWidget widget) {
            if (col + widget.getColSlots() > COLS || row + widget.getRowSlots() > ROWS) {
                Bole.LOGGER.error(widget.getClass().getSimpleName() + " cannot be set here! " + widget.getColSlots() + ", " + widget.getRowSlots() + ", " + col + ", " + row);
                return false;
            }
            EmptyPropertyWidget empty = new EmptyPropertyWidget(1, 1, widget);
            for (int i = 0; i < widget.getColSlots(); ++i) {
                for (int j = 0; j < widget.getRowSlots(); ++j) {
                    this.widgets.get(col + i).set(row + j, empty);
                }
            }
            this.widgets.get(col).set(row, widget);
            return true;
        }

        public boolean addSlot(AbstractPropertyWidget widget) {
            final int addCnt = widget.getColSlots() == 2 ? 2 : 1;
            for (int i = 0; i < ROWS; ++i) {
                if (i + widget.getRowSlots() > ROWS) break;
                BAD:
                for (int j = 0; j < COLS; j += addCnt) {
                    if (j + widget.getColSlots() > COLS) break;
                    for (int p = 0; p < widget.getRowSlots(); ++p) {
                        for (int q = 0; q < widget.getColSlots(); ++q) {
                            if (this.widgets.get(j + q).get(i + p) != null) continue BAD;
                        }
                    }
                    return setSlot(j, i, widget);
                }
            }
            // Bole.LOGGER.error(widget.getClass().getSimpleName() + " cannot be added here! " + widget.getColSlots() + ", " + widget.getRowSlots());
            return false;
        }

        public Page addSlotLazy(AbstractPropertyWidget widget) {
            this.lazyList.add(widget);
            return this;
        }

        public Page addSlotLazyBefore(AbstractPropertyWidget widget, Class<?> before) {
            int size = this.lazyList.size();
            for (int i = 0; i < size; ++i) {
                if (this.lazyList.get(i).getClass() == before) {
                    this.lazyList.add(i, widget);
                    return this;
                }
            }
            throw new RuntimeException("No " + before.getSimpleName() + " in the lazy list.");
        }

        public Page addSlotLazyAfter(AbstractPropertyWidget widget, Class<?> after) {
            if (after == null) {
                this.lazyList.add(0, widget);
                return this;
            }
            int size = this.lazyList.size();
            for (int i = 0; i < size; ++i) {
                if (this.lazyList.get(i).getClass() == after) {
                    this.lazyList.add(i + 1, widget);
                    return this;
                }
            }
            throw new RuntimeException("No " + after.getSimpleName() + " in the lazy list.");
        }

        protected void addSlotsFromLazyList() {
            int cnt = 0;
            for (AbstractPropertyWidget widget : this.lazyList) {
                if (!addSlot(widget)) {
                    ++cnt;
                }
            }
            this.lazyList = new ArrayList<>();
            if (cnt > 0) {
                Bole.LOGGER.error(cnt + " widgets failed to be added.");
            }
        }

        @Override
        public ScreenElement getSubScreenElement(double mouseX, double mouseY) {
            int x = (int) mouseX - this.box.left(), y = (int) mouseY - this.box.top();
            int w = PROPERTY_WIDGET_WIDTH + PROPERTY_WIDGET_MARGIN_WIDTH, h = PROPERTY_WIDGET_HEIGHT + PROPERTY_WIDGET_MARGIN_HEIGHT;
            int col = x / w;
            int row = y / h;
            if (col >= COLS || row >= ROWS) {
                return null;
            }
            AbstractPropertyWidget widget = this.widgets.get(col).get(row);
            if (widget instanceof EmptyPropertyWidget emptyWidget) {
                widget = emptyWidget.father;
            }
            if (widget == null || mouseX > widget.box.right() || mouseY > widget.box.bottom()) {
                return null;
            }
            return widget;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ScreenElement widget = getSubScreenElement(mouseX, mouseY);
            if (widget != null) {
                return widget.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }
    }

    /**
     * Many widgets can be placed on one page.
     * Widgets can have different sizes.
     */
    public abstract class AbstractPropertyWidget extends ScreenElement {
        protected final int colSlots, rowSlots;
        protected final List<OrderedText> tooltipLines;
        private final OrderedText widgetClassText;

        public AbstractPropertyWidget(int colSlots, int rowSlots) {
            super(colSlots * (Page.PROPERTY_WIDGET_WIDTH + Page.PROPERTY_WIDGET_MARGIN_WIDTH) - Page.PROPERTY_WIDGET_MARGIN_WIDTH,
                    rowSlots * (Page.PROPERTY_WIDGET_HEIGHT + Page.PROPERTY_WIDGET_MARGIN_HEIGHT) - Page.PROPERTY_WIDGET_MARGIN_HEIGHT);
            this.colSlots = colSlots;
            this.rowSlots = rowSlots;
            this.tooltipLines = new ArrayList<>();
            initTooltipLines();
            String name = this.getClass().getName();
            name = name.substring(name.lastIndexOf('.') + 1).replaceFirst("\\$", " -> ");
            this.widgetClassText = new LiteralText(name).formatted(Formatting.DARK_GRAY).asOrderedText();
        }

        protected abstract void initTooltipLines();

        protected void initTooltipTitle(String translateKey) {
            addTooltipLines(translateKey, Formatting.YELLOW);
        }

        protected void initTooltipDescription(String translateKey) {
            addTooltipLines(translateKey, Formatting.GRAY);
        }

        protected void initTooltipEmptyLine() {
            this.tooltipLines.add(new LiteralText(" ").asOrderedText());
        }

        protected void initTooltipButtonDescription(String translateKey) {
            addTooltipLines(translateKey, Formatting.WHITE);
        }

        protected final void addTooltipLine(String translateKey, Formatting color) {
            this.tooltipLines.add(new TranslatableText(translateKey).formatted(color).asOrderedText());
        }

        protected final void addTooltipLines(String translateKey, Formatting color) {
            List<OrderedText> lines = MinecraftClient.getInstance().textRenderer.wrapLines(new TranslatableText(translateKey).formatted(color), 250);
            this.tooltipLines.addAll(lines);
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            super.draw(matrices, x, y, mouseX, mouseY);
            drawContent(matrices, x, y, mouseX, mouseY);
            drawDebugBox(matrices, this.box, isHovered() ? 0x88b9ac67 : 0x88c55c2d);
        }

        protected abstract void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY);

        @Override
        public void drawHovered(MatrixStack matrices, int mouseX, int mouseY) {
            drawTooltip(matrices);
        }

        protected void drawTooltip(MatrixStack matrices) {
            if (debugMode) {
                this.tooltipLines.add(this.widgetClassText);
                renderTooltip(matrices, this.tooltipLines, 0.5F, this.box.left(), this.box.bottom());
                this.tooltipLines.remove(this.tooltipLines.size() - 1);
            }
            else renderTooltip(matrices, this.tooltipLines, 0.5F, this.box.left(), this.box.bottom());
        }

        @Override
        public ScreenElement getSubScreenElement(double mouseX, double mouseY) {
            return null;
        }

        public final int getRowSlots() {
            return this.rowSlots;
        }

        public final int getColSlots() {
            return this.colSlots;
        }

        public final boolean isHovered() {
            return getHovered() == this;
        }
    }

    /**
     * A template widget composed of three parts: icon, bar and buttons.
     */
    protected abstract class TemplatePropertyWidget1 extends AbstractPropertyWidget {
        protected static final int GAP = 1, ICON_LEFT = 0, ICON_WIDTH = 10;
        protected static final int BAR_LEFT = ICON_LEFT + ICON_WIDTH + GAP;
        protected static final int BAR_TEXT_LEFT = BAR_LEFT + 2;
        protected static final int BUTTON_WIDTH = 6, BUTTON_TEXTURE_OFFSET = 10 - BUTTON_WIDTH >> 1;
        protected static final float TEXT_HEIGHT = 3.25F, TEXT_SIZE = 0.5F;

        protected static final int IDX_ICON = 0, IDX_BAR = 1, IDX_BUTTON_BEGIN = 2;

        protected final int barWidth;
        protected final int[] buttons;

        public TemplatePropertyWidget1(int colSlots, boolean hasBar, int buttonCnt) {
            super(colSlots, 1);
            this.buttons = new int[buttonCnt];
            if (hasBar) {
                barWidth = this.box.width() - BAR_LEFT - GAP - buttonCnt * (BUTTON_WIDTH + GAP);
            }
            else {
                barWidth = -1;
            }
            int buttonLeft = BAR_LEFT + barWidth + GAP;
            for (int i = 0; i < buttonCnt; ++i) {
                this.buttons[i] = buttonLeft + i * (BUTTON_WIDTH + GAP);
            }
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            super.draw(matrices, x, y, mouseX, mouseY);
        }

        protected void drawIcon(MatrixStack matrices, int u, int v) {
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), this.box.left() + ICON_LEFT, this.box.top(), u, v);
        }

        protected void drawBar(MatrixStack matrices, float p, int u, int v) {
            if (p < 0.0F) p = 0.0F;
            else if (p > 1.0F) p = 1.0F;
            drawTextureNormally(matrices, 256, 256, this.barWidth * p, 10, getZOffset(), this.box.left() + BAR_LEFT, this.box.top(), u, v);
        }

        protected void drawBarText(MatrixStack matrices, String text, int color) {
            drawText(matrices, text, color, TEXT_SIZE, this.box.left() + BAR_TEXT_LEFT, this.box.top() + TEXT_HEIGHT);
        }

        protected void drawBarText(MatrixStack matrices, Text text, int color) {
            drawText(matrices, text, color, TEXT_SIZE, this.box.left() + BAR_TEXT_LEFT, this.box.top() + TEXT_HEIGHT);
        }

        protected void drawButton(MatrixStack matrices, int index, int u, int v) {
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), this.box.left() + this.buttons[index] - BUTTON_TEXTURE_OFFSET, this.box.top(), u, v);
        }

        protected void drawButton(MatrixStack matrices, int index, ItemStack itemStack) {
            final float size = 0.5F;
            int px = (int) ((this.box.left() + this.buttons[index] - BUTTON_TEXTURE_OFFSET + 1) / size), py = (int) ((this.box.top() + 1) / size);
            MatrixStack matrixStack = matrixScaleOn(size, size, size);
            ItemStack forDraw = new ItemStack(((IMixinItemStack)(Object) itemStack).getRealItem());
            itemRenderer.renderInGui(forDraw, px, py);
            itemRenderer.renderGuiItemOverlay(textRenderer, forDraw, px, py, String.valueOf(itemStack.getCount()));
            matrixScaleOff(matrixStack);
        }

        protected int calMousePosition(double mouseX, double mouseY) {
            double offsetX = mouseX - this.box.left(), offsetY = mouseY - this.box.top();
            int index = -1;
            if (offsetX < ICON_LEFT + ICON_WIDTH) {
                if (offsetX > ICON_LEFT + 1 && offsetX < ICON_LEFT + ICON_WIDTH - 1
                        && offsetY > 1 && offsetY < 9) {
                    index = 0;
                }
            }
            else if (offsetX < BAR_LEFT + this.barWidth) {
                if (offsetX > BAR_LEFT && offsetY > 2 && offsetY < 8) {
                    index = 1;
                }
            }
            else {
                for (int i = this.buttons.length - 1; i >= 0; --i) {
                    if (offsetX > this.buttons[i]) {
                        if (offsetX < this.buttons[i] + BUTTON_WIDTH && offsetY > 1 && offsetY < 9) {
                            index = 2 + i;
                        }
                        break;
                    }
                }
            }
            return index;
        }
    }

    /**
     * A widget that displays nothing.
     * It can also be used as a placeholder for large widgets.
     */
    public final class EmptyPropertyWidget extends AbstractPropertyWidget {
        private final AbstractPropertyWidget father;

        public EmptyPropertyWidget(int colSlots, int rowSlots) {
            this(colSlots, rowSlots, null);
        }

        public EmptyPropertyWidget(int colSlots, int rowSlots, AbstractPropertyWidget father) {
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
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {}

        public AbstractPropertyWidget getFather() {
            return this.father;
        }
    }

    /**
     * A widget that displays text in the center.
     */
    public class CenteredTextPropertyWidget extends AbstractPropertyWidget {
        private static final int DEFAULT_LINE_HEIGHT = 8;
        private Text text;
        private int color;
        private float size;

        public CenteredTextPropertyWidget(int colSlots, int rowSlots, Text text, int color, float size) {
            super(colSlots, rowSlots);
            setText(text); setColor(color); setSize(size);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawText(matrices, text, color, size,
                    x + (this.box.width() - (int) (textRenderer.getWidth(text) * this.size) >> 1),
                    y + (this.box.height() - (int) (DEFAULT_LINE_HEIGHT * this.size) >> 1));
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

    public class LeftTextPropertyWidget extends AbstractPropertyWidget {
        private static final int DEFAULT_LINE_HEIGHT = 8;
        private Text text;
        private int color;
        private float size;

        public LeftTextPropertyWidget(int colSlots, int rowSlots, Text text, int color, float size) {
            super(colSlots, rowSlots);
            setText(text); setColor(color); setSize(size);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawText(matrices, text, color, size,
                    x + 2,
                    y + (this.box.height() - (int) (DEFAULT_LINE_HEIGHT * this.size) >> 1));
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
