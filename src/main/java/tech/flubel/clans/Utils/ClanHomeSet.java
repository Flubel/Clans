package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;

public class ClanHomeSet {

    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public ClanHomeSet(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void setClanHome(Player player) {
        String clanName = getClanName(player);

        if (clanName == null) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("home.no-clan"));
            return;
        }

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        String leader = clansConfig.getString("clans." + clanName + ".leader");
        if (!player.getName().equals(leader)) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("home.no-auth"));
            return;
        }

        Location playerLocation = player.getLocation();

        clansConfig.set("clans." + clanName + ".home.world", playerLocation.getWorld().getName());
        clansConfig.set("clans." + clanName + ".home.x", playerLocation.getX());
        clansConfig.set("clans." + clanName + ".home.y", playerLocation.getY());
        clansConfig.set("clans." + clanName + ".home.z", playerLocation.getZ());
        clansConfig.set("clans." + clanName + ".home.yaw", playerLocation.getYaw());
        clansConfig.set("clans." + clanName + ".home.pitch", playerLocation.getPitch());

        try {
            clansConfig.save(clansFile);
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("home.success"));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("home.error"));
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
