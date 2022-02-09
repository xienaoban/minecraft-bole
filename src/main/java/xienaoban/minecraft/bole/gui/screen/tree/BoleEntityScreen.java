package xienaoban.minecraft.bole.gui.screen.tree;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.BoleClient;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.gui.screen.AbstractBoleScreen;
import xienaoban.minecraft.bole.gui.screen.homepage.BoleHomepageScreenState;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.network.ClientNetworkManager;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleEntityScreen<E extends Entity, H extends BoleEntityScreenHandler<E>> extends AbstractBoleScreen<E, H> {
    protected int entityDisplayPlan;
    protected DisplayedEntityPropertyWidget targetDisplayedEntityPropertyWidget;

    public BoleEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        this.entityDisplayPlan = chooseEntityDisplayPlan(this.pages.get(0));
        this.pages.get(0).addSlotLazy(new AirPropertyWidget())
                .addSlotLazy(new BoundingBoxPropertyWidget());
        this.pages.get(1).addSlotLazy(new InvulnerablePropertyWidget())
                .addSlotLazy(new SilentPropertyWidget())
                .addSlotLazy(new CustomNamePropertyWidget())
                .addSlotLazy(new NetherPortalCooldownPropertyWidget());
    }

    @Override
    protected void initCustom() {
        this.pages.get(1).setSlot(0, 5, new CenteredTextPropertyWidget(4, 2, new TranslatableText(Keys.TEXT_UNSUPPORTED_ENTITY), 0xaa666666, 1.0F));
    }

    @Override
    protected void initButtons() {
        super.initButtons();
        addBookmark(0, new TranslatableText(Keys.TEXT_RETURN_TO_HOMEPAGE), button -> {
            BoleClient.getInstance().setHomepageScreenState(new BoleHomepageScreenState());
            ClientNetworkManager.requestBoleScreen();
        });
        addBookmark(8, new TranslatableText(Keys.TEXT_SETTINGS), button -> {
            BoleClient.getInstance().setHomepageScreenState(new BoleHomepageScreenState(8, 0));
            ClientNetworkManager.requestBoleScreen();
        });
        addBookmark(9, new TranslatableText(Keys.TEXT_ABOUT), button -> {
            BoleClient.getInstance().setHomepageScreenState(new BoleHomepageScreenState(9, 0));
            ClientNetworkManager.requestBoleScreen();
        });
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        setTexture(Textures.ILLUSTRATIONS);
        drawTextureNormally(matrices, 64, 64, 32, 32, getZOffset(), x + 75, y + 102, this.handler.isMonster ? 32 : 0, 0);
        super.drawLeftContent(matrices, delta, x, y, mouseX, mouseY);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        setTexture(Textures.ILLUSTRATIONS);
        drawTextureNormally(matrices, 64, 64, 16, 16, getZOffset(), x + 94, y  - 12, this.handler.isMonster ? 16 : 0, 32);
        super.drawRightContent(matrices, delta, x, y, mouseX, mouseY);
    }

    protected int chooseEntityDisplayPlan(Page page) {
        Box box = this.handler.entity.getVisibilityBoundingBox();
        double x = box.getXLength(), y = box.getYLength();
        double area = x * y, ratio = y / x;
        int plan;
        if (ratio < 1 / 2.2) {
            if (area < 0.4) plan = 1;       // small
            else plan = 3;                  // flat
        }
        else {
            if (area < 0.5) plan = 1;       // small
            else if (ratio > 2.5) plan = 4; // tall
            else plan = 2;                  // median
        }
        int left, top, width, height;
        switch (plan) {
            case 1 -> { left = 2; top = 0; width = 2; height = 4; }
            case 2 -> { left = 0; top = 0; width = 2; height = 5; }
            case 3 -> { left = 0; top = 3; width = 4; height = 2; }
            case 4 -> { left = 0; top = 0; width = 2; height = 7; }
            default -> { left = 0; top = 0; width = 4; height = 6; }
        }
        this.targetDisplayedEntityPropertyWidget = new DisplayedEntityPropertyWidget(width, height, this.handler.entity);
        page.setSlot(left, top, this.targetDisplayedEntityPropertyWidget);
        return plan;
    }

    public abstract class VariantsPropertyWidget extends AbstractPropertyWidget {
        private final E[] variants;
        private final Text[] names;
        private final int variantsSize;
        private final int eachWidth, margin;
        private int lastChosen;

        public VariantsPropertyWidget(int colSlots, int rowSlots) {
            super(colSlots, rowSlots);
            this.variants = initEntities();
            this.variantsSize = this.variants.length;
            this.names = initNames();
            this.eachWidth = this.box.width() / this.variantsSize;
            this.margin = ((this.box.width() % this.variantsSize) >> 1) + 1;
            this.lastChosen = -1;
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            boolean nothingChosen = true;
            for (int i = 0; i < this.variantsSize; ++i) {
                E entity = this.variants[i];
                int xx = x + this.eachWidth * i + this.margin;
                drawEntity(matrices, entity, xx, y, xx + this.eachWidth, this.box.bottom() - 10, mouseX, mouseY);
                drawName(matrices, this.names[i], xx + this.eachWidth / 2, this.box.bottom() - 8);
                if (isChosen(this.variants[i])) {
                    nothingChosen = false;
                    if (this.lastChosen != i) {
                        if (this.lastChosen != -1 && targetDisplayedEntityPropertyWidget != null) {
                            targetDisplayedEntityPropertyWidget.updateDisplayedEntity();
                        }
                        this.lastChosen = i;
                    }
                    drawSelectedTick(matrices, i, true);
                }
            }
            if (nothingChosen) this.lastChosen = -2;
            if (isHovered() && canChoose()) {
                int i = calIndex(mouseX, mouseY);
                if (i >= 0 && i < this.variantsSize && !isChosen(this.variants[i])) {
                    drawSelectedTick(matrices, i, false);
                }
            }
        }

        private void drawSelectedTick(MatrixStack matrices, int index, boolean selected) {
            setTexture(Textures.ICONS);
            int xx = this.box.left() + this.eachWidth * index + this.margin + this.eachWidth / 2 - 5;
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), xx, this.box.bottom() - 18, 210 - (selected ? 10 : 0), 20);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calIndex((int) mouseX, (int) mouseY);
            if (index < 0 || index >= this.variantsSize || !canChoose()) {
                return false;
            }
            setChosen(this.variants[index]);
            return true;
        }

        private int calIndex(int mouseX, int mouseY) {
            if (mouseY < this.box.top() + 4 || mouseY > this.box.bottom() - 4) {
                return -1;
            }
            return (mouseX - this.margin - this.box.left()) / this.eachWidth;
        }

        protected abstract E[] initEntities();
        protected abstract Text[] initNames();
        protected abstract boolean canChoose();
        protected abstract boolean isChosen(E fake);
        protected abstract void setChosen(E fake);

        protected void drawEntity(MatrixStack matrices, E fake, int x0, int y0, int x1, int y1, int mouseX, int mouseY) {
            drawEntityAuto(fake, x0, y0, x1, y1, 0, 10);
        }

        protected void drawName(MatrixStack matrices, Text text, int xMid, int yTop) {
            drawTextCenteredX(matrices, text, 0xaa220000, 0.5F, xMid, yTop);
        }
    }

    /**
     * A widget that displays the target entity. <br/>
     * The entity can be rotated according to the mouse.
     */
    public class DisplayedEntityPropertyWidget extends AbstractPropertyWidget {
        private static final int UPDATE_TICKS = 20;
        private Entity displayedEntity, targetEntity;
        private int nextUpdateTicks;

        public DisplayedEntityPropertyWidget(int colSlots, int rowSlots, Entity targetEntity) {
            super(colSlots, rowSlots);
            setTargetEntity(targetEntity);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (BoleClient.getInstance().getScreenTicks() > this.nextUpdateTicks) {
                updateDisplayedEntity();
            }
            drawEntityAuto(this.displayedEntity, x + 2, y, x + this.box.width() - 2, y + this.box.height() - 4,
                    (mouseX) / 33.0F + 0.0001F, (mouseY) / 53.0F + 5.0F);
        }

        public void updateDisplayedEntity() {
            copyEntityNbtForDisplay(this.targetEntity, this.displayedEntity);
            this.nextUpdateTicks = BoleClient.getInstance().getScreenTicks() + UPDATE_TICKS;
        }

        public void setTargetEntity(Entity targetEntity) {
            this.targetEntity = targetEntity;
            this.displayedEntity = targetEntity.getType().create(MinecraftClient.getInstance().world);
            if (this.displayedEntity == null) {
                if (targetEntity instanceof AbstractClientPlayerEntity clientPlayer) {
                    GameProfile profile = clientPlayer.getGameProfile();
                    this.displayedEntity = new OtherClientPlayerEntity(clientPlayer.clientWorld, new GameProfile(profile.getId(), profile.getName()));
                    // to make name label invisible
                    // @see net.minecraft.client.render.entity.LivingEntityRenderer#hasLabel
                    Vec3d targetPos = targetEntity.getPos();
                    this.displayedEntity.setPosition(targetPos.getX(), targetPos.getY() - 4097, targetPos.getZ());
                }
                else {
                    this.displayedEntity = EntityType.ARMOR_STAND.create(MinecraftClient.getInstance().world);
                }
            }
            updateDisplayedEntity();
        }
    }

    /**
     * A widget that displays the bounding box of the target entity.
     */
    public class BoundingBoxPropertyWidget extends TemplatePropertyWidget1 {
        public BoundingBoxPropertyWidget() {
            super(2, true, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_BOUNDING_BOX);
            initTooltipDescription(Keys.PROPERTY_WIDGET_BOUNDING_BOX_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            Entity entity = handler.entity;
            Box box = entity.getBoundingBox();
            drawIcon(matrices, 0, 50);
            drawBar(matrices, 1.0F, 10, 50);
            drawText(matrices, String.format("%.2f", box.getXLength()), 0xffee3d3d, 0.5F, x + BAR_LEFT + 2, y + TEXT_HEIGHT);
            drawText(matrices, String.format("%.2f", box.getYLength()), 0xff04b904, 0.5F, x + BAR_LEFT + 2 + 13, y + TEXT_HEIGHT);
            drawText(matrices, String.format("%.2f", box.getZLength()), 0xff175fe4, 0.5F, x + BAR_LEFT + 2 + 26, y + TEXT_HEIGHT);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }
    }

    /**
     * A widget that displays the cooldown of the target entity to the nether portals. <br/>
     * You can set the cooldown to zero or infinity.
     */
    public class NetherPortalCooldownPropertyWidget extends TemplatePropertyWidget1 {
        public NetherPortalCooldownPropertyWidget() {
            super(2, true, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_NETHER_PORTAL_COOLDOWN);
            initTooltipDescription(Keys.PROPERTY_WIDGET_NETHER_PORTAL_COOLDOWN_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_NETHER_PORTAL_COOLDOWN_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int cooldown = ((IMixinEntity)handler.entity).getNetherPortalCooldown();
            boolean lock = cooldown == BoleEntityScreenHandler.NETHER_PORTAL_LOCK;
            float p = Math.min(1.0F, (float)cooldown / handler.entity.getDefaultNetherPortalCooldown());
            drawIcon(matrices, 0, 30);
            drawBar(matrices, 1.0F, 10, 30);
            drawBar(matrices, p, 50, 30);
            drawButton(matrices, 0, 200 + (lock ? 10 : 0), 0);
            String text;
            if (debugMode) text = ((IMixinEntity)handler.entity).getNetherPortalCooldown() + "t";
            else if (lock) text = "∞";
            else {
                text = (((IMixinEntity)handler.entity).getNetherPortalCooldown() / 20) + "s";
            }
            drawBarText(matrices, text, LIGHT_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            int cooldown;
            if (((IMixinEntity)handler.entity).getNetherPortalCooldown() == BoleEntityScreenHandler.NETHER_PORTAL_LOCK) {
                cooldown = 0;
            }
            else {
                cooldown = BoleEntityScreenHandler.NETHER_PORTAL_LOCK;
            }
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_NETHER_PORTAL_COOLDOWN, cooldown);
            return true;
        }
    }

    /**
     * A widget that displays the custom name of the target entity. <br/>
     * You can set the custom name to always be displayed.
     */
    public class CustomNamePropertyWidget extends TemplatePropertyWidget1 {
        private Text lastCustomName;
        private Text cacheText;
        private int cacheColor;

        public CustomNamePropertyWidget() {
            super(2, true, 1);
            this.lastCustomName = new LiteralText(""); // not null
            this.cacheText = null;
            this.cacheColor = 0xff000000;
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_CUSTOM_NAME);
            initTooltipDescription(Keys.PROPERTY_WIDGET_CUSTOM_NAME_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_CUSTOM_NAME_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 0, 40);
            drawBar(matrices, 1.0F, 10, 40);
            drawButton(matrices, 0, 220 + (handler.entity.isCustomNameVisible() ? 0 : 10), 0);
            setCacheText();
            drawBarText(matrices, this.cacheText, this.cacheColor);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            Entity entity = handler.entity;
            boolean visible = entity.isCustomNameVisible();
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_CUSTOM_NAME_VISIBLE, !visible);
            return true;
        }

        private void setCacheText() {
            Text customName = handler.entity.getCustomName();
            if (customName == this.lastCustomName) {
                return;
            }
            else {
                this.lastCustomName = customName;
            }
            if (customName == null) {
                this.cacheColor = 0xffccb65d;
                this.cacheText = new TranslatableText(Keys.TEXT_UNNAMED);
            }
            else {
                this.cacheColor = 0xff997617;
                final int maxWidth = 2 * (33 - 2 * 2);
                if (textRenderer.getWidth(customName) > maxWidth) {
                    String trimmed = textRenderer.trimToWidth(customName.asString(), maxWidth - 6, false) + "...";
                    this.cacheText = new LiteralText(trimmed);
                }
                else {
                    this.cacheText = customName;
                }
            }
        }
    }

    /**
     * Allows you to make the entity shut up forever.
     */
    public class SilentPropertyWidget extends TemplatePropertyWidget1 {

        public SilentPropertyWidget() {
            super(1, false, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_SILENT);
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_SILENT_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 100, 0);
            drawButton(matrices, 0, 200 + (isCurrentSilent() ? 10 : 0), 10);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            boolean newState = !isCurrentSilent();
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_SILENT, newState);
            return true;
        }

        private boolean isCurrentSilent() {
            return handler.entity.isSilent();
        }
    }

    public class InvulnerablePropertyWidget extends TemplatePropertyWidget1 {

        public InvulnerablePropertyWidget() {
            super(1, false, 1);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_INVULNERABLE);
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_INVULNERABLE_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 100, 10);
            drawButton(matrices, 0, 200 + (isCurrentInvulnerable() ? 10 : 0), 0);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return false;
            if (isGod()) {
                boolean newState = !isCurrentInvulnerable();
                handler.sendClientEntitySettings(Keys.ENTITY_SETTING_INVULNERABLE, newState);
            }
            else showOverlayMessage(Keys.HINT_TEXT_ONLY_IN_GOD_MODE);
            return true;
        }

        private boolean isCurrentInvulnerable() {
            return handler.entity.isInvulnerable();
        }
    }

    public class AirPropertyWidget extends TemplatePropertyWidget1 {
        public AirPropertyWidget() {
            super(2, true, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_AIR);
            initTooltipDescription(Keys.PROPERTY_WIDGET_AIR_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 0, 20);
            drawBar(matrices, 1.0F, 10, 20);
            drawBar(matrices, handler.entity.getAir() / (float) handler.entity.getMaxAir(), 50, 20);
            String text;
            if (debugMode) text = handler.entity.getAir() + "t/" + handler.entity.getMaxAir() + "t";
            else text = (handler.entity.getAir() / 20) + "s/" + (handler.entity.getMaxAir() / 20) + "s";
            drawBarText(matrices, text, LIGHT_TEXT_COLOR);
        }
    }
}
