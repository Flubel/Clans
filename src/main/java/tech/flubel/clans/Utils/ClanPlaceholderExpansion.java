package tech.flubel.clans.Utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tech.flubel.clans.Clans;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (identifier.startsWith("list_")) {
            return getTopClanInfo(identifier);
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

    private String getTopClanInfo(String identifier) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (!clansConfig.contains("clans")) return "";

        // Calculate ranks
        Map<String, Double> clanRanks = new HashMap<>();
        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            int members = clansConfig.getStringList("clans." + clanName + ".members").size();
            int coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader").size();
            String leader = clansConfig.getString("clans." + clanName + ".leader");

            int totalMembers = members + coLeaders + (leader != null ? 1 : 0);
            double balance = clansConfig.getDouble("clans." + clanName + ".balance");

            double rank = (0.6 * totalMembers) + (0.4 * balance);
            clanRanks.put(clanName, rank);
        }

        List<String> sortedClans = new ArrayList<>(clanRanks.keySet());
        sortedClans.sort((a, b) -> Double.compare(clanRanks.get(b), clanRanks.get(a)));

        // Parse identifier (like list_1_name)
        try {
            String[] parts = identifier.split("_"); // [list, 1, name]
            int index = Integer.parseInt(parts[1]) - 1;
            String type = parts[2];

            if (index < 0 || index >= sortedClans.size()) return "";

            String clanName = sortedClans.get(index);

            switch (type.toLowerCase()) {
                case "name":
                    return ChatColor.translateAlternateColorCodes('&',
                            clansConfig.getString("clans." + clanName + ".prefix", clanName));
                case "leader":
                    return clansConfig.getString("clans." + clanName + ".leader", "---");
                case "balance":
                    return String.valueOf(clansConfig.getDouble("clans." + clanName + ".balance", 0.0));
                default:
                    return "---";
            }
        } catch (Exception e) {
            return "---";
        }
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
