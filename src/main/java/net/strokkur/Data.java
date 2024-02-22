package net.strokkur;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Data {

    static File folder = new File("plugins/Strokkur");
    static File file = new File("plugins/Strokkur/format-config.yml");
    static YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    static {
        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            if (!file.exists()) {
                file.createNewFile();

                cfg.set("format.chatmessage", "%luckperms_prefix%%player_name% <gray>»</gray> ");
                cfg.set("format.hovermessage", List.of("<bold><red>%player_name%'s Stats<reset>",
                        "<yellow>Money:</yellow> <white>0</white>",
                        "<green>Exp:</green> <white>0</white>"));

                cfg.set("placeholder.item.enabled", true);
                cfg.set("placeholder.inv.enabled", true);
                cfg.set("placeholder.inv.format", "<dark_gray>[<green>%player_name%'s Inventory</green>]</dark_gray>");

                cfg.set("placeholder.item.chat", List.of("[item]", "[i]"));
                cfg.set("placeholder.inv.chat", List.of("[inv]", "[inventory]"));

                cfg.set("placeholder.allow-other-content", true);

                cfg.set("message.invExpiration", "<dark_red>[!]</dark_red> <red>This inventory already expired</red>");
                cfg.set("message.noItem", "<dark_red>[!]</dark_red> <red>You are not holding an item!</red>");
                cfg.set("message.reload", "<red>[!]</red> <yellow>Successfully reloaded <aqua>format-config.yml</aqua></yellow>");
                cfg.set("message.invInfo", "<dark_gray>[<yellow>Click to view inventory</yellow>]</dark_gray>");

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

        format = cfg.getString("format.chatmessage");
        hoverFormat = cfg.getStringList("format.hovermessage");

        item = cfg.getBoolean("placeholder.item.enabled");
        inv = cfg.getBoolean("placeholder.inv.enabled");
        invText = cfg.getString("placeholder.inv.format");

        other_content = cfg.getBoolean("placeholder.allow-other-content");

        itemPlaceholders = cfg.getStringList("placeholder.item.chat");
        invPlaceholders = cfg.getStringList("placeholder.inv.chat");

        expirationMessage = cfg.getString("message.invExpiration");
        noItem = cfg.getString("message.noItem");
        reloadMessage = cfg.getString("message.reload");
        invInfo = cfg.getString("message.invInfo");
    }

    public static List<String> hoverFormat, itemPlaceholders, invPlaceholders;
    public static String expirationMessage, format, invText, reloadMessage, noItem, invInfo;
    public static boolean item, inv, other_content;


}
