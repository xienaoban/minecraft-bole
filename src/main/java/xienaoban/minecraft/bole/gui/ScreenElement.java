package xienaoban.minecraft.bole.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreen;

public abstract class ScreenElement implements Element {
    protected final ElementBox box;

    public ScreenElement(int elementWidth, int elementHeight) {
        this.box = new ElementBox(elementWidth, elementHeight);
    }

    public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        this.box.position(x, y, true);
    }

    public void focus() {
        focus(this);
    }

    public void unfocus() {
        focus(null);
    }

    private void focus(@Nullable ScreenElement element) {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (!(currentScreen instanceof AbstractBoleScreen)) {
            return;
        }
        currentScreen.setFocused(element);
    }
}