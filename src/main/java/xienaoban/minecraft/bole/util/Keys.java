package xienaoban.minecraft.bole.util;

import java.nio.file.Path;

public interface Keys {
    // ID
    String BOLE = "bole";
    String NAMESPACE = "bole";
    String GITHUB_RELEASE = "https://github.com/xienaoban/minecraft-bole/releases";
    String CURSEFORGE_RELEASE = "https://www.curseforge.com/minecraft/mc-mods/bole";

    // Path
    String ENTITY_SORT_ORDER_CONFIG_FILENAME = "bole_entity_sort_order.txt";
    static Path ENTITY_SORT_ORDER_CONFIG_PATH() {
        return MiscUtil.getConfigPath().resolve(Keys.ENTITY_SORT_ORDER_CONFIG_FILENAME);
    }

    // Entity Setting Channels
    String ENTITY_SETTING_HIGHLIGHT_ENTITIES =                      "highlight_entities";
    String ENTITY_SETTING_OFFER_OR_DROP_GOD_MODE_ONLY =             "offer_or_drop";
    String ENTITY_SETTING_NETHER_PORTAL_COOLDOWN =                  "nether_portal_cooldown";
    String ENTITY_SETTING_CUSTOM_NAME_VISIBLE =                     "custom_name_visible";
    String ENTITY_SETTING_SILENT =                                  "silent";
    String ENTITY_SETTING_INVULNERABLE =                            "invulnerable";
    String ENTITY_SETTING_NO_AI =                                   "no_ai";
    String ENTITY_SETTING_BABY =                                    "baby";
    String ENTITY_SETTING_RESET_BEEHIVE =                           "reset_beehive";
    String ENTITY_SETTING_RESET_VILLAGER_JOB =                      "reset_villager_job";
    String ENTITY_SETTING_VILLAGER_RESTOCK =                        "villager_restock";
    String ENTITY_SETTING_VILLAGER_CLOTHING =                       "villager_clothing";
    String ENTITY_SETTING_ADD_WANDERING_VILLAGER_WANDERING_TIME =   "add_wandering_villager_wandering_time";
    String ENTITY_SETTING_SHEEP_EAT_GRASS =                         "sheep_eat_grass";
    String ENTITY_SETTING_RABBIT_VARIANT =                          "rabbit_variant";
    String ENTITY_SETTING_MOOSHROOM_VARIANT =                       "mooshroom_variant";
    String ENTITY_SETTING_CAT_VARIANT =                             "cat_variant";
    String ENTITY_SETTING_PARROT_VARIANT =                          "parrot_variant";
    String ENTITY_SETTING_FROG_VARIANT =                            "frog_variant";
    String ENTITY_SETTING_LLAMA_VARIANT =                           "llama_variant";
    String ENTITY_SETTING_FOX_VARIANT =                             "fox_variant";
    String ENTITY_SETTING_PANDA_VARIANT =                           "panda_variant";
    String ENTITY_SETTING_AXOLOTL_VARIANT =                         "axolotl_variant";
    String ENTITY_SETTING_HORSE_COLOR_VARIANT =                     "horse_color_variant";
    String ENTITY_SETTING_HORSE_MARKING_VARIANT =                   "horse_marking_variant";
    String ENTITY_SETTING_SIT_ON_PLAYER_COOLDOWN =                  "sit_on_player_cooldown";
    String ENTITY_SETTING_TROPICAL_FISH_VARIANT =                   "tropical_fish_variant";

    // Translation Keys
    String GUI_OK = "gui.ok";
    String GUI_CANCEL = "gui.cancel";
    String GUI_YES = "gui.yes";
    String GUI_NO = "gui.no";
    String COLOR_PREFIX = "color.minecraft.";

