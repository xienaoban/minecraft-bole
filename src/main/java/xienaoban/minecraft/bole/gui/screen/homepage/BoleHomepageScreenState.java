package xienaoban.minecraft.bole.gui.screen.homepage;

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
public class BoleHomepageScreenState {
    private final double mouseX, mouseY;
    private final int bookmarkIndex, pageIndex;
    private final boolean debugMode;

    public BoleHomepageScreenState(int bookmarkIndex, int pageIndex, boolean debugMode) {
        MinecraftClient client = MinecraftClient.getInstance();
        Mouse mouse = client.mouse;
        this.mouseX = mouse.getX();
        this.mouseY = mouse.getY();
        this.bookmarkIndex = bookmarkIndex;
        this.pageIndex = pageIndex;
        this.debugMode = debugMode;
    }

    public double getMouseX() {
        return this.mouseX;
    }

    public double getMouseY() {
        return this.mouseY;
    }

    public int getBookmarkIndex() {
        return this.bookmarkIndex;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }
}
