package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class Requests {

    private final JavaPlugin plugin;

    public Requests(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void showRequests(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        String playerClan = null;
        boolean isLeaderOrCoLeader = false;

        if (clansConfig.contains("clans")) {
            for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
                String leader = clansConfig.getString("clans." + clanName + ".leader");
                List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");

                if (leader != null && leader.equals(player.getName())) {
                    playerClan = clanName;
                    isLeaderOrCoLeader = true;
                    break;
                }

                if (coLeaders.contains(player.getName())) {
                    playerClan = clanName;
                    isLeaderOrCoLeader = true;
                    break;
                }
            }
        }

        if (!isLeaderOrCoLeader || playerClan == null) {
            player.sendMessage(ChatColor.RED + "Only clan leaders or co-leaders can view join requests.");
            return;
        }

        File requestsFile = new File(plugin.getDataFolder(), "join_requests.yml");
        FileConfiguration reqConfig = YamlConfiguration.loadConfiguration(requestsFile);

        List<String> requests = reqConfig.getStringList("requests." + playerClan);

        if (requests.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "There are no join requests for your clan.");
        } else {
            String prefix = clansConfig.getString("clans." + playerClan + ".prefix");
            String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.GREEN + "| Join Requests for Clan: " + TranslatedClanName);
            for (String requester : requests) {
                player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + requester);
            }
        }
    }
}
