package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.screen.tree.BoleAbstractDonkeyEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class BoleLlamaEntityScreen<E extends LlamaEntity, H extends BoleLlamaEntityScreenHandler<E>> extends BoleAbstractDonkeyEntityScreen<E, H> {
    public BoleLlamaEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new LlamaVariantsPropertyWidget(), null);
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

    public class LlamaVariantsPropertyWidget extends VariantsPropertyWidget {
        private static final int VARIANT_CNT = 4;

        public LlamaVariantsPropertyWidget() {
            super(4, 3);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_LLAMA_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_LLAMA_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            LlamaEntity[] entities = new LlamaEntity[VARIANT_CNT];
            for (int i = 0; i < VARIANT_CNT; ++i) {
                LlamaEntity entity = (LlamaEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a LlamaEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVariant(LlamaEntity.Variant.byId(i));
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            String[] keys = {Keys.LLAMA_VARIANT_SAND, Keys.LLAMA_VARIANT_SNOW, Keys.LLAMA_VARIANT_WOOD, Keys.LLAMA_VARIANT_DIRTY};
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
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_LLAMA_VARIANT, fake.getVariant());
        }
    }
}
