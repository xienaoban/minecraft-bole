package xienaoban.minecraft.bole.network;

import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public interface Channels {
    // Client side
    Identifier REQUEST_BOLE_SCREEN = new Identifier(Keys.NAMESPACE, "request_bole_screen");
    Identifier REQUEST_SERVER_ENTITY_DATA = new Identifier(Keys.NAMESPACE, "request_server_entity_data");
    Identifier SEND_CLIENT_ENTITY_SETTINGS = new Identifier(Keys.NAMESPACE, "send_client_entity_settings");
    Identifier REQUEST_SERVER_ENTITIES_GLOWING = new Identifier(Keys.NAMESPACE, "request_server_entities_glowing");
    Identifier SEND_HIGHLIGHT_EVENT = new Identifier(Keys.NAMESPACE, "send_highlight_event");

    // Server side
    Identifier SEND_SERVER_ENTITY_DATA = new Identifier(Keys.NAMESPACE, "send_server_entity_data");
    Identifier SEND_SERVER_ENTITIES_GLOWING = new Identifier(Keys.NAMESPACE, "send_server_entities_glowing");
    Identifier SEND_OVERLAY_MESSAGE = new Identifier(Keys.NAMESPACE, "send_overlay_message");
}
