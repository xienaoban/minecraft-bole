package xienaoban.minecraft.bole.gui.screen.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreen;
import xienaoban.minecraft.bole.gui.screen.GenericHandledScreen;
import xienaoban.minecraft.bole.util.Keys;

import java.util.List;
import java.util.Random;

public class BeehiveScreen extends GenericHandledScreen<BeehiveScreenHandler> {
    private static final int[][] LATTICES = {{0, 0}, {32, 0}, {0, 50}, {32, 50}, {16, 25}, {-16, 25}, {48, 25}};
    private static final int MAX_HONEY_CNT = BeehiveBlock.FULL_HONEY_LEVEL;
    private static final int MAX_BEE_CNT = BeehiveBlockEntity.MAX_BEE_COUNT;

    private long mills;
    private final BeeAction[] actions;
    private int lastBeeCnt;

    public BeehiveScreen(BeehiveScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        if (DO_NOT_SHOW_REI) {
            this.backgroundWidth = this.width;
            this.backgroundHeight = this.height;
        }
        this.mills = System.currentTimeMillis();
        this.actions = new BeeAction[MAX_BEE_CNT];
        this.lastBeeCnt = 0;
        for (int i = 0; i < MAX_BEE_CNT; ++i) {
            this.actions[i] = new BeeAction();
        }
        BoleClient.getInstance().setScreenOpen(true);
    }

