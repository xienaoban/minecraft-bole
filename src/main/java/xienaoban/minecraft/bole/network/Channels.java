package xienaoban.minecraft.bole.network;

import net.minecraft.util.Identifier;
import xienaoban.minecraft.bole.util.Keys;

public interface Channels {
    // Client side
    Identifier REQUEST_SERVER_BOLE_CONFIGS = new Identifier(Keys.NAMESPACE, "request_server_bole_configs");
    Identifier REQUEST_BOLE_SCREEN = new Identifier(Keys.NAMESPACE, "request_bole_screen");
    Identifier REQUEST_BOLE_HANDBOOK_ITEM = new Identifier(Keys.NAMESPACE, "request_bole_handbook_item");
    Identifier REQUEST_SERVER_ENTITY_DATA = new Identifier(Keys.NAMESPACE, "request_server_entity_data");
    Identifier SEND_CLIENT_ENTITY_SETTINGS = new Identifier(Keys.NAMESPACE, "send_client_entity_settings");
    Identifier REQUEST_SERVER_ENTITIES_GLOWING = new Identifier(Keys.NAMESPACE, "request_server_entities_glowing");
    Identifier SEND_HIGHLIGHT_EVENT = new Identifier(Keys.NAMESPACE, "send_highlight_event");
    Identifier REQUEST_BEEHIVE_SCREEN = new Identifier(Keys.NAMESPACE, "request_beehive_screen");
    Identifier REQUEST_MERCHANT_INVENTORY_SCREEN = new Identifier(Keys.NAMESPACE, "request_merchant_inventory_screen");
    Identifier REQUEST_BEEHIVE_INFO = new Identifier(Keys.NAMESPACE, "request_beehive_info");

    // Server side
    Identifier SEND_SERVER_BOLE_CONFIGS = new Identifier(Keys.NAMESPACE, "send_server_bole_configs");
    Identifier SEND_SERVER_ENTITY_DATA = new Identifier(Keys.NAMESPACE, "send_server_entity_data");
    Identifier SEND_SERVER_ENTITIES_GLOWING = new Identifier(Keys.NAMESPACE, "send_server_entities_glowing");
    Identifier SEND_OVERLAY_MESSAGE = new Identifier(Keys.NAMESPACE, "send_overlay_message");
    Identifier SEND_WANDERING_TRADER_SPAWN_MESSAGE = new Identifier(Keys.NAMESPACE, "send_wandering_trader_spawn_message");
    Identifier SEND_BEEHIVE_INFO = new Identifier(Keys.NAMESPACE, "send_beehive_info");
}
