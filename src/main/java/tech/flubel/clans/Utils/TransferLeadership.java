package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransferLeadership {
    private final JavaPlugin plugin;

    public TransferLeadership(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void transferLeadership(Player leader, String targetName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        String clanName = getClanName(leader);
        if (clanName == null) {
            leader.sendMessage(ChatColor.RED + "You are not in a clan.");
            return;
        }

        // Get clan information
        List<String> members = config.getStringList("clans." + clanName + ".members");
        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
        String currentLeader = config.getString("clans." + clanName + ".leader");

        // Check if the current player is the leader
        if (!leader.getName().equals(currentLeader)) {
            leader.sendMessage(ChatColor.RED + "Only the current leader can transfer leadership.");
            return;
        }

        // Check if the target player is in the clan (either a member or co-leader)
        if (!members.contains(targetName) && !coLeaders.contains(targetName)) {
            leader.sendMessage(ChatColor.RED + targetName + " is not a valid member or co-leader in your clan.");
            return;
        }

        // Transfer leadership to the target player
        config.set("clans." + clanName + ".leader", targetName);
        if (coLeaders.contains(targetName)) {
            coLeaders.remove(targetName);
            config.set("clans." + clanName + ".co_leader", coLeaders);
        }
        if (members.contains(targetName)) {
            members.remove(targetName);
            config.set("clans." + clanName + ".members", members);
        }

        if (!coLeaders.contains(leader.getName())) {
            coLeaders.add(leader.getName());
            config.set("clans." + clanName + ".co_leader", coLeaders);
        }


        List<String> clanMembers = getClanMembers(clanName);
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {

            String prefix = config.getString("clans." + clanName + ".prefix");
            String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
            target.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "|" + ChatColor.GREEN + " You are now the leader of Clan " + TranslatedClanName + ChatColor.GREEN +  ".");
        }


        for (String memberName : clanMembers) {
            Player member = Bukkit.getPlayer(memberName);
            if (member != null && member.isOnline()) {
                member.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "|" + ChatColor.GREEN +
                        " Clan leadership has been transferred to " + ChatColor.BOLD + targetName + ChatColor.GREEN + ".");
            }
        }
        try {
            config.save(clansFile);
            leader.sendMessage(ChatColor.GREEN + "You have successfully transferred leadership to " + targetName + ".");
        } catch (Exception e) {
            leader.sendMessage(ChatColor.RED + "Error transferring leadership.");
            e.printStackTrace();
        }
    }

    private String getClanName(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        for (String clanName : config.getConfigurationSection("clans").getKeys(false)) {
            List<String> members = config.getStringList("clans." + clanName + ".members");
            List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
            String leader = config.getString("clans." + clanName + ".leader");

            if (members.contains(player.getName()) || coLeaders.contains(player.getName()) || player.getName().equals(leader)) {
                return clanName;
            }
        }
        return null;
    }


    private List<String> getClanMembers(String clanName) {
        List<String> members = new ArrayList<>();
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (clansConfig.contains("clans." + clanName + ".members")) {
            members.addAll(clansConfig.getStringList("clans." + clanName + ".members"));
        }
        if (clansConfig.contains("clans." + clanName + ".leader")) {
            members.add(clansConfig.getString("clans." + clanName + ".leader"));
        }
        if (clansConfig.contains("clans." + clanName + ".co_leader")) {
            members.addAll(clansConfig.getStringList("clans." + clanName + ".co_leader"));
        }

        return members;
    }
}
