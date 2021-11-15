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
        this.pages.get(0).addSlotLazy(new AttractiveFoodContentWidget());
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

    public class AttractiveFoodContentWidget extends TemplateContentWidget1 {
        public AttractiveFoodContentWidget() {
            super(2, false, 0);
        }

        protected AttractiveFoodContentWidget(int colSlots, boolean hasBar, int buttonCnt) {
            super(colSlots, hasBar, buttonCnt);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(110, 0);
            if (handler.entityAttractiveFood == null) {
                drawBarText(new TranslatableText(Keys.TEXT_LOADING), CONTENT_TEXT_COLOR);
            }
            else if (handler.entityAttractiveFood.length > 0) {
                drawItems(matrices, x, y, handler.entityAttractiveFood);
            }
            else {
                drawBarText(new TranslatableText(Keys.TEXT_EMPTY_WITH_BRACKETS), CONTENT_TEXT_COLOR);
            }
        }

        protected void drawItems(MatrixStack matrices, int x, int y, Item[] items) {
            float w = Math.min(9.0F, (this.box.width() - 20.0F) / Math.max(1, items.length - 1));
            for (int i = items.length - 1; i >= 0; --i) {
                Item item = items[i];
                final float size = 8.0F / 16.0F;
                setTexture(Textures.ICONS);
                drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x + i * w + 10, y, 220, 10);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(size, size, size);
                itemRenderer.renderInGuiWithOverrides(new ItemStack(item), (int)((x + i * w + 11) / size), (int)((y + 1) / size));
                RenderSystem.popMatrix();
            }
        }
    }
}
