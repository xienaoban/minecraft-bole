package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.gui.screen.BoleTameableShoulderEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleParrotEntityScreen<E extends ParrotEntity, H extends BoleParrotEntityScreenHandler<E>> extends BoleTameableShoulderEntityScreen<E, H> {
    public BoleParrotEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new ParrotVariantsPropertyWidget(), null);
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

    public class ParrotVariantsPropertyWidget extends VariantsPropertyWidget {
        private static final int VARIANT_CNT = 5;

        public ParrotVariantsPropertyWidget() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_PARROT_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_PARROT_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            ParrotEntity[] entities = new ParrotEntity[VARIANT_CNT];
            for (int i = 0; i < VARIANT_CNT; ++i) {
                ParrotEntity entity = (ParrotEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a ParrotEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVariant(i);
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            String[] keys = { Keys.PARROT_VARIANT_RED, Keys.PARROT_VARIANT_BLUE, Keys.PARROT_VARIANT_GREEN, Keys.PARROT_VARIANT_CYAN, Keys.PARROT_VARIANT_GRAY };
            return Arrays.stream(keys).map(TranslatableText::new).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGodMode();
        }

        @Override
        protected boolean isChosen(E fake) {
            return handler.entity.getVariant() == fake.getVariant();
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_PARROT_VARIANT, fake.getVariant());
        }
    }
}
