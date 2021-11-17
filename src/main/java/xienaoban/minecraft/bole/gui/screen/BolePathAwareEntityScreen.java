package xienaoban.minecraft.bole.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BolePathAwareEntityScreen<E extends PathAwareEntity, H extends BolePathAwareEntityScreenHandler<E>> extends BoleMobEntityScreen<E, H> {
    public BolePathAwareEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazy(new AttractiveItemsPropertyWidget());
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

    public class AttractiveItemsPropertyWidget extends TemplatePropertyWidget1 {
        public AttractiveItemsPropertyWidget() {
            super(2, false, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_ATTRACTIVE_ITEMS);
            initTooltipDescription(Keys.PROPERTY_WIDGET_ATTRACTIVE_ITEMS_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(110, 0);
            if (handler.entityAttractiveItems == null) {
                drawBarText(new TranslatableText(Keys.TEXT_LOADING), CONTENT_TEXT_COLOR);
            }
            else if (handler.entityAttractiveItems.length > 0) {
                drawItems(matrices, handler.entityAttractiveItems);
            }
            else {
                drawBarText(new TranslatableText(Keys.TEXT_EMPTY_WITH_BRACKETS), CONTENT_TEXT_COLOR);
            }
        }

        protected void drawItems(MatrixStack matrices, Item[] items) {
            float w = Math.min(9.0F, (this.box.width() - 20.0F) / Math.max(1, items.length - 1));
            for (int i = items.length - 1; i >= 0; --i) {
                Item item = items[i];
                final float size = 8.0F / 16.0F;
                setTexture(Textures.ICONS);
                drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), this.box.left() + i * w + 10, this.box.top(), 220, 10);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(size, size, size);
                itemRenderer.renderInGuiWithOverrides(new ItemStack(item), (int)((this.box.left() + i * w + 11) / size), (int)((this.box.top() + 1) / size));
                RenderSystem.popMatrix();
            }
        }
    }
}
