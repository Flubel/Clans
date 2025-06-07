package tech.flubel.clans.Utils;

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

public class Promote {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public Promote(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void promotePlayer(Player leader, String targetName) {

        String clanName = getClanName(leader);
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        // Check if the clan exists
        if (clanName == null) {
            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("promote.no-clan"));
            return;
        }

        // Get clan information
        List<String> members = config.getStringList("clans." + clanName + ".members");
        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
        String leaderName = config.getString("clans." + clanName + ".leader");

        // Check if the leader is promoting the target player
        if (!leader.getName().equals(leaderName)) {
            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("promote.no-auth"));
            return;
        }

        // Check if the target player is a member
        if (!members.contains(targetName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);

            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("promote.no-member", placeholders));
            return;
        }

        // Check if the player is already a co-leader
        if (coLeaders.contains(targetName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);
            leader.sendMessage(ChatColor.YELLOW +""+ ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("promote.already-promoted", placeholders));
            return;
        }

        // Promote the member to co-leader
        members.remove(targetName);
        coLeaders.add(targetName);

        // Save the updated lists to the configuration
        config.set("clans." + clanName + ".members", members);
        config.set("clans." + clanName + ".co_leader", coLeaders);

        try {
            config.save(clansFile);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);

            leader.sendMessage(ChatColor.GREEN +""+ ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("promote.success", placeholders));
        } catch (Exception e) {
            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("promote.error"));
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
