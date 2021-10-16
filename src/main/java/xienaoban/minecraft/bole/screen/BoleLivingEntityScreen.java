package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.util.Textures;

@Environment(EnvType.CLIENT)
public class BoleLivingEntityScreen<E extends LivingEntity, H extends BoleLivingEntityScreenHandler<E>> extends AbstractBoleScreen<E, H> {
    public BoleLivingEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {}

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {}

    public class HealthContentWidget extends AbstractContentWidget {
        public HealthContentWidget() {
            super(1, 1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            E entity = handler.entity;
            int health = (int)entity.getHealth();
            int maxHealth = (int)entity.getMaxHealth();
            int healthWidth = 40 * health / maxHealth;
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, getZOffset(),
                    x, y, x + 10, y + 10, 0, 0, 10, 10);
            drawTextureNormally(matrices, 256, 256, getZOffset(),
                    x + 11, y, x + 51, y + 10, 10, 0, 50, 10);
            drawTextureNormally(matrices, 256, 256, getZOffset(),
                    x + 11, y, x + 11 + healthWidth, y + 10, 50, 0, 50 + healthWidth, 10);
            textRenderer.draw(matrices, maxHealth + "/" + health, x + 12, y + 1, 0xaaffffff);
        }
    }
}
