package xienaoban.minecraft.bole.gui;

public class ElementBox {
    private int x0, y0, x1, y1, w, h;

    public ElementBox() {
        this.w = 0;
        this.h = 0;
        this.x0 = 0;
        this.y0 = 0;
        this.x1 = 0;
        this.y1 = 0;
    }

    public ElementBox(int w, int h) {
        this.w = w;
        this.h = h;
        this.x0 = 0;
        this.y0 = 0;
        this.x1 = this.x0 + w;
        this.y1 = this.y0 + h;
    }

    public int left() {
        return this.x0;
    }

    public void left(int x0, boolean lockWidth) {
        this.x0 = x0;
        if (lockWidth) {
            this.x1 = this.x0 + this.w;
        }
        else {
            this.w = this.x1 - this.x0;
        }
    }

    public int right() {
        return this.x1;
    }

    public void right(int x1, boolean lockWidth) {
        this.x1 = x1;
        if (lockWidth) {
            this.x0 = this.x1 - this.w;
        }
        else {
            this.w = this.x1 - this.x0;
        }
    }

    public int top() {
        return this.y0;
    }

    public void top(int y0, boolean lockHeight) {
        this.y0 = y0;
        if (lockHeight) {
            this.y1 = this.y0 + this.h;
        }
        else {
            this.h = this.y1 - this.y0;
        }
    }

    public int bottom() {
        return this.y1;
    }

    public void bottom(int y1, boolean lockHeight) {
        this.y1 = y1;
        if (lockHeight) {
            this.y0 = this.y1 - this.h;
        }
        else {
            this.h = this.y1 - this.y0;
        }
    }

    public int width() {
        return this.w;
    }

    public int height() {
        return this.h;
    }

    public void position(int x0, int y0, boolean lockWidthAndHeight) {
        if (x0 == this.x0 && y0 == this.y0) {
            return;
        }
        left(x0, lockWidthAndHeight);
        top(y0, lockWidthAndHeight);
    }

    public void size(int w, int h) {
        right(x0 + w, false);
        bottom(y0 + h, false);
    }
}
