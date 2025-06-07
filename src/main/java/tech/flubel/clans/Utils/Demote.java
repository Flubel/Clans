package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demote {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public Demote(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }


    public void demotePlayer(Player leader, String targetName) {
        String clanName = getClanName(leader);
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        // Check if the clan exists
        if (!config.contains("clans." + clanName)) {
            leader.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("demote.no-exist"));
            return;
        }
        if(clanName == null) {
            leader.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("demote.no-clan"));
            return;
        }

        // Get clan information
        List<String> members = config.getStringList("clans." + clanName + ".members");
        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
        String leaderName = config.getString("clans." + clanName + ".leader");

        // Check if the leader is demoting a co-leader
        if (!leader.getName().equals(leaderName)) {
            leader.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("demote.no-auth"));
            return;
        }

        // Check if the target player is a co-leader
        if (!coLeaders.contains(targetName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);
            leader.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("demote.no-dem", placeholders));
            return;
        }

        // Check if the player is the leader, since the leader cannot be demoted
        if (targetName.equals(leaderName)) {
            leader.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("demote.void"));
            return;
        }

        // Demote the co-leader to member
        coLeaders.remove(targetName);
        members.add(targetName);

        // Save the updated lists to the configuration
        config.set("clans." + clanName + ".members", members);
        config.set("clans." + clanName + ".co_leader", coLeaders);
        Player demoted = Bukkit.getPlayer(targetName);

        try {
            config.save(clansFile);
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);

            leader.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("demote.success", placeholders));

            Map<String, String> placeholders1 = new HashMap<>();
            placeholders1.put("leader", leader.getName());
            demoted.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("demote.success-demoted", placeholders1));
        } catch (Exception e) {
            leader.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("demote.error"));
            e.printStackTrace();
        }
    }


    private String getClanName(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            if (clansConfig.getString("clans." + clanName + ".leader").equals(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".co_leader").contains(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".members").contains(player.getName())) {
                return clanName;
            }
        }
        return null;
    }

}
