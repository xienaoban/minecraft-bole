package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleAbstractDonkeyEntityScreen<E extends AbstractDonkeyEntity, H extends BoleAbstractDonkeyEntityScreenHandler<E>> extends BoleHorseBaseEntityScreen<E, H> {
    public BoleAbstractDonkeyEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new DonkeyChestPropertyWidget(), HorseRunAndJumpPropertyWidget.class);
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

    public class DonkeyChestPropertyWidget extends TemplatePropertyWidget1 {

        public DonkeyChestPropertyWidget() {
            super(1, false, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_DONKEY_CHEST);
            initTooltipDescription(Keys.PROPERTY_WIDGET_DONKEY_CHEST_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 140, 0);
            drawBarText(matrices, String.valueOf(handler.entity.getInventoryColumns() * 3), DARK_TEXT_COLOR);
        }
    }
}
