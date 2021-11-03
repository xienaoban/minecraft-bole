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
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.mixin.IMixinEntity;
import xienaoban.minecraft.bole.util.Keys;

@Environment(EnvType.CLIENT)
public class BoleEntityScreen<E extends Entity, H extends BoleEntityScreenHandler<E>> extends AbstractBoleScreen<E, H> {
    protected int entityDisplayPlan;

    public BoleEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initCustom() {
        this.curRightPage.addSlot(new CustomNameContentWidget());
        this.curRightPage.addSlot(new BoundingBoxContentWidget());
        this.curRightPage.addSlot(new NetherPortalCooldownContentWidget());
        this.curRightPage.setSlot(5, 0, new CenteredTextContentWidget(2, 2, new TranslatableText(Keys.TEXT_UNSUPPORTED_ENTITY), 0xaa666666, 1.0F));
    }

    @Override
    protected void drawLeftContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        drawEntityAuto(this.handler.entity, x + 26, y + 8, x + CONTENT_WIDTH - 26, y + (CONTENT_HEIGHT >> 1) + 12, mouseX + 0.001F, mouseY + 0.001F);
    }

    @Override
    protected void drawRightContent(MatrixStack matrices, float delta, int x, int y, int mouseX, int mouseY) {
        this.curRightPage.draw(matrices, x, y, mouseX, mouseY);
    }

    protected int chooseEntityDisplayPlan(ContentWidgets widgets) {
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
            case 1: left = 1; top = 0; width = 1; height = 4; break;
            case 2: left = 0; top = 0; width = 1; height = 5; break;
            case 3: left = 0; top = 3; width = 2; height = 2; break;
            case 4: left = 0; top = 0; width = 1; height = 8; break;
            default: left = 0; top = 0; width = 2; height = 6; break;
        }
        widgets.setSlot(top, left, new DisplayedEntityContentWidget(height, width, this.handler.entity));
        return plan;
    }

    public class DisplayedEntityContentWidget extends AbstractContentWidget {
        private Entity displayedEntity, targetEntity;

        public DisplayedEntityContentWidget(int rowSlots, int colSlots, Entity targetEntity) {
            super(rowSlots, colSlots);
            setTargetEntity(targetEntity);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawEntityAuto(this.displayedEntity, x + 2, y, x + this.widgetWidth - 2, y + this.widgetHeight - 4,
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

    public class BoundingBoxContentWidget extends AbstractContentWidget {

        public BoundingBoxContentWidget() {
            super(1, 1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            E entity = handler.entity;
            Box box = entity.getBoundingBox();
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 90, 0);
            if (Math.abs(box.getXLength() - box.getZLength()) < 0.01) {
                drawText(matrices, String.format("X/Z:%.2f Y:%.2f", box.getXLength(), box.getYLength()), CONTENT_TEXT_COLOR, 0.5F, x + 12, y + 3.25F);
            } else {
                drawText(matrices, String.format("X:%.2f Y:%.2f", box.getXLength(), box.getYLength()), CONTENT_TEXT_COLOR, 0.5F, x + 12, y + 1.25F);
                drawText(matrices, String.format("Z:%.2f", box.getZLength()), CONTENT_TEXT_COLOR, 0.5F, x + 12, y + 5.25F);
            }
        }
    }

    public class NetherPortalCooldownContentWidget extends AbstractContentWidget {
        public NetherPortalCooldownContentWidget() {
            super(1, 1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int cooldown = ((IMixinEntity)handler.entity).getNetherPortalCooldown();
            boolean lock = cooldown == Keys.NETHER_PORTAL_LOCK;
            float p = Math.min(1.0F, (float)cooldown / handler.entity.getDefaultNetherPortalCooldown());
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 0, 30);
            drawTextureNormally(matrices, 256, 256, 33, 10, getZOffset(), x + 11, y, 10, 30);
            drawTextureNormally(matrices, 256, 256, 33.0F * p, 10, getZOffset(), x + 11, y, 50, 30);
            drawTextureNormally(matrices, 256, 256, 7, 10, getZOffset(), x + 44, y, 43 + (lock ? 40 : 0), 30);
            String text;
            if (lock) {
                text = "∞";
            }
            else if (debugMode) {
                text = ((IMixinEntity)handler.entity).getNetherPortalCooldown() + "ticks";
            }
            else {
                text = (((IMixinEntity)handler.entity).getNetherPortalCooldown() / 20) + "s";
            }
            drawText(matrices, text, 0xbbffffff, 0.5F, x + 13, y + 3.25F);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double offsetX = mouseX - this.elementBox.left();
            if (offsetX >= 44 && offsetX <= 51 && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                int cooldown;
                if (((IMixinEntity)handler.entity).getNetherPortalCooldown() == Keys.NETHER_PORTAL_LOCK) {
                    cooldown = 0;
                }
                else {
                    cooldown = Keys.NETHER_PORTAL_LOCK;
                }
                handler.sendClientEntitySettings(Keys.ENTITY_SETTING_NETHER_PORTAL_COOLDOWN, cooldown);
            }
            return true;
        }
    }

    public class CustomNameContentWidget extends AbstractContentWidget {
        private Text lastCustomName;
        private Text cacheText;
        private int cacheColor;

        public CustomNameContentWidget() {
            super(1, 1);
            this.lastCustomName = new LiteralText(""); // not null
            this.cacheText = null;
            this.cacheColor = 0xff000000;
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 0, 40);
            drawTextureNormally(matrices, 256, 256, 33, 10, getZOffset(), x + 11, y, 10, 40);
            setCacheText();
            drawText(matrices, this.cacheText, this.cacheColor, 0.5F, x + 13, y + 3.25F);
        }

        public void setCacheText() {
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
}
