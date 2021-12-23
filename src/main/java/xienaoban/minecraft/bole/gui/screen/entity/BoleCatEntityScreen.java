package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.gui.screen.BoleTameableEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

@Environment(EnvType.CLIENT)
public class BoleCatEntityScreen<E extends CatEntity, H extends BoleCatEntityScreenHandler<E>> extends BoleTameableEntityScreen<E, H> {
    public BoleCatEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new CatVariantsPropertyWidget1(), null).addSlotLazyAfter(new CatVariantsPropertyWidget2(), CatVariantsPropertyWidget1.class);
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

    public class CatVariantsPropertyWidget1 extends VariantsPropertyWidget {
        private static final String[] NAMES = { Keys.CAT_VARIANT_TABBY, Keys.CAT_VARIANT_BLACK, Keys.CAT_VARIANT_RED, Keys.CAT_VARIANT_SIAMESE,
                Keys.CAT_VARIANT_BRITISH_SHORTHAIR, Keys.CAT_VARIANT_CALICO, Keys.CAT_VARIANT_PERSIAN, Keys.CAT_VARIANT_RAGDOLL,
                Keys.CAT_VARIANT_WHITE, Keys.CAT_VARIANT_JELLIE, Keys.CAT_VARIANT_ALL_BLACK };

        public CatVariantsPropertyWidget1() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_CAT_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_CAT_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            final int types = typeTo() - typeFrom();
            CatEntity[] entities = new CatEntity[types];
            CatEntity real = handler.entity;
            for (int i = typeFrom(); i < typeTo(); ++i) {
                CatEntity entity = (CatEntity) real.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a CatEntity on the client side.");
                }
                copyEntityNbtForDisplay(real, entity);
                entity.setCatType(i);
                entities[i - typeFrom()] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            final int types = typeTo() - typeFrom();
            Text[] res = new Text[types];
            for (int i = typeFrom(); i < typeTo(); ++i) {
                res[i - typeFrom()] = new TranslatableText(NAMES[i]);
            }
            return res;
        }

        @Override
        protected boolean canChoose() {
            return isGodMode();
        }

        @Override
        protected boolean isChosen(E fake) {
            return handler.entity.getCatType() == fake.getCatType();
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_CAT_VARIANT, fake.getCatType());
        }

        @Override
        protected void drawEntity(MatrixStack matrices, E fake, int x0, int y0, int x1, int y1, int mouseX, int mouseY) {
            int mid = x0 + x1 >> 1;
            drawEntityAuto(fake, mid - 10, y0 - 12, mid + 10, y1, 0, 10);
        }

        protected int typeFrom() {
            return 0;
        }

        protected int typeTo() {
            return 5;
        }
    }

    public class CatVariantsPropertyWidget2 extends CatVariantsPropertyWidget1 {
        @Override
        protected int typeFrom() {
            return 5;
        }

        @Override
        protected int typeTo() {
            return 11;
        }
    }
}
