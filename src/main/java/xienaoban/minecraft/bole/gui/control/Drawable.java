package xienaoban.minecraft.bole.gui.control;

import net.minecraft.client.util.math.MatrixStack;

public interface Drawable {
    void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY);
}
