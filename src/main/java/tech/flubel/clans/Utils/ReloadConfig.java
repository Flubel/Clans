package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import tech.flubel.clans.Clans;

public class ReloadConfig {

    private Clans plugin;

    public ReloadConfig(Clans plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig(CommandSender sender) {
        if (sender.hasPermission("clans.admin")) {
            plugin.reloadConfig();
            FileConfiguration config = plugin.getConfig();
        } else {
            sender.sendMessage("You do not have permission to reload the configuration.");
        }
    }
}
