package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.gui.screen.BoleMerchantEntityScreen;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleWanderingTraderEntityScreen<E extends WanderingTraderEntity, H extends BoleWanderingTraderEntityScreenHandler<E>> extends BoleMerchantEntityScreen<E, H> {
    public BoleWanderingTraderEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazy(new DespawnDelayPropertyWidget());
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

    public class DespawnDelayPropertyWidget extends TemplatePropertyWidget1 {
        public DespawnDelayPropertyWidget() {
            super(2, true, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_WANDERING_TRADER_DESPAWN_DELAY);
            initTooltipDescription(Keys.PROPERTY_WIDGET_WANDERING_TRADER_DESPAWN_DELAY_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_WANDERING_TRADER_DESPAWN_DELAY_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int delay = handler.entityDespawnDelay, maxDelay = 48000;
            drawIcon(matrices, 0, 130);
            drawBar(matrices, 1.0F, 10, 130);
            drawBar(matrices, delay / (float) maxDelay, 50, 130);
            drawButton(matrices, 0, handler.addDespawnDelayCost);
            if (debugMode) drawBarText(matrices, delay + "t/" + maxDelay + "t", LIGHT_TEXT_COLOR);
            else if (delay == 0) drawBarText(matrices, "∞/" + (maxDelay / (60 * 20)) + "min", LIGHT_TEXT_COLOR);
            else if (delay < 3 * 60 * 20) drawBarText(matrices, (delay / 20) + "s/" + (maxDelay / (60 * 20)) + "min", LIGHT_TEXT_COLOR);
            else drawBarText(matrices, (delay / (60 * 20)) + "min/" + (maxDelay / (60 * 20)) + "min", LIGHT_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            if (!handler.trySpendBuckets(handler.addDespawnDelayCost)) {
                showOverlayMessage(Keys.HINT_TEXT_NOT_ENOUGH_ITEMS);
            }
            else handler.sendClientEntitySettings(Keys.ENTITY_SETTING_ADD_WANDERING_TIME);
            return true;
        }
    }
}
