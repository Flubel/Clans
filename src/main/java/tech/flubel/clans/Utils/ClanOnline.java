package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.Clans;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClanOnline {
    private final Clans plugin;
    private final LanguageManager languageManager;

    public ClanOnline(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = (Clans) plugin;
        this.languageManager = languageManager;
    }


    public void FetchOnlineMembers(Player player) {

        String clanName = getClanName(player);
        if (clanName == null) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("online.no-clan"));
            return;
        }

        List<String> clanMembers = getClanMembers(clanName);



        List<Player> onlineClanMembers = new ArrayList<>();
        for (String name : clanMembers) {
            Player onlinePlayer = plugin.getServer().getPlayer(name);
            if (onlinePlayer != null && onlinePlayer.isOnline()) {
                onlineClanMembers.add(onlinePlayer);
            }
        }

        // Example: show online members to the player
        if (onlineClanMembers.isEmpty()) {
            player.sendMessage(ChatColor.GRAY +""+ ChatColor.BOLD + "| " + ChatColor.GRAY + languageManager.get("online.no-online"));
        } else {
            player.sendMessage(ChatColor.GOLD +""+ ChatColor.BOLD + "| " + ChatColor.GOLD + languageManager.get("online.title"));
            for (Player member : onlineClanMembers) {
                if(member.getName().equals(player.getName())){
                    player.sendMessage(ChatColor.GREEN + "> " + member.getName());
                } else {
                    player.sendMessage(ChatColor.YELLOW + "- " + member.getName());
                }
            }
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

}
