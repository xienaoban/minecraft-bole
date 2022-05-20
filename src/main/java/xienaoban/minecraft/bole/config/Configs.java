package xienaoban.minecraft.bole.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ActionResult;
import xienaoban.minecraft.bole.util.Keys;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Config(name = Keys.MOD_NAME)
@Config.Gui.CategoryBackground(category = Configs.CLIENT, background = "minecraft:textures/block/moss_block.png")
@Config.Gui.CategoryBackground(category = Configs.SERVER, background = "minecraft:textures/block/tube_coral_block.png")
public final class Configs implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static final String CLIENT = "default", SERVER = "server";

    @ConfigEntry.Gui.Excluded
    private static ConfigHolder<Configs> holder;

    public static void init() {
        holder = AutoConfig.register(Configs.class, GsonConfigSerializer::new);
        holder.registerLoadListener((configHolder, configs) -> {
            configs.onUpdate();
            return ActionResult.PASS;
        });
        holder.registerSaveListener((configHolder, configs) -> {
            configs.onUpdate();
            return ActionResult.PASS;
        });
    }

    public static Configs getInstance() {
        return holder.getConfig();
    }

    @Environment(EnvType.CLIENT)
    public static ConfigHolder<Configs> getHolder() {
        return holder;
    }

    ///////// Configs /////////

    @ConfigEntry.Category(CLIENT)
    @ConfigEntry.Gui.Tooltip()
    ShoulderCreatureHudPosition shoulderCreatureHudPosition = ShoulderCreatureHudPosition.NONE;

    @ConfigEntry.Category(CLIENT)
    @ConfigEntry.Gui.Tooltip()
    boolean notifyWhenLeashFallFromPlayer = false;

    @ConfigEntry.Category(CLIENT)
    @ConfigEntry.Gui.Tooltip()
    boolean receiveWanderingTraderSpawnBroadcasts = false;

    @ConfigEntry.Category(CLIENT)
    @ConfigEntry.Gui.Tooltip()
    int highlightEntitiesBlindnessTime = 40;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean allowHotKeyToOpenBoleHandbookScreen = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean blockAccidentalInjuryToPets = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean petsCanTeleportToMoreBlocks = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean forbidToSetNetherPortalCooldownOfOtherPlayers = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    boolean broadcastWhenWanderingTraderSpawn = false;

    @ConfigEntry.Category(SERVER)
    @ConfigEntry.Gui.Tooltip()
    List<String> bannedEntitySettings = new ArrayList<>();

    ///////// Misc /////////

    // Set to "transient" to avoid serialization or deserialization by Gson
    @ConfigEntry.Gui.Excluded
    private transient final Set<String> bannedEntitySettingsSet = new HashSet<>();

    public void onUpdate() {
        this.bannedEntitySettingsSet.clear();
        for (String name : this.bannedEntitySettings) {
            if (name.startsWith("#")) {
                continue;
            }
            this.bannedEntitySettingsSet.add(name);
        }
    }

    ///////// Getters /////////

    public ShoulderCreatureHudPosition getShoulderCreatureHudPosition() {
        return shoulderCreatureHudPosition;
    }

    public boolean isNotifyWhenLeashFallFromPlayer() {
        return notifyWhenLeashFallFromPlayer;
    }

    public boolean isReceiveWanderingTraderSpawnBroadcasts() {
        return receiveWanderingTraderSpawnBroadcasts;
    }

    public int getHighlightEntitiesBlindnessTime() {
        return highlightEntitiesBlindnessTime;
    }

    public boolean isAllowHotKeyToOpenBoleHandbookScreen() {
        return allowHotKeyToOpenBoleHandbookScreen;
    }

    public boolean isBlockAccidentalInjuryToPets() {
        return blockAccidentalInjuryToPets;
    }

    public boolean isPetsCanTeleportToMoreBlocks() {
        return petsCanTeleportToMoreBlocks;
    }

    public boolean isForbidToSetNetherPortalCooldownOfOtherPlayers() {
        return forbidToSetNetherPortalCooldownOfOtherPlayers;
    }

    public boolean isBroadcastWhenWanderingTraderSpawn() {
        return broadcastWhenWanderingTraderSpawn;
    }

    public boolean isEntitySettingBanned(String settingId) {
        return this.bannedEntitySettingsSet.contains(settingId);
    }

    public enum ShoulderCreatureHudPosition {
        NONE, TOP, BOTTOM, SIDES
    }
}
