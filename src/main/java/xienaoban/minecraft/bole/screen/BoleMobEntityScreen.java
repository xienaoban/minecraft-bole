package xienaoban.minecraft.bole.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BoleMobEntityScreen<E extends MobEntity, H extends BoleMobEntityScreenHandler<E>> extends BoleLivingEntityScreen<E, H> {
    public BoleMobEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initCustom() {
        this.widgetsWithEntity.addSlot(new HealthContentWidget());
        this.widgetsWithEntity.addSlot(new EmptyContentWidget(2, 1));
        this.widgetsWithEntity.addSlot(new EmptyContentWidget(2, 2));
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        drawPlanEntity(matrices, mouseX, mouseY);
        drawPlanWidgets(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int mouseX, int mouseY) {}
}
