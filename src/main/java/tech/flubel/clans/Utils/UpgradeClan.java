package tech.flubel.clans.Utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.Clans;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeClan {
    private final JavaPlugin plugin;
    private final Economy economy;
    private final LanguageManager languageManager;

    public UpgradeClan(JavaPlugin plugin, Economy economy, LanguageManager languageManager) {
        this.plugin = plugin;
        this.economy = economy;
        this.languageManager = languageManager;
    }


    public void ClanUpgrader(Player player) throws IOException {
        String clanName = getClanName(player);

        if (clanName == null) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("upgrade.no-clan"));
            return;
        }

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (!clansConfig.getString("clans." + clanName + ".leader").equals(player.getName()) &&
                !clansConfig.getStringList("clans." + clanName + ".co_leader").contains(player.getName())) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("upgrade.no-auth"));
            return;
        }
        int requiredBalance = plugin.getConfig().getInt("upgrade_cost", 10000);
        if (economy.getBalance(player) < requiredBalance) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("upgrade.not-enough-balance"));
            return;
        }
        int slotsUp = plugin.getConfig().getInt("upgrade_slots", 1);

        economy.withdrawPlayer(player, requiredBalance);
        int currentMaxMembers = clansConfig.getInt("clans." + clanName + ".max_members");

        clansConfig.set("clans." + clanName + ".max_members", currentMaxMembers + slotsUp);

        List<String> clanMembers = getClanMembers(clanName);

        for (String memberName : clanMembers) {
            Player member = Bukkit.getPlayer(memberName);
            if (member != null && member.isOnline()) {

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("upgrader_name", player.getName());

                member.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.GOLD + languageManager.get("upgrade.success", placeholders));
            }
        }

        try {
            clansConfig.save(clansFile);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(languageManager.get("clan.error.save-clan"));
            return;
        }



    }

    private List<String> getClanMembers(String clanName) {
        List<String> members = new ArrayList<>();
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (clansConfig.contains("clans." + clanName + ".members")) {
            members.addAll(clansConfig.getStringList("clans." + clanName + ".members"));
        }
        if (clansConfig.contains("clans." + clanName + ".leader")) {
            members.add(clansConfig.getString("clans." + clanName + ".leader"));
        }
        if (clansConfig.contains("clans." + clanName + ".co_leader")) {
            members.addAll(clansConfig.getStringList("clans." + clanName + ".co_leader"));
        }

        return members;
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
