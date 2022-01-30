package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleMerchantEntityScreen<E extends MerchantEntity, H extends BoleMerchantEntityScreenHandler<E>> extends BolePassiveEntityScreen<E, H> {
    public BoleMerchantEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyBefore(new MerchantInventoryPropertyWidget(), BoundingBoxPropertyWidget.class);
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

    public class MerchantInventoryPropertyWidget extends AbstractPropertyWidget {

        public MerchantInventoryPropertyWidget() {
            super(2, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_MERCHANT_INVENTORY);
            initTooltipDescription(Keys.PROPERTY_WIDGET_MERCHANT_INVENTORY_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 0, 90);
            drawTextureNormally(matrices, 256, 256, 40, 20, getZOffset(), x + 11, y + 1, 10, 90);
            SimpleInventory inventory = handler.entityInventory;
            if (inventory == null) {
                drawText(matrices, new TranslatableText(Keys.TEXT_LOADING), DARK_TEXT_COLOR, 0.5F, this.box.left() + 13, this.box.top() + 3.25F);
                return;
            }
            final float size = 8.0F / 16.0F;
            MatrixStack matrixStack = matrixScaleOn(size, size, size);
            for (int i = 0; i < inventory.size(); ++i) {
                int xx = i % 4, yy = i / 4;
                int px = (int) ((x + 12 + xx * 10) / size), py = (int) ((y + 2 + yy * 10) / size);
                itemRenderer.renderInGui(inventory.getStack(i), px, py);
                itemRenderer.renderGuiItemOverlay(textRenderer, inventory.getStack(i), px, py);
            }
            matrixScaleOff(matrixStack);
        }
    }
}
