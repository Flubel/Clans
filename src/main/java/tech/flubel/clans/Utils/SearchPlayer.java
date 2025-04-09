package tech.flubel.clans.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class SearchPlayer {

    private final JavaPlugin plugin;

    public SearchPlayer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isPlayerInClan(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            List<String> members = clansConfig.getStringList("clans." + clanName + ".members");
            if (members.contains(player.getName())
                    ||
                clansConfig.getString("clans." + clanName + ".co_leader").equals(player.getName())
                        ||
                clansConfig.getString("clans." + clanName + ".leader").equals(player.getName())) {
                return true;
            }
        }
        return false;
    }
}
