package net.strokkur.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.strokkur.Data;
import net.strokkur.datasave.SInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SCFormatCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length > 0) {
            if (args[0].equals("viewinv6237345")) {
                SInventory.openInventory(Integer.parseInt(args[1]), (Player) sender);
                return true;
            }

            if (args[0].equals("reload") && sender.hasPermission("strokkur.reload")) {
                Data.reload();
                sender.sendMessage(MiniMessage.miniMessage().deserialize(Data.reloadMessage));
                return true;
            }
        }

        return true;
    }
}