    String AUTHOR =                     "xienaoban";
    String AUTHOR_TRANS =               "xienaoban.trans";
    String MOD_NAME =                   "bole";
    String BOLE_HANDBOOK_TITLE =        "title.bole.bole_handbook";
    String BOLE_HANDBOOK_DESCRIPTION =  "description.bole.bole_handbook";
    String KEY_CATEGORY_BOLE =          "key.category.bole";
    String KEY_OPEN_BOLE_GUI =          "key.bole.open_bole_gui";

    String RABBIT_VARIANT_BROWN_TYPE =          "entity.minecraft.rabbit.variant.brown_type";
    String RABBIT_VARIANT_WHITE_TYPE =          "entity.minecraft.rabbit.variant.white_type";
    String RABBIT_VARIANT_BLACK_TYPE =          "entity.minecraft.rabbit.variant.black_type";
    String RABBIT_VARIANT_WHITE_SPOTTED_TYPE =  "entity.minecraft.rabbit.variant.white_spotted_type";
    String RABBIT_VARIANT_GOLD_TYPE =           "entity.minecraft.rabbit.variant.gold_type";
    String RABBIT_VARIANT_SALT_TYPE =           "entity.minecraft.rabbit.variant.salt_type";
    String RABBIT_VARIANT_KILLER_BUNNY_TYPE =   "entity.minecraft.rabbit.variant.killer_bunny_type";

    String PARROT_VARIANT_RED =     "entity.minecraft.parrot.variant.red";
    String PARROT_VARIANT_BLUE =    "entity.minecraft.parrot.variant.blue";
    String PARROT_VARIANT_GREEN =   "entity.minecraft.parrot.variant.green";
    String PARROT_VARIANT_CYAN =    "entity.minecraft.parrot.variant.cyan";
    String PARROT_VARIANT_GRAY =    "entity.minecraft.parrot.variant.gray";

    String LLAMA_VARIANT_SAND =     "entity.minecraft.llama.variant.sand";
    String LLAMA_VARIANT_SNOW =     "entity.minecraft.llama.variant.snow";
    String LLAMA_VARIANT_WOOD =     "entity.minecraft.llama.variant.wood";
    String LLAMA_VARIANT_DIRTY =    "entity.minecraft.llama.variant.dirty";

    String HORSE_COLOR_VARIANT_WHITE =      "entity.minecraft.horse.variant.color.white";
    String HORSE_COLOR_VARIANT_CREAMY =     "entity.minecraft.horse.variant.color.creamy";
    String HORSE_COLOR_VARIANT_CHESTNUT =   "entity.minecraft.horse.variant.color.chestnut";
    String HORSE_COLOR_VARIANT_BROWN =      "entity.minecraft.horse.variant.color.brown";
    String HORSE_COLOR_VARIANT_BLACK =      "entity.minecraft.horse.variant.color.black";
    String HORSE_COLOR_VARIANT_GRAY =       "entity.minecraft.horse.variant.color.gray";
    String HORSE_COLOR_VARIANT_DARKBROWN =  "entity.minecraft.horse.variant.color.darkbrown";

    String HORSE_MARKING_VARIANT_NONE =         "entity.minecraft.horse.variant.marking.none";
    String HORSE_MARKING_VARIANT_WHITE =        "entity.minecraft.horse.variant.marking.white";
    String HORSE_MARKING_VARIANT_WHITE_FIELD =  "entity.minecraft.horse.variant.marking.white_field";
    String HORSE_MARKING_VARIANT_WHITE_DOTS =   "entity.minecraft.horse.variant.marking.white_dots";
    String HORSE_MARKING_VARIANT_BLACK_DOTS =   "entity.minecraft.horse.variant.marking.black_dots";

