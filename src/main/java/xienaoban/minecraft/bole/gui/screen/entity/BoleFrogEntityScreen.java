package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BoleFrogEntityScreen<E extends FrogEntity, H extends BoleFrogEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public static final FrogVariant[] FROG_VARIANTS = initFrogVariants();

    public BoleFrogEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new FrogVariantsPropertyWidget(), null);
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

    private static FrogVariant[] initFrogVariants() {
        return Arrays.stream(FrogVariant.class.getDeclaredFields())
                .filter(field -> FrogVariant.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    try {
                        return (FrogVariant) field.get(FrogVariant.class);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .toArray(FrogVariant[]::new);
    }

    public class FrogVariantsPropertyWidget extends VariantsPropertyWidget {
        public FrogVariantsPropertyWidget() {
            super(3, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_FROG_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_FROG_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            FrogEntity[] entities = new FrogEntity[FROG_VARIANTS.length];
            for (int i = 0; i < FROG_VARIANTS.length; ++i) {
                FrogEntity entity = (FrogEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a FrogEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVariant(FROG_VARIANTS[i]);
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            return Arrays.stream(FROG_VARIANTS).map(frogVariant -> Text.translatable(Keys.FROG_VARIANT_PREFIX
                    + Objects.requireNonNull(Registries.FROG_VARIANT.getId(frogVariant)).getPath())).toArray(Text[]::new);
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
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_FROG_VARIANT, fake.getVariant());
        }
    }
}
