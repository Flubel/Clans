package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListPlayers {

    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public ListPlayers(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }


    public void PlayerLister(Player player) {
        String clanName = getClanName(player);
        if (clanName == null || clanName.isEmpty()) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("list_players.no-clan"));
        }else{
            player.sendMessage(getClanMembers(clanName));
        }
    }

    private String getClanMembers(String clanName) {
        StringBuilder formattedMessage = new StringBuilder();
        List<String> members = new ArrayList<>();
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        // Get clan name
        String prefix = clansConfig.getString("clans." + clanName + ".prefix");
        String formattedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
        formattedMessage.append(formattedClanName + "\n");

        int totalPlayers = 0;

        // Add leader
        if (clansConfig.contains("clans." + clanName + ".leader")) {
            String leader = clansConfig.getString("clans." + clanName + ".leader");
            formattedMessage.append(ChatColor.GOLD + "" + ChatColor.BOLD + languageManager.get("list_players.leader_title") + "\n")
                    .append(ChatColor.BLUE + leader + "\n");
            totalPlayers++;  // Count the leader
        }

        // Add co-leaders
        if (clansConfig.contains("clans." + clanName + ".co_leader")) {
            List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");
            if (!coLeaders.isEmpty()) {
                formattedMessage.append(ChatColor.YELLOW + "" + ChatColor.BOLD + languageManager.get("list_players.co-leader_title") + "\n")
                        .append(ChatColor.DARK_GREEN + String.join(", ", coLeaders) + "\n");
                totalPlayers += coLeaders.size();  // Count co-leaders
            }
        }

        // Add members
        if (clansConfig.contains("clans." + clanName + ".members")) {
            members.addAll(clansConfig.getStringList("clans." + clanName + ".members"));
            if (!members.isEmpty()) {
                formattedMessage.append(ChatColor.YELLOW + "" + ChatColor.BOLD + languageManager.get("list_players.member_title") + "\n")
                        .append(ChatColor.GREEN + String.join(", ", members) + "\n");
                totalPlayers += members.size();  // Count members
            }
        }
        formattedMessage.append(" " + "\n");

        // Show total players
        int maxmembersclan = clansConfig.getInt("clans." + clanName + ".max_members");
        formattedMessage.append(ChatColor.GOLD + languageManager.get("list_players.footer")  + " (").append(ChatColor.WHITE + String.valueOf(totalPlayers) + "/" + maxmembersclan + ChatColor.GOLD + ")\n");

        return formattedMessage.toString();
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
