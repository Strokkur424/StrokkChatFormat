package net.strokkur.event;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.strokkur.Data;
import net.strokkur.datasave.SInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class MessageEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage(PlayerChatEvent e) {
        e.setCancelled(true);

        String msg = e.getMessage();
        Player p = e.getPlayer();

        Component hoverContent = deserialize(addMsgCommandSuggestion("<hover:show_text:\"" + modifiedHover(p) + "\">" + setPlaceholders(p, Data.format) + "<reset>", p));
        Component outContent = deserialize(addMsgCommandSuggestion(msg, p));

        boolean inv = false;

        if (Data.inv && msg.contains("[inv]")) {
            inv = true;
            int id = new SInventory(p).getID();
            if (Data.other_content) {
                String[] strings = msg.split("\\[inv]", 2);
                outContent = deserialize(addMsgCommandSuggestion(strings[0], p) + "<click:run_command:/scformat viewinv6237345 " + id + ">" + setPlaceholders(p, Data.invText) + "</click>" + addMsgCommandSuggestion(strings[1], p));

            }
            else {
                outContent = deserialize("<click:run_command:/scformat viewinv6237345 " + id + ">" + setPlaceholders(p, Data.invText) + "</click>");
            }
        }

        if (!inv && Data.item && msg.contains("[item]")) {
            ItemStack item = p.getInventory().getItemInMainHand();

            Component hover = item.displayName();
            if (item.lore() != null) {
                hover = hover.appendNewline();
                for (int i = 0; i < item.lore().size(); i++) {
                    hover = hover.append(item.lore().get(i));
                    if (i != item.lore().size() - 1)
                        hover = hover.appendNewline();
                }
            }

            hover = hover.appendNewline();
            hover = hover.append(deserialize(getMaterialText(item)));

            String itemComponent ="<hover:show_text:\"" + serialize(hover) + "\">" + Data.itemText.replaceAll("\\{item}", serialize(item.displayName())) + "</hover>";

            if (Data.other_content) {
                String[] strings = msg.split("\\[item]", 2);
                outContent = deserialize(addMsgCommandSuggestion(strings[0], p) + itemComponent + addMsgCommandSuggestion(strings[1], p));
            }
            else {
                outContent = deserialize(itemComponent);
            }
        }

        Component out = hoverContent.append(deserialize("<reset>")).append(outContent);
        Bukkit.broadcast(out);
    }

    String modifiedHover(Player p) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < Data.hoverFormat.size(); i++) {
            String str = Data.hoverFormat.get(i);
            out.append(setPlaceholders(p, str));
            if (i != Data.hoverFormat.size() - 1)
                out.append(deserialize("<newline>"));
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
        return "<click:suggest_command:/msg " + p.getName() + ">" + middleMsg + "</click>";
    }

    String getMaterialText(ItemStack is) {
        return "<dark_gray>minecraft:" + is.getType().toString().toLowerCase() + "</dark_gray>";
    }
}
