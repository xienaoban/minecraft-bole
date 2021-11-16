package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleLivingEntityScreen<E extends LivingEntity, H extends BoleLivingEntityScreenHandler<E>> extends BoleEntityScreen<E, H> {
    public BoleLivingEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new HealthPropertyWidget(), null);
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    /**
     * A widget that displays the health (and the max default health) of the target entity. <br/>
     * Each red bar represents 20 blood, and the golden bar represents more than 20 blood.
     */
    public class HealthPropertyWidget extends AbstractPropertyWidget {
        private final int[][] barCache;
        private final int lineCnt;

        public HealthPropertyWidget() {
            super(2, 1);
            this.barCache = new int[3][2];
            int mh = (int) handler.entity.getMaxHealth();
            this.lineCnt = (int) Math.min(Math.ceil(mh / 20.0), 3);
            calBars(0, mh);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_HEALTH);
            initTooltipDescription(Keys.PROPERTY_WIDGET_HEALTH_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            LivingEntity entity = handler.entity;
            int health = (int)entity.getHealth();
            int maxHealth = (int)entity.getMaxHealth();
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 0, this.lineCnt > 2 ? 10 : 0);
            calBars(1, health);
            float textOffset = 2.25F + this.lineCnt;
            int barOffset = -1 + this.lineCnt;
            if (maxHealth <= 8) {
                drawCustomLengthHealthBar(matrices, 8, x + 11, y, 10, 0, true);
                drawCustomLengthHealthBar(matrices, 8 * health / maxHealth, x + 11, y, 50, 0, health == maxHealth);
            }
            else if (maxHealth <= 60) {
                for (int i = this.lineCnt - 1; i >= 0; --i) {
                    drawCustomLengthHealthBar(matrices, this.barCache[i][0], x + 11, y + barOffset - (i << 1), 10, 0, true);
                    drawCustomLengthHealthBar(matrices, this.barCache[i][1], x + 11, y + barOffset - (i << 1), 50, 0, health >= maxHealth);
                }
            }
            else {
                drawCustomLengthHealthBar(matrices, 20, x + 11, y + barOffset - 4, 10, 10, true);
                drawCustomLengthHealthBar(matrices, 20 * this.barCache[2][1] / this.barCache[2][0], x + 11, y + barOffset - 4, 50, 10, health == maxHealth);
                for (int i = 1; i >= 0; --i) {
                    drawCustomLengthHealthBar(matrices, this.barCache[i][0], x + 11, y + barOffset - (i << 1), 10, 0, true);
                    drawCustomLengthHealthBar(matrices, this.barCache[i][1], x + 11, y + barOffset - (i << 1), 50, 0, health >= maxHealth);
                }
            }
            drawText(matrices, maxHealth + "/" + health, 0xbbffffff, 0.5F, x + 13, y + textOffset);
        }

        /**
         * @param health health should not be greater than 20
         */
        protected void drawCustomLengthHealthBar(MatrixStack matrices, int health, int x, int y, int u, int v, boolean close) {
            int w = health << 1;
            drawTextureNormally(matrices, 256, 256, w, 10, getZOffset(), x, y, u, v);
            if (close && health < 20) {
                drawTextureNormally(matrices, 256, 256, 1, 10, getZOffset(), x + w, y, u + 39, v);
            }
        }

        private void calBars(int pos, int health) {
            for (int i = 0; i < 3; ++i) {
                int v = Math.min(20, health);
                this.barCache[i][pos] = v;
                health -= v;
            }
            if (health > 0) {
                this.barCache[2][pos] += health;
            }
        }
    }
}
