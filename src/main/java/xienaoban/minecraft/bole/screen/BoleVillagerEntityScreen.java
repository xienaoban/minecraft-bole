package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BoleVillagerEntityScreen<E extends VillagerEntity, H extends BoleVillagerEntityScreenHandler<E>> extends BoleLivingEntityScreen<E, H> {
    public BoleVillagerEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        drawPlanEntity(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        drawPlanEntity(matrices, mouseX, mouseY);
    }
}
