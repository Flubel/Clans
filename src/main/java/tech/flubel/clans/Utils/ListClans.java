package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.util.*;

public class ListClans {

//    public static void ClanLister(Player player) {
//        File clansFile = new File(player.getServer().getPluginManager().getPlugin("Clans").getDataFolder(), "clans.yml");
//        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);
//
//        if (!clansConfig.contains("clans")) {
//            player.sendMessage("There are no clans yet.");
//            return;
//        }
//
//        Set<String> clanNamesSet = clansConfig.getConfigurationSection("clans").getKeys(false);
//        if (clanNamesSet.isEmpty()) {
//            player.sendMessage("There are no clans to display.");
//            return;
//        }
//
//        Map<String, Integer> clanMemberCounts = new HashMap<>();
//
//        for (String clanName : clanNamesSet) {
//            List<String> members = clansConfig.getStringList("clans." + clanName + ".members");
//            List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");
//            String leader = clansConfig.getString("clans." + clanName + ".leader");
//
//            int totalMembers = members.size() + coLeaders.size() + (leader != null ? 1 : 0);
//            clanMemberCounts.put(clanName, totalMembers);
//        }
//
//        List<String> sortedClans = new ArrayList<>(clanNamesSet);
//        sortedClans.sort((a, b) -> Integer.compare(clanMemberCounts.get(b), clanMemberCounts.get(a)));
//
//        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.GOLD + "Top Clans List:");
//        for (int i = 0; i < sortedClans.size(); i++) {
//            String clanName = sortedClans.get(i);
//
//            String leader = clansConfig.getString("clans." + clanName + ".leader");
//            String prefix = clansConfig.getString("clans." + clanName + ".prefix");
//            String maxmembers = clansConfig.getString("clans." + clanName + ".max_members");
//            String formattedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
//            int totalMembers = clanMemberCounts.get(clanName);
//
//            player.sendMessage(ChatColor.BOLD + "" + (i + 1) + ") " + formattedClanName + ChatColor.YELLOW + " | Leader: " + leader + " | Members: ("
//                    + totalMembers + "/" + maxmembers + ").");
//        }
//    }



    public static void ClanLister(Player player, LanguageManager languageManager) {
        File clansFile = new File(player.getServer().getPluginManager().getPlugin("Clans").getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (!clansConfig.contains("clans")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("list_clans.no-clans"));
            return;
        }

        Set<String> clanNamesSet = clansConfig.getConfigurationSection("clans").getKeys(false);
        if (clanNamesSet.isEmpty()) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("list_clans.no-clans"));
            return;
        }

        Map<String, Double> clanRanks = new HashMap<>();

        for (String clanName : clanNamesSet) {
            List<String> members = clansConfig.getStringList("clans." + clanName + ".members");
            List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");
            String leader = clansConfig.getString("clans." + clanName + ".leader");

            int totalMembers = members.size() + coLeaders.size() + (leader != null ? 1 : 0);
            double balance = clansConfig.getDouble("clans." + clanName + ".balance");

            // Calculate weighted rank based on members (60%) and balance (40%)
            double rank = (0.6 * totalMembers) + (0.4 * balance);
            clanRanks.put(clanName, rank);
        }

        List<String> sortedClans = new ArrayList<>(clanNamesSet);
        sortedClans.sort((a, b) -> Double.compare(clanRanks.get(b), clanRanks.get(a)));

        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.GOLD + languageManager.get("list_clans.header"));
        for (int i = 0; i < sortedClans.size(); i++) {
            String clanName = sortedClans.get(i);
            List<String> members = clansConfig.getStringList("clans." + clanName + ".members");
            List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");

            String leader = clansConfig.getString("clans." + clanName + ".leader");
            String prefix = clansConfig.getString("clans." + clanName + ".prefix");
            String maxmembers = clansConfig.getString("clans." + clanName + ".max_members");
            String formattedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
            int totalMembers = members.size() + coLeaders.size() + (leader != null ? 1 : 0);
            double clanBalance = clansConfig.getDouble("clans." + clanName + ".balance");

            player.sendMessage(ChatColor.BOLD + "" + (i + 1) + ") " + formattedClanName + ChatColor.YELLOW + " | " + languageManager.get("list_clans.leader_title") + ": " + leader + " ("
                    + totalMembers + "/" + maxmembers + ") | $" + clanBalance);
        }
    }

}