    // The following variants (types) are already defined in official lang files.
    String TROPICAL_FISH_VARIANT_KOB =          "entity.minecraft.tropical_fish.type.kob";
    String TROPICAL_FISH_VARIANT_SUNSTREAK =    "entity.minecraft.tropical_fish.type.sunstreak";
    String TROPICAL_FISH_VARIANT_SNOOPER =      "entity.minecraft.tropical_fish.type.snooper";
    String TROPICAL_FISH_VARIANT_DASHER =       "entity.minecraft.tropical_fish.type.dasher";
    String TROPICAL_FISH_VARIANT_BRINELY =      "entity.minecraft.tropical_fish.type.brinely";
    String TROPICAL_FISH_VARIANT_SPOTTY =       "entity.minecraft.tropical_fish.type.spotty";
    String TROPICAL_FISH_VARIANT_FLOPPER =      "entity.minecraft.tropical_fish.type.flopper";
    String TROPICAL_FISH_VARIANT_STRIPEY =      "entity.minecraft.tropical_fish.type.stripey";
    String TROPICAL_FISH_VARIANT_GLITTER =      "entity.minecraft.tropical_fish.type.glitter";
    String TROPICAL_FISH_VARIANT_BLOCKFISH =    "entity.minecraft.tropical_fish.type.blockfish";
    String TROPICAL_FISH_VARIANT_BETTY =        "entity.minecraft.tropical_fish.type.betty";
    String TROPICAL_FISH_VARIANT_CLAYFISH =     "entity.minecraft.tropical_fish.type.clayfish";

    String MOOSHROOM_VARIANT_PREFIX = "entity.minecraft.mooshroom.variant.";

    String CAT_VARIANT_PREFIX = "entity.minecraft.cat.variant.";

    String FROG_VARIANT_PREFIX = "entity.minecraft.frog.variant.";

    String FOX_VARIANT_PREFIX = "entity.minecraft.fox.variant.";

    String PANDA_VARIANT_PREFIX = "entity.minecraft.panda.variant.";

    String VILLAGER_CLOTHING_PREFIX = "entity.minecraft.villager.clothing.";

    String TAG_GROUP_DEFAULT =      "tag_group.bole.default";
    String TAG_GROUP_CLASS =        "tag_group.bole.class";
    String TAG_GROUP_INTERFACE =    "tag_group.bole.interface";
    String TAG_GROUP_NAMESPACE =    "tag_group.bole.namespace";

    String TAG_DEFAULT_HUMAN =              "tag.bole.default.human";
    String TAG_DEFAULT_TERRESTRIAL_ANIMAL = "tag.bole.default.terrestrial_animal";
    String TAG_DEFAULT_AQUATIC_ANIMAL =     "tag.bole.default.aquatic_animal";
    String TAG_DEFAULT_ANIMAL =             "tag.bole.default.animal";
    String TAG_DEFAULT_HUMANOID =           "tag.bole.default.humanoid";
    String TAG_DEFAULT_PATROL =             "tag.bole.default.patrol";
    String TAG_DEFAULT_MONSTER =            "tag.bole.default.monster";
    String TAG_DEFAULT_OTHER =              "tag.bole.default.other";

    String AUTO_CONFIG_PREFIX = "text.autoconfig.bole.option.";
    String AUTO_CONFIG_POSTFIX = ".@Tooltip";

