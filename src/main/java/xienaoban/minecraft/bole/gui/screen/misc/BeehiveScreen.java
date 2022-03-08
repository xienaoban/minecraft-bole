package xienaoban.minecraft.bole.gui.screen.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.Textures;

public class BeehiveScreen extends HandledScreen<BeehiveScreenHandler> {
    private static final int[][] LATTICES = {{0, 0}, {32, 0}, {0, 50}, {32, 50}, {16, 25}, {-16, 25}, {48, 25}};

    public BeehiveScreen(BeehiveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, Textures.BEEHIVE);
        int w = (this.width - 128) >> 1;
        int h = (this.height - 128) >> 1;
        int lw = w + 32, lh = h + 23;
        for (int i = 0; i < 5; ++i) {
            drawLattice(matrices, LATTICES[i][0] + lw, LATTICES[i][1] + lh, Math.random() > 0.5 ? 2 : 0);
        }
        drawLattice(matrices, LATTICES[5][0] + lw, LATTICES[5][1] + lh, 1);
        drawLattice(matrices, LATTICES[6][0] + lw, LATTICES[6][1] + lh, 1);
        drawTexture(matrices, w, h, 0, 0, 128, 128);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    private void drawLattice(MatrixStack matrices, int w, int h, int type) {
        drawTexture(matrices, w, h, 16 + type * 32, 140, 32, 32);
    }
}
