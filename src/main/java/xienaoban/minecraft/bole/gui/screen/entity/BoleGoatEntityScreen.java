package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.gui.screen.tree.BoleMobEntityScreen;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleGoatEntityScreen<E extends GoatEntity, H extends BoleGoatEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleGoatEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new ScreamingPropertyWidget(), BoleMobEntityScreen.LeashPropertyWidget.class);
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

    public class ScreamingPropertyWidget extends TemplatePropertyWidget1 {

        public ScreamingPropertyWidget() {
            super(1, true, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_SCREAMING_GOAT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_SCREAMING_GOAT_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 120, 10);
            drawBar(matrices, 1.0F, 220 + (handler.entity.isScreaming() ? 0 : 10), 20);
        }
    }
}
