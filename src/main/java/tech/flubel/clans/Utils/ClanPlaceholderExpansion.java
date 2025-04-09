package tech.flubel.clans.Utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.flubel.clans.Clans;

import java.io.File;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {

    private final Clans plugin;

    public ClanPlaceholderExpansion(Clans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "clans";
    }

    @Override
    public String getAuthor() {
        return "Flubel";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equals("name")) {
            return getPlayerClan(player);
        }
        if (identifier.equals("badge")) {
            return getPlayerClanBadge(player);
        }

        return null;
    }

    private String getPlayerClan(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            if (clansConfig.getString("clans." + clanName + ".leader").equals(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".co_leader").contains(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".members").contains(player.getName())) {
                return clansConfig.getString("clans." + clanName + ".prefix");
            }
        }
        return "";
    }
    private String getPlayerClanBadge(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            if (clansConfig.getString("clans." + clanName + ".leader").equals(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".co_leader").contains(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".members").contains(player.getName())) {

                String prefix = clansConfig.getString("clans." + clanName + ".prefix");

                if (prefix != null) {
                    String colorCode = "";
                    if (prefix.startsWith("&")) {
                        colorCode = prefix.substring(0, 2);
                    }

                    if (!colorCode.isEmpty() && ChatColor.getByChar(colorCode.charAt(1)) != null) {
                        prefix = colorCode + "🛡";
                    } else {
                        prefix = "&6🛡";
                    }
                }

                return prefix;
            }
        }

        return "";
    }


}
