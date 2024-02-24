package net.strokkur;

import net.strokkur.event.MessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("ALL")
public class Data {

    static final File folder = new File("plugins/Strokkur");
    static final File file = new File("plugins/Strokkur/format-config.yml");
    static YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    static {
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Bukkit.getConsoleSender().sendMessage("Guess the folder existed even though it should not have existed :P");
            }
        }

        try {
            if (!file.exists()) {
                //noinspection BlockingMethodInNonBlockingContext
                if (!file.createNewFile()) {
                    Bukkit.getConsoleSender().sendMessage("File was not created even though the file didn't exist just a nanosecond ago ._.");
                }

                cfg.set("format.chatmessage", "%luckperms_prefix%%player_name% §8»§r ");
                cfg.set("format.hovermessage", List.of("§4§l%player_name%'s Stats§r",
                        "§eMoney: §f0§r",
                        "§aExp: §f0§r"));

                cfg.set("placeholder.item.enabled", true);
                cfg.set("placeholder.item.multiple", true);
                cfg.set("placeholder.item.format", "§8[§f{item}§8]");

                cfg.set("placeholder.inv.enabled", true);
                cfg.set("placeholder.inv.multiple", true);
                cfg.set("placeholder.inv.format", "§8[§a%player_name%'s Inventory§8]§r");

                cfg.set("placeholder.item.chat", List.of("[item]", "[i]"));
                cfg.set("placeholder.inv.chat", List.of("[inv]", "[inventory]"));

                cfg.set("message.invExpiration", "§4[!] §cThis inventory already expired.");
                cfg.set("message.noItem", "§4[!] §cYou are not holding an item!");
                cfg.set("message.reload", "§6[!] §eSuccessfully reloaded §bformat-config.yml§e.");
                cfg.set("message.invInfo", "§8[§eClick to view inventory§8]§r");

                try {
                    cfg.save(file);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        reload();
    }

    public static void reload() {
        cfg = YamlConfiguration.loadConfiguration(file);

        format      = cfg.getString    ("format.chatmessage");
        hoverFormat = cfg.getStringList("format.hovermessage");

        item         = cfg.getBoolean("placeholder.item.enabled");
        multipleItem = cfg.getBoolean("placeholder.item.multiple");
        itemText     = cfg.getString ("placeholder.item.format");

        inv          = cfg.getBoolean("placeholder.inv.enabled");
        multipleInv  = cfg.getBoolean("placeholder.inv.multiple");
        invText      = cfg.getString ("placeholder.inv.format");

        itemPlaceholders = cfg.getStringList ("placeholder.item.chat");
        invPlaceholders  =  cfg.getStringList("placeholder.inv.chat");

        expirationMessage = cfg.getString("message.invExpiration");
        noItem            = cfg.getString("message.noItem");
        reloadMessage     = cfg.getString("message.reload");
        invInfo           = cfg.getString("message.invInfo");

        MessageEvent.reloadPatterns();
    }

    public static List<String> hoverFormat, itemPlaceholders, invPlaceholders;
    public static String expirationMessage, format, invText, itemText, reloadMessage, noItem, invInfo;
    public static boolean item, inv, multipleItem, multipleInv;


}
