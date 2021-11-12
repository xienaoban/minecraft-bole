package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BolePathAwareEntityScreen<E extends PathAwareEntity, H extends BolePathAwareEntityScreenHandler<E>> extends BoleMobEntityScreen<E, H> {
    public BolePathAwareEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initCustom() {
        super.initCustom();
        this.pages.get(0).addSlot(new InterestedItemContentWidget());
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    public class InterestedItemContentWidget extends AbstractContentWidget {
        public InterestedItemContentWidget() {
            super(2, 1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawText(matrices, Arrays.toString(handler.entityInterestedItems), 0xff000000, 0.5F, x, y);
        }
    }
}
