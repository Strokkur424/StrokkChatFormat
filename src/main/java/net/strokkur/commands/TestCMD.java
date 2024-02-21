package net.strokkur.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= args.length - 1; i++) {
            builder.append(args[i]).append(" ");
        }
        System.out.println(builder);
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(builder.toString() ));

        return true;
    }
}
