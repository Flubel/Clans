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

public class ClanJoin {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public ClanJoin(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

//    public void requestJoinClan(Player player, String clanName) {
//        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
//        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);
//
//        // Check if clan exists
//        if (!config.contains("clans." + clanName)) {
//            player.sendMessage(ChatColor.RED + "Clan " + clanName + " does not exist.");
//            return;
//        }
//
//        // Check if player is already in any clan
//        for (String existingClan : config.getConfigurationSection("clans").getKeys(false)) {
//            List<String> members = config.getStringList("clans." + existingClan + ".members");
//            List<String> coLeaders = config.getStringList("clans." + existingClan + ".co_leader");
//            String leader = config.getString("clans." + existingClan + ".leader");
//
//            if (members.contains(player.getName()) || coLeaders.contains(player.getName()) || leader.equals(player.getName())) {
//                player.sendMessage(ChatColor.RED + "You are already in a clan.");
//                return;
//            }
//        }
//
//        // Notify leader and co-leaders of the clan
//        String leader = config.getString("clans." + clanName + ".leader");
//        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
//
//        Player leaderPlayer = Bukkit.getPlayer(leader);
//        if (leaderPlayer != null && leaderPlayer.isOnline()) {
//            leaderPlayer.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + player.getName() + " wants to join your clan: " + clanName);
//        }
//
//        for (String co : coLeaders) {
//            Player coPlayer = Bukkit.getPlayer(co);
//            if (coPlayer != null && coPlayer.isOnline()) {
//                coPlayer.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + player.getName() + " wants to join your clan: " + clanName);
//            }
//        }
//
//        File requestsFile = new File(plugin.getDataFolder(), "join_requests.yml");
//        FileConfiguration reqConfig = YamlConfiguration.loadConfiguration(requestsFile);
//
//        if (reqConfig.contains("requests")) {
//            for (String otherClan : reqConfig.getConfigurationSection("requests").getKeys(false)) {
//                List<String> otherRequests = reqConfig.getStringList("requests." + otherClan);
//                if (otherRequests.remove(player.getName())) {
//                    reqConfig.set("requests." + otherClan, otherRequests);
//                }
//            }
//        }
//
//        List<String> requests = reqConfig.getStringList("requests." + clanName);
//        if (!requests.contains(player.getName())) {
//            requests.add(player.getName());
//            reqConfig.set("requests." + clanName, requests);
//
//            try {
//                reqConfig.save(requestsFile);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        player.sendMessage(ChatColor.GREEN + "Join request sent to the clan " + clanName + "'s leader and co-leaders.");
//
//    }


    public void requestJoinClan(Player player, String clanName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        // Check if clan exists
        if (!config.contains("clans." + clanName)) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("join.no-clan"));
            return;
        }

        // Check if player is already in any clan
        for (String existingClan : config.getConfigurationSection("clans").getKeys(false)) {
            List<String> members = config.getStringList("clans." + existingClan + ".members");
            List<String> coLeaders = config.getStringList("clans." + existingClan + ".co_leader");
            String leader = config.getString("clans." + existingClan + ".leader");

            if (members.contains(player.getName()) || coLeaders.contains(player.getName()) || leader.equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("join.already-member"));
                return;
            }
        }

        MemberCount memberCount = new MemberCount(plugin);
        int currentMembers = memberCount.getClanMembersCount(clanName);

        if(currentMembers >= config.getInt("clans." + clanName + ".max_members")){
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW +  languageManager.get("join.full"));
            return;
        }

        // Load requests
        File requestsFile = new File(plugin.getDataFolder(), "join_requests.yml");
        FileConfiguration reqConfig = YamlConfiguration.loadConfiguration(requestsFile);

        // Remove player's request from all other clans
        if (reqConfig.contains("requests")) {
            for (String otherClan : reqConfig.getConfigurationSection("requests").getKeys(false)) {
                List<String> otherRequests = reqConfig.getStringList("requests." + otherClan);
                if (otherRequests.remove(player.getName())) {
                    reqConfig.set("requests." + otherClan, otherRequests);
                }
            }
        }

        // Add new request
        List<String> requests = reqConfig.getStringList("requests." + clanName);
        if (!requests.contains(player.getName())) {
            requests.add(player.getName());
            reqConfig.set("requests." + clanName, requests);
        }

        try {
            reqConfig.save(requestsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Notify leader and co-leaders
        String leader = config.getString("clans." + clanName + ".leader");
        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());

        Player leaderPlayer = Bukkit.getPlayer(leader);
        if (leaderPlayer != null && leaderPlayer.isOnline()) {
            leaderPlayer.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("join.join-req", placeholders));
        }

        for (String co : coLeaders) {
            Player coPlayer = Bukkit.getPlayer(co);
            if (coPlayer != null && coPlayer.isOnline()) {
                coPlayer.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("join.join-req", placeholders));
            }
        }

        String prefix = config.getString("clans." + clanName + ".prefix");
        String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);

        Map<String, String> placeholders1 = new HashMap<>();
        placeholders1.put("clan_name", TranslatedClanName);

        String message = languageManager.get("join.success", placeholders1);
        message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);

        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
    }

}
