package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import xienaoban.minecraft.bole.gui.screen.tree.BoleSchoolingFishEntityScreen;
import xienaoban.minecraft.bole.mixin.IMixinDyeColor;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class BoleTropicalFishEntityScreen<E extends TropicalFishEntity, H extends BoleTropicalFishEntityScreenHandler<E>> extends BoleSchoolingFishEntityScreen<E, H> {
    private static final int TITLE_COLOR = 0xff6d2d1a;

    private List<Syncable> syncWidgets;

    public BoleTropicalFishEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new VariantTurnPagePropertyWidget(), null);
        BigFishPropertyWidget bigFishPropertyWidget = new BigFishPropertyWidget();
        TropicalFishBigVariantsPropertyWidget tropicalFishBigVariantsPropertyWidget = new TropicalFishBigVariantsPropertyWidget();
        TropicalFishSmallVariantsPropertyWidget tropicalFishSmallVariantsPropertyWidget = new TropicalFishSmallVariantsPropertyWidget();
        TropicalFishBaseColorPropertyWidget1 tropicalFishBaseColorPropertyWidget1 = new TropicalFishBaseColorPropertyWidget1();
        TropicalFishBaseColorPropertyWidget2 tropicalFishBaseColorPropertyWidget2 = new TropicalFishBaseColorPropertyWidget2();
        TropicalFishPatternColorPropertyWidget1 tropicalFishPatternColorPropertyWidget1 = new TropicalFishPatternColorPropertyWidget1();
        TropicalFishPatternColorPropertyWidget2 tropicalFishPatternColorPropertyWidget2 = new TropicalFishPatternColorPropertyWidget2();
        Page p1 = new Page()
                .addSlotLazy(bigFishPropertyWidget)
                .addSlotLazy(new LeftTextPropertyWidget(4, 1, new TranslatableText(Keys.PROPERTY_WIDGET_TROPICAL_FISH_VARIANT).formatted(Formatting.BOLD, Formatting.UNDERLINE), TITLE_COLOR, 0.5F))
                .addSlotLazy(tropicalFishBigVariantsPropertyWidget)
                .addSlotLazy(tropicalFishSmallVariantsPropertyWidget);
        Page p2 = new Page()
                .addSlotLazy(new LeftTextPropertyWidget(4, 1, new TranslatableText(Keys.PROPERTY_WIDGET_TROPICAL_FISH_BASE_COLOR).formatted(Formatting.BOLD, Formatting.UNDERLINE), TITLE_COLOR, 0.5F))
                .addSlotLazy(tropicalFishBaseColorPropertyWidget1)
                .addSlotLazy(tropicalFishBaseColorPropertyWidget2)
                .addSlotLazy(new LeftTextPropertyWidget(4, 1, new TranslatableText(Keys.PROPERTY_WIDGET_TROPICAL_FISH_PATTERN_COLOR).formatted(Formatting.BOLD, Formatting.UNDERLINE), TITLE_COLOR, 0.5F))
                .addSlotLazy(tropicalFishPatternColorPropertyWidget1)
                .addSlotLazy(tropicalFishPatternColorPropertyWidget2);
        this.pages.add(p1);
        this.pages.add(p2);
        this.syncWidgets = new ArrayList<>();
        this.syncWidgets.add(bigFishPropertyWidget);
        this.syncWidgets.add(tropicalFishBigVariantsPropertyWidget);
        this.syncWidgets.add(tropicalFishSmallVariantsPropertyWidget);
        this.syncWidgets.add(tropicalFishBaseColorPropertyWidget1);
        this.syncWidgets.add(tropicalFishBaseColorPropertyWidget2);
        this.syncWidgets.add(tropicalFishPatternColorPropertyWidget1);
        this.syncWidgets.add(tropicalFishPatternColorPropertyWidget2);
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

    private void syncAll(Syncable except) {
        for (Syncable syncable : this.syncWidgets) {
            if (syncable != except) syncable.sync();
        }
    }

    public class VariantTurnPagePropertyWidget extends AbstractPropertyWidget {
        private final Text text;
        private final TropicalFishEntity[] entities;

        public VariantTurnPagePropertyWidget() {
            super(4, 2);
            this.text = new TranslatableText(Keys.TEXT_TROPICAL_FISH_VARIANT_TURN_PAGE).formatted(Formatting.BOLD, Formatting.UNDERLINE);
            ArrayList<Integer> commons = Arrays.stream(TropicalFishEntity.COMMON_VARIANTS).boxed().collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(commons);
            this.entities = commons.stream().limit(7).map(v -> {
                TropicalFishEntity entity = (TropicalFishEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) throw new RuntimeException("Failed to create a TropicalFishEntity on the client side.");
                ((IMixinEntity) entity).setTouchingWater(true);
                entity.setVariant(v);
                return entity;
            }).toArray(TropicalFishEntity[]::new);
            for (int i = 0; i < this.entities.length; ++i) {
                TropicalFishEntity e = this.entities[i];
                if ((i & 1) == 0) e.setVariant(e.getVariant() & (~0xFF));
                else e.setVariant(e.getVariant() | 1);
            }
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            final int bt = this.box.top() + 3 + 10, sz = 16;
            int left = ((this.box.width() - this.entities.length * sz) >> 1) + x + (sz / 3);
            for (int i = 0; i < this.entities.length; ++i) {
                drawFishEntity(this.entities[i], sz, left + sz * i, bt, 0, 100);
            }
            drawText(matrices, this.text, TITLE_COLOR, 0.5F,
                    x + (this.box.width() - (textRenderer.getWidth(text) >> 1) >> 1),
                    this.box.bottom() - DEFAULT_LINE_HEIGHT);
        }
    }


    public class BigFishPropertyWidget extends AbstractPropertyWidget implements Syncable {
        private final TropicalFishEntity entity;

        public BigFishPropertyWidget() {
            super(4, 4);
            this.entity = (TropicalFishEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
            if (this.entity == null) throw new RuntimeException("Failed to create a TropicalFishEntity on the client side.");
            sync();
            ((IMixinEntity) this.entity).setTouchingWater(true);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int t = (int) (System.currentTimeMillis() % 8000);
            t = t > 4000 ? 6000 - t : t - 2000;
            t >>= 4;
            drawFishEntity(this.entity, 60, (this.box.left() + this.box.right() >> 1) - 6, this.box.bottom() - 8, 0, t);
        }

        @Override
        public void sync() {
            copyEntityNbtForDisplay(handler.entity, this.entity);
        }
    }

    public class TropicalFishBigVariantsPropertyWidget extends VariantsPropertyWidget implements Syncable {
        private static final int VARIANTS = 6;
        private static final String[][] KEYS = {
                {Keys.TROPICAL_FISH_VARIANT_KOB, Keys.TROPICAL_FISH_VARIANT_SUNSTREAK, Keys.TROPICAL_FISH_VARIANT_SNOOPER, Keys.TROPICAL_FISH_VARIANT_DASHER, Keys.TROPICAL_FISH_VARIANT_BRINELY, Keys.TROPICAL_FISH_VARIANT_SPOTTY},
                {Keys.TROPICAL_FISH_VARIANT_FLOPPER, Keys.TROPICAL_FISH_VARIANT_STRIPEY, Keys.TROPICAL_FISH_VARIANT_GLITTER, Keys.TROPICAL_FISH_VARIANT_BLOCKFISH, Keys.TROPICAL_FISH_VARIANT_BETTY, Keys.TROPICAL_FISH_VARIANT_CLAYFISH}
        };

        public TropicalFishBigVariantsPropertyWidget() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_TROPICAL_FISH_VARIANT);
            initTooltipDescription(Keys.PROPERTY_WIDGET_TROPICAL_FISH_VARIANT_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            TropicalFishEntity[] entities = new TropicalFishEntity[VARIANTS];
            TropicalFishEntity real = handler.entity;
            for (int i = 0; i < VARIANTS; ++i) {
                TropicalFishEntity entity = (TropicalFishEntity) real.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) throw new RuntimeException("Failed to create a TropicalFishEntity on the client side.");
                copyEntityNbtForDisplay(real, entity);
                ((IMixinEntity) entity).setTouchingWater(true);
                entity.setVariant((entity.getVariant() & (~0xFFFF)) | shape() | (i << 8));
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            return Arrays.stream(KEYS[shape()]).map(TranslatableText::new).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGod();
        }

        @Override
        protected boolean isChosen(E fake) {
            return ((handler.entity.getVariant() ^ fake.getVariant()) & 0xFFFF) == 0;
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_TROPICAL_FISH_VARIANT, (handler.entity.getVariant() & (~0xFFFF)) | (fake.getVariant() & 0xFFFF));
            syncAll(this);
        }

        @Override
        protected void drawEntity(MatrixStack matrices, E fake, int x0, int y0, int x1, int y1, int mouseX, int mouseY) {
            drawFishEntity(fake, 20, (x0 + x1 >> 1) - 2, y1, 0, 100);
        }

        protected int shape() {
            return 1;
        }

        public void sync() {
            for (int i = 0; i < this.variants.length; ++i) {
                TropicalFishEntity entity = this.variants[i];
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVariant((entity.getVariant() & (~0xFFFF)) | shape() | (i << 8));
            }
        }
    }

    public class TropicalFishSmallVariantsPropertyWidget extends TropicalFishBigVariantsPropertyWidget {
        @Override
        protected int shape() {
            return 0;
        }
    }

    public class TropicalFishBaseColorPropertyWidget1 extends VariantsPropertyWidget implements Syncable {
        private static final int MID = IMixinDyeColor.getValues().length >> 1;

        public TropicalFishBaseColorPropertyWidget1() {
            super(4, 2);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_TROPICAL_FISH_BASE_COLOR);
            initTooltipDescription(Keys.PROPERTY_WIDGET_TROPICAL_FISH_BASE_COLOR_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            TropicalFishEntity[] entities = new TropicalFishEntity[indexTo() - indexFrom()];
            TropicalFishEntity real = handler.entity;
            for (int i = indexFrom(); i < indexTo(); ++i) {
                TropicalFishEntity entity = (TropicalFishEntity) real.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) throw new RuntimeException("Failed to create a TropicalFishEntity on the client side.");
                copyEntityNbtForDisplay(real, entity);
                ((IMixinEntity) entity).setTouchingWater(true);
                entity.setVariant((entity.getVariant() & ~(0xFF << offset())) | (i << offset()));
                entities[i - indexFrom()] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            Stream<DyeColor> stream = Arrays.stream(IMixinDyeColor.getValues());
            return (low() ? stream.limit(MID) : stream.skip(MID)).map(dyeColor -> new TranslatableText(Keys.COLOR_PREFIX + dyeColor.getName())).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return isGod();
        }

        @Override
        protected boolean isChosen(E fake) {
            return (((handler.entity.getVariant() ^ fake.getVariant()) >> offset()) & 0xFF) == 0;
        }

        @Override
        protected void setChosen(E fake) {
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_TROPICAL_FISH_VARIANT, (handler.entity.getVariant() & ~(0xFF << offset())) | (fake.getVariant() & (0xFF << offset())));
            syncAll(this);
        }

        @Override
        protected void drawEntity(MatrixStack matrices, E fake, int x0, int y0, int x1, int y1, int mouseX, int mouseY) {
            drawFishEntity(fake, 16, (x0 + x1 >> 1) - 2, y1, 0, 100);
        }

        private int indexFrom() {
            return low() ? 0 : MID;
        }

        private int indexTo() {
            return low() ? MID : IMixinDyeColor.getValues().length;
        }

        protected boolean low() {
            return true;
        }

        protected int offset() {
            return 16;
        }

        public void sync() {
            for (int i = indexFrom(); i < indexTo(); ++i) {
                TropicalFishEntity entity = this.variants[i - indexFrom()];
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVariant((entity.getVariant() & ~(0xFF << offset())) | (i << offset()));
            }
        }
    }

    public class TropicalFishBaseColorPropertyWidget2 extends TropicalFishBaseColorPropertyWidget1 {
        @Override
        protected boolean low() {
            return false;
        }
    }

    public class TropicalFishPatternColorPropertyWidget1 extends TropicalFishBaseColorPropertyWidget1 {
        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_TROPICAL_FISH_PATTERN_COLOR);
            initTooltipDescription(Keys.PROPERTY_WIDGET_TROPICAL_FISH_PATTERN_COLOR_DESCRIPTION);
        }

        @Override
        protected boolean low() {
            return true;
        }

        @Override
        protected int offset() {
            return 24;
        }
    }

    public class TropicalFishPatternColorPropertyWidget2 extends TropicalFishPatternColorPropertyWidget1 {
        @Override
        protected boolean low() {
            return false;
        }
    }

    private interface Syncable {
        void sync();
    }
}
