package xienaoban.minecraft.bole.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleEntityScreen<E extends Entity, H extends BoleEntityScreenHandler<E>> extends AbstractBoleScreen<E, H> {
    protected int entityDisplayPlan;

    public BoleEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        drawEntityAuto(this.handler.entity, 26, 8, CONTENT_WIDTH - 26, (CONTENT_HEIGHT >> 1) + 12, mouseX + 0.001F, mouseY + 0.001F);
        Box box = this.handler.entity.getBoundingBox();
        Text boxText = new TranslatableText(Keys.TEXT_BOUNDING_BOX).append(": " + String.format("%.1f", box.getXLength()) + ", " + String.format("%.1f", box.getYLength()) + ", " + String.format("%.1f", box.getZLength()));
        drawText(matrices, boxText, 0xff444444, CONTENT_WIDTH - this.textRenderer.getWidth(boxText) >> 1, CONTENT_HEIGHT - (CONTENT_HEIGHT >> 2));
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        Text unsupported = new TranslatableText(Keys.TEXT_UNSUPPORTED_ENTITY);
        drawText(matrices, unsupported, 0xaa666666, CONTENT_WIDTH - this.textRenderer.getWidth(unsupported) >> 1, CONTENT_HEIGHT >> 1);
    }

    protected int chooseEntityDisplayPlan(ContentWidgets widgets) {
        Box box = this.handler.entity.getVisibilityBoundingBox();
        double x = box.getXLength(), y = box.getYLength();
        double area = x * y, ratio = y / x;
        int plan;
        if (ratio < 1 / 2.2) {
            if (area < 0.4) plan = 1;       // small
            else plan = 3;                  // flat
        }
        else {
            if (area < 0.5) plan = 1;       // small
            else if (ratio > 2.5) plan = 4; // tall
            else plan = 2;                  // median
        }
        int left, top, width, height;
        switch (plan) {
            case 1: left = 1; top = 0; width = 1; height = 4; break;
            case 2: left = 0; top = 0; width = 1; height = 5; break;
            case 3: left = 0; top = 3; width = 2; height = 2; break;
            case 4: left = 0; top = 0; width = 1; height = 8; break;
            default: left = 0; top = 0; width = 2; height = 6; break;
        }
        widgets.setSlot(new DisplayedEntityContentWidget(height, width, this.handler.entity), top, left);
        return plan;
    }

    public class BoundingBoxContentWidget extends AbstractContentWidget {

        public BoundingBoxContentWidget() {
            super(1, 1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            E entity = handler.entity;
            Box box = entity.getBoundingBox();
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 90, 0);
            if (box.getXLength() == box.getZLength()) {
                drawText(matrices, String.format("X/Z:%.2f Y:%.2f", box.getXLength(), box.getYLength()), CONTENT_TEXT_COLOR, 0.5F, x + 12, y + 3);
            } else {
                drawText(matrices, String.format("X:%.2f Y:%.2f", box.getXLength(), box.getYLength()), CONTENT_TEXT_COLOR, 0.5F, x + 12, y + 1);
                drawText(matrices, String.format("Z:%.2f", box.getZLength()), CONTENT_TEXT_COLOR, 0.5F, x + 12, y + 5);
            }
        }
    }

    public class NetherPortalCooldownContentWidget extends AbstractContentWidget {
        public NetherPortalCooldownContentWidget() {
            super(1, 1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 100, 0);
            drawTextureNormally(matrices, 256, 256, 40, 10, getZOffset(), x + 11, y, 110, 0);
            drawTextureNormally(matrices, 256, 256, 40.0F * ((IMixinEntity)handler.entity).getNetherPortalCooldown() / handler.entity.getDefaultNetherPortalCooldown(), 10, getZOffset(), x + 11, y, 150, 0);
            String text;
            if (debugMode) {
                text = ((IMixinEntity)handler.entity).getNetherPortalCooldown() + "ticks";
            }
            else {
                text = (((IMixinEntity)handler.entity).getNetherPortalCooldown() / 20) + "s";
            }
            drawText(matrices, text, 0xbbffffff, 0.5F, x + 13, y + 3.25F);
        }
    }
}
