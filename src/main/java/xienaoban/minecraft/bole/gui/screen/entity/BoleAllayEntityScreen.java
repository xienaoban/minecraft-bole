package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.client.PlayerDataCacheManager;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.gui.screen.tree.BolePathAwareEntityScreen;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleAllayEntityScreen<E extends AllayEntity, H extends BoleAllayEntityScreenHandler<E>> extends BolePathAwareEntityScreen<E, H> {
    public BoleAllayEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazy(new LikedPropertyWidget());
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

    public class LikedPropertyWidget extends TemplatePropertyWidget1 {
        private Text lastBarTextCache, trimmedBarTextCache;

        public LikedPropertyWidget() {
            super(2, true, 1);
            this.lastBarTextCache = null;
            this.trimmedBarTextCache = null;
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_ALLEY_LIKED);
            initTooltipDescription(Keys.PROPERTY_WIDGET_ALLEY_LIKED_DESCRIPTION);
            initTooltipDescription(Keys.PROPERTY_WIDGET_DESCRIPTION_GET_NAME_BY_UUID);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_ALLEY_LIKED_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 0, 150);
            drawBar(matrices, 1, 10, 150);
            drawButton(matrices, 0, 230, 30 - (handler.likedNoteBlockPosition != null ? 0 : 20));
            Text barText = handler.likedPlayerName;
            int barTextColor = PlayerDataCacheManager.isNoPlayerData(barText) ? 0xCCca64ea : 0xCC9332bf;
            if (barText == null) {
                barText = Text.translatable(Keys.TEXT_EMPTY_WITH_BRACKETS);
            }
            if (this.lastBarTextCache != barText) {
                this.lastBarTextCache = barText;
                final int maxWidth = 2 * (33 - 2 * 2);
                if (textRenderer.getWidth(barText) > maxWidth) {
                    String trimmed = textRenderer.trimToWidth(barText.getString(), maxWidth - 6, false) + "...";
                    barText = Text.literal(trimmed);
                }
                this.trimmedBarTextCache = barText;
            }
            drawBarText(matrices, this.trimmedBarTextCache, barTextColor);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            if (handler.likedNoteBlockPosition == null) {
                showOverlayMessage(Keys.HINT_TEXT_ALLAY_LIKE_NO_NOTEBLOCK);
                return true;
            }
            HighlightManager hl = BoleClient.getInstance().getHighlightManager();
            hl.setHighlightedJobSiteOrBeehive(hl.highlight(handler.likedNoteBlockPosition, 6 * 20));
            close();
            return true;
        }
    }
}
