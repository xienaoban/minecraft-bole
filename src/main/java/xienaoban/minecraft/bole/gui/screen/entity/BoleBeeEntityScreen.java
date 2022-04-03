package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleBeeEntityScreen<E extends BeeEntity, H extends BoleBeeEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleBeeEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazy(new BeehivePropertyWidget());
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

    public class BeehivePropertyWidget extends TemplatePropertyWidget1 {
        private static final Text NO_HIVE = new LiteralText(" - ");
        private int nextTicks;
        private Text cacheDistance;

        public BeehivePropertyWidget() {
            super(2, true, 2);
            this.nextTicks = -123456;
            this.cacheDistance = NO_HIVE;
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_BEEHIVE);
            initTooltipDescription(Keys.PROPERTY_WIDGET_BEEHIVE_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_BEEHIVE_DESCRIPTION_BUTTON1);
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_BEEHIVE_DESCRIPTION_BUTTON2);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            BlockPos pos = handler.entityBeehivePosition;
            drawIcon(matrices, 180, 0);
            drawButton(matrices, 0, 230, 30 - (pos != null ? 0 : 20));
            drawButton(matrices, 1, 240, 20);
            int cutTicks = BoleClient.getInstance().getScreenTicks();
            if (cutTicks > this.nextTicks) {
                this.nextTicks = cutTicks + 10;
                if (pos != null) {
                    double dis = pos.getSquaredDistance(handler.entity.getPos());
                    this.cacheDistance = new LiteralText(String.format("%.2fm", Math.sqrt(dis)));
                }
                else this.cacheDistance = NO_HIVE;
            }
            drawBarText(matrices, this.cacheDistance, DARK_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index < IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            switch (index) {
                case IDX_BUTTON_BEGIN -> {
                    BlockPos pos = handler.entityBeehivePosition;
                    if (pos != null) {
                        HighlightManager hl = BoleClient.getInstance().getHighlightManager();
                        GlobalPos globalPos = GlobalPos.create(handler.entity.world.getRegistryKey(), pos);
                        hl.setHighlightedJobSiteOrBeehive(hl.highlight(globalPos, 6 * 20));
                        close();
                    }
                    else {
                        showOverlayMessage(Keys.HINT_TEXT_NO_BEEHIVE);
                    }
                }
                case IDX_BUTTON_BEGIN + 1 -> {
                    BlockPos pos = handler.entityBeehivePosition;
                    if (pos != null) {
                        handler.sendClientEntitySettings(Keys.ENTITY_SETTING_RESET_BEEHIVE);
                        handler.entityBeehivePosition = null;
                        this.cacheDistance = NO_HIVE;
                    }
                    else {
                        showOverlayMessage(Keys.HINT_TEXT_NO_BEEHIVE);
                    }
                }
            }
            return true;
        }
    }
}
