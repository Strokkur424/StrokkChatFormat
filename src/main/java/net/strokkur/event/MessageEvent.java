package net.strokkur.event;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.strokkur.Data;
import net.strokkur.datasave.SInventory;
import net.strokkur.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void messageEvent(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        String msg = e.getMessage();
        Player p = e.getPlayer();

        TextComponent preText = new TextComponent(TextComponent.fromLegacyText(replacePlaceholders(p, Data.format)));
        preText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHoverFormat(p, Data.hoverFormat)));
        preText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName()));

        String plainMsg = new TextComponent(TextComponent.fromLegacyText(msg)).toPlainText();
        Pair<Boolean, String> containsInv = containsString(plainMsg, Data.invPlaceholders);
        Pair<Boolean, String> containsItem = containsString(plainMsg, Data.itemPlaceholders);

        if (!containsInv.getLeft() && !containsItem.getLeft()) {
            TextComponent msgComp = new TextComponent(TextComponent.fromLegacyText(msg));
            msgComp.setHoverEvent(null);
            broadcast(preText, msgComp);
            console(p, msg);
            return;
        }

        if (containsInv.getLeft()
                && p.hasPermission("strokkur.inv")
                && Data.inv) {

            int id = new SInventory(p).getID();

            TextComponent invText = new TextComponent(TextComponent.fromLegacyText(replacePlaceholders(p, Data.invText)));
            invText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHoverFormat(p, List.of(Data.invInfo))));
            invText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/scformat viewinv6237345 " + id));

            broadcast(preText, invText);
            console(p, msg);
            return;
        }

        /*

        [((§x§[a-f0-9]){6})(§[a-f0-9])]?*

        */

        if (containsItem.getLeft()
                && p.hasPermission("strokkur.item")
                && Data.item) {

            ItemStack is = p.getInventory().getItemInMainHand();
            if (is.getType().equals(Material.AIR)) {
                p.sendMessage(Data.noItem);
                return;
            }

            ItemTag itemTag = ItemTag.ofNbt(is.getItemMeta() == null ? null : is.getItemMeta().getAsString());

            String[] split = replacePlaceholders(p, Data.itemText).split("\\{item}", 2);
            TextComponent item = new TextComponent(TextComponent.fromLegacyText(getItemName(is)));
            TextComponent first = new TextComponent(TextComponent.fromLegacyText(split[0]));

            HoverEvent hoverE = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(is.getType().getKey().toString(), is.getAmount(), itemTag));
            item.setHoverEvent(hoverE);
            first.setHoverEvent(hoverE);

            if (split.length > 1) {
                TextComponent second = new TextComponent(TextComponent.fromLegacyText(split[1]));
                second.setHoverEvent(hoverE);
                item.addExtra(second);
            }

            broadcast(preText, first, item);
            console(p, msg);
        }
    }
    String getItemName(ItemStack is) {
        if (!is.getItemMeta().getDisplayName().equals("")) {
            return color(is) + is.getItemMeta().getDisplayName();
        }

        String n = is.getType().name().toLowerCase().replace("_", " ");
        String[] split = n.split(" ");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String str = split[i];
            String s1 = str.substring(0, 1);
            String s2 = str.substring(1);
            out.append(s1.toUpperCase() + s2);
            if (i < split.length - 1)
                out.append(" ");
        }
        return color(is) + out;
    }

    List<Material> yellow = List.of(Material.ELYTRA, Material.TOTEM_OF_UNDYING, Material.GOLDEN_APPLE, Material.HEART_OF_THE_SEA, Material.NETHER_STAR, Material.DRAGON_BREATH,
                                    Material.CREEPER_BANNER_PATTERN, Material.SKULL_BANNER_PATTERN, Material.EXPERIENCE_BOTTLE);
    List<Material> blue = List.of(Material.END_CRYSTAL, Material.CONDUIT, Material.BEACON);
    List<Material> pink = List.of(Material.ENCHANTED_GOLDEN_APPLE, Material.MOJANG_BANNER_PATTERN, Material.DRAGON_EGG, Material.JIGSAW, Material.STRUCTURE_BLOCK,
                                    Material.STRUCTURE_VOID, Material.BARRIER, Material.DEBUG_STICK, Material.LIGHT);

    List<String> yellowString = List.of("enchanted_book", "skull", "head");
    List<String> blueString = List.of("music_disc");
    List<String> pinkString = List.of("command");

    String color(ItemStack is) {
        String name = is.getType().name().toLowerCase();
        Material material = is.getType();

        if (yellow.contains(material) || containsString(name, yellowString).getLeft()) {
            return "§e";
        }

        if (blue.contains(material) || containsString(name, blueString).getLeft()) {
            return "§b";
        }

        if (pink.contains(material) || containsString(name, pinkString).getLeft()) {
            return "§d";
        }

        if (!is.getEnchantments().isEmpty())
            return "§b";

        return "§f";
    }

    final static Pattern mmFormat = Pattern.compile("<#[a-f0-9]{6}>", Pattern.CASE_INSENSITIVE);
    final static Pattern regularFormat = Pattern.compile("§#[a-f0-9]{6}", Pattern.CASE_INSENSITIVE);
    public static String replacePlaceholders(Player p, String str) {
        String r = PlaceholderAPI.setPlaceholders(p, str);
        r = r.replaceAll("&", "§");

        Matcher mmFormatMatcher = mmFormat.matcher(r);
        while (mmFormatMatcher.find()) {
            int index = mmFormatMatcher.start();
            char[] chars = new char[6];
            for (int i = 0; i < 6; i++)
                chars[i] = r.charAt(index + 2 + i);
            r = mmFormatMatcher.replaceFirst(makeThisIntoAColorFormat(chars));
            mmFormatMatcher = mmFormat.matcher(r);
        }

        Matcher regularMatcher = regularFormat.matcher(r);
        while (regularMatcher.find()) {
            int index = regularMatcher.start();
            char[] chars = new char[6];
            for (int i = 0; i < 6; i++)
                chars[i] = r.charAt(index + 2 + i);
            r = regularMatcher.replaceFirst(makeThisIntoAColorFormat(chars));
            regularMatcher = regularFormat.matcher(r);
        }

        return r;
    }
    static String makeThisIntoAColorFormat(char[] chars) {
        StringBuilder builder = new StringBuilder("§x");
        for (char c : chars) {
            builder.append("§" + c);
        }
        return builder.toString();
    }
    BaseComponent[] getHoverFormat(Player p, List<String> list) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            out.append(replacePlaceholders(p, list.get(i)));
            if (i != list.size() - 1)
                out.append("\n");
        }
        return TextComponent.fromLegacyText(out.toString());
    }
    public static Pair<Boolean, String> containsString(String str, List<String> list) {
        for (String s : list) {
            if (str.contains(s))
                return new Pair<>(true, s);
        }
        return new Pair<>(false, "none");
    }

    public static void broadcast(TextComponent... text) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(text);
        }
    }
    public static void console(Player p, String msg) {
        Bukkit.getConsoleSender().sendMessage(p.getName() + ": " + msg);
    }
}
