package xienaoban.minecraft.bole.gui;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public abstract class ScreenElement implements Element {
    protected final ElementBox box;

    public ScreenElement() {
        this.box = new ElementBox();
    }

    public ScreenElement(int elementWidth, int elementHeight) {
        this.box = new ElementBox(elementWidth, elementHeight);
    }

    public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.box.position(x, y, true);
    }
}
