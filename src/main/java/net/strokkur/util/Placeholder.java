package net.strokkur.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.strokkur.Data;
import net.md_5.bungee.api.chat.TextComponent;
import net.strokkur.datasave.SInventory;
import net.strokkur.event.MessageEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Placeholder {

    final List<Trio<Integer, Integer, Type>> places = new ArrayList<>();
    final String original;

    int translation = 0;


    public Placeholder(String str) {
        original = str;

        invLength = new TextComponent(TextComponent.fromLegacyText(Data.invText)).toPlainText().length();
        itemLength = new TextComponent(TextComponent.fromLegacyText(Data.invText)).toPlainText().length();
    }

    public void add(Trio<Integer, Integer, Type> place) {
        places.add(place);
    }

    Integer id = null;

    @Nullable
    public List<TextComponent> compute(Player p) {
        List<TextComponent> out = new ArrayList<>();
        String copy = original;
        sort();

        boolean firstInv = false, firstItem = false;

        for (var trio : places) {

            var v = split(trio.left - translation, copy);
            out.add(new TextComponent(TextComponent.fromLegacyText(v.left)));

            if (trio.right.equals(Type.INV) && p.hasPermission("strokkur.inv") && !(!Data.multipleItem && firstInv)) {
                if (id == null) id = new SInventory(p).getID();

                TextComponent invText = new TextComponent(TextComponent.fromLegacyText(MessageEvent.replacePlaceholders(p, Data.invText)));
                invText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageEvent.getHoverFormat(p, List.of(Data.invInfo)))));
                invText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/scformat viewinv6237345 " + id));

                firstInv = true;

                out.add(invText);
            }
            else if (trio.right.equals(Type.ITEM) && p.hasPermission("strokkur.item") && !(!Data.multipleInv && firstItem)) {
                ItemStack is = p.getInventory().getItemInMainHand();
                if (is.getType().equals(Material.AIR)) {
                    p.sendMessage(Data.noItem);
                    return null;
                }

                ItemTag itemTag = ItemTag.ofNbt(is.getItemMeta() == null ? null : is.getItemMeta().getAsString());

                String[] split = MessageEvent.replacePlaceholders(p, Data.itemText).split("\\{item}", 2);
                TextComponent item = new TextComponent(TextComponent.fromLegacyText(MessageEvent.getItemName(is)));
                TextComponent first = new TextComponent(TextComponent.fromLegacyText(split[0]));

                HoverEvent hoverE = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(is.getType().getKey().toString(), is.getAmount(), itemTag));
                item.setHoverEvent(hoverE);
                first.setHoverEvent(hoverE);

                if (split.length > 1) {
                    TextComponent second = new TextComponent(TextComponent.fromLegacyText(split[1]));
                    item.addExtra(second);
                }

                firstItem = true;

                out.add(first);
                out.add(item);
            }
            else {
                out.add(new TextComponent(TextComponent.fromLegacyText(split(trio.middle, v.right).left)));
            }

            copy = split(trio.middle + trio.left - translation, copy).right;

            translation += v.left.length();
            translation += trio.middle;
        }

        out.add(new TextComponent(TextComponent.fromLegacyText(copy)));
        return out;
    }


    void sort() {
        places.sort(Comparator.comparingInt(t -> t.left));
    }

    Pair<String, String> split(int i, String str) {
        return new Pair<>(str.substring(0, i), str.substring(i));
    }

    public enum Type {
        INV,
        ITEM
    }
}
