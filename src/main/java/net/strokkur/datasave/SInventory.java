package net.strokkur.datasave;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.strokkur.Data;
import net.strokkur.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class SInventory {

    static HashMap<Integer, Inventory> inventoryLists = new HashMap<>();
    static HashMap<UUID, Integer> playerInventoryList = new HashMap<>();
    int id;
    public SInventory(Player p) {
        int i = new Random().nextInt();
        id = i;

        if (playerInventoryList.containsKey(p.getUniqueId())) {
            inventoryLists.remove(playerInventoryList.get(p.getUniqueId()));
        }

        inventoryLists.put(i, getAsInventory(p.getInventory()));
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
        Inventory inv = inventoryLists.get(ID);
        if (inv == null) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(Data.expirationMessage));
            return;
        }

        p.openInventory(inv);
        p.playSound(p, Sound.BLOCK_CHEST_OPEN, 1, 1);
    }

    public Inventory getAsInventory(PlayerInventory p) {
        Inventory inv = Bukkit.createInventory(null, 54);
        ItemStack filler = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        filler.editMeta(meta -> meta.displayName(MiniMessage.miniMessage().deserialize("<black></black>")));

        // Fill inventory with filler items
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, filler);
        }

        setItemStack(inv, 0, p.getHelmet());
        setItemStack(inv, 1, p.getChestplate());
        setItemStack(inv, 2, p.getLeggings());
        setItemStack(inv, 3, p.getBoots());

        setItemStack(inv, 7, p.getItemInOffHand());
        setItemStack(inv, 8, p.getItemInMainHand());

        // Set the rest of the items
        for (int i = 0; i < 36; i++) {
            setItemStack(inv, i + 18, p.getItem(i));
        }

        return inv;
    }

    void setItemStack(Inventory inv, int index, ItemStack is) {
        inv.setItem(index, Objects.requireNonNullElse(is, new ItemStack(Material.AIR)));
    }

}
