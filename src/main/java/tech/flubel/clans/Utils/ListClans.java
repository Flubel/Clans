package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ListClans {

    public static void ClanLister(Player player) {
        File clansFile = new File(player.getServer().getPluginManager().getPlugin("Clans").getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (!clansConfig.contains("clans")) {
            player.sendMessage("There are no clans yet.");
            return;
        }

        Set<String> clanNamesSet = clansConfig.getConfigurationSection("clans").getKeys(false);
        if (clanNamesSet.isEmpty()) {
            player.sendMessage("There are no clans to display.");
            return;
        }

        // Map of clan name to total member count (leader + co-leader + members)
        Map<String, Integer> clanMemberCounts = new HashMap<>();

        for (String clanName : clanNamesSet) {
            List<String> members = clansConfig.getStringList("clans." + clanName + ".members");
            List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");
            String leader = clansConfig.getString("clans." + clanName + ".leader");

            int totalMembers = members.size() + coLeaders.size() + (leader != null ? 1 : 0);
            clanMemberCounts.put(clanName, totalMembers);
        }

        // Sort clan names based on member count descending
        List<String> sortedClans = new ArrayList<>(clanNamesSet);
        sortedClans.sort((a, b) -> Integer.compare(clanMemberCounts.get(b), clanMemberCounts.get(a)));

        // Display sorted clans
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.GOLD + "Top Clans List:");
        for (int i = 0; i < sortedClans.size(); i++) {
            String clanName = sortedClans.get(i);

            String leader = clansConfig.getString("clans." + clanName + ".leader");
            String prefix = clansConfig.getString("clans." + clanName + ".prefix");
            String maxmembers = clansConfig.getString("clans." + clanName + ".max_members");
            String formattedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
            int totalMembers = clanMemberCounts.get(clanName);

            player.sendMessage(ChatColor.BOLD + "" + (i + 1) + ") " + formattedClanName + ChatColor.YELLOW + " | Leader: " + leader + " | Members: ("
                    + totalMembers + "/" + maxmembers + ").");
        }
    }
}
