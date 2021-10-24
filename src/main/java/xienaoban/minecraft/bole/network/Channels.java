package xienaoban.minecraft.bole.network;

import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public interface Channels {
    Identifier REQUEST_BOLE_SCREEN = new Identifier(Keys.NAMESPACE, "request_bole_screen");
    Identifier REQUEST_SERVER_ENTITY_DATA = new Identifier(Keys.NAMESPACE, "request_server_entity_data");
    Identifier SEND_SERVER_ENTITY_DATA = new Identifier(Keys.NAMESPACE, "send_server_entity_data");
}
