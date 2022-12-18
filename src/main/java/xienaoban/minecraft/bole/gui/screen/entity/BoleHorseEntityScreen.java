package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAbstractHorseEntityScreen;
import xienaoban.minecraft.bole.mixin.IMixinHorseEntity;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleHorseEntityScreen<E extends HorseEntity, H extends BoleHorseEntityScreenHandler<E>> extends BoleAbstractHorseEntityScreen<E, H> {
    public BoleHorseEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        pages.get(1).addSlotLazyAfter(new HorseMarkingVariantsPropertyWidget(), null)
                .addSlotLazyAfter(new HorseColorVariantsPropertyWidget(), null);
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

    public class HorseColorVariantsPropertyWidget extends VariantsPropertyWidget {
        public HorseColorVariantsPropertyWidget() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_HORSE_COLOR_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_HORSE_COLOR_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            World world = MinecraftClient.getInstance().world;
            HorseMarking marking = handler.entity.getMarking();
            HorseEntity[] entities = Arrays.stream(HorseColor.values()).map(color -> {
                HorseEntity entity = (HorseEntity) handler.entity.getType().create(world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a HorseEntity on the client side.");
                }
                ((IMixinHorseEntity) entity).callSetHorseVariant(color, marking);
                return entity;
            }).toArray(HorseEntity[]::new);
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            String[] keys = { Keys.HORSE_COLOR_VARIANT_WHITE, Keys.HORSE_COLOR_VARIANT_CREAMY, Keys.HORSE_COLOR_VARIANT_CHESTNUT, Keys.HORSE_COLOR_VARIANT_BROWN, Keys.HORSE_COLOR_VARIANT_BLACK, Keys.HORSE_COLOR_VARIANT_GRAY, Keys.HORSE_COLOR_VARIANT_DARKBROWN };
            return Arrays.stream(keys).map(Text::translatable).toArray(Text[]::new);
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
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_HORSE_COLOR_VARIANT, fake.getVariant());
        }
    }

    public class HorseMarkingVariantsPropertyWidget extends VariantsPropertyWidget {
        public HorseMarkingVariantsPropertyWidget() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_HORSE_MARKING_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_HORSE_MARKING_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            World world = MinecraftClient.getInstance().world;
            HorseColor color = handler.entity.getVariant();
            HorseEntity[] entities = Arrays.stream(HorseMarking.values()).map(marking -> {
                HorseEntity entity = (HorseEntity) handler.entity.getType().create(world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a HorseEntity on the client side.");
                }
                ((IMixinHorseEntity) entity).callSetHorseVariant(color, marking);
                return entity;
            }).toArray(HorseEntity[]::new);
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            String[] keys = { Keys.HORSE_MARKING_VARIANT_NONE, Keys.HORSE_MARKING_VARIANT_WHITE, Keys.HORSE_MARKING_VARIANT_WHITE_FIELD, Keys.HORSE_MARKING_VARIANT_WHITE_DOTS, Keys.HORSE_MARKING_VARIANT_BLACK_DOTS };
            return Arrays.stream(keys).map(Text::translatable).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGod();
        }

        @Override
        protected boolean isChosen(E fake) {
            return handler.entity.getMarking() == fake.getMarking();
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_HORSE_MARKING_VARIANT, fake.getMarking());
        }
    }
}
