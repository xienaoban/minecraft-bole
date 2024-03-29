package xienaoban.minecraft.bole.core;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import xienaoban.minecraft.bole.Bole;
import xienaoban.minecraft.bole.mixin.MixinMinecraftClient;
import xienaoban.minecraft.bole.util.Keys;

/**
 * If the mod is installed correctly, a bole handbook screen will be opened when the player right-clicks the book.
 * But if the mod is not installed, a vanilla book screen will be opened which displays the download address.
 *
 * I didn't choose to define a new book item, instead I just made a book with custom NBT to ensure a good compatibility.
 * And I implemented the opening of the book in the mixin.
 * @see MixinMinecraftClient#onUseBoleHandbook
 */
public class BoleHandbookItem {
    // Any writable book with this nbt key will be recognized as a bole handbook.
    public static final String ID = "bole_handbook";

    public static ItemStack createBook() {
        return createWritableBook();
    }

    /**
     * It's a better solution to use WritableBook to implement the handbook,
     * as it does not require access from the server.
     * @see ClientPlayerEntity#useBook
     */
    private static ItemStack createWritableBook() {
        ItemStack stack = new ItemStack(Items.WRITABLE_BOOK);
        NbtCompound nbt = new NbtCompound();
        addSameNbt(nbt);
        nbt.put(WrittenBookItem.PAGES_KEY, createWritablePages());
        stack.setNbt(nbt);
        return stack;
    }

    /**
     * It's not a good choice to use WrittenBook to implement the handbook,
     * as opening this screen requires server-side permission.
     * @see ServerPlayerEntity#useBook
     * @see ClientPlayNetworkHandler#onOpenWrittenBook
     */
    @Deprecated
    private static ItemStack createWrittenBook() {
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
        NbtCompound nbt = new NbtCompound();
        addSameNbt(nbt);
        nbt.putString(WrittenBookItem.TITLE_KEY, Text.translatable(Keys.BOLE_HANDBOOK_TITLE).getString());
        nbt.putString(WrittenBookItem.AUTHOR_KEY, Text.translatable(Keys.BOLE).getString());
        nbt.putBoolean(WrittenBookItem.RESOLVED_KEY, true);
        nbt.put(WrittenBookItem.PAGES_KEY, createWrittenPages());
        stack.setNbt(nbt);
        return stack;
    }

    private static void addSameNbt(NbtCompound nbt) {
        nbt.putString(ID, createVersion());
        nbt.put("display", createDisplay());
        nbt.putInt("CustomModelData", 14489768);
    }

    private static String createVersion() {
        return Bole.getModVersion();
    }

    private static NbtElement createDisplay() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Name", "{\"translate\": \"" + Keys.BOLE_HANDBOOK_TITLE + "\", \"color\": \"aqua\", \"bold\": true, \"italic\": false}");
        NbtList lore = new NbtList();
        lore.add(NbtString.of("{\"translate\": \"" + Keys.BOLE_HANDBOOK_DESCRIPTION + "\", \"color\": \"dark_aqua\", \"italic\": false}"));
        nbt.put("Lore", lore);
        return nbt;
    }

    private static NbtList createWritablePages() {
        String page0 = """
                §l%s§2§l%s
                
                §r§0%s
                
                Curseforge: §9§n%s
                """
                .formatted(trans(Keys.TEXT_MOD_NAME_IS), trans(Keys.MOD_NAME), trans(Keys.TEXT_MOD_NOT_INSTALLED), Keys.CURSEFORGE_RELEASE);
        NbtList pages = new NbtList();
        pages.add(NbtString.of(page0));
        return pages;
    }

    private static NbtList createWrittenPages() {
        String page0Json = """
                [
                    {
                        "text": "%s",
                        "color": "black",
                        "bold": true
                    },
                    {
                        "text": "%s",
                        "color": "dark_green",
                        "bold": true
                    },
                    "\\n\\n",
                    {
                        "text": "%s",
                        "color": "black",
                        "bold": false
                    },
                    "\\n\\n",
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
                                "text": "%s"
                            }
                        }
                    }
                ]
                """
                .formatted(trans(Keys.TEXT_MOD_NAME_IS), trans(Keys.MOD_NAME), trans(Keys.TEXT_MOD_NOT_INSTALLED), Keys.GITHUB_RELEASE, trans(Keys.TEXT_CLICK_ME));
        NbtList pages = new NbtList();
        pages.add(NbtString.of(page0Json));
        return pages;
    }

    /**
     * If the player doesn't have the mod installed, then the translation files will not be in the client either.
     * @return translated string of the current language
     */
    private static String trans(String translateKey) {
        return Text.translatable(translateKey).getString();
    }
}