    @Override
    protected void init() {
        super.init();
        if (DO_NOT_SHOW_REI) {
            this.backgroundWidth = this.width;
            this.backgroundHeight = this.height;
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        long lastMills = this.mills;
        this.mills = System.currentTimeMillis();
        int diff = (int) (this.mills - lastMills);
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, Textures.BEEHIVE);
        int w = (this.width - 128) >> 1;
        int h = (this.height - 128) >> 1;

        int beeCnt = this.handler.blockBeeCnt;
        int honeyCnt = this.handler.blockHoneyCnt;

        if (this.lastBeeCnt > beeCnt) {
            this.lastBeeCnt = beeCnt;
            BeeAction tmp = this.actions[0];
            for (int i = 1; i < MAX_BEE_CNT; ++i) {
                this.actions[i - 1] = this.actions[i];
            }
            this.actions[MAX_BEE_CNT - 1] = tmp;
        }
        else {
            this.lastBeeCnt = beeCnt;
        }

        int lw = w + 32, lh = h + 23;
        for (int i = 0; i < MAX_HONEY_CNT; ++i) {
            drawLattice(matrices, LATTICES[i][0] + lw, LATTICES[i][1] + lh, i < honeyCnt ? 2 : 0);
        }
        drawLattice(matrices, LATTICES[5][0] + lw, LATTICES[5][1] + lh, 1);
        drawLattice(matrices, LATTICES[6][0] + lw, LATTICES[6][1] + lh, 1);
        drawTexture(matrices, w, h, 0, 0, 128, 128);
        int color = 0xBCFFFFFF;
        for (int i = 0; i < beeCnt; ++i) {
            BeeAction action = this.actions[i];
            action.run(diff);
            int x, y;
            if (honeyCnt + i < MAX_HONEY_CNT) {
                x = LATTICES[honeyCnt + i][0] + lw + 16;
                y = LATTICES[honeyCnt + i][1] + lh + 29;
            }
            else {
                int p = i + honeyCnt - MAX_HONEY_CNT + 1;
                x = w + p * 32;
                y = h + 24 + ((p & 1) == 0 ? 0 : 8);
            }
            BeehiveScreenHandler.BeeInfo bee = this.handler.bees[i];
            int beeSize = bee.entity.isBaby() ? 46 : 32;
            float t = 14.0F * Math.min(bee.ticksInHive, bee.minOccupationTicks) / bee.minOccupationTicks;
            drawHorizontalLine(matrices, 0xFF443300, 1.1F, getZOffset(), x - 7.5F, x + 7.5F, y - 1);
            drawHorizontalLine(matrices, bee.entity.hasNectar() ? 0xFFFFBB00 : 0x64FFBB00, 0.6F, getZOffset(), x - 7, x - 7 + t, y - 1);
            InventoryScreen.drawEntity(x, y, beeSize, action.mouseX, action.mouseY, bee.entity);
            Text customName = bee.entity.getCustomName();
            if (customName != null) {
                int wHalf = (getTextWidth(customName) >> 2) + 1, yyy = y - (bee.entity.isBaby() ? 20 : 25);
                drawHorizontalLine(matrices, 0x55777777, 3, getZOffset(), x - wHalf, x + wHalf, yyy);
                drawTextHalfCenteredX(matrices, bee.entity.getCustomName(), color, x, yyy - 2);
            }
            if (mouseX > x - 10 && mouseX < x + 10 && mouseY > y - 20 && mouseY < y) {
                List<Text> texts = List.of(
                        bee.entity.getName(),
                        new TranslatableText(Keys.TEXT_HAS_NECTAR, new TranslatableText(bee.entity.hasNectar() ? Keys.GUI_YES : Keys.GUI_NO)).formatted(Formatting.GRAY),
                        new TranslatableText(Keys.TEXT_TIME_IN_BEEHIVE, (bee.ticksInHive / 20) + "s/" + (bee.minOccupationTicks / 20) + "s").formatted(Formatting.GRAY)
                );
                int maxLength = texts.stream().mapToInt(this::getTextWidth).max().getAsInt() >> 2;
                renderTooltip(matrices, texts.stream().map(Text::asOrderedText).toList(), 0.5F, x - maxLength - 2, y);
            }
        }
        this.textRenderer.draw(matrices, honeyCnt + "/" + MAX_HONEY_CNT, LATTICES[5][0] + lw + 16 - 8.5F, LATTICES[5][1] + lh + 8, color);
        this.textRenderer.draw(matrices, beeCnt + "/" + MAX_BEE_CNT, LATTICES[6][0] + lw + 16 - 8.5F, LATTICES[6][1] + lh + 8, color);
        drawTextCenteredX(matrices, new TranslatableText(Keys.TEXT_HONEY), color, LATTICES[5][0] + lw + 16.5F, LATTICES[5][1] + lh + 16);
        drawTextCenteredX(matrices, EntityType.BEE.getName(), color, LATTICES[6][0] + lw + 16.5F, LATTICES[6][1] + lh + 16);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {}

    private void drawLattice(MatrixStack matrices, int w, int h, int type) {
        drawTexture(matrices, w, h, 16 + type * 32, 140, 32, 32);
    }

    private void drawTextHalfCenteredX(MatrixStack matrices, Text text, int color, int xMid, int y) {
        int w2 = getTextWidth(text) >> 2;
        final float size = 0.5F;
        MatrixStack matrixStack = AbstractBoleScreen.matrixScaleOn(size, size, size);
        this.textRenderer.draw(matrices, text, (xMid - w2) << 1, y << 1, color);
        AbstractBoleScreen.matrixScaleOff(matrixStack);
    }

    private static class BeeAction {
        public float mouseX, mouseY;
        private float speedMouseX, speedMouseY;
        private int mouseMoveTime, mouseCooldownTime;

        private final Random random;

        public BeeAction () {
            this.random = new Random();
            this.mouseX = this.random.nextFloat(-50, 50);
            this.mouseY = this.random.nextFloat(-1, 20);
            this.mouseCooldownTime = this.random.nextInt(3 * 1000);
        }

        public void run(int mills) {
            if (this.mouseMoveTime > 0) {
                this.mouseMoveTime -= mills;
                this.mouseX += this.speedMouseX * mills;
                this.mouseY += this.speedMouseY * mills;
                if (Math.abs(this.mouseX) > 50) this.speedMouseX = -0.2F * this.speedMouseX;
                if (this.mouseY > 20 || this.mouseY < 2) this.speedMouseY = -0.2F * this.speedMouseY;
            }
            else {
                this.mouseCooldownTime -= mills;
                if (this.mouseCooldownTime < 0) {
                    this.mouseCooldownTime = this.random.nextInt(5 * 1000);
                    this.mouseMoveTime = this.random.nextInt(1000);
                    this.speedMouseX = this.random.nextFloat(-0.3F, 0.3F);
                    this.speedMouseY = this.random.nextFloat(-0.1F, 0.1F);
                }
            }
        }
    }
}
