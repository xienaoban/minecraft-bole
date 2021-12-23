package xienaoban.minecraft.bole.gui.screen.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import xienaoban.minecraft.bole.client.BoleClient;
import xienaoban.minecraft.bole.client.highlight.HighlightManager;
import xienaoban.minecraft.bole.gui.screen.BoleMerchantEntityScreen;
import xienaoban.minecraft.bole.util.Keys;
import xienaoban.minecraft.bole.util.MiscUtil;

import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BoleVillagerEntityScreen<E extends VillagerEntity, H extends BoleVillagerEntityScreenHandler<E>> extends BoleMerchantEntityScreen<E, H> {
    public BoleVillagerEntityScreen(H handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void initPages() {
        super.initPages();
        this.pages.get(1).addSlotLazyAfter(new ClothingPropertyWidget(), null).addSlotLazy(new JobSitePropertyWidget()).addSlotLazy(new RestockPropertyWidget());
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

    public class JobSitePropertyWidget extends TemplatePropertyWidget1 {
        private int lastTicks;
        private Text cacheDistance;

        public JobSitePropertyWidget() {
            super(2, true, 2);
            this.lastTicks = -123456;
            this.cacheDistance = new LiteralText(" - ");
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_VILLAGER_JOB_SITE);
            initTooltipDescription(Keys.PROPERTY_WIDGET_VILLAGER_JOB_SITE_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_VILLAGER_JOB_SITE_DESCRIPTION_BUTTON1);
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_VILLAGER_JOB_SITE_DESCRIPTION_BUTTON2);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            GlobalPos pos = handler.entityJobSitePos;
            drawIcon(matrices, 170, 0);
            drawButton(matrices, 0, 230, 30 - (pos != null ? 0 : 20));
            drawButton(matrices, 1, 240, 20);
            int cutTicks = BoleClient.getInstance().getScreenTicks();
            if (cutTicks - this.lastTicks > 10) {
                this.lastTicks = cutTicks;
                if (pos != null) {
                    double dis = pos.getPos().getSquaredDistance(handler.entity.getPos(), true);
                    this.cacheDistance = new LiteralText(String.format("%.2fm", Math.sqrt(dis)));
                }
            }
            drawBarText(matrices, this.cacheDistance, DARK_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index < IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            switch (index) {
                case IDX_BUTTON_BEGIN -> {
                    GlobalPos pos = handler.entityJobSitePos;
                    if (!handler.hasJob()) {
                        showOverlayMessage(Keys.HINT_TEXT_NO_JOB);
                    }
                    else if (pos == null) {
                        showOverlayMessage(Keys.HINT_TEXT_NO_JOB_SITE);
                    }
                    else if (!Objects.equals(pos.getDimension(), handler.entity.world.getRegistryKey())) {
                        showOverlayMessage(Keys.HINT_TEXT_JOB_SITE_DIFFERENT_DIMENSION);
                    }
                    else {
                        HighlightManager hl = BoleClient.getInstance().getHighlightManager();
                        hl.setHighlightedJobSiteOrBeehive(hl.highlight(pos, 6 * 20));
                        onClose();
                    }
                }
                case IDX_BUTTON_BEGIN + 1 -> {
                    PlayerEntity player = handler.player;
                    Ingredient swords = Ingredient.ofItems(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
                    if (swords.test(player.getMainHandStack()) && swords.test(player.getOffHandStack())) {
                        handler.sendClientEntitySettings(Keys.ENTITY_SETTING_RESET_JOB);
                        onClose();
                        player.sendMessage(new TranslatableText(Keys.TEXT_VILLAGER_AGREE_TO_RESET_JOB), false);
                    }
                    else {
                        showOverlayMessage(Keys.HINT_TEXT_REFUSE_TO_RESET_JOB);
                    }
                }
            }
            return true;
        }
    }

    public class RestockPropertyWidget extends TemplatePropertyWidget1 {
        private final ItemStack overTime;

        public RestockPropertyWidget() {
            super(2, true, 1);
            this.overTime = new ItemStack(Items.EMERALD, calOvertime());
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK);
            initTooltipDescription(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION);
            initTooltipEmptyLine();
            initTooltipButtonDescription(Keys.PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION_BUTTON1);
        }

        @Override
        protected void drawContent(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            int restocksToday = handler.entityRestocksToday;
            this.overTime.setCount(calOvertime());
            drawIcon(matrices, 0, 110);
            drawBar(matrices, 1.0F, 10, 110);
            drawBar(matrices, restocksToday / 3.0F, 50, 110);
            if (canRestock()) {
                drawButton(matrices, 0, this.overTime);
            }
            else {
                drawButton(matrices, 0, 230, 10);
            }
            drawBarText(matrices, restocksToday + "/3", LIGHT_TEXT_COLOR);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int index = calMousePosition(mouseX, mouseY);
            if (index != IDX_BUTTON_BEGIN || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                return false;
            }
            if (!handler.hasJob()) {
                showOverlayMessage(Keys.HINT_TEXT_NO_JOB);
            }
            else if (!canRestock()) {
                showOverlayMessage(Keys.HINT_TEXT_FAR_FROM_JOB_SITE);
            }
            else if (!handler.trySpendItems(this.overTime)) {
                showOverlayMessage(Keys.HINT_TEXT_NOT_ENOUGH_ITEMS);
            }
            else {
                handler.sendClientEntitySettings(Keys.ENTITY_SETTING_RESTOCK, this.overTime);
                this.overTime.setCount(calOvertime());
            }
            return true;
        }

        private boolean canRestock() {
            GlobalPos jobSite = handler.entityJobSitePos;
            World world = MinecraftClient.getInstance().world;
            if (jobSite == null || world == null) {
                return false;
            }
            return jobSite.getDimension() == world.getRegistryKey() && jobSite.getPos().isWithinDistance(handler.entity.getPos(), 1.73);
        }

        private int calOvertime() {
            return Math.max(0, handler.entityRestocksToday - 3 + 1);
        }
    }

    public class ClothingPropertyWidget extends VariantsPropertyWidget {
        private static final VillagerType[] CLOTHES = {VillagerType.PLAINS, VillagerType.TAIGA, VillagerType.DESERT, VillagerType.JUNGLE, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.SWAMP};

        public ClothingPropertyWidget() {
            super(4, 3);
        }

        @Override
        protected void initTooltipLines() {
            initTooltipTitle(Keys.PROPERTY_WIDGET_VILLAGER_CLOTHING);
            initTooltipDescription(Keys.PROPERTY_WIDGET_VILLAGER_CLOTHING_DESCRIPTION);
        }

        @Override
        protected E[] initEntities() {
            VillagerEntity[] entities = new VillagerEntity[CLOTHES.length];
            for (int i = 0; i < CLOTHES.length; ++i) {
                VillagerEntity entity = (VillagerEntity) handler.entity.getType().create(MinecraftClient.getInstance().world);
                if (entity == null) {
                    throw new RuntimeException("Failed to create a VillagerEntity on the client side.");
                }
                copyEntityNbtForDisplay(handler.entity, entity);
                entity.setVillagerData(entity.getVillagerData().withType(CLOTHES[i]));
                entities[i] = entity;
            }
            return MiscUtil.cast(entities);
        }

        @Override
        protected Text[] initNames() {
            return Arrays.stream(CLOTHES).map(type -> new TranslatableText(Keys.VILLAGER_CLOTHING_PREFIX + type.toString())).toArray(Text[]::new);
        }

        @Override
        protected boolean canChoose() {
            return true;
        }

        @Override
        protected boolean isChosen(E fake) {
            return handler.entity.getVillagerData().getType() == fake.getVillagerData().getType();
        }

        @Override
        protected void setChosen(E fake) {
            VillagerType type = fake.getVillagerData().getType();
            handler.sendClientEntitySettings(Keys.ENTITY_SETTING_VILLAGER_CLOTHING, type);
        }
    }
}