    String PROPERTY_WIDGET_HANDBOOK_ENTITY_DESCRIPTION_BUTTON1 = "property_widget.bole.handbook_entity.description.button1";
    String PROPERTY_WIDGET_HANDBOOK_ENTITY_DESCRIPTION_BUTTON2 = "property_widget.bole.handbook_entity.description.button2";
    String PROPERTY_WIDGET_AIR = "property_widget.bole.air";
    String PROPERTY_WIDGET_AIR_DESCRIPTION = "property_widget.bole.air.description";
    String PROPERTY_WIDGET_BOUNDING_BOX = "property_widget.bole.bounding_box";
    String PROPERTY_WIDGET_BOUNDING_BOX_DESCRIPTION = "property_widget.bole.bounding_box.description";
    String PROPERTY_WIDGET_NETHER_PORTAL_COOLDOWN = "property_widget.bole.nether_portal_cooldown";
    String PROPERTY_WIDGET_NETHER_PORTAL_COOLDOWN_DESCRIPTION = "property_widget.bole.nether_portal_cooldown.description";
    String PROPERTY_WIDGET_NETHER_PORTAL_COOLDOWN_DESCRIPTION_BUTTON1 = "property_widget.bole.nether_portal_cooldown.description.button1";
    String PROPERTY_WIDGET_CUSTOM_NAME = "property_widget.bole.custom_name";
    String PROPERTY_WIDGET_CUSTOM_NAME_DESCRIPTION = "property_widget.bole.custom_name.description";
    String PROPERTY_WIDGET_CUSTOM_NAME_DESCRIPTION_BUTTON1 = "property_widget.bole.custom_name.description.button1";
    String PROPERTY_WIDGET_SILENT = "property_widget.bole.silent";
    String PROPERTY_WIDGET_SILENT_DESCRIPTION_BUTTON1 = "property_widget.bole.silent.description.button1";
    String PROPERTY_WIDGET_INVULNERABLE = "property_widget.bole.invulnerable";
    String PROPERTY_WIDGET_INVULNERABLE_DESCRIPTION_BUTTON1 = "property_widget.bole.invulnerable.description.button1";
    String PROPERTY_WIDGET_HEALTH = "property_widget.bole.health";
    String PROPERTY_WIDGET_HEALTH_DESCRIPTION = "property_widget.bole.health.description";
    String PROPERTY_WIDGET_HEALTH_DESCRIPTION_HORSE_BASE = "property_widget.bole.health.description.horse_base";
    String PROPERTY_WIDGET_STATUS_EFFECTS = "property_widget.bole.status_effects";
    String PROPERTY_WIDGET_LEASH = "property_widget.bole.leash";
    String PROPERTY_WIDGET_LEASH_DESCRIPTION = "property_widget.bole.leash.description";
    String PROPERTY_WIDGET_HAS_AI = "property_widget.bole.has_ai";
    String PROPERTY_WIDGET_HAS_AI_DESCRIPTION_BUTTON1 = "property_widget.bole.has_ai.description.button1";
    String PROPERTY_WIDGET_TAME = "property_widget.bole.tame";
    String PROPERTY_WIDGET_TAME_DESCRIPTION = "property_widget.bole.tame.description";
    String PROPERTY_WIDGET_TAME_DESCRIPTION_BUTTON1 = "property_widget.bole.tame.description.button1";
    String PROPERTY_WIDGET_BABY = "property_widget.bole.baby";
    String PROPERTY_WIDGET_BABY_DESCRIPTION = "property_widget.bole.baby.description";
    String PROPERTY_WIDGET_BABY_DESCRIPTION_BUTTON1 = "property_widget.bole.baby.description.button1";
    String PROPERTY_WIDGET_ATTRACTIVE_ITEMS = "property_widget.bole.attractive_items";
    String PROPERTY_WIDGET_ATTRACTIVE_ITEMS_DESCRIPTION = "property_widget.bole.attractive_items.description";
    String PROPERTY_WIDGET_BREEDING_ITEMS = "property_widget.bole.breeding_items";
    String PROPERTY_WIDGET_BREEDING_ITEMS_DESCRIPTION = "property_widget.bole.breeding_items.description";
    String PROPERTY_WIDGET_BEEHIVE = "property_widget.bole.beehive";
    String PROPERTY_WIDGET_BEEHIVE_DESCRIPTION = "property_widget.bole.beehive.description";
    String PROPERTY_WIDGET_BEEHIVE_DESCRIPTION_BUTTON1 = "property_widget.bole.beehive.description.button1";
    String PROPERTY_WIDGET_BEEHIVE_DESCRIPTION_BUTTON2 = "property_widget.bole.beehive.description.button2";
    String PROPERTY_WIDGET_EAT_GRASS = "property_widget.bole.eat_grass";
    String PROPERTY_WIDGET_EAT_GRASS_DESCRIPTION = "property_widget.bole.eat_grass.description";
    String PROPERTY_WIDGET_EAT_GRASS_DESCRIPTION_BUTTON1 = "property_widget.bole.eat_grass.description.button1";
    String PROPERTY_WIDGET_SIT_ON_PLAYER_COOLDOWN = "property_widget.bole.sit_on_player_cooldown";
    String PROPERTY_WIDGET_SIT_ON_PLAYER_COOLDOWN_DESCRIPTION = "property_widget.bole.sit_on_player_cooldown.description";
    String PROPERTY_WIDGET_SIT_ON_PLAYER_COOLDOWN_DESCRIPTION_BUTTON1 = "property_widget.bole.sit_on_player_cooldown.description.button1";
    String PROPERTY_WIDGET_HORSE_RUN_AND_JUMP = "property_widget.bole.horse_run_and_jump";
    String PROPERTY_WIDGET_HORSE_RUN_AND_JUMP_DESCRIPTION = "property_widget.bole.horse_run_and_jump.description";
    String PROPERTY_WIDGET_DONKEY_CHEST = "property_widget.bole.donkey_chest";
    String PROPERTY_WIDGET_DONKEY_CHEST_DESCRIPTION = "property_widget.bole.donkey_chest.description";
    String PROPERTY_WIDGET_MERCHANT_INVENTORY = "property_widget.bole.merchant_inventory";
    String PROPERTY_WIDGET_MERCHANT_INVENTORY_DESCRIPTION = "property_widget.bole.merchant_inventory.description";
    String PROPERTY_WIDGET_VILLAGER_JOB_SITE = "property_widget.bole.villager_job_site";
    String PROPERTY_WIDGET_VILLAGER_JOB_SITE_DESCRIPTION = "property_widget.bole.villager_job_site.description";
    String PROPERTY_WIDGET_VILLAGER_JOB_SITE_DESCRIPTION_BUTTON1 = "property_widget.bole.villager_job_site.description.button1";
    String PROPERTY_WIDGET_VILLAGER_JOB_SITE_DESCRIPTION_BUTTON2 = "property_widget.bole.villager_job_site.description.button2";
    String PROPERTY_WIDGET_VILLAGER_RESTOCK = "property_widget.bole.villager_restock";
    String PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION = "property_widget.bole.villager_restock.description";
    String PROPERTY_WIDGET_VILLAGER_RESTOCK_DESCRIPTION_BUTTON1 = "property_widget.bole.villager_restock.description.button1";
    String PROPERTY_WIDGET_VILLAGER_CLOTHING = "property_widget.bole.villager_clothing";
    String PROPERTY_WIDGET_VILLAGER_CLOTHING_DESCRIPTION = "property_widget.bole.villager_clothing.description";
    String PROPERTY_WIDGET_RABBIT_VARIANT = "property_widget.bole.rabbit_variant";
    String PROPERTY_WIDGET_RABBIT_VARIANT_DESCRIPTION = "property_widget.bole.rabbit_variant.description";
    String PROPERTY_WIDGET_MOOSHROOM_VARIANT = "property_widget.bole.mooshroom_variant";
    String PROPERTY_WIDGET_MOOSHROOM_VARIANT_DESCRIPTION = "property_widget.bole.mooshroom_variant.description";
    String PROPERTY_WIDGET_CAT_VARIANT = "property_widget.bole.cat_variant";
    String PROPERTY_WIDGET_CAT_VARIANT_DESCRIPTION = "property_widget.bole.cat_variant.description";
    String PROPERTY_WIDGET_PARROT_VARIANT = "property_widget.bole.parrot_variant";
    String PROPERTY_WIDGET_PARROT_VARIANT_DESCRIPTION = "property_widget.bole.parrot_variant.description";
    String PROPERTY_WIDGET_FROG_VARIANT = "property_widget.bole.frog_variant";
    String PROPERTY_WIDGET_FROG_VARIANT_DESCRIPTION = "property_widget.bole.frog_variant.description";
    String PROPERTY_WIDGET_LLAMA_VARIANT = "property_widget.bole.llama_variant";
    String PROPERTY_WIDGET_LLAMA_VARIANT_DESCRIPTION = "property_widget.bole.llama_variant.description";
    String PROPERTY_WIDGET_HORSE_COLOR_VARIANT = "property_widget.bole.horse_color_variant";
    String PROPERTY_WIDGET_HORSE_COLOR_VARIANT_DESCRIPTION = "property_widget.bole.horse_color_variant.description";
    String PROPERTY_WIDGET_HORSE_MARKING_VARIANT = "property_widget.bole.horse_marking_variant";
    String PROPERTY_WIDGET_HORSE_MARKING_VARIANT_DESCRIPTION = "property_widget.bole.horse_marking_variant.description";
    String PROPERTY_WIDGET_FOX_VARIANT = "property_widget.bole.fox_variant";
    String PROPERTY_WIDGET_FOX_VARIANT_DESCRIPTION = "property_widget.bole.fox_variant.description";
    String PROPERTY_WIDGET_PANDA_MAIN_GENE = "property_widget.bole.panda_main_gene";
    String PROPERTY_WIDGET_PANDA_MAIN_GENE_DESCRIPTION = "property_widget.bole.panda_main_gene.description";
    String PROPERTY_WIDGET_PANDA_HIDDEN_GENE = "property_widget.bole.panda_hidden_gene";
    String PROPERTY_WIDGET_PANDA_HIDDEN_GENE_DESCRIPTION = "property_widget.bole.panda_hidden_gene.description";
    String PROPERTY_WIDGET_AXOLOTL_VARIANT = "property_widget.bole.axolotl_variant";
    String PROPERTY_WIDGET_AXOLOTL_VARIANT_DESCRIPTION = "property_widget.bole.axolotl_variant.description";
    String PROPERTY_WIDGET_WANDERING_TRADER_DESPAWN_DELAY = "property_widget.bole.wandering_trader_despawn_delay";
    String PROPERTY_WIDGET_WANDERING_TRADER_DESPAWN_DELAY_DESCRIPTION = "property_widget.bole.wandering_trader_despawn_delay.description";
    String PROPERTY_WIDGET_WANDERING_TRADER_DESPAWN_DELAY_DESCRIPTION_BUTTON1 = "property_widget.bole.wandering_trader_despawn_delay.description.button1";
    String PROPERTY_WIDGET_TROPICAL_FISH_VARIANT = "property_widget.bole.tropical_fish_variant";
    String PROPERTY_WIDGET_TROPICAL_FISH_VARIANT_DESCRIPTION = "property_widget.bole.tropical_fish_variant.description";
    String PROPERTY_WIDGET_TROPICAL_FISH_BASE_COLOR = "property_widget.bole.tropical_fish_base_color";
    String PROPERTY_WIDGET_TROPICAL_FISH_BASE_COLOR_DESCRIPTION = "property_widget.bole.tropical_fish_base_color.description";
    String PROPERTY_WIDGET_TROPICAL_FISH_PATTERN_COLOR = "property_widget.bole.tropical_fish_pattern_color";
    String PROPERTY_WIDGET_TROPICAL_FISH_PATTERN_COLOR_DESCRIPTION = "property_widget.bole.tropical_fish_pattern_color.description";
    String PROPERTY_WIDGET_MOISTNESS = "property_widget.bole.moistness";
    String PROPERTY_WIDGET_MOISTNESS_DESCRIPTION = "property_widget.bole.moistness.description";
    String PROPERTY_WIDGET_SCREAMING_GOAT = "property_widget.bole.screaming_goat";
    String PROPERTY_WIDGET_SCREAMING_GOAT_DESCRIPTION = "property_widget.bole.screaming_goat.description";
    String PROPERTY_WIDGET_ALLEY_LIKED = "property_widget.bole.allay_liked";
    String PROPERTY_WIDGET_ALLEY_LIKED_DESCRIPTION = "property_widget.bole.allay_liked.description";
    String PROPERTY_WIDGET_ALLEY_LIKED_DESCRIPTION_BUTTON1 = "property_widget.bole.allay_liked.description.button1";
    String PROPERTY_WIDGET_DESCRIPTION_GET_NAME_BY_UUID = "property_widget.bole.description.get_name_by_uuid";

