package xienaoban.minecraft.bole.gui;

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
        ContentWidgets page;
        page = this.pages.get(0);
        this.entityDisplayPlan = chooseEntityDisplayPlan(page);
        page.addSlot(new HealthContentWidget());
        page.addSlot(new BoundingBoxContentWidget());
        page = this.pages.get(1);
        page.addSlot(new NetherPortalCooldownContentWidget());
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curLeftPage.draw(matrices, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curRightPage.draw(matrices, x, y, mouseX, mouseY);
    }
}
