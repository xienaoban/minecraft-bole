package xienaoban.minecraft.bole.gui.screen.tree;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class BoleAbstractHorseEntityScreen<E extends AbstractHorseEntity, H extends BoleAbstractHorseEntityScreenHandler<E>> extends BoleAnimalEntityScreen<E, H> {
    public BoleAbstractHorseEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new HorseRunAndJumpPropertyWidget(), AirPropertyWidget.class);
        this.pages.get(1).addSlotLazyAfter(new TamePropertyWidget(), BabyPropertyWidget.class);
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

    public class TamePropertyWidget extends AbstractTamePropertyWidget {
        @Override
        protected boolean isTame() {
            return handler.entity.isTame();
        }

        @Override
        protected UUID getOwnerUuid() {
            return handler.entity.getOwnerUuid();
        }
    }

    /**
     * Wiki: <br>
     * Horse's movement speed ranges from 0.1125–0.3375 in internal units, with an average of 0.225. For reference, the
     * player's normal walking speed is 0.1. The speed listed does not include any status effect that affects the speed
     * of the horse or the player. <br/>
     * A horse's maximum speed is 14.23 blocks/second, and the average horse speed is about 9 blocks/sec. <br/>
     * Minimum: 4.74 blocks/sec. <br/>
     * Player speed (walking): 4.32 blocks/sec. <br/>
     * Player speed (sprinting): 5.61 blocks/sec. <br/>
     *
     * Horse's jump strength ranges from 0.4–1.0, with an average of 0.7. <br/>
     * A jump strength of 0.5 is enough to clear 25⁄16 blocks, while 1.0 is enough to clear 11⁄2 blocks. <br/>
     * (Min: 1.0862309923172m | Max: 5.292624264955521m)
     *
     * The conversion algorithm is from <strong>hwyla-addon-horseinfo</strong>.
     */
    public class HorseRunAndJumpPropertyWidget extends AbstractPropertyWidget {
        private static final double RUN_LOW = 7, RUN_MID = 11;
        private static final double JUMP_LOW = 2, JUMP_MID = 4;
        private final double runningSpeed, jumpHeight;
        private final int ru, rv, ju, jv, rw, jw;

        public HorseRunAndJumpPropertyWidget() {
            super(2, 2);
            this.runningSpeed = Objects.requireNonNull(handler.entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getBaseValue() * 42.157787584f;
            this.rw = Math.min((int)(this.runningSpeed / 14.23 * 40), 39);
            if (this.runningSpeed < RUN_LOW) {
                this.ru = 10;
                this.rv = 70;
            }
            else if (this.runningSpeed < RUN_MID) {
                this.ru = 50;
                this.rv = 70;
            }
            else {
                this.ru = 10;
                this.rv = 80;
            }
            double jumpStrength = handler.entity.getJumpStrength();
            this.jumpHeight = -0.1817584952f * Math.pow(jumpStrength, 3) + 3.689713992f * Math.pow(jumpStrength, 2) + 2.128599134f * jumpStrength - 0.343930367f;
            this.jw = Math.min((int)(this.jumpHeight / 5.29 * 40), 39);
            if (this.jumpHeight < JUMP_LOW) {
                this.ju = 10;
                this.jv = 70;
            }
            else if (this.jumpHeight < JUMP_MID) {
                this.ju = 50;
                this.jv = 70;
            }
            else {
                this.ju = 10;
                this.jv = 80;
            }
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_HORSE_RUN_AND_JUMP);
            initTooltipDescription(Keys.PROPERTY_WIDGET_HORSE_RUN_AND_JUMP_DESCRIPTION);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            final int hhh = Page.PROPERTY_WIDGET_HEIGHT + Page.PROPERTY_WIDGET_MARGIN_HEIGHT;
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 0, 70);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y + hhh, 0, 80);
            drawTextureNormally(matrices, 256, 256, this.rw, 10, getZOffset(), x + 11, y, this.ru, this.rv);
            drawTextureNormally(matrices, 256, 256, this.jw, 10, getZOffset(), x + 11, y + hhh, this.ju, this.jv);
            drawTextureNormally(matrices, 256, 256, 1, 10, getZOffset(), x + 11 + this.rw, y, this.ru + 39, this.rv);
            drawTextureNormally(matrices, 256, 256, 1, 10, getZOffset(), x + 11 + this.jw, y + hhh, this.ju + 39, this.jv);
            String run = debugMode ? String.format("%.3f", Objects.requireNonNull(handler.entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).getBaseValue())
                    : String.format("%.2fm/s", this.runningSpeed);
            String jump = debugMode ? String.format("%.3f", handler.entity.getJumpStrength())
                    : String.format("%.2fm", this.jumpHeight);
            drawText(matrices, run, LIGHT_TEXT_COLOR, 0.5F, x + 13, y + 3.25F);
            drawText(matrices, jump, LIGHT_TEXT_COLOR, 0.5F, x + 13, y + hhh + 3.25F);
        }
    }
}
