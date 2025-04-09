package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class Kick {
    private final JavaPlugin plugin;

    public Kick(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void kickPlayer(Player kicker, String targetName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        String clanName = getClanName(kicker); // Assume you have a method to get the clan name of the player
        if (clanName == null) {
            kicker.sendMessage(ChatColor.RED + "You are not in any clan.");
            return;
        }

        // Get clan information
        List<String> members = config.getStringList("clans." + clanName + ".members");
        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
        String leader = config.getString("clans." + clanName + ".leader");

        // Check if the kicker is the leader
        if (kicker.getName().equals(leader)) {
            // Leader can kick anyone (members and co-leaders)
            kickFromClan(targetName, clanName, members, coLeaders, config, clansFile, kicker);
        }
        // Check if the kicker is a co-leader
        else if (coLeaders.contains(kicker.getName())) {
            // Co-leader can only kick members, not co-leaders
            if (coLeaders.contains(targetName)) {
                kicker.sendMessage(ChatColor.RED + "You cannot kick a co-leader.");
                return;
            }
            kickFromClan(targetName, clanName, members, coLeaders, config, clansFile, kicker);
        }
        // If the kicker is a member, they cannot kick anyone
        else {
            kicker.sendMessage(ChatColor.RED + "You do not have permission to kick players.");
        }
    }

    private void kickFromClan(String targetName, String clanName, List<String> members, List<String> coLeaders, FileConfiguration config, File clansFile, Player kicker) {
        // Check if the target player is in the clan
        if (!members.contains(targetName) && !coLeaders.contains(targetName)) {
            kicker.sendMessage(ChatColor.RED + targetName + " is not a member or co-leader of your clan.");
            return;
        }

        // Remove target from the appropriate list (members or co-leaders)
        if (members.contains(targetName)) {
            members.remove(targetName);
            config.set("clans." + clanName + ".members", members);
        }
        if (coLeaders.contains(targetName)) {
            coLeaders.remove(targetName);
            config.set("clans." + clanName + ".co_leader", coLeaders);
        }

        // Save the changes
        try {
            config.save(clansFile);
            kicker.sendMessage(ChatColor.GREEN + "Successfully kicked " + targetName + " from the clan.");
            // Optionally, notify the kicked player
            Player target = kicker.getServer().getPlayer(targetName);
            if (target != null) {
                target.sendMessage(ChatColor.RED + "You have been kicked from the clan.");
            }
        } catch (Exception e) {
            kicker.sendMessage(ChatColor.RED + "An error occurred while kicking the player.");
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
