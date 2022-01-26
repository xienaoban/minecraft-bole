package xienaoban.minecraft.bole.core;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.TranslatableText;
import xienaoban.minecraft.bole.util.Keys;

import java.util.Optional;

public class BoleHandbookItem {
    private static final String ID = "bole_handbook";

    public static ItemStack createBook() {
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
        NbtCompound nbt = new NbtCompound();
        nbt.putString(ID, createVersion());
        nbt.putInt("CustomModelData", 14489768);
        nbt.putString(WrittenBookItem.TITLE_KEY, new TranslatableText(Keys.BOLE_HANDBOOK_TITLE).getString());
        nbt.putString(WrittenBookItem.AUTHOR_KEY, new TranslatableText(Keys.BOLE).getString());
        nbt.putBoolean(WrittenBookItem.RESOLVED_KEY, true);
        nbt.put(WrittenBookItem.PAGES_KEY, createPages());
        stack.setNbt(nbt);
        return stack;
    }

    private static String createVersion() {
        String version = "<unknown>";
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Keys.BOLE);
        if (modContainer.isPresent()) version = modContainer.get().getMetadata().getVersion().toString();
        return version;
    }

    private static NbtList createPages() {
        String page0Json = """
                [
                    {
                        "translate": "%s",
                        "with": [
                            {
                                "translate": "%s",
                                "color": "dark_green",
                                "bold": true
                            }
                        ],
                        "color": "black",
                        "bold": true
                    },
                    "\\n\\n",
                    {
                        "translate": "%s",
                        "color": "black",
                        "bold": false
                    },
                    "\\n",
                    {
                        "text": "[ Github ]",
                        "color": "blue",
                        "bold": false,
                        "underlined": true,
                        "clickEvent": {
                            "action": "open_url",
                            "value": "%s"
                        },
                        "hoverEvent": {
                            "action": "show_text",
                            "contents": {
                                "translate": "%s"
                            }
                        }
                    }
                ]
                """
                .formatted(Keys.TEXT_MOD_NAME_IS, Keys.MOD_NAME, Keys.TEXT_MOD_NOT_INSTALLED, Keys.GITHUB_RELEASE, Keys.TEXT_CLICK_ME);
        NbtList pages = new NbtList();
        pages.add(NbtString.of(page0Json));
        return pages;
    }
}
