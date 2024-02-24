package net.strokkur.event;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.strokkur.Data;
import net.strokkur.util.Pair;
import net.strokkur.util.Placeholder;
import net.strokkur.util.Trio;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void messageEvent(AsyncPlayerChatEvent e) {
        if (e.isCancelled())
            return;

        e.setCancelled(true);

        String msg = e.getMessage();
        Player p = e.getPlayer();

        TextComponent preText = new TextComponent(TextComponent.fromLegacyText(replacePlaceholders(p, Data.format)));
        preText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getHoverFormat(p, Data.hoverFormat))));
        preText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName()));

        List<TextComponent> components = replaceInventoryAndItemPlaceholders(msg, p);
        List<TextComponent> out = new ArrayList<>(List.of(preText));
        if (components == null)
            return;

        out.addAll(components);

        broadcast(out.toArray(new TextComponent[0]));
        console(p, msg);
    }

    static final String regex = "(§[0-9a-f]|§x(§[0-9a-f]){6})?";
    static List<Pattern> invPatterns;
    static List<Pattern> itemPatterns;

    List<TextComponent> replaceInventoryAndItemPlaceholders(String legacyText, Player p) {

        Placeholder holder = new Placeholder(legacyText);
        String copy = legacyText;

        for (Pattern r : invPatterns) {
            if (!Data.inv)
                break;

            Matcher matcher = r.matcher(copy);
            while (matcher.find()) {
                int index = matcher.start();
                int length = matcher.end() - index;

                copy = replaceWithAs(copy, index, matcher.end());
                holder.add(new Trio<>(index, length, Placeholder.Type.INV));

                matcher = r.matcher(copy);
            }
        }

        copy = legacyText;

        for (Pattern r : itemPatterns) {
            if (!Data.item)
                break;

            Matcher matcher = r.matcher(copy);
            while (matcher.find()) {
                int index = matcher.start();
                int length = matcher.end() - index;

                copy = replaceWithAs(copy, index, matcher.end());
                holder.add(new Trio<>(index, length, Placeholder.Type.ITEM));

                matcher = r.matcher(copy);
            }
        }

        return holder.compute(p);
    }
    static String replaceWithAs(String str, int start, int end) {
        String one = str.substring(0, start);
        String two = "a".repeat(str.substring(start, end).length());
        String three = str.substring(end);
        return one + two + three;
    }

    final static List<Character> specialChars = List.of('.', '+', '*', '?', '^', '$', '(', ')', '[', ']', '{', '}', '|', '\\');
    public static void reloadPatterns() {
        invPatterns = new ArrayList<>();
        for (String str : Data.invPlaceholders) {
            StringBuilder builder = new StringBuilder();
            for (char c : str.toCharArray()) {
                builder.append(regex);
                if (specialChars.contains(c))
                    builder.append("\\");
                builder.append(c);
            }
            invPatterns.add(Pattern.compile(builder.toString(), Pattern.CASE_INSENSITIVE));
        }

        itemPatterns = new ArrayList<>();
        for (String str : Data.itemPlaceholders) {
            StringBuilder builder = new StringBuilder();
            for (char c : str.toCharArray()) {
                builder.append(regex);
                if (specialChars.contains(c))
                    builder.append("\\");
                builder.append(c);
            }
            itemPatterns.add(Pattern.compile(builder.toString(), Pattern.CASE_INSENSITIVE));
        }
    }

    public static String getItemName(ItemStack is) {
        assert is.getItemMeta() != null;
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
            out.append(s1.toUpperCase()).append(s2);
            if (i < split.length - 1)
                out.append(" ");
        }
        return color(is) + out;
    }

    static List<Material> yellow = List.of(Material.ELYTRA, Material.TOTEM_OF_UNDYING, Material.GOLDEN_APPLE, Material.HEART_OF_THE_SEA, Material.NETHER_STAR, Material.DRAGON_BREATH,
                                    Material.CREEPER_BANNER_PATTERN, Material.SKULL_BANNER_PATTERN, Material.EXPERIENCE_BOTTLE);
    static List<Material> blue = List.of(Material.END_CRYSTAL, Material.CONDUIT, Material.BEACON);
    static List<Material> pink = List.of(Material.ENCHANTED_GOLDEN_APPLE, Material.MOJANG_BANNER_PATTERN, Material.DRAGON_EGG, Material.JIGSAW, Material.STRUCTURE_BLOCK,
                                    Material.STRUCTURE_VOID, Material.BARRIER, Material.DEBUG_STICK, Material.LIGHT);

    static List<String> yellowString = List.of("enchanted_book", "skull", "head");
    static List<String> blueString = List.of("music_disc");
    static List<String> pinkString = List.of("command");

    public static String color(ItemStack is) {
        String name = is.getType().name().toLowerCase();
        Material material = is.getType();

        if (yellow.contains(material) || containsString(name, yellowString).left) {
            return "§e";
        }

        if (blue.contains(material) || containsString(name, blueString).left) {
            return "§b";
        }

        if (pink.contains(material) || containsString(name, pinkString).left) {
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
            builder.append("§").append(c);
        }
        return builder.toString();
    }
    public static BaseComponent[] getHoverFormat(Player p, List<String> list) {
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
