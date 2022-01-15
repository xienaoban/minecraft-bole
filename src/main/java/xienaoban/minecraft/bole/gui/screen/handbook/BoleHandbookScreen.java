package xienaoban.minecraft.bole.gui.screen.handbook;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.entity.EntityLookup;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.client.EntityManager;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.gui.ScreenManager;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreen;
import xienaoban.minecraft.bole.mixin.IMixinWorld;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public final class BoleHandbookScreen extends AbstractBoleScreen<Entity, BoleHandbookScreenHandler> {
    public BoleHandbookScreen(BoleHandbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        BoleHandbookScreenState state = BoleClient.getInstance().getHandbookState();
        if (state != null && this.client != null) {
            BoleClient.getInstance().setHandbookState(null);
            InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212993, state.getMouseX(), state.getMouseY());
            this.bookmarks.get(state.getBookmarkIndex()).onPress();
            setPageIndex(state.getPageIndex());
        }
    }

    @Override
    protected void initPages() {
        initCatalog(EntityManager.getInstance().getTagGroups().get(0));
    }

    @Override
    protected void initCustom() {}

    @Override
    protected void initButtons() {
        super.initButtons();
        int cnt = 0;
        for (EntityManager.TagGroup tags : EntityManager.getInstance().getTagGroups()) {
            addBookmark(cnt, tags.getText(), button -> initCatalog(tags));
            ++cnt;
        }
        addBookmark(8, new TranslatableText(Keys.TEXT_SETTINGS), button -> {
            resetPages();
            Page page = this.pages.get(0);
            String setConfigKey = BoleClient.getInstance().isHost() ? Keys.TEXT_SET_CONFIGS_LOCAL_IS_REMOTE : Keys.TEXT_SET_CONFIGS_LOCAL_IS_NOT_REMOTE;
            page.addSlot(new LeftTextPropertyWidget(4, 1, new TranslatableText(setConfigKey), DARK_TEXT_COLOR, 0.5F));
            page.addSlot(new OpenLocalConfigsPropertyWidget());
            page.addSlot(new EmptyPropertyWidget(4, 1));

            String curConfigKey = BoleClient.getInstance().isHost() ? Keys.TEXT_GET_CONFIGS_LOCAL_IS_REMOTE : Keys.TEXT_GET_CONFIGS_LOCAL_IS_NOT_REMOTE;
            page.addSlot(new LeftTextPropertyWidget(4, 1, new TranslatableText(curConfigKey), DARK_TEXT_COLOR, 0.5F));
            for (Field field : Configs.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigEntry.Gui.Excluded.class)) continue;
                String name = field.getName();
                boolean success = page.addSlot(new ConfigItemPropertyWidget(name));
                if (!success) {
                    page = new Page();
                    this.pages.add(page);
                    page.addSlot(new ConfigItemPropertyWidget(name));
                }
            }
            setPageIndex(0);
        });
        addBookmark(9, new TranslatableText(Keys.TEXT_ABOUT), button -> {
            resetPages();
            Page page0 = this.pages.get(0);
            page0.addSlot(new CenteredTextPropertyWidget(4, 1, new TranslatableText(Keys.TEXT_MOD_NAME_IS, new TranslatableText(Keys.MOD_NAME)), DARK_TEXT_COLOR, 0.5F));
            page0.addSlot(new CenteredTextPropertyWidget(4, 1, new TranslatableText(Keys.TEXT_MOD_AUTHOR_IS, new TranslatableText(Keys.XIENAOBAN)), DARK_TEXT_COLOR, 0.5F));
            FabricLoader.getInstance().getModContainer(Keys.BOLE).ifPresent(modContainer -> page0.addSlot(new CenteredTextPropertyWidget(4, 1, new TranslatableText(Keys.TEXT_MOD_VERSION_IS, modContainer.getMetadata().getVersion()), DARK_TEXT_COLOR, 0.5F)));
            page0.addSlot(new CrashClientPropertyWidget());
            setPageIndex(0);
        });
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    private void initCatalog(EntityManager.TagGroup group) {
        resetPages();
        this.pages.get(0).addSlot(new LeftTextPropertyWidget(4, 1, new TranslatableText(group.getName() + ".description"), DARK_TEXT_COLOR, 0.5F));
        group.dfsTags((root, depth) -> {
            int index = 0;
            while (!this.pages.get(index).addSlot(new TagItemPropertyWidget(depth, root))) {
                ++index;
                if (this.pages.size() == index) {
                    this.pages.add(new Page());
                }
            }
            return true;
        });
        setPageIndex(0);
    }

    public class TagItemPropertyWidget extends AbstractPropertyWidget {
        private static final int TAB = 5;
        private final int sub;
        private final EntityManager.Tag tag;
        private final Text text;

        public TagItemPropertyWidget(int sub, EntityManager.Tag tag) {
            super(4, 1);
            this.sub = sub;
            this.tag = tag;
            this.text = new TranslatableText(tag.getName()).append(" (" + tag.getEntities().size() + ")");
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 40, 10, getZOffset(), x + this.sub * TAB, y, 0, 240);
            drawName(matrices, 0xb0222222);
        }

        @Override
        public void drawHovered(MatrixStack matrices, int mouseX, int mouseY) {
            drawName(matrices, 0xff000000);
        }

        private void drawName(MatrixStack matrices, int color) {
            if (this.sub < 0) { // impossible (sub always >= 0)
                drawText(matrices, this.text, color, 1.0F, this.box.left() + 12 - this.sub * TAB, this.box.top() + 1);
            }
            else {
                drawText(matrices, this.text, color, 0.5F, this.box.left() + 12 + this.sub * TAB, this.box.top() + 3.25F);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            resetPages();
            int index = 0;
            for (EntityManager.EntityInfo entityInfo : this.tag.getEntities()) {
                while (!pages.get(index).addSlot(new LivingEntityPropertyWidget(entityInfo.getType()))) {
                    ++index;
                    if (pages.size() == index) {
                        pages.add(new Page());
                    }
                }
            }
            setPageIndex(0);
            return true;
        }
    }

    public class LivingEntityPropertyWidget extends AbstractPropertyWidget {
        private static final int BUTTONS_CUT = 20;
        private final LivingEntity entity;
        private final ItemStack spawnEgg;
        private final Text entityName;
        private final float entitySize;
        private long hoverTime;

        public LivingEntityPropertyWidget(EntityType<?> entityType) {
            super(1, 3);
            LivingEntity tmp = (LivingEntity) entityType.create(MinecraftClient.getInstance().world);
            if (tmp == null) {
                tmp = EntityType.ARMOR_STAND.create(MinecraftClient.getInstance().world);
            }
            this.entity = tmp;
            this.spawnEgg = new ItemStack(SpawnEggItem.forEntity(entityType));
            this.entityName = entityType.getName();
            this.entitySize = calEntitySize();
            this.hoverTime = -1;
        }

        private float calEntitySize() {
            Box bound = this.entity.getBoundingBox();
            float maxH = (float) ((bound.getZLength() + bound.getYLength())) / 1.414F;
            float maxW = (float) ((bound.getXLength() + bound.getZLength())) / 1.414F;
            float scale = Math.max(maxH / (this.box.height() - 4) * this.box.width(), maxW);
            return 16.0F / scale * ( 0.7F + 0.5F / (1 + (float)Math.pow(2.71828, -(scale - 1) * 3)));
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawEntity();
            drawTextCenteredX(matrices, this.entityName, DARK_TEXT_COLOR, 0.5F, this.box.left() + (this.box.width() >> 1), this.box.bottom() - (Page.PROPERTY_WIDGET_HEIGHT >> 1));
            if (isHovered()) {
                if (this.hoverTime == -1) this.hoverTime = System.currentTimeMillis();
                long diff = System.currentTimeMillis() - this.hoverTime;
                int u;
                if (diff < 150 || (diff % 2400) < 100) u = 240;
                else if (diff < 300) u = 230;
                else u = 220;
                drawRectangle(matrices, 0x77794500, getZOffset(), this.box.left(), this.box.top(), this.box.right(), this.box.bottom());
                drawTextCenteredX(matrices, this.entityName, 0xffffffff, 0.5F, this.box.left() + (this.box.width() >> 1), this.box.bottom() - (Page.PROPERTY_WIDGET_HEIGHT >> 1));
                boolean whichButton = mouseY > this.box.top() + BUTTONS_CUT;
                drawRectangle(matrices, !whichButton ? 0xd4ffffff : 0xaaffffff, getZOffset(), this.box.left() + 1, this.box.top() + 1, this.box.right() - 1, this.box.top() + BUTTONS_CUT);
                drawRectangle(matrices, whichButton ? 0xd4ffffff : 0xaaffffff, getZOffset(), this.box.left() + 1, this.box.top() + BUTTONS_CUT, this.box.right() - 1, this.box.bottom() - 6);
                int mid = this.box.left() + this.box.right() >> 1;
                float size = 2.0F;
                MatrixStack matrixStack = matrixScaleOn(size, size, size);
                setTexture(Textures.ICONS);
                drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), mid / size - 5, this.box.top() / size, u, 0);
                matrixScaleOff(matrixStack);
                size = 0.5F;
                matrixStack = matrixScaleOn(size, size, size);
                itemRenderer.renderInGui(this.spawnEgg, (int) ((mid - 4) / size), (int) ((this.box.top() + 21) / size));
                matrixScaleOff(matrixStack);
            }
            else this.hoverTime = -1;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseY < this.box.top() + BUTTONS_CUT) {
                PlayerEntity player = handler.player;
                if (!(player.isSpectator() || player.isCreative()) && player.totalExperience < Keys.HIGHLIGHT_EXPERIENCE_COST) {
                    showOverlayMessage(new TranslatableText(Keys.HINT_TEXT_HIGHLIGHT_NOT_ENOUGH_EXPERIENCE));
                    return true;
                }
                EntityType<?> entityType = this.entity.getType();
                HighlightManager hl = BoleClient.getInstance().getHighlightManager();
                assert MinecraftClient.getInstance().world != null;
                EntityLookup<Entity> lookup = ((IMixinWorld) MinecraftClient.getInstance().world).callGetEntityLookup();
                AtomicInteger cnt = new AtomicInteger();
                // @see net.minecraft.entity.Entity#shouldRender(double)
                double d = this.entity.getBoundingBox().getAverageSideLength();
                if (Double.isNaN(d)) d = 1.0;
                double dis2 = (d *= 64.0 * Entity.getRenderDistanceMultiplier()) * d;
                lookup.forEach(entityType, entity -> {
                    if (entity.squaredDistanceTo(player) < dis2) {
                        hl.highlight(entity, 8 * 20);
                        cnt.incrementAndGet();
                    }
                });
                player.sendMessage(new TranslatableText(Keys.TEXT_HIGHLIGHT, cnt.get(), new TranslatableText(entityType.getTranslationKey()), (int) Math.sqrt(dis2)).formatted(Formatting.GOLD), false);
                ClientNetworkManager.sendHighlightEvent();
                onClose();
                return true;
            }
            else if (mouseY < this.box.bottom() - 6) {
                if (isGodMode()) {
                    handler.sendClientEntitySettings(Keys.ENTITY_SETTING_OFFER_OR_DROP_GOD_MODE_ONLY, new ItemStack(this.spawnEgg.getItem()));
                    showOverlayMessage(new TranslatableText(Keys.HINT_TEXT_OFFER_OR_DROP, new TranslatableText(this.spawnEgg.getTranslationKey())));
                }
                else showOverlayMessage(new TranslatableText(Keys.HINT_TEXT_ONLY_IN_GOD_MODE));
                return true;
            }
            return false;
        }

        /**
         * @see net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity
         */
        @SuppressWarnings("deprecation")
        private void drawEntity() {
            float size = entitySize;
            int t = (int) (System.currentTimeMillis() % 8000);
            t = t > 4000 ? 6000 - t : t - 2000;
            float f = (float) Math.atan(t / 420.0F) * 6F;
            float g = -45;
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(this.box.left() + (this.box.width() >> 1), this.box.bottom() - Page.PROPERTY_WIDGET_MARGIN_HEIGHT - size * (float) this.entity.getBoundingBox().getXLength(), 1050.0F);
            matrixStack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            MatrixStack matrixStack2 = new MatrixStack();
            matrixStack2.translate(0.0, 0.0, 1000.0);
            matrixStack2.scale(size, size, size);
            Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
            Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g);
            quaternion.hamiltonProduct(quaternion2);
            matrixStack2.multiply(quaternion);
            entity.bodyYaw = 180.0F + f * 2;
            entity.setYaw(180.0F - f);
            entity.setPitch(g);
            entity.headYaw = entity.getYaw();
            entity.prevHeadYaw = entity.getYaw();
            DiffuseLighting.method_34742();
            EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            quaternion2.conjugate();
            entityRenderDispatcher.setRotation(quaternion2);
            entityRenderDispatcher.setRenderShadows(false);
            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrixStack2, immediate, 0xF000F0));
            immediate.draw();
            entityRenderDispatcher.setRenderShadows(true);
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
            DiffuseLighting.enableGuiDepthLighting();
        }
    }

    public class ConfigItemPropertyWidget extends AbstractPropertyWidget {
        private final Field field;
        private final Text name;

        public ConfigItemPropertyWidget(String attr) {
            super(4, 1);
            try {
                this.field = Configs.class.getDeclaredField(attr);
                this.field.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.name = new TranslatableText(Keys.AUTO_CONFIG_PREFIX + attr);
            initTooltipDescription(Keys.AUTO_CONFIG_PREFIX + attr + Keys.AUTO_CONFIG_POSTFIX);
        }

        @Override
        protected void initTooltipLines() {
            // "attr" isn't accessible here   x   initTooltipDescription(Keys.AUTO_CONFIG_PREFIX + attr);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            String value;
            try { value = this.field.get(BoleClient.getInstance().getServerConfigs()).toString(); }
            catch (Exception e) {
                e.printStackTrace();
                value = Keys.ERROR_TEXT_DATA_LOAD;
            }
            Text valueText = new TranslatableText(value);
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 30, 10, getZOffset(), x, y, 0, 200);
            drawTextureRotated180(matrices, 256, 256, 30, 10, getZOffset(), x + this.box.width() - 30, y, 0, 200);
            drawText(matrices, this.name, 0xff003e6a, 0.5F, this.box.left() + 2, this.box.top() + 3F);
            drawText(matrices, valueText, 0xff0162a6, 0.5F, this.box.right() - (textRenderer.getWidth(valueText) >> 1) - 3, this.box.top() + 3F);
        }
    }

    public class OpenLocalConfigsPropertyWidget extends AbstractPropertyWidget {
        private final Text title;
        public OpenLocalConfigsPropertyWidget() {
            super(4, 1);
            this.title = new TranslatableText(Keys.TEXT_OPEN_LOCAL_CONFIGS);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            int u = isHovered() ? 30 : 0;
            drawTextureNormally(matrices, 256, 256, 30, 10, getZOffset(), x, y, u, 200);
            drawTextureRotated180(matrices, 256, 256, 30, 10, getZOffset(), x + this.box.width() - 30, y, u, 200);
            drawTextCenteredX(matrices, this.title, DARK_TEXT_COLOR, 0.5F, this.box.left() + this.box.right() >> 1, this.box.top() + 3F);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            setHovered(null);
            assert client != null;
            client.setScreen(ScreenManager.getConfigScreen(BoleHandbookScreen.this));
            return true;
        }
    }

    public class CrashClientPropertyWidget extends AbstractPropertyWidget {

        public CrashClientPropertyWidget() {
            super(4, 1);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (!debugMode) return;
            drawTextCenteredX(matrices, "-= Click me to crash the client =-", 0xffff2222, 0.5F, this.box.left() + this.box.right() >> 1, this.box.top() + 3F);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!debugMode) return false;
            throw new NullPointerException("Surprise~~");
        }
    }
}
