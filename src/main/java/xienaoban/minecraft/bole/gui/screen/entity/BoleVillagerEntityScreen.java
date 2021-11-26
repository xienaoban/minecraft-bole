package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.gui.screen.BoleMerchantEntityScreen;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleVillagerEntityScreen<E extends VillagerEntity, H extends BoleVillagerEntityScreenHandler<E>> extends BoleMerchantEntityScreen<E, H> {
    public BoleVillagerEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazy(new RestockPropertyWidget());
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

    public class RestockPropertyWidget extends TemplatePropertyWidget1 {

        public RestockPropertyWidget() {
            super(2, true, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK);
            initTooltipDescription(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int restocksToday = handler.entityRestocksToday;
            drawIcon(matrices, 0, 110);
            drawBar(matrices, 10, 110, 1.0F);
            drawBar(matrices, 50, 110, restocksToday / 3.0F);
            drawButton(matrices, 230, 10, 0);
            drawBarText(matrices, restocksToday + "/3", LIGHT_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_RESTOCK);
            return true;
        }
    }
}
