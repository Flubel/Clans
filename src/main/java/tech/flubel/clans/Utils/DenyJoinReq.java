package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class DenyJoinReq {
    private final JavaPlugin plugin;

    public DenyJoinReq(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void denyRequest(Player denier, String targetName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        String playerName = denier.getName();
        String targetLower = targetName.toLowerCase();

        if (!clansConfig.contains("clans")) {
            denier.sendMessage(ChatColor.RED + "There are no clans.");
            return;
        }

        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            String leader = clansConfig.getString("clans." + clanName + ".leader");
            List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");

            // Check if denier is a leader or co-leader of this clan
            if (playerName.equals(leader) || coLeaders.contains(playerName)) {
                File requestsFile = new File(plugin.getDataFolder(), "join_requests.yml");
                FileConfiguration requestsConfig = YamlConfiguration.loadConfiguration(requestsFile);

                List<String> requests = requestsConfig.getStringList("requests." + clanName);

                // Check if player actually requested to join
                if (requests.contains(targetName)) {
                    requests.remove(targetName);
                    requestsConfig.set("requests." + clanName, requests);

                    try {
                        requestsConfig.save(requestsFile);
                    } catch (Exception e) {
                        denier.sendMessage(ChatColor.RED + "Failed to update join request file.");
                        e.printStackTrace();
                        return;
                    }

                    denier.sendMessage(ChatColor.YELLOW + "You have denied " + targetName + "'s request to join " + clanName + ".");

                    Player target = Bukkit.getPlayer(targetName);
                    if (target != null && target.isOnline()) {
                        target.sendMessage(ChatColor.RED + "Your request to join " + clanName + " has been denied.");
                    }

                    return;
                } else {
                    denier.sendMessage(ChatColor.RED + targetName + " has not requested to join your clan.");
                    return;
                }
            }
        }

        denier.sendMessage(ChatColor.RED + "You are not a leader or co-leader of any clan.");
    }
}
