package net.strokkur.datasave;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.strokkur.Data;
import net.strokkur.Main;
import net.strokkur.util.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class SInventory {

    static HashMap<Integer, FastInv> inventoryLists = new HashMap<>();
    static HashMap<UUID, Integer> playerInventoryList = new HashMap<>();
    int id;
    public SInventory(Player p) {
        int i = new Random().nextInt();
        id = i;

        if (playerInventoryList.containsKey(p.getUniqueId())) {
            inventoryLists.remove(playerInventoryList.get(p.getUniqueId()));
            playerInventoryList.remove(p.getUniqueId());
        }

        inventoryLists.put(i, getAsInventory(p));
        playerInventoryList.put(p.getUniqueId(), i);
        new BukkitRunnable() {
            @Override
            public void run() {
                inventoryLists.remove(i);
            }
        }.runTaskLater(Main.plugin, 20 * 60 * 10);
    }

    public int getID() {
        return id;
    }

    public static void openInventory(int ID, Player p) {
        FastInv inv = inventoryLists.get(ID);
        if (inv == null) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(Data.expirationMessage));
            return;
        }

        inv.open(p);
        p.playSound(p, Sound.BLOCK_CHEST_OPEN, 1, 1);
    }

    public FastInv getAsInventory(Player pl) {
        PlayerInventory p = pl.getInventory();
        FastInv inv = new FastInv(54, pl.getName() + "'s Inventory");

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        filler.editMeta(meta -> meta.displayName(MiniMessage.miniMessage().deserialize("<black></black>")));
        inv.setItems(0, 53, filler, e -> e.setCancelled(true));

        setItemStack(inv, 1, p.getHelmet());
        setItemStack(inv, 2, p.getChestplate());
        setItemStack(inv, 3, p.getLeggings());
        setItemStack(inv, 4, p.getBoots());
        setItemStack(inv, 7, p.getItemInOffHand());

        // Set the rest of the items
        for (int i = 8; i >= 0; i--) {
            setItemStack(inv, i + 9 * 5, p.getItem(i));
        }

        for (int i = 35; i >= 9; i--) {
            setItemStack(inv, i + 9, p.getItem(i));
        }

        return inv;
    }

    void setItemStack(FastInv inv, int index, ItemStack is) {
        inv.setItem(index, Objects.requireNonNullElse(is, new ItemStack(Material.AIR)), e -> e.setCancelled(true));
    }

}
