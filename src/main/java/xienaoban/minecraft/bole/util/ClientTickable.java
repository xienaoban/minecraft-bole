package xienaoban.minecraft.bole.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ClientTickable {
    @Environment(EnvType.CLIENT)
    void clientTick(int ticks);
}
