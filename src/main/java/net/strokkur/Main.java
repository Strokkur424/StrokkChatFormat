package net.strokkur;

import net.strokkur.commands.SCFormatCMD;
import net.strokkur.event.MessageEvent;
import net.strokkur.util.fastinv.FastInvManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Plugin plugin;
    public static Main main;

    @Override
    public void onEnable() {
        plugin = this;
        main = this;

        FastInvManager.register(this);

        event(new MessageEvent());
        registerCommand(new SCFormatCMD(), "scformat");
        //registerCommand(new TestCMD(), "test");
    }

    public void event(Listener e) {
        getServer().getPluginManager().registerEvents(e, plugin);
    }
    public void registerCommand(CommandExecutor e, String name) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null)
            return;

        cmd.setExecutor(e);
        if (e instanceof TabCompleter tab) {
            cmd.setTabCompleter(tab);
        }
    }

}
