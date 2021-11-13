package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleAnimalEntityScreen<E extends AnimalEntity, H extends BoleAnimalEntityScreenHandler<E>> extends BolePassiveEntityScreen<E, H> {
    public BoleAnimalEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initCustom() {
        super.initCustom();
        this.pages.get(0).addSlot(new BreedingItemsContentWidget());
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    public class BreedingItemsContentWidget extends AttractiveFoodContentWidget {
        public BreedingItemsContentWidget() {
            super(2, false, 0);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(120, 0);
            Item[] items = handler.getBreedingItems();
            if (items.length == 0) {
                drawBarText(new TranslatableText(Keys.TEXT_EMPTY_WITH_BRACKETS), CONTENT_TEXT_COLOR);
            }
            else {
                drawItems(matrices, x, y, items);
            }
        }
    }
}
