package net.strokkur.event;

import io.papermc.paper.event.player.AsyncChatEvent;
import it.unimi.dsi.fastutil.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.strokkur.Data;
import net.strokkur.datasave.SInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MessageEvent implements Listener {

    /*  This is only for testing gradient behaviour
    @EventHandler(priority = EventPriority.NORMAL)
    public void message(AsyncChatEvent e) {
        Component c = deserialize("<gradient:#e0d989:#e08c89>" + serialize(e.message()) +  "</gradient>");
        e.message(c);
    }*/

    @EventHandler(priority = EventPriority.LOW)
    public void preMessage(AsyncChatEvent e) {
        String msg = serialize(e.message());
        Player p = e.getPlayer();
        Component outContent = deserialize(addMsgCommandSuggestion(msg, p));

        boolean inv = false;

        var invText = containsWordFromList(msg, Data.invPlaceholders);
        var itemText = containsWordFromList(msg, Data.itemPlaceholders);

        if (p.hasPermission("strokkur.inv")
                && Data.inv
                && invText.left()) {

            inv = true;
            int id = new SInventory(p).getID();
            String[] strings = msg.split(regexify(invText.right()), 2);
            if (Data.other_content && strings.length == 2) {
                outContent = deserialize(addMsgCommandSuggestion(strings[0], p) + "<hover:show_text:\"" + setPlaceholders(p, Data.invInfo) + "\">" + "<click:run_command:/scformat viewinv6237345 " + id + ">" + setPlaceholders(p, Data.invText) + "<reset>" + addMsgCommandSuggestion(strings[1], p));
            }
            else {
                outContent = deserialize("<hover:show_text:" + setPlaceholders(p, Data.invInfo) + ">").append(deserialize("<click:run_command:/scformat viewinv6237345 " + id + ">" + setPlaceholders(p, Data.invText)));
            }
        }

        if (p.hasPermission("strokkur.item")
                && !inv
                && Data.item
                && itemText.left()) {

            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType().equals(Material.AIR)) {
                p.sendMessage(deserialize(Data.noItem));
            }
            else {
                String itemComponent = serialize(item.displayName().hoverEvent(item.asHoverEvent()));
                String[] strings = msg.split(regexify(itemText.right()), 2);

                if (Data.other_content && strings.length == 2) {
                    outContent = deserialize(addMsgCommandSuggestion(strings[0], p) + itemComponent + "<reset>" + addMsgCommandSuggestion(strings[1], p));
                } else {
                    outContent = deserialize(itemComponent);
                }
            }
        }

        e.message(outContent);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void afterMessage(AsyncChatEvent e) {
        Player p = e.getPlayer();

        Component hoverContent = deserialize(addMsgCommandSuggestion("<hover:show_text:\"" + modifiedHover(p) + "\">" + setPlaceholders(p, Data.format) + "</hover>", p));
        Component out = deserialize("<reset>").append(hoverContent.append(deserialize("<reset>")).append(e.message())).append(deserialize("<reset>"));

        Bukkit.broadcast(out);
        e.setCancelled(true);
    }

    String modifiedHover(Player p) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Data.hoverFormat.size(); i++) {
            String str = Data.hoverFormat.get(i);
            out.append(setPlaceholders(p, str));
            if (i != Data.hoverFormat.size() - 1)
                out.append("<newline>");
        }
        return out.toString();
    }

    String setPlaceholders(Player p, String str) {
        return PlaceholderAPI.setPlaceholders(p, str);
    }

    Component deserialize(String str) {
        return MiniMessage.miniMessage().deserialize(str);
    }
    String serialize(Component comp) {
        return MiniMessage.miniMessage().serialize(comp);
    }

    String addMsgCommandSuggestion(String middleMsg, Player p) {
        return "<click:suggest_command:/msg " + p.getName() + ">" + middleMsg;
    }

    Pair<Boolean, String> containsWordFromList(String str, List<String> list) {
        for (String s : list) {
            if (str.contains(s))
                return Pair.of(true, s);
        }
        return Pair.of(false, "none");
    }
    String regexify(String str) {
        if (str.charAt(0) == '[')
            return "\\" + str;
        return str;
    }
}
