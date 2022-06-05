package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.client.PlayerDataCacheManager;
import xienaoban.minecraft.bole.util.Keys;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class BoleAnimalEntityScreen<E extends AnimalEntity, H extends BoleAnimalEntityScreenHandler<E>> extends BolePassiveEntityScreen<E, H> {
    public BoleAnimalEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new BreedingItemsPropertyWidget(), AttractiveItemsPropertyWidget.class);
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

    public class BreedingItemsPropertyWidget extends AttractiveItemsPropertyWidget {
        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_BREEDING_ITEMS);
            initTooltipDescription(Keys.PROPERTY_WIDGET_BREEDING_ITEMS_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 120, 0);
            Item[] items = handler.entityBreedingItems;
            if (items.length == 0) {
                drawBarText(matrices, Text.translatable(Keys.TEXT_EMPTY_WITH_BRACKETS), DARK_TEXT_COLOR);
            }
            else {
                drawItems(matrices, items);
            }
        }
    }


    public abstract class AbstractTamePropertyWidget extends TemplatePropertyWidget1 {
        public AbstractTamePropertyWidget() {
            super(2, true, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_TAME);
            initTooltipDescription(Keys.PROPERTY_WIDGET_TAME_DESCRIPTION);
            initTooltipDescription(Keys.PROPERTY_WIDGET_DESCRIPTION_GET_NAME_BY_UUID);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_TAME_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 0, 150);
            drawBar(matrices, 1, 10, 150);
            drawButton(matrices, 0, 240, 10);
            boolean isTame = isTame();
            UUID uuid = getOwnerUuid();
            int color;
            Text barText;
            if (isTame && uuid != null) {
                barText = PlayerDataCacheManager.getInstance().getPlayerName(uuid);
                color = PlayerDataCacheManager.isNoPlayerData(barText) ? 0xCCca64ea : 0xCC9332bf;
            }
            else {
                barText = Text.translatable(Keys.TEXT_NOT_TAMED);
                color = 0xCCca64ea;
            }
            drawBarText(matrices, barText, color);
        }

        protected abstract boolean isTame();
        protected abstract UUID getOwnerUuid();
    }
}
