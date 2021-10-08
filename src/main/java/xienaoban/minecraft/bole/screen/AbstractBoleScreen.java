package xienaoban.minecraft.bole.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.util.Drawer;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoleScreen<E extends Entity, T extends AbstractBoleScreenHandler<E>> extends HandledScreen<T> {
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    private static final int BOOK_TEXTURE_CUT = 29;

    protected int bodyLeft, bodyRight, bodyTop, bodyBottom, bodyWidth, bodyHeight;

    public AbstractBoleScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
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
        this.bodyWidth = (192 - BOOK_TEXTURE_CUT) * 2;
        this.bodyHeight = 192;
        this.bodyLeft = (this.width - this.bodyWidth) / 2;
        this.bodyRight = this.bodyLeft + this.bodyWidth;
        this.bodyTop = this.height / 2 - this.bodyHeight / 2 + 10;
        this.bodyBottom = this.bodyTop + this.bodyHeight;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BOOK_TEXTURE);
        int x0 = this.bodyLeft, x1 = this.bodyRight, y0 = this.bodyTop, y1 = this.bodyBottom;
        int u0 = BOOK_TEXTURE_CUT, u1 = u0 + this.bodyWidth / 2, v0 = 0, v1 = v0 + this.bodyHeight;
        Drawer.drawTextureFlippedHorizontally(matrices, 256, 256, this.getZOffset(),
                x0, y0, this.width >> 1, y1, u0, v0, u1, v1);
        Drawer.drawTexture(matrices, 256, 256, this.getZOffset(),
                this.width >> 1, y0, x1, y1, u0, v0, u1, v1);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, this.titleX, this.titleY, 4210752);
    }
}
