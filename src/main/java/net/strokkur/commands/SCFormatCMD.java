package net.strokkur.commands;

import net.md_5.bungee.api.chat.TextComponent;
import net.strokkur.Data;
import net.strokkur.Main;
import net.strokkur.datasave.SInventory;
import net.strokkur.event.MessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class SCFormatCMD implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length > 0) {
            if (args[0].equals("viewinv6237345")) {
                SInventory.openInventory(Integer.parseInt(args[1]), (Player) sender);
                return true;
            }

            if (args[0].equals("reload") && sender.hasPermission("strokkur.reload")) {
                Data.reload();
                sender.sendMessage(Data.reloadMessage);
                return true;
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return args.length == 1 ? (sender.hasPermission("strokkur.reload") ? List.of("reload") : List.of()) : List.of();
    }

}
