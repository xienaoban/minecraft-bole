package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BoleFishEntityScreen<E extends FishEntity, H extends BoleFishEntityScreenHandler<E>> extends BoleWaterCreatureEntityScreen<E, H> {
    public BoleFishEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
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

    /**
     * Draw an entity.
     * This method is similar to InventoryScreen.drawEntity(), but there are some differences:
     * <p>1. It can draw any Entity, not only LivingEntity. </p>
     * <p>2. The entity it renders is brighter (but it brings some lighting bugs...). </p>
     * <p>3. It can't recognize the yaw of LivingEntity, so don’t use it to render a rotating LivingEntity. </p>
     * @see net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity
     */
    public static void drawFishEntity(Entity entity, int size, int x, int y, float mouseX, float mouseY) {
        float f = (float)Math.atan(mouseX / 40.0f);
        float g = (float)Math.atan(mouseY / 40.0f);
        drawEntityGeneric(entity, size, x, y, g * 20.0f, entity.getYaw(0.0F) - f * 40.0F + 90.0F, 0);
    }
}
