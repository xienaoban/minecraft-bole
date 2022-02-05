package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.mixin.IMixinTameableShoulderEntity;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleTameableShoulderEntityScreen<E extends TameableShoulderEntity, H extends BoleTameableShoulderEntityScreenHandler<E>> extends BoleTameableEntityScreen<E, H> {
    public BoleTameableShoulderEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazy(new SitOnPlayerCooldownPropertyWidget());
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

    public class SitOnPlayerCooldownPropertyWidget extends TemplatePropertyWidget1 {

        public SitOnPlayerCooldownPropertyWidget() {
            super(2, true, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_SIT_ON_PLAYER_COOLDOWN);
            initTooltipDescription(Keys.PROPERTY_WIDGET_SIT_ON_PLAYER_COOLDOWN_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_SIT_ON_PLAYER_COOLDOWN_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int cooldownTicks = ((IMixinTameableShoulderEntity)handler.entity).getTicks();
            int cooldown = Math.max(0, 100 - cooldownTicks);
            boolean lock = isLocked();
            float p = Math.min(1.0F, (float)(100 - cooldownTicks) / IMixinTameableShoulderEntity.getReadyToSitCooldown());
            drawIcon(matrices, 0, 120);
            drawBar(matrices, 1.0F, 10, 120);
            drawBar(matrices, p, 50, 120);
            drawButton(matrices, 0, 200 + (lock ? 10 : 0), 0);
            String text;
            if (debugMode) text = cooldownTicks + "t";
            else if (lock) text = "∞";
            else text = (cooldown / 20) + "s";
            drawBarText(matrices, text, LIGHT_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            int ticks;
            if (isLocked()) ticks = 123;
            else ticks = BoleTameableShoulderEntityScreenHandler.SIT_ON_PLAYER_LOCK;
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_SIT_ON_PLAYER_COOLDOWN, ticks);
            return true;
        }

        private boolean isLocked() {
            return ((IMixinTameableShoulderEntity)handler.entity).getTicks() <= BoleTameableShoulderEntityScreenHandler.SIT_ON_PLAYER_LOCK;
        }
    }
}
