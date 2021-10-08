package xienaoban.minecraft.bole.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class BoleHorseScreen extends HandledScreen<BoleHorseScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/horse.png");
    private final HorseEntity entity;

    public BoleHorseScreen(BoleHorseScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, getTitle(handler).orElse(title));
        entity = handler.getEntity();
    }

    private static Optional<Text> getTitle(BoleHorseScreenHandler handler) {
        HorseEntity entity = handler.getEntity();
        if (entity == null) return Optional.empty();
        return Optional.of(entity.getDisplayName());
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.entity.canBeSaddled()) {
            this.drawTexture(matrices, i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
        }

        if (this.entity.hasArmorSlot()) {
            this.drawTexture(matrices, i + 7, j + 35, 0, this.backgroundHeight + 54, 18, 18);
        }

        // InventoryScreen.drawEntity(i + 51, j + 60, 17, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.entity);
    }
}
