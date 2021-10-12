package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BoleLivingEntityScreen extends AbstractBoleScreen<LivingEntity, BoleLivingEntityScreenHandler> {
    public BoleLivingEntityScreen(BoleLivingEntityScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.drawBackground(matrices, delta, mouseX, mouseY);
        drawPlanEntity(mouseX, mouseY);
    }
}
