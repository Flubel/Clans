package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import tech.flubel.clans.Clans;
import tech.flubel.clans.LanguageManager.LanguageManager;

public class ReloadConfig {

    private Clans plugin;
    private LanguageManager languageManager;

    public ReloadConfig(Clans plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void reloadConfig(CommandSender sender) {
        if (sender.hasPermission("clans.admin")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("config.success"));
            plugin.setLanguageManager(new LanguageManager(plugin));
            FileConfiguration config = plugin.getConfig();
        } else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("config.no-perm"));
        }
    }
}
