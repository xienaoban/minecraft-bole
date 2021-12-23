package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleTameableEntityScreen<E extends TameableEntity, H extends BoleTameableEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleTameableEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new TamePropertyWidget(), LeashPropertyWidget.class);
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

    public class TamePropertyWidget extends TemplatePropertyWidget1 {

        public TamePropertyWidget() {
            super(1, true, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_TAME);
            initTooltipDescription(Keys.PROPERTY_WIDGET_TAME_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 150, 10);
            drawBar(matrices, 1.0F, 220 + (handler.entity.isTamed() ? 0 : 10), 20);
        }
    }
}
