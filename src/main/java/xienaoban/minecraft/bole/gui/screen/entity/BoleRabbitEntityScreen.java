package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleRabbitEntityScreen<E extends RabbitEntity, H extends BoleRabbitEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleRabbitEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new RabbitVariantsPropertyWidget(), null);
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

    public class RabbitVariantsPropertyWidget extends VariantsPropertyWidget {
        private static final int[] VARIANTS = {RabbitEntity.BROWN_TYPE, RabbitEntity.WHITE_TYPE, RabbitEntity.BLACK_TYPE, RabbitEntity.WHITE_SPOTTED_TYPE, RabbitEntity.GOLD_TYPE, RabbitEntity.SALT_TYPE, RabbitEntity.KILLER_BUNNY_TYPE};

        public RabbitVariantsPropertyWidget() {
            super(4, 3);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_RABBIT_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_RABBIT_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            RabbitEntity[] entities = new RabbitEntity[VARIANTS.length];
            for (int i = 0; i < VARIANTS.length; ++i) {
                RabbitEntity entity = (RabbitEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a RabbitEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setRabbitType(VARIANTS[i]);
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            String[] keys = {Keys.RABBIT_VARIANT_BROWN_TYPE, Keys.RABBIT_VARIANT_WHITE_TYPE, Keys.RABBIT_VARIANT_BLACK_TYPE, Keys.RABBIT_VARIANT_WHITE_SPOTTED_TYPE, Keys.RABBIT_VARIANT_GOLD_TYPE, Keys.RABBIT_VARIANT_SALT_TYPE, Keys.RABBIT_VARIANT_KILLER_BUNNY_TYPE};
            return Arrays.stream(keys).map(TranslatableText::new).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGod();
        }

        @Override
        protected boolean isChosen(E fake) {
            return handler.entity.getRabbitType() == fake.getRabbitType();
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_RABBIT_VARIANT, fake.getRabbitType());
        }
    }
}
