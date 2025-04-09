package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanChat {


    private final JavaPlugin plugin;

    public ClanChat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendClanChatMessage(Player sender, String message) {
        String clanName = getClanName(sender);
        if (clanName == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a clan!");
            return;
        }

        List<String> clanMembers = getClanMembers(clanName);

        for (String memberName : clanMembers) {
            Player member = Bukkit.getPlayer(memberName);
            if (member != null && member.isOnline()) {
                if(getClanMember(clanName).contains(sender.getName())){
                    member.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Clan " +  ChatColor.YELLOW + "[Member] " + sender.getName() + ": " + ChatColor.WHITE + message);
                } else if (getClanCoLeaders(clanName).contains(sender.getName())){
                    member.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Clan " + ChatColor.GOLD + "[Co-Leader] " + sender.getName() + ": " + ChatColor.WHITE + message);
                }else if (getClanLeader(clanName).contains(sender.getName())){
                    member.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Clan " + ChatColor.RED + "[Leader] " + sender.getName() + ": " + ChatColor.WHITE + message);
                } else {
                    member.sendMessage(sender.getName() + ": " + message);
                }
            }
        }

    }

    private String getClanLeader(String clanName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);
        return clansConfig.getString("clans." + clanName + ".leader");
    }

    private List<String> getClanCoLeaders(String clanName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);
        return clansConfig.getStringList("clans." + clanName + ".co_leader");
    }

    private List<String> getClanMember(String clanName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);
        return clansConfig.getStringList("clans." + clanName + ".members");
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
