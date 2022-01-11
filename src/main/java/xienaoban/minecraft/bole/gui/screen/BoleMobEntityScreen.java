package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleMobEntityScreen<E extends MobEntity, H extends BoleMobEntityScreenHandler<E>> extends BoleLivingEntityScreen<E, H> {
    public BoleMobEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new LeashPropertyWidget(), StatusEffectsPropertyWidget.class);
        this.pages.get(1).addSlotLazyAfter(new HasAiPropertyWidget(), InvulnerablePropertyWidget.class);
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curLeftPage.draw(matrices, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curRightPage.draw(matrices, x, y, mouseX, mouseY);
    }

    public class LeashPropertyWidget extends TemplatePropertyWidget1 {
        private final boolean canBeLeashed;

        public LeashPropertyWidget() {
            super(1, true, 0);
            this.canBeLeashed = handler.entity.canBeLeashedBy(handler.player) || handler.entity.isLeashed();
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_LEASH);
            initTooltipDescription(Keys.PROPERTY_WIDGET_LEASH_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 150, 0);
            drawBar(matrices, 1.0F, 220 + (this.canBeLeashed ? 0 : 10), 20);
        }
    }

    public class HasAiPropertyWidget extends TemplatePropertyWidget1 {
        public HasAiPropertyWidget() {
            super(1, false, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_HAS_AI);
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_HAS_AI_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 110, 10);
            drawButton(matrices, 0, 200 + (handler.entity.isAiDisabled() ? 10 : 0), 40);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            boolean noAi = !handler.entity.isAiDisabled();
            if (isGodMode()) handler.sendClientEntitySettings(Keys.ENTITY_SETTING_NO_AI, noAi);
            else {
                int healthAndSatiety = handler.entity instanceof HostileEntity ? 8 : 2;
                setPopup(new PopUpConfirmWindow(
                        new TranslatableText(noAi ? Keys.WARNING_TEXT_DISABLE_AI : Keys.WARNING_TEXT_ENABLE_AI, healthAndSatiety, healthAndSatiety),
                        () -> handler.sendClientEntitySettings(Keys.ENTITY_SETTING_NO_AI, noAi))
                );
            }
            return true;
        }
    }
}
