package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.village.VillagerType;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.gui.screen.BoleMerchantEntityScreen;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleVillagerEntityScreen<E extends VillagerEntity, H extends BoleVillagerEntityScreenHandler<E>> extends BoleMerchantEntityScreen<E, H> {
    public BoleVillagerEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new ClothingPropertyWidget(), null).addSlotLazy(new RestockPropertyWidget());
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

    public class RestockPropertyWidget extends TemplatePropertyWidget1 {

        public RestockPropertyWidget() {
            super(2, true, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK);
            initTooltipDescription(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int restocksToday = handler.entityRestocksToday;
            drawIcon(matrices, 0, 110);
            drawBar(matrices, 10, 110, 1.0F);
            drawBar(matrices, 50, 110, restocksToday / 3.0F);
            drawButton(matrices, 230, 10, 0);
            drawBarText(matrices, restocksToday + "/3", LIGHT_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_RESTOCK);
            return true;
        }
    }

    public class ClothingPropertyWidget extends AbstractPropertyWidget {
        private static final VillagerType[] CLOTHES = {VillagerType.PLAINS, VillagerType.TAIGA, VillagerType.DESERT, VillagerType.JUNGLE, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.SWAMP};
        private static final Text[] NAMES = Arrays.stream(CLOTHES).map(type -> new TranslatableText(type.toString())).toArray(Text[]::new);
        private final VillagerEntity[] clothingEntities;
        private final int eachWidth, margin;

        public ClothingPropertyWidget() {
            super(4, 3);
            this.clothingEntities = new VillagerEntity[CLOTHES.length];
            this.eachWidth = this.box.width() / this.clothingEntities.length;
            this.margin = ((this.box.width() % this.clothingEntities.length) >> 1) + 1;
            for (int i = 0; i < CLOTHES.length; ++i) {
                VillagerEntity entity = (VillagerEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a VillagerEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVillagerData(entity.getVillagerData().withType(CLOTHES[i]));
                this.clothingEntities[i] = entity;
            }
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_VILLAGER_CLOTHING);
            initTooltipDescription(Keys.PROPERTY_WIDGET_VILLAGER_CLOTHING_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            for (int i = 0; i < this.clothingEntities.length; ++i) {
                VillagerEntity entity = this.clothingEntities[i];
                int xx = x + this.eachWidth * i + this.margin;
                drawEntityAuto(entity, xx, y, xx + this.eachWidth, this.box.bottom() - 10, 0, 10);
                drawTextCenteredX(matrices, NAMES[i], 0xaa220000, 0.5F, xx + this.eachWidth / 2.0F, this.box.bottom() - 8);
                if (CLOTHES[i] == handler.entity.getVillagerData().getType()) {
                    drawSelectedTick(matrices, i, true);
                }
            }
            if (getHovered() == this) {
                int i = calIndex(mouseX, mouseY);
                if (i >= 0 && i < CLOTHES.length && CLOTHES[i] != handler.entity.getVillagerData().getType()) {
                    drawSelectedTick(matrices, i, false);
                }
            }
        }

        private void drawSelectedTick(MatrixStack matrices, int index, boolean selected) {
            setTexture(Textures.ICONS);
            int xx = this.box.left() + this.eachWidth * index + this.margin + this.eachWidth / 2 - 5;
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), xx, this.box.bottom() - 18, 210 - (selected ? 10 : 0), 20);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calIndex((int) mouseX, (int) mouseY);
            if (index < 0 || index >= CLOTHES.length) {
                return false;
            }
            VillagerType type = CLOTHES[index];
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_CLOTHING, type);
            if (targetDisplayedEntityPropertyWidget != null) {
                targetDisplayedEntityPropertyWidget.updateDisplayedEntity();
            }
            return true;
        }

        private int calIndex(int mouseX, int mouseY) {
            if (mouseY < this.box.top() + 4 || mouseY > this.box.bottom() - 4) {
                return -1;
            }
            return (mouseX - this.margin - this.box.left()) / this.eachWidth;
        }
    }
}
