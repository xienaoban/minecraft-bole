package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.gui.screen.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.mixin.IMixinSheepEntity;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class BoleSheepEntityScreen<E extends SheepEntity, H extends BoleSheepEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleSheepEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazy(new EatGrassPropertyWidget());
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

    public class EatGrassPropertyWidget extends TemplatePropertyWidget1 {
        private static final Predicate<BlockState> GRASS_PREDICATE = MiscUtil.getFieldValue(null, EatGrassGoal.class, "GRASS_PREDICATE");

        private int interval;

        public EatGrassPropertyWidget() {
            super(1, false, 1);
            this.interval = -1;
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_EAT_GRASS);
            initTooltipDescription(Keys.PROPERTY_WIDGET_EAT_GRASS_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_EAT_GRASS_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 160, 0);
            int u = isEating() ? 10 : (canEat() ? 0 : 20);
            drawButton(matrices, 200 + u, 30, 0);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            if (!isEating() && canEat()) {
                handler.sendClientEntitySettings(Keys.ENTITY_SETTING_EAT_GRASS);
                this.interval = BoleClient.getInstance().getTicks() + 10;
            }
            return true;
        }

        private boolean canEat() {
            World world = MinecraftClient.getInstance().world;
            if (world == null) {
                return false;
            }
            BlockPos blockPos = handler.entity.getBlockPos();
            return GRASS_PREDICATE.test(world.getBlockState(blockPos)) || world.getBlockState(blockPos.down()).isOf(Blocks.GRASS_BLOCK);
        }

        private boolean isEating() {
            return ((IMixinSheepEntity) handler.entity).getEatGrassTimer() > 0 || this.interval > BoleClient.getInstance().getTicks();
        }
    }
}
