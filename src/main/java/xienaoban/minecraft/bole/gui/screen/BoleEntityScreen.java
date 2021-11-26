package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleEntityScreen<E extends Entity, H extends BoleEntityScreenHandler<E>> extends AbstractBoleScreen<E, H> {
    protected int entityDisplayPlan;

    public BoleEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        this.entityDisplayPlan = chooseEntityDisplayPlan(this.pages.get(0));
        this.pages.get(0).addSlotLazy(new BoundingBoxPropertyWidget());
        this.pages.get(1).addSlotLazy(new CustomNamePropertyWidget())
                .addSlotLazy(new SilentPropertyWidget())
                .addSlotLazy(new NetherPortalCooldownPropertyWidget());
    }

    @Override
    protected void initCustom() {
        this.pages.get(1).setSlot(0, 5, new CenteredTextPropertyWidget(4, 2, new TranslatableText(Keys.TEXT_UNSUPPORTED_ENTITY), 0xaa666666, 1.0F));
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
            case 1: left = 2; top = 0; width = 2; height = 4; break;
            case 2: left = 0; top = 0; width = 2; height = 5; break;
            case 3: left = 0; top = 3; width = 4; height = 2; break;
            case 4: left = 0; top = 0; width = 2; height = 7; break;
            default: left = 0; top = 0; width = 4; height = 6; break;
        }
        page.setSlot(left, top, new DisplayedEntityPropertyWidget(width, height, this.handler.entity));
        return plan;
    }

    /**
     * A widget that displays the target entity. <br/>
     * The entity can be rotated according to the mouse.
     */
    public class DisplayedEntityPropertyWidget extends AbstractPropertyWidget {
        private Entity displayedEntity, targetEntity;

        public DisplayedEntityPropertyWidget(int colSlots, int rowSlots, Entity targetEntity) {
            super(colSlots, rowSlots);
            setTargetEntity(targetEntity);
        }

        @Override
        protected void initTooltipLines() {}

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawEntityAuto(this.displayedEntity, x + 2, y, x + this.box.width() - 2, y + this.box.height() - 4,
                    (mouseX) / 33.0F + 0.0001F, (mouseY) / 53.0F + 5.0F);
        }

        public void updateDisplayedEntity() {
            NbtCompound nbt = this.targetEntity.writeNbt(new NbtCompound());
            nbt.remove("Dimension");
            nbt.remove("Rotation");
            nbt.remove("CustomName");
            nbt.remove("CustomNameVisible");
            nbt.remove("AngryAt");
            try {
                this.displayedEntity.readNbt(nbt);
            }
            catch (Exception e) {
                Bole.LOGGER.warn("Cannot copy nbt of [" + this.targetEntity.getType().getTranslationKey() + "]: " + e);
            }
        }

        public void setTargetEntity(Entity targetEntity) {
            this.targetEntity = targetEntity;
            this.displayedEntity = targetEntity.getType().create(MinecraftClient.getInstance().world);
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
            drawBar(matrices, 10, 50, 1.0F);
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
            boolean lock = cooldown == Keys.NETHER_PORTAL_LOCK;
            float p = Math.min(1.0F, (float)cooldown / handler.entity.getDefaultNetherPortalCooldown());
            drawIcon(matrices, 0, 30);
            drawBar(matrices, 10, 30, 1.0F);
            drawBar(matrices, 50, 30, p);
            drawButton(matrices, 200 + (lock ? 10 : 0), 0, 0);
            String text;
            if (lock) {
                text = "∞";
            }
            else if (debugMode) {
                text = ((IMixinEntity)handler.entity).getNetherPortalCooldown() + "t";
            }
            else {
                text = (((IMixinEntity)handler.entity).getNetherPortalCooldown() / 20) + "s";
            }
            drawBarText(matrices, text, LIGHT_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            int cooldown;
            if (((IMixinEntity)handler.entity).getNetherPortalCooldown() == Keys.NETHER_PORTAL_LOCK) {
                cooldown = 0;
            }
            else {
                cooldown = Keys.NETHER_PORTAL_LOCK;
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
            drawBar(matrices, 10, 40, 1.0F);
            drawButton(matrices, 220 + (handler.entity.isCustomNameVisible() ? 0 : 10), 0, 0);
            setCacheText();
            drawBarText(matrices, this.cacheText, this.cacheColor);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
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
        private boolean silentCache;            // to ensure that the button pattern responds in the first time
        private int silentSwitchCacheTicks;     // (rather than waiting for the response of the server)

        public SilentPropertyWidget() {
            super(1, false, 1);
            this.silentCache = false;
            this.silentSwitchCacheTicks = -233;
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_SILENT);
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_SILENT_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 100, 0);
            drawButton(matrices, 200 + (isCurrentSilent() ? 10 : 0), 10, 0);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            boolean newState = !isCurrentSilent();
            this.silentCache = newState;
            this.silentSwitchCacheTicks = BoleClient.getInstance().getTicks() + 8;
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_SILENT, newState);
            return true;
        }

        private boolean isCurrentSilent() {
            return this.silentSwitchCacheTicks > BoleClient.getInstance().getTicks()
                    ? this.silentCache : handler.entity.isSilent();
        }
    }
}
