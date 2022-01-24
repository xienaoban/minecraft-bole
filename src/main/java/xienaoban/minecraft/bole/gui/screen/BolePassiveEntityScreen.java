package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BolePassiveEntityScreen<E extends PassiveEntity, H extends BolePassiveEntityScreenHandler<E>> extends BolePathAwareEntityScreen<E, H> {
    public BolePassiveEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new BabyPropertyWidget(), NetherPortalCooldownPropertyWidget.class);
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

    /**
     * A widget that displays the child's growth progress.
     * You can set the child to never grow up.
     */
    public class BabyPropertyWidget extends TemplatePropertyWidget1 {
        public static final int BABY_MIN_AGE = -24000;
        public static final int LOCK = -0x70000000; // Do not use 0x80000000, as it may overflow into positive.

        public BabyPropertyWidget() {
            super(2, true, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_BABY);
            initTooltipDescription(Keys.PROPERTY_WIDGET_BABY_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_BABY_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int age = handler.entityBreedingAge;
            drawIcon(matrices, 0, 60);
            drawBar(matrices, 1.0F, 10, 60);
            drawBar(matrices, 1.0F * (age - BABY_MIN_AGE) / (-BABY_MIN_AGE), 50, 60);
            drawButton(matrices, 0, 200 + (age < BABY_MIN_AGE ? 10 : 0), 0);
            if (debugMode) {
                drawBarText(matrices, age + "t", LIGHT_TEXT_COLOR);
            }
            else if (age >= 0) {
                drawBarText(matrices, new TranslatableText(Keys.TEXT_GROWN_UP), LIGHT_TEXT_COLOR);
            }
            else if (age < BABY_MIN_AGE) {
                drawBarText(matrices, new TranslatableText(Keys.TEXT_NEVER_GROW_UP), LIGHT_TEXT_COLOR);
            }
            else {
                drawBarText(matrices, String.format("%.2f%%", 100.0F - 100.0F * age / BABY_MIN_AGE), LIGHT_TEXT_COLOR);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            int age = handler.entityBreedingAge;
            if (age >= 0) {
                showOverlayMessage(Keys.HINT_TEXT_NOT_BABY);
                return true;
            }
            if (age >= BABY_MIN_AGE) age = LOCK;
            else age = BABY_MIN_AGE;
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_BABY, age);
            return true;
        }
    }
}
