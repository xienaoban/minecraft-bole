package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import xienaoban.minecraft.bole.mixin.MixinEntity;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.Textures;

@Environment(EnvType.CLIENT)
public class BoleEntityScreen<E extends Entity, H extends BoleEntityScreenHandler<E>> extends AbstractBoleScreen<E, H> {
    public BoleEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        drawEntityAuto(this.displayedEntity, 26, 8, CONTENT_WIDTH - 26, (CONTENT_HEIGHT >> 1) + 12, mouseX + 0.001F, mouseY + 0.001F);
        Box box = this.handler.entity.getBoundingBox();
        Text boxText = new TranslatableText(Keys.TEXT_BOUNDING_BOX).append(": " + String.format("%.1f", box.getXLength()) + ", " + String.format("%.1f", box.getYLength()) + ", " + String.format("%.1f", box.getZLength()));
        drawText(matrices, boxText, 0xff444444, CONTENT_WIDTH - this.textRenderer.getWidth(boxText) >> 1, CONTENT_HEIGHT - (CONTENT_HEIGHT >> 2));
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        Text unsupported = new TranslatableText(Keys.TEXT_UNSUPPORTED_ENTITY);
        drawText(matrices, unsupported, 0xaa666666, CONTENT_WIDTH - this.textRenderer.getWidth(unsupported) >> 1, CONTENT_HEIGHT >> 1);
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
            drawText(matrices, ((MixinEntity)handler.entity).getNetherPortalCooldown() + "ms", CONTENT_TEXT_COLOR, 0.5F, x + 12, y + 3);
        }
    }
}
