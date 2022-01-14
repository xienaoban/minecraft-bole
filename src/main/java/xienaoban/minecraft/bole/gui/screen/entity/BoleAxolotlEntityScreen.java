package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import xienaoban.minecraft.bole.gui.screen.BoleAnimalEntityScreen;
import xienaoban.minecraft.bole.mixin.IMixinAxolotlEntity;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleAxolotlEntityScreen<E extends AxolotlEntity, H extends BoleAxolotlEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleAxolotlEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new AxolotlVariantsPropertyWidget(), null);
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

    public class AxolotlVariantsPropertyWidget extends VariantsPropertyWidget {
        public AxolotlVariantsPropertyWidget() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_AXOLOTL_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_AXOLOTL_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            AxolotlEntity.Variant[] variants = AxolotlEntity.Variant.VARIANTS;
            AxolotlEntity[] entities = new AxolotlEntity[variants.length];
            World world = MinecraftClient.getInstance().world;
            for (int i = 0; i < variants.length; ++i) {
                AxolotlEntity entity = (AxolotlEntity) handler.entity.getType().create(world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a AxolotlEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                ((IMixinAxolotlEntity) entity).callSetVariant(variants[i]);
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            return Arrays.stream(AxolotlEntity.Variant.VARIANTS).map(variant -> new LiteralText(variant.getName())).toArray(Text[]::new);
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
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_AXOLOTL_VARIANT, fake.getVariant());
        }
    }
}
