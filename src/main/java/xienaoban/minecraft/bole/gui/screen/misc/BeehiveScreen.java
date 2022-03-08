package xienaoban.minecraft.bole.gui.screen.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.client.KeyBindingManager;
import xienaoban.minecraft.bole.gui.Textures;

public class BeehiveScreen extends HandledScreen<BeehiveScreenHandler> {
    private static final int[][] LATTICES = {{0, 0}, {32, 0}, {0, 50}, {32, 50}, {16, 25}, {-16, 25}, {48, 25}};
    private static final int MAX_HONEY_CNT = BeehiveBlock.FULL_HONEY_LEVEL;
    private static final int MAX_BEE_CNT = BeehiveBlockEntity.MAX_BEE_COUNT;

    private final BeeEntity[] bees;

    public BeehiveScreen(BeehiveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.bees = new BeeEntity[MAX_BEE_CNT];
        for (int i = 0; i < MAX_BEE_CNT; ++i) {
            this.bees[i] = EntityType.BEE.create(inventory.player.getWorld());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindingManager.KEY_BOLE_SCREEN.matchesKey(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, Textures.BEEHIVE);
        int w = (this.width - 128) >> 1;
        int h = (this.height - 128) >> 1;

        int beeCnt = this.handler.entity.getBeeCount();
        int honeyCnt = BeehiveBlockEntity.getHoneyLevel(this.handler.blockState);
        beeCnt = 2;
        honeyCnt = 2;

        int lw = w + 32, lh = h + 23;
        for (int i = 0; i < MAX_HONEY_CNT; ++i) {
            drawLattice(matrices, LATTICES[i][0] + lw, LATTICES[i][1] + lh, i < honeyCnt ? 2 : 0);
        }
        drawLattice(matrices, LATTICES[5][0] + lw, LATTICES[5][1] + lh, 1);
        drawLattice(matrices, LATTICES[6][0] + lw, LATTICES[6][1] + lh, 1);
        drawTexture(matrices, w, h, 0, 0, 128, 128);
        for (int i = 0; i < beeCnt; ++i) {
            int x = LATTICES[MAX_HONEY_CNT - i - 1][0] + lw + 16;
            int y = LATTICES[MAX_HONEY_CNT - i - 1][1] + lh + 30;
            InventoryScreen.drawEntity(x, y, 34, 0.0F - (mouseX - (this.width >> 1)) / 16.0F, -16.0F - (mouseY - (this.height >> 1)) / 16.0F, this.bees[i]);
        }
        this.textRenderer.draw(matrices, beeCnt + "/" + MAX_BEE_CNT, LATTICES[5][0] + lw + 16 - 8.5F, LATTICES[5][1] + lh + 8, 0xAAFFFFFF);
        this.textRenderer.draw(matrices, honeyCnt + "/" + MAX_HONEY_CNT, LATTICES[6][0] + lw + 16 - 8.5F, LATTICES[6][1] + lh + 8, 0xAAFFFFFF);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    private void drawLattice(MatrixStack matrices, int w, int h, int type) {
        drawTexture(matrices, w, h, 16 + type * 32, 140, 32, 32);
    }
}
