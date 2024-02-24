package net.strokkur.commands;

import net.strokkur.Data;
import net.strokkur.datasave.SInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
