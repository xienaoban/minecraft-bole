package xienaoban.minecraft.bole.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import xienaoban.minecraft.bole.util.Keys;

@Config(name = Keys.MOD_NAME)
public class Configs implements ConfigData {
    @ConfigEntry.Gui.Excluded
    private static ConfigHolder<Configs> holder;

    public static void init() {
        AutoConfig.register(Configs.class, GsonConfigSerializer::new);
        holder = AutoConfig.getConfigHolder(Configs.class);
    }

    public static Configs getInstance() {
        return holder.getConfig();
    }

    public boolean invulnerablePets = false;
}
