package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

public class Requests {

    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public Requests(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
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
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("join_requests.no-auth"));
            return;
        }

        File requestsFile = new File(plugin.getDataFolder(), "join_requests.yml");
        FileConfiguration reqConfig = YamlConfiguration.loadConfiguration(requestsFile);

        List<String> requests = reqConfig.getStringList("requests." + playerClan);

        if (requests.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW +""+ ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("join_requests.no-requests"));
        } else {
            String prefix = clansConfig.getString("clans." + playerClan + ".prefix");
            String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);


            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("clan_name", TranslatedClanName);

            String message = languageManager.get("join_requests.request-list-header", placeholders);
            message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN + "" + ChatColor.BOLD);

            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.GREEN + message);




            for (String requester : requests) {
                player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + requester);
            }
        }
    }
}
