package xienaoban.minecraft.bole.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.util.Drawer;

@Environment(EnvType.CLIENT)
public class BoleHandbookScreen extends HandledScreen<BoleHandbookScreenHandler> {
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");

    public BoleHandbookScreen(BoleHandbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(BOOK_TEXTURE);
        final int w = 192 - 29, h = 192;
        int x0 = this.width / 2, x1 = x0 + w, y0 = this.height / 2 - h / 2 + 10, y1 = y0 + h;
        int u0 = 29, u1 = u0 + w, v0 = 0, v1 = v0 + h;
        Drawer.drawTexture(matrices, 256, 256, this.getZOffset(),
                x0, y0, x1, y1, u0, v0, u1, v1);
        Drawer.drawTextureFlippedHorizontally(matrices, 256, 256, this.getZOffset(),
                x0 - w, y0, x1 - w, y1, u0, v0, u1, v1);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindingManager.KEY_BOLE_SCREEN.matchesKey(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