    String HINT_TEXT_OFFER_OR_DROP = "text.hint.bole.offer_or_drop";
    String HINT_TEXT_ONLY_IN_GOD_MODE = "text.hint.bole.only_in_god_mode";
    String HINT_TEXT_HIGHLIGHT_NOT_ENOUGH_EXPERIENCE = "text.hint.bole.highlight_not_enough_experience";
    String HINT_TEXT_FORBID_TO_SET_NETHER_PORTAL_COOLDOWN_OF_OTHER_PLAYERS = "text.hint.bole.forbid_to_set_nether_portal_cooldown_of_other_players";
    String HINT_TEXT_NOT_BABY = "text.hint.bole.not_baby";
    String HINT_TEXT_NO_BEEHIVE = "text.hint.bole.no_beehive";
    String HINT_TEXT_EATING_GRASS = "text.hint.bole.eating_grass";
    String HINT_TEXT_FAR_FROM_GRASS = "text.hint.bole.far_from_grass";
    String HINT_TEXT_NOT_ENOUGH_ITEMS = "text.hint.bole.not_enough_items";
    String HINT_TEXT_FAR_FROM_JOB_SITE = "text.hint.bole.far_from_job_site";
    String HINT_TEXT_NO_JOB = "text.hint.bole.no_job";
    String HINT_TEXT_NO_JOB_SITE = "text.hint.bole.no_job_site";
    String HINT_TEXT_JOB_SITE_DIFFERENT_DIMENSION = "text.hint.bole.job_site_different_dimension";
    String HINT_TEXT_REFUSE_TO_RESET_JOB = "text.hint.bole.refuse_to_reset_job";
    String HINT_TEXT_SOMETHING_IS_WRONG = "text.hint.bole.something_is_wrong";
    String HINT_TEXT_ALLAY_LIKE_NO_NOTEBLOCK = "text.hint.bole.allay_like_no_noteblock";
    String HINT_TEXT_ENTITY_REORDER_DONE = "text.hint.bole.entity_reorder_done";

