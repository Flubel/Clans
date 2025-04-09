package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class Demote {
    private final JavaPlugin plugin;

    public Demote(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public void demotePlayer(Player leader, String targetName) {
        String clanName = getClanName(leader);
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        // Check if the clan exists
        if (!config.contains("clans." + clanName)) {
            leader.sendMessage(ChatColor.RED + "Clan " + clanName + " does not exist.");
            return;
        }

        // Get clan information
        List<String> members = config.getStringList("clans." + clanName + ".members");
        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
        String leaderName = config.getString("clans." + clanName + ".leader");

        // Check if the leader is demoting a co-leader
        if (!leader.getName().equals(leaderName)) {
            leader.sendMessage(ChatColor.RED + "Only the leader can demote players.");
            return;
        }

        // Check if the target player is a co-leader
        if (!coLeaders.contains(targetName)) {
            leader.sendMessage(ChatColor.RED + targetName + " is not a co-leader.");
            return;
        }

        // Check if the player is the leader, since the leader cannot be demoted
        if (targetName.equals(leaderName)) {
            leader.sendMessage(ChatColor.RED + "You cannot demote the leader.");
            return;
        }

        // Demote the co-leader to member
        coLeaders.remove(targetName);
        members.add(targetName);

        // Save the updated lists to the configuration
        config.set("clans." + clanName + ".members", members);
        config.set("clans." + clanName + ".co_leader", coLeaders);

        try {
            config.save(clansFile);
            leader.sendMessage(ChatColor.GREEN + "Successfully demoted " + targetName + " to Member.");
        } catch (Exception e) {
            leader.sendMessage(ChatColor.RED + "Error demoting the player.");
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
