package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleMobEntityScreen<E extends MobEntity, H extends BoleMobEntityScreenHandler<E>> extends BoleLivingEntityScreen<E, H> {
    public BoleMobEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new LeashPropertyWidget(), StatusEffectsPropertyWidget.class);
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curLeftPage.draw(matrices, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curRightPage.draw(matrices, x, y, mouseX, mouseY);
    }

    public class LeashPropertyWidget extends TemplatePropertyWidget1 {

        public LeashPropertyWidget() {
            super(1, true, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_LEASH);
            initTooltipDescription(Keys.PROPERTY_WIDGET_LEASH_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 150, 0);
            drawBar(matrices, 1.0F, 220 + (handler.entity.canBeLeashedBy(handler.player) ? 0 : 10), 20);
        }
    }
}