    String ERROR_TEXT_DATA_LOAD = "text.error.bole.data_load";
    String ERROR_TEXT_CLIENT_SERVER_MOD_VERSION_NOT_MATCH = "text.error.bole.client_server_mod_version_not_match";

    String WARNING_TEXT_ENABLE_AI = "text.warning.bole.enable_ai";
    String WARNING_TEXT_DISABLE_AI = "text.warning.bole.disable_ai";
    String WARNING_TEXT_OPEN_MERCHANT_INVENTORY = "text.warning.bole.open_merchant_inventory";
    String WARNING_TEXT_VILLAGER_RESET_JOB = "text.warning.bole.villager_reset_job";
    String WARNING_TEXT_VILLAGER_CHANGE_CLOTH = "text.warning.bole.villager_change_cloth";
    String WARNING_TEXT_ENTITY_REORDER_DONE = "text.warning.bole.entity_reorder_done";

    String TEXT_COLON = "text.bole.colon";
    String TEXT_COMMA = "text.bole.comma";
    String TEXT_NUMBER_OF_ELEMENTS = "text.bole.number_of_elements";
    String TEXT_MOD_NOT_INSTALLED = "text.bole.mod_not_installed";
    String TEXT_CLICK_ME = "text.bole.click_me";
    String TEXT_TARGET_ENTITY_TOO_FAR = "text.bole.target_entity_too_far";
    String TEXT_OPEN_LOCAL_CONFIGS = "text.bole.open_local_configs";
    String TEXT_SET_CONFIGS_LOCAL_IS_REMOTE = "text.bole.set_configs_local_is_remote";
    String TEXT_SET_CONFIGS_LOCAL_IS_NOT_REMOTE = "text.bole.set_configs_local_is_not_remote";
    String TEXT_GET_CONFIGS_LOCAL_IS_REMOTE = "text.bole.get_configs_local_is_remote";
    String TEXT_GET_CONFIGS_LOCAL_IS_NOT_REMOTE = "text.bole.get_configs_local_is_not_remote";
    String TEXT_OTHER_CLIENT_CONFIGS = "text.bole.other_client_configs";
    String TEXT_CUSTOM_ENTITY_ORDER_CONFIG = "text.bole.custom_entity_order_config";
    String TEXT_CUSTOM_ENTITY_ORDER_CONFIG_DESCRIPTION = "text.bole.custom_entity_order_config.description";
    String TEXT_SERVER_MOD_VERSION = "text.bole.server_mod_version";
    String TEXT_ANTI_MISTOUCH_WARNING = "text.bole.anti_mistouch_warning";
    String TEXT_RETURN_TO_HOMEPAGE = "text.bole.return_to_homepage";
    String TEXT_SETTINGS = "text.bole.settings";
    String TEXT_ABOUT = "text.bole.about";
    String TEXT_MOD_NAME_IS = "text.bole.mod_name_is";
    String TEXT_MOD_AUTHOR_IS = "text.bole.mod_author_is";
    String TEXT_MOD_VERSION_IS = "text.bole.mod_version_is";
    String TEXT_UNSUPPORTED_ENTITY = "text.bole.unsupported_entity";
    String TEXT_UNNAMED = "text.bole.unnamed";
    String TEXT_GROWN_UP = "text.bole.grown_up";
    String TEXT_NEVER_GROW_UP = "text.bole.never_grow_up";
    String TEXT_LOADING = "text.bole.loading";
    String TEXT_EMPTY_WITH_BRACKETS = "text.bole.empty_with_brackets";
    String TEXT_HIGHLIGHT = "text.bole.highlight";
    String TEXT_MAIN_GENE = "text.bole.main_gene";
    String TEXT_HIDDEN_GENE = "text.bole.hidden_gene";
    String TEXT_WANDERING_TRADER_SPAWN_MESSAGE = "text.bole.wandering_trader_spawn_message";
    String TEXT_UNKNOWN_PLAYER = "text.bole.unknown_player";
    String TEXT_SERVER_BAN_HOTKEY = "text.bole.server_ban_hotkey";
    String TEXT_INVENTORY_OF = "text.bole.inventory_of";
    String TEXT_WAIT_FOR_SERVER = "text.bole.wait_for_server";
    String TEXT_TROPICAL_FISH_VARIANT_TURN_PAGE = "text.bole.tropical_fish_variant_turn_page";
    String TEXT_HONEY = "text.bole.honey";
    String TEXT_HAS_NECTAR = "text.bole.has_nectar";
    String TEXT_TIME_IN_BEEHIVE = "text.bole.time_in_beehive";
    String TEXT_HONEY_LEVEL = "text.bole.honey_level";
    String TEXT_BEE_COUNT = "text.bole.bee_count";
    String TEXT_BEE_INFO = "text.bole.bee_info";
    String TEXT_MINOR = "text.bole.minor";
    String TEXT_ADULT = "text.bole.adult";
    String TEXT_LEASH_FALL = "text.bole.leash_fall";
    String TEXT_CURRENT_FEATURE_REQUEST = "text.bole.current_feature_request";
    String TEXT_FEATURE_REQUEST_BANNED_FROM_SERVER = "text.bole.feature_request_banned_from_server";
    String TEXT_REQUESTING_MOJANG_API = "text.bole.requesting_mojang_api";
    String TEXT_FAIL_TO_REQUEST_MOJANG_API = "text.bole.fail_to_request_mojang_api";
    String TEXT_NOT_GENUINE_PLAYER = "text.bole.not_genuine_player";
    String TEXT_NOT_TAMED = "text.bole.not_tamed";
}
