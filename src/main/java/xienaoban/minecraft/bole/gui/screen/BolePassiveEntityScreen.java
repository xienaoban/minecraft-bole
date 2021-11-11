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
    protected void initCustom() {
        super.initCustom();
        this.pages.get(1).addSlot(new BabyContentWidget());
    }

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
    public class BabyContentWidget extends TemplateContentWidget1 {
        public static final int BABY_MIN_AGE = -24000;
        public static final int LOCK = -0x70000000; // Do not use 0x80000000, as it may overflow into positive.

        public BabyContentWidget() {
            super(2, true, 1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int age = BolePassiveEntityScreenHandler.getRealBreedingAge(handler.entity);
            drawIcon(0, 60);
            drawBar(10, 60, 1.0F);
            drawBar(50, 60, 1.0F * (age - BABY_MIN_AGE) / (-BABY_MIN_AGE));
            drawButton(200 + (age < BABY_MIN_AGE ? 10 : 0), 0, 0);
            if (debugMode) {
                drawBarText(age + "ticks", 0xbbffffff);
            }
            else if (age >= 0) {
                drawBarText(new TranslatableText(Keys.TEXT_GROWN_UP), 0xbbffffff);
            }
            else if (age < BABY_MIN_AGE) {
                drawBarText(new TranslatableText(Keys.TEXT_NEVER_GROW_UP), 0xbbffffff);
            }
            else {
                drawBarText(String.format("%.2f%%", 100.0F - 100.0F * age / BABY_MIN_AGE), 0xbbffffff);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            int age = BolePassiveEntityScreenHandler.getRealBreedingAge(handler.entity);
            if (age >= 0) {
                return false;
            }
            if (age >= BABY_MIN_AGE) {
                age = LOCK;
            }
            else {
                age = BABY_MIN_AGE;
            }
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_BABY, age);
            return true;
        }
    }
}
