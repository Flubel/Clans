package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DeleteClan {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public DeleteClan(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void deleteclan(String clanName, Player player) {
        if (player.hasPermission("clans.admin")) {
            File clansFile = new File(plugin.getDataFolder(), "clans.yml");
            FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

            if (clansConfig.contains("clans." + clanName)) {
                String prefix = clansConfig.getString("clans." + clanName + ".prefix");
                String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);

                clansConfig.set("clans." + clanName, null);
                try {
                    clansConfig.save(clansFile);
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("clan_name", TranslatedClanName);

                    String message = languageManager.get("delete.success", placeholders);
                    message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);

                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
                } catch (IOException e) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("delete.error"));
                    plugin.getLogger().info(e.getMessage());
                }
            } else {

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("clan_name", clanName);

                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("delete.no-clan", placeholders));
            }
        } else {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("delete.no-perm"));
        }
    }



}
