package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;
import java.util.Comparator;

@Environment(EnvType.CLIENT)
public class BolePandaEntityScreen<E extends PandaEntity, H extends BolePandaEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BolePandaEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new PandaHiddenGenePropertyWidget(), null).addSlotLazyAfter(new PandaMainGenePropertyWidget(), null);
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

    public class PandaMainGenePropertyWidget extends VariantsPropertyWidget {

        public PandaMainGenePropertyWidget() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_PANDA_MAIN_GENE);
            initTooltipDescription(Keys.PROPERTY_WIDGET_PANDA_MAIN_GENE_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            PandaEntity[] res = Arrays.stream(PandaEntity.Gene.values())
                    .sorted(Comparator.comparingInt(PandaEntity.Gene::getId))
                    .map(gene -> {
                        PandaEntity entity = (PandaEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                        if (entity == null) {
                            throw new RuntimeException("Failed to create a PandaEntity on the client side.");
                        }
                        entity.setMainGene(gene);
                        entity.setHiddenGene(gene);
                        return entity;
                    })
                    .toArray(PandaEntity[]::new);
            return MiscUtil.cast(res);
        }

        @Override
        protected Text[] initNames() {
            return Arrays.stream(PandaEntity.Gene.values())
                    .sorted(Comparator.comparingInt(PandaEntity.Gene::getId))
                    .map(gene -> new TranslatableText(Keys.PANDA_VARIANT_PREFIX + gene.getName()))
                    .toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGod();
        }

        @Override
        protected boolean isChosen(E fake) {
            return getGene(handler.entity) == getGene(fake);
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_PANDA_VARIANT, isMainGene(), getGene(fake));
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int color = isMainGene() ? 0x550063b1 : 0x55f86848;
            drawHorizontalLine(matrices, color, 2, getZOffset(), this.box.left() + 2, this.box.right() - 2, this.box.top() + 6);
            super.drawContent(matrices, x, y, mouseX, mouseY);
        }

        @Override
        protected void drawEntity(MatrixStack matrices, E fake, int x0, int y0, int x1, int y1, int mouseX, int mouseY) {
            boolean recessive = getGene(fake).isRecessive();
            Text text = new TranslatableText(recessive ? Keys.TEXT_HIDDEN_GENE : Keys.TEXT_MAIN_GENE);
            int color = recessive ? 0xaaf86848 : 0xaa0063b1;
            drawTextCenteredX(matrices, text, color, 0.5F, x0 + x1 >> 1, y1 + 5);
            drawEntityAuto(fake, x0, y0 - 6, x1, y1, 0, 1);
        }

        @Override
        protected void drawName(MatrixStack matrices, Text text, int xMid, int yTop) {
            super.drawName(matrices, text, xMid, yTop - 1);
        }

        protected void setGene(PandaEntity entity, PandaEntity.Gene gene) {
            entity.setMainGene(gene);
        }

        protected PandaEntity.Gene getGene(PandaEntity entity) {
            return entity.getMainGene();
        }

        protected boolean isMainGene() {
            return true;
        }
    }

    public class PandaHiddenGenePropertyWidget extends PandaMainGenePropertyWidget {
        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_PANDA_HIDDEN_GENE);
            initTooltipDescription(Keys.PROPERTY_WIDGET_PANDA_HIDDEN_GENE_DESCRIPTION);
        }

        @Override
        protected void setGene(PandaEntity entity, PandaEntity.Gene gene) {
            entity.setHiddenGene(gene);
        }

        @Override
        protected PandaEntity.Gene getGene(PandaEntity entity) {
            return entity.getHiddenGene();
        }

        @Override
        protected boolean isMainGene() {
            return false;
        }
    }
}
