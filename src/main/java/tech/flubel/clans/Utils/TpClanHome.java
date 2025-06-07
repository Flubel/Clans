package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;

public class TpClanHome {

    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public TpClanHome(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void teleportToClanHome(Player player) {
        // Get the player's clan name
        String clanName = getClanName(player);

        if (clanName == null) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_home.no-clan"));
            return; // Stop execution if the player is not in a clan
        }

        // Load the clans configuration file
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        // Check if the clan has a home set
        if (!clansConfig.contains("clans." + clanName + ".home")) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_home.no-home"));
            return;
        }

        // Retrieve the clan's home location
        String worldName = clansConfig.getString("clans." + clanName + ".home.world");
        double x = clansConfig.getDouble("clans." + clanName + ".home.x");
        double y = clansConfig.getDouble("clans." + clanName + ".home.y");
        double z = clansConfig.getDouble("clans." + clanName + ".home.z");
        float yaw = (float) clansConfig.getDouble("clans." + clanName + ".home.yaw");
        float pitch = (float) clansConfig.getDouble("clans." + clanName + ".home.pitch");

        // Check if the world is valid and exists
        if (plugin.getServer().getWorld(worldName) == null) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_home.no-world"));
            return;
        }

        // Create the location object
        Location homeLocation = new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);

        // Teleport the player to the clan home location
        player.teleport(homeLocation);
        player.sendMessage(ChatColor.GREEN +""+ ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("clan_home.success-tp"));
    }

    // Method to get the player's clan name
    private String getClanName(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        // Loop through all clans to find the player's clan
        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            if (clansConfig.getString("clans." + clanName + ".leader").equals(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".co_leader").contains(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".members").contains(player.getName())) {
                return clanName;
            }
        }
        return null; // Return null if the player is not in any clan
    }
}
