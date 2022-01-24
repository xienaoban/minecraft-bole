package xienaoban.minecraft.bole.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xienaoban.minecraft.bole.gui.Textures;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Collection;

@Environment(EnvType.CLIENT)
public class BoleLivingEntityScreen<E extends LivingEntity, H extends BoleLivingEntityScreenHandler<E>> extends BoleEntityScreen<E, H> {
    public BoleLivingEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(0).addSlotLazyAfter(new HealthPropertyWidget(), null);
        this.pages.get(0).addSlotLazyAfter(new StatusEffectsPropertyWidget(), AirPropertyWidget.class);
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

    /**
     * A widget that displays the health (and the max default health) of the target entity. <br/>
     * Each red bar represents 20 blood, and the golden bar represents more than 20 blood.
     */
    public class HealthPropertyWidget extends AbstractPropertyWidget {
        private final int[][] barCache;
        private final int lineCnt;

        public HealthPropertyWidget() {
            super(2, 1);
            this.barCache = new int[3][2];
            int mh = (int) handler.entity.getMaxHealth();
            this.lineCnt = (int) Math.min(Math.ceil(mh / 20.0), 3);
            calBars(0, mh);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_HEALTH);
            initTooltipDescription(Keys.PROPERTY_WIDGET_HEALTH_DESCRIPTION);
            if (handler.entity instanceof HorseBaseEntity) {
                initTooltipDescription(Keys.PROPERTY_WIDGET_HEALTH_DESCRIPTION_HORSE_BASE);
            }
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            LivingEntity entity = handler.entity;
            int health = (int)entity.getHealth();
            int maxHealth = (int)entity.getMaxHealth();
            if (!debugMode && health > maxHealth) {
                health = maxHealth;
            }
            setTexture(Textures.ICONS);
            drawTextureNormally(matrices, 256, 256, 10, 10, getZOffset(), x, y, 0, this.lineCnt > 2 ? 10 : 0);
            calBars(1, health);
            float textOffset = 2.25F + this.lineCnt;
            int barOffset = -1 + this.lineCnt;
            if (maxHealth <= 8) {
                drawCustomLengthHealthBar(matrices, 8, x + 11, y, 10, 0, true);
                drawCustomLengthHealthBar(matrices, 8 * health / maxHealth, x + 11, y, 50, 0, health == maxHealth);
            }
            else if (maxHealth <= 60) {
                for (int i = this.lineCnt - 1; i >= 0; --i) {
                    drawCustomLengthHealthBar(matrices, this.barCache[i][0], x + 11, y + barOffset - (i << 1), 10, 0, true);
                    drawCustomLengthHealthBar(matrices, this.barCache[i][1], x + 11, y + barOffset - (i << 1), 50, 0, health >= maxHealth);
                }
            }
            else {
                drawCustomLengthHealthBar(matrices, 20, x + 11, y + barOffset - 4, 10, 10, true);
                drawCustomLengthHealthBar(matrices, 20 * this.barCache[2][1] / this.barCache[2][0], x + 11, y + barOffset - 4, 50, 10, health == maxHealth);
                for (int i = 1; i >= 0; --i) {
                    drawCustomLengthHealthBar(matrices, this.barCache[i][0], x + 11, y + barOffset - (i << 1), 10, 0, true);
                    drawCustomLengthHealthBar(matrices, this.barCache[i][1], x + 11, y + barOffset - (i << 1), 50, 0, health >= maxHealth);
                }
            }
            drawText(matrices, health + "/" + maxHealth, LIGHT_TEXT_COLOR, 0.5F, x + 13, y + textOffset);
        }

        /**
         * @param health health should not be greater than 20
         */
        protected void drawCustomLengthHealthBar(MatrixStack matrices, int health, int x, int y, int u, int v, boolean close) {
            int w = health << 1;
            if (w > 40) {
                w = 40;
            }
            drawTextureNormally(matrices, 256, 256, w, 10, getZOffset(), x, y, u, v);
            if (close && health < 20) {
                drawTextureNormally(matrices, 256, 256, 1, 10, getZOffset(), x + w, y, u + 39, v);
            }
        }

        private void calBars(int pos, int health) {
            for (int i = 0; i < 3; ++i) {
                int v = Math.min(20, health);
                this.barCache[i][pos] = v;
                health -= v;
            }
            if (health > 0) {
                this.barCache[2][pos] += health;
            }
        }
    }

    public class StatusEffectsPropertyWidget extends TemplatePropertyWidget1 {

        public StatusEffectsPropertyWidget() {
            super(2, false, 0);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_STATUS_EFFECTS);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            drawIcon(matrices, 130, 0);
            drawEffects(matrices);
        }

        @Override
        protected void drawTooltip(MatrixStack matrices) {
            Collection<StatusEffectInstance> effects = handler.entityStatusEffects;
            if (effects.isEmpty()) {
                addTooltipLine(Keys.TEXT_EMPTY_WITH_BRACKETS, Formatting.GRAY);
            }
            else {
                int maxWidth = 0;
                for (StatusEffectInstance effect : effects) {
                    Text text = new TranslatableText(effect.getEffectType().getTranslationKey());
                    maxWidth = Math.max(textRenderer.getWidth(text), maxWidth);
                }
                for (StatusEffectInstance effect : effects) {
                    MutableText text1 = new TranslatableText(effect.getEffectType().getTranslationKey()).append(String.valueOf(effect.getAmplifier() + 1)).formatted(Formatting.WHITE);
                    MutableText text2 = new LiteralText((effect.getDuration() / 20) + "s").formatted(Formatting.GRAY);
                    int w = textRenderer.getWidth(text1) + textRenderer.getWidth(text2);
                    String dot = ".".repeat(Math.max(0, (maxWidth + 40 - w) / 2));
                    this.tooltipLines.add(text1.append(new LiteralText(dot).formatted(Formatting.DARK_GRAY)).append(text2).asOrderedText());
                }
            }
            super.drawTooltip(matrices);
            this.tooltipLines.clear();
            initTooltipLines();
        }

        protected void drawEffects(MatrixStack matrices) {
            Collection<StatusEffectInstance> effects = handler.entityStatusEffects;
            if (effects == null) {
                drawBarText(matrices, new TranslatableText(Keys.TEXT_LOADING), DARK_TEXT_COLOR);
                return;
            }
            if (effects.isEmpty()) {
                drawBarText(matrices, new TranslatableText(Keys.TEXT_EMPTY_WITH_BRACKETS), DARK_TEXT_COLOR);
                return;
            }
            float w = Math.min(9.0F, (this.box.width() - 20.0F) / Math.max(1, effects.size() - 1));
            MinecraftClient client = MinecraftClient.getInstance();
            StatusEffectSpriteManager statusEffectSpriteManager = client.getStatusEffectSpriteManager();
            int i = effects.size() - 1;
            final float size = 8.0F / 18.0F;
            MatrixStack matrixStack = matrixScaleOn(size, size, size);
            for (StatusEffectInstance effectInstance : effects) {
                StatusEffect effect = effectInstance.getEffectType();
                Sprite sprite = statusEffectSpriteManager.getSprite(effect);
                setTexture(sprite.getAtlas().getId());
                drawSprite(matrices, (int)((this.box.left() + i * w + 11) / size), (int)((this.box.top() + 1) / size), getZOffset(), 18, 18, sprite);
                --i;
            }
            matrixScaleOff(matrixStack);
        }
    }
}
