package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.mixin.IMixinMooshroomEntity;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleMooshroomEntityScreen<E extends MooshroomEntity, H extends BoleMooshroomEntityScreenHandler<E>> extends BoleCowEntityScreen<E, H> {
    public BoleMooshroomEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new MooshroomVariantsPropertyWidget(), null);
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

    public class MooshroomVariantsPropertyWidget extends VariantsPropertyWidget {
        private static final MooshroomEntity.Type[] TYPES = MooshroomEntity.Type.values();

        public MooshroomVariantsPropertyWidget() {
            super(2, 3);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_MOOSHROOM_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_MOOSHROOM_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            MooshroomEntity[] entities = new MooshroomEntity[TYPES.length];
            for (int i = 0; i < TYPES.length; ++i) {
                MooshroomEntity entity = (MooshroomEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a MooshroomEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);

                ((IMixinMooshroomEntity) entity).callSetType(TYPES[i]);
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            return Arrays.stream(TYPES).map(type -> Text.translatable(Keys.MOOSHROOM_VARIANT_PREFIX
                    + ((IMixinMooshroomEntity.IMixinMooshroomEntityType)(Object) type).getName())).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGod();
        }

        @Override
        protected boolean isChosen(E fake) {
            return handler.entity.getMooshroomType() == fake.getMooshroomType();
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_MOOSHROOM_VARIANT, fake.getMooshroomType());
        }
    }
}
