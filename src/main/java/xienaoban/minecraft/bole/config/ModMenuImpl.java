package xienaoban.minecraft.bole.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import xienaoban.minecraft.bole.gui.ScreenManager;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ScreenManager::getConfigScreen;
    }
}
