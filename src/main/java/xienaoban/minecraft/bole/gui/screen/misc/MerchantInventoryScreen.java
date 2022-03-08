package xienaoban.minecraft.bole.gui.screen.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreenHandler;

@Environment(EnvType.CLIENT)
public class MerchantInventoryScreen extends HandledScreen<MerchantInventoryScreenHandler> {
    private final MerchantEntity merchantEntity;
    public MerchantInventoryScreen(MerchantInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.merchantEntity = (MerchantEntity) AbstractBoleScreenHandler.clientEntity();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, Textures.HORSE_SCREEN);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        drawTexture(matrices, i + 79, j + 17 + 18, 0, this.backgroundHeight, 4 * 18, 2 * 18);
        InventoryScreen.drawEntity(i + 52, j + 64, 20, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, this.merchantEntity);
    }
}
