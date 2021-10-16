package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import xienaoban.minecraft.bole.util.Keys;

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
        this.textRenderer.draw(matrices, boxText, CONTENT_WIDTH - this.textRenderer.getWidth(boxText) >> 1, CONTENT_HEIGHT - (CONTENT_HEIGHT >> 2), 0xff444444);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        Text unsupported = new TranslatableText(Keys.TEXT_UNSUPPORTED_ENTITY);
        this.textRenderer.draw(matrices, unsupported, CONTENT_WIDTH - this.textRenderer.getWidth(unsupported) >> 1, CONTENT_HEIGHT >> 1, 0xaa666666);
    }


}
