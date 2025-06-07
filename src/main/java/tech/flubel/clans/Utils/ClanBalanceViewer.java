package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ClanBalanceViewer {

    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public ClanBalanceViewer(JavaPlugin plugin, LanguageManager languageManager){
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void balanceviewer(Player player){
        String clanName = getClanName(player);

        if (clanName == null) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("bank.no-clan"));
            return;
        }

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        int clanbalance = clansConfig.getInt("clans." + clanName + ".balance");


        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("amount", String.valueOf(clanbalance));

        String message = languageManager.get("bank.view.success", placeholders);
        message = message.replace(String.valueOf(clanbalance), ChatColor.GREEN + "" + ChatColor.BOLD + "$" +clanbalance + ChatColor.YELLOW);


        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + message);

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
