package xienaoban.minecraft.bole.gui.screen.homepage;

import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.world.entity.EntityLookup;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.client.entity.EntityManager;
import xienaoban.minecraft.bole.client.PlayerDataCacheManager;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.config.Configs;
import xienaoban.minecraft.bole.core.BoleHandbookItem;
import xienaoban.minecraft.bole.gui.ScreenManager;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreen;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.mixin.IMixinWorld;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The home page of the bole handbook.
 */
@Environment(EnvType.CLIENT)
public final class BoleHomepageScreen extends AbstractBoleScreen<Entity, BoleHomepageScreenHandler> {
    public BoleHomepageScreen(BoleHomepageScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        BoleHomepageScreenState state = BoleClient.getInstance().getHomepageScreenState();
        if (state != null && this.client != null) {
            BoleClient.getInstance().setHomepageScreenState(null);
            InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212993, state.getMouseX(), state.getMouseY());
            this.bookmarks.get(state.getBookmarkIndex()).onPress();
            setPageIndex(state.getPageIndex());
            this.debugMode = state.isDebugMode();
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
        addBookmark(8, Text.translatable(Keys.TEXT_SETTINGS), button -> {
            resetPages();
            BoleClient boleClient = BoleClient.getInstance();
            Page page = this.pages.get(0);
            String setConfigKey = boleClient.isHost() ? Keys.TEXT_SET_CONFIGS_LOCAL_IS_REMOTE : Keys.TEXT_SET_CONFIGS_LOCAL_IS_NOT_REMOTE;
            page.addSlot(new LeftTextPropertyWidget(4, 1, Text.translatable(setConfigKey), DARK_TEXT_COLOR, 0.5F));
            page.addSlot(new OpenLocalConfigsPropertyWidget());
            page.addSlot(new EmptyPropertyWidget(4, 1));
            page.addSlot(new LeftTextPropertyWidget(4, 1, Text.translatable(Keys.TEXT_OTHER_CLIENT_CONFIGS), DARK_TEXT_COLOR, 0.5F));
            page.addSlot(new CustomEntityOrderPropertyWidget());

            page = new Page();
            this.pages.add(page);
            String curConfigKey = boleClient.isHost() ? Keys.TEXT_GET_CONFIGS_LOCAL_IS_REMOTE : Keys.TEXT_GET_CONFIGS_LOCAL_IS_NOT_REMOTE;
            page.addSlot(new LeftTextPropertyWidget(4, 1, Text.translatable(Keys.TEXT_SERVER_MOD_VERSION, Bole.getInstance().getServerVersion()), DARK_TEXT_COLOR, 0.5F));
            page.addSlot(new LeftTextPropertyWidget(4, 1, Text.translatable(curConfigKey), DARK_TEXT_COLOR, 0.5F));
            for (Field field : Configs.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigEntry.Gui.Excluded.class)) continue;
                if (Configs.CLIENT.equals(field.getAnnotation(ConfigEntry.Category.class).value())) continue;
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
        addBookmark(9, Text.translatable(Keys.TEXT_ABOUT), button -> {
            resetPages();
            this.pages.add(new Page());
            Page page0 = this.pages.get(0);
            Page page1 = this.pages.get(1);
            page0.addSlot(new CenteredTextPropertyWidget(4, 1, Text.translatable(Keys.TEXT_MOD_NAME_IS, Text.translatable(Keys.MOD_NAME)), DARK_TEXT_COLOR, 0.5F));
            page0.addSlot(new CenteredTextPropertyWidget(4, 1, Text.translatable(Keys.TEXT_MOD_AUTHOR_IS, Text.translatable(Keys.AUTHOR_TRANS)), DARK_TEXT_COLOR, 0.5F));
            FabricLoader.getInstance().getModContainer(Keys.BOLE).ifPresent(modContainer -> page0.addSlot(new CenteredTextPropertyWidget(4, 1, Text.translatable(Keys.TEXT_MOD_VERSION_IS, modContainer.getMetadata().getVersion()), DARK_TEXT_COLOR, 0.5F)));
            page1.addSlot(new OpenDebugPropertyWidget());
            page1.addSlot(new DebugGiveBookPropertyWidget());
            page1.addSlot(new DebugReorderPropertyWidget());
            page1.addSlot(new DebugClearMojangApiPropertyWidget());
            page1.addSlot(new DebugCrashClientPropertyWidget());
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
        this.pages.get(0).addSlot(new LeftTextPropertyWidget(4, 1, Text.translatable(group.getName() + ".description"), DARK_TEXT_COLOR, 0.5F));
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
            this.text = Text.translatable(tag.getName()).append(" (" + tag.getEntities().size() + ")");
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            int xBase = x + this.sub * TAB;
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), xBase, y, 220, 40);
            drawTextureNormally(matrices, 256, 256, 30, 10, getZOffset(), xBase + 10, y, 0, 200);
            drawName(matrices, 0xb0222222);
        }

        @Override
        public void drawHovered(MatrixStack matrices, int mouseX, int mouseY) {
            super.drawHovered(matrices, mouseX, mouseY);
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
            playScreenSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, 0.5F, 1.5F);
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
            if (tmp == null) tmp = EntityType.ARMOR_STAND.create(MinecraftClient.getInstance().world);
            if (tmp instanceof FishEntity) ((IMixinEntity) tmp).setTouchingWater(true);
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
        protected void initTooltipLines() {
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_HANDBOOK_ENTITY_DESCRIPTION_BUTTON1);
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_HANDBOOK_ENTITY_DESCRIPTION_BUTTON2);
        }

        @Override
        protected void drawTooltip(MatrixStack matrices) {
            boolean d = debugMode;
            if (d) {
                this.tooltipLines.add(Text.translatable(EntityType.getId(this.entity.getType()).toString()).formatted(Formatting.GRAY).asOrderedText());
                this.tooltipLines.add(this.widgetClassText);
            }
            int maxWidth = 0;
            for (OrderedText line : this.tooltipLines) {
                maxWidth = Math.max(maxWidth, textRenderer.getWidth(line));
            }
            renderTooltip(matrices, this.tooltipLines, 0.5F, (this.box.left() + this.box.right() >> 1) - (maxWidth >> 2), this.box.bottom());
            if (d) {
                this.tooltipLines.remove(this.tooltipLines.size() - 1);
                this.tooltipLines.remove(this.tooltipLines.size() - 1);
            }
        }

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
                boolean whichButton = mouseY >= this.box.top() + BUTTONS_CUT;
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
            if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return true;
            if (mouseY < this.box.top() + BUTTONS_CUT) {
                PlayerEntity player = handler.player;
                String settingId = Keys.ENTITY_SETTING_HIGHLIGHT_ENTITIES;
                if (debugMode) {
                    player.sendMessage(Text.translatable(Keys.TEXT_CURRENT_FEATURE_REQUEST, settingId).formatted(Formatting.YELLOW), false);
                }
                if (Bole.getInstance().getServerConfigs().isEntitySettingBanned(settingId)) {
                    showOverlayMessage(Text.translatable(Keys.TEXT_FEATURE_REQUEST_BANNED_FROM_SERVER, settingId));
                    return true;
                }
                if (!(isDetached()) && player.totalExperience < BoleHomepageScreenHandler.HIGHLIGHT_EXPERIENCE_COST) {
                    showOverlayMessage(Text.translatable(Keys.HINT_TEXT_HIGHLIGHT_NOT_ENOUGH_EXPERIENCE));
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
                playScreenSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 0.6F, -10.0F);
                player.sendMessage(Text.translatable(Keys.TEXT_HIGHLIGHT, cnt.get(), Text.translatable(entityType.getTranslationKey()), (int) Math.sqrt(dis2)).formatted(Formatting.DARK_GREEN, Formatting.BOLD), true);
                ClientNetworkManager.sendHighlightEvent(Configs.getInstance().getHighlightEntitiesBlindnessTime());
                close();
                return true;
            }
            else if (mouseY < this.box.bottom() - 6) {
                if (isGod()) {
                    showOverlayMessage(Text.translatable(Keys.HINT_TEXT_OFFER_OR_DROP, Text.translatable(this.spawnEgg.getTranslationKey())));
                    handler.sendClientEntitySettings(Keys.ENTITY_SETTING_OFFER_OR_DROP_GOD_MODE_ONLY, new ItemStack(this.spawnEgg.getItem()));
                }
                else showOverlayMessage(Text.translatable(Keys.HINT_TEXT_ONLY_IN_GOD_MODE));
                return true;
            }
            return false;
        }

        /**
         * @see net.minecraft.client.gui.screen.ingame.InventoryScreen#drawEntity
         */
        private void drawEntity() {
            float x = this.box.left() + (this.box.width() >> 1);
            float y = this.box.bottom() - Page.PROPERTY_WIDGET_MARGIN_HEIGHT - entitySize * (float) this.entity.getBoundingBox().getXLength();
            int t = (int) (System.currentTimeMillis() % 8000);
            t = t > 4000 ? 6000 - t : t - 2000;
            float f = (float) Math.atan(t / 420.0F) * 6F;
            float g = -45;
            entity.bodyYaw = 180.0F + f * 2;
            entity.setYaw(180.0F - f);
            entity.setPitch(g);
            entity.headYaw = entity.getYaw();
            entity.prevHeadYaw = entity.getYaw();
            drawEntityGeneric(entity, entitySize, x, y, g, 0, 0);
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
            this.name = Text.translatable(Keys.AUTO_CONFIG_PREFIX + attr);
            initTooltipDescription(Keys.AUTO_CONFIG_PREFIX + attr + Keys.AUTO_CONFIG_POSTFIX);
        }

        @Override
        protected void initTooltipLines() {
            // "attr" isn't accessible here   x   initTooltipDescription(Keys.AUTO_CONFIG_PREFIX + attr);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            Text valueText;
            try {
                if (List.class.isAssignableFrom(this.field.getType())) {
                    int value = ((List<?>) this.field.get(Bole.getInstance().getServerConfigs())).size();
                    valueText = Text.translatable(Keys.TEXT_NUMBER_OF_ELEMENTS, value);
                }
                else {
                    String value = this.field.get(Bole.getInstance().getServerConfigs()).toString();
                    valueText = Text.translatable(value);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                valueText = Text.translatable(Keys.ERROR_TEXT_DATA_LOAD);
            }
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 30, 10, getZOffset(), x, y, 0, 210);
            drawTextureRotated180(matrices, 256, 256, 30, 10, getZOffset(), x + this.box.width() - 30, y, 0, 210);
            drawText(matrices, this.name, 0xff003e6a, 0.5F, this.box.left() + 2, this.box.top() + 3F);
            drawText(matrices, valueText, 0xff0162a6, 0.5F, this.box.right() - (textRenderer.getWidth(valueText) >> 1) - 3, this.box.top() + 3F);
        }
    }

    public class OpenLocalConfigsPropertyWidget extends AbstractPropertyWidget {
        private final Text title;
        public OpenLocalConfigsPropertyWidget() {
            super(4, 1);
            this.title = Text.translatable(Keys.TEXT_OPEN_LOCAL_CONFIGS);
        }

        protected OpenLocalConfigsPropertyWidget(Text title) {
            super(4, 1);
            this.title = title;
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            int v = isHovered() ? 220 : 230;
            drawTextureNormally(matrices, 256, 256, 30, 10, getZOffset(), x, y, 0, v);
            drawTextureRotated180(matrices, 256, 256, 30, 10, getZOffset(), x + this.box.width() - 30, y, 0, v);
            drawTextCenteredX(matrices, this.title, DARK_TEXT_COLOR, 0.5F, this.box.left() + this.box.right() >> 1, this.box.top() + 3F);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            setHovered(null);
            assert client != null;
            playScreenSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, 0.5F, 1.5F);
            client.setScreen(ScreenManager.getConfigScreen(BoleHomepageScreen.this));
            return true;
        }
    }

    public class CustomEntityOrderPropertyWidget extends OpenLocalConfigsPropertyWidget {
        public CustomEntityOrderPropertyWidget() {
            super(Text.translatable(Keys.TEXT_CUSTOM_ENTITY_ORDER_CONFIG));
        }

        @Override
        protected void initTooltipLines() {
            Path orderPath = Keys.ENTITY_SORT_ORDER_CONFIG_PATH();
            MutableText text = Text.translatable(Keys.TEXT_CUSTOM_ENTITY_ORDER_CONFIG_DESCRIPTION, orderPath.toAbsolutePath().toString())
                    .formatted(Formatting.GRAY);
            List<OrderedText> lines = MinecraftClient.getInstance().textRenderer.wrapLines(text, 6000);
            this.tooltipLines.addAll(lines);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            playScreenSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, 0.5F, 1.5F);
            Path orderPath = Keys.ENTITY_SORT_ORDER_CONFIG_PATH();
            Util.getOperatingSystem().open(orderPath.toFile());
            setPopup(new PopUpConfirmWindow(Text.translatable(Keys.WARNING_TEXT_ENTITY_REORDER_DONE, orderPath.getFileName().toString()), () -> {
                EntityManager.getInstance().reorderAllEntities();
                showOverlayMessage(Keys.HINT_TEXT_ENTITY_REORDER_DONE);
            }));
            return true;
        }
    }

    public class OpenDebugPropertyWidget extends AbstractPropertyWidget {
        public OpenDebugPropertyWidget() {
            super(4, 1);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (debugMode) {
                drawTextCenteredX(matrices, "-= DO NOT CLICK ANY OF THEM =-", 0xffcc2222, 0.5F, this.box.left() + this.box.right() >> 1, this.box.top() + 3F);
            } else {
                drawTextCenteredX(matrices, "-= Bole Debug Mode (Press R-ALT) =-", 0xffcc2222, 0.5F, this.box.left() + this.box.right() >> 1, this.box.top() + 3F);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            debugMode = !debugMode;
            playScreenSound(SoundEvents.UI_BUTTON_CLICK, 0.5F, 1.0F);
            return true;
        }
    }

    public abstract class DebugPropertyWidget extends AbstractPropertyWidget {
        private final String text;

        public DebugPropertyWidget(String text) {
            super(4, 1);
            this.text = text;
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (!debugMode) {
                return;
            }
            drawTextCenteredX(matrices, this.text, 0xff22cc22, 0.5F, this.box.left() + this.box.right() >> 1, this.box.top() + 3F);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!debugMode || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            boolean ret = onMouseClick();
            playScreenSound(SoundEvents.UI_BUTTON_CLICK, 0.6F, 1.0F);
            return ret;
        }

        protected abstract boolean onMouseClick();
    }

    public class DebugGiveBookPropertyWidget extends DebugPropertyWidget {

        public DebugGiveBookPropertyWidget() {
            super("-= Get a Bole Handbook =-");
        }

        @Override
        protected boolean onMouseClick() {
            if (isGod()) {
                if (client != null && client.player != null) {
                    client.player.getInventory().insertStack(BoleHandbookItem.createBook());
                }
                ClientNetworkManager.requestBoleHandbook();
                playScreenSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }
            else showOverlayMessage(Keys.HINT_TEXT_ONLY_IN_GOD_MODE);
            return true;
        }
    }

    public class DebugReorderPropertyWidget extends DebugPropertyWidget {
        public DebugReorderPropertyWidget() {
            super("-= Reorder Entities In Homepage =-");
        }

        @Override
        public boolean onMouseClick() {
            EntityManager.getInstance().reorderAllEntities();
            return true;
        }
    }

    public class DebugClearMojangApiPropertyWidget extends DebugPropertyWidget {
        public DebugClearMojangApiPropertyWidget() {
            super("-= Clear Mojang APIs =-");
        }

        @Override
        public boolean onMouseClick() {
            PlayerDataCacheManager.getInstance().debugClear();
            return true;
        }
    }

    public class DebugCrashClientPropertyWidget extends DebugPropertyWidget {
        public DebugCrashClientPropertyWidget() {
            super("-= Crash the Client =-");
        }

        @Override
        public boolean onMouseClick() {
            throw new NullPointerException("Surprise~~");
        }
    }
}
