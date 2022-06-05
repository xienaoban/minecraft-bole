package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class BoleTameableEntityScreen<E extends TameableEntity, H extends BoleTameableEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleTameableEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new TamePropertyWidget(), BabyPropertyWidget.class);
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    public class TamePropertyWidget extends AbstractTamePropertyWidget {
        @Override
        protected boolean isTame() {
            return handler.entity.isTamed();
        }

        @Override
        protected UUID getOwnerUuid() {
            return handler.entity.getOwnerUuid();
        }
    }
}
