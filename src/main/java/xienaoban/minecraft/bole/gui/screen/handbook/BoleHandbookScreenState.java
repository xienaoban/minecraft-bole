package xienaoban.minecraft.bole.gui.screen.handbook;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

/**
 * Jumping between the entity screen and the handbook screen brings some problems.
 * For example, the mouse position will be reset to the center.
 * So some data is stored in this class for smooth jumping.
 */
@Environment(EnvType.CLIENT)
public class BoleHandbookScreenState {
    private final double mouseX, mouseY;
    private final int bookmarkIndex, pageIndex;

    public BoleHandbookScreenState() {
        this(0, 0);
    }

    public BoleHandbookScreenState(int bookmarkIndex, int pageIndex) {
        MinecraftClient client = MinecraftClient.getInstance();
        Mouse mouse = client.mouse;
        this.mouseX = mouse.getX();
        this.mouseY = mouse.getY();
        this.bookmarkIndex = bookmarkIndex;
        this.pageIndex = pageIndex;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public int getBookmarkIndex() {
        return bookmarkIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }
}
