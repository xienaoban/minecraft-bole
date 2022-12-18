package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleFoxEntityScreen<E extends FoxEntity, H extends BoleFoxEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleFoxEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new FoxVariantsPropertyWidget(), null);
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

    public class FoxVariantsPropertyWidget extends VariantsPropertyWidget {
        private static final FoxEntity.Type[] VARIANTS = FoxEntity.Type.values();

        public FoxVariantsPropertyWidget() {
            super(2, 3);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_FOX_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_FOX_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            FoxEntity[] entities = new FoxEntity[VARIANTS.length];
            for (int i = 0; i < VARIANTS.length; ++i) {
                FoxEntity entity = (FoxEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a FoxEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVariant(VARIANTS[i]);
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            return Arrays.stream(VARIANTS).map(type -> Text.translatable(Keys.FOX_VARIANT_PREFIX + type.asString())).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGod();
        }

        @Override
        protected boolean isChosen(E fake) {
            return handler.entity.getVariant() == fake.getVariant();
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_FOX_VARIANT, fake.getVariant());
        }
    }
}
