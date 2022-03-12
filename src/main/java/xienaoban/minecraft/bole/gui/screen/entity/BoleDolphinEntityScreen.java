package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.screen.tree.BoleWaterCreatureEntityScreen;
import xienaoban.minecraft.bole.mixin.IMixinDolphinEntity;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleDolphinEntityScreen<E extends DolphinEntity, H extends BoleDolphinEntityScreenHandler<E>> extends BoleWaterCreatureEntityScreen<E, H> {
    public BoleDolphinEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new MoistnessPropertyWidget(), AirPropertyWidget.class);
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

    public class MoistnessPropertyWidget extends TemplatePropertyWidget1 {
        public MoistnessPropertyWidget() {
            super(2, true, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_MOISTNESS);
            initTooltipDescription(Keys.PROPERTY_WIDGET_MOISTNESS_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int moistness = handler.entity.getMoistness();
            int maxMoistness = IMixinDolphinEntity.getMaxMoistness();
            drawIcon(matrices, 0, 140);
            drawBar(matrices, 1.0F, 10, 20);
            drawBar(matrices, moistness / (float) maxMoistness, 50, 20);
            String text;
            if (debugMode) text = moistness + "t/" + maxMoistness + "t";
            else text = (moistness / 20) + "s/" + (maxMoistness / 20) + "s";
            drawBarText(matrices, text, LIGHT_TEXT_COLOR);
        }
    }
}
