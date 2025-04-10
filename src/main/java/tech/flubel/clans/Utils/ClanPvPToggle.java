package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClanPvPToggle {
    private final JavaPlugin plugin;

    public ClanPvPToggle(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void pvptoggler(Player player){
        String clanName = getClanName(player);

        if (clanName == null) {
            player.sendMessage(ChatColor.RED + "You are not in a clan!");
            return;
        }

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);


        if (!clansConfig.getString("clans." + clanName + ".leader").equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "Only Leaders can toggle pvp between clan members.");
            return;
        }
        boolean currentState = clansConfig.getBoolean("clans." + clanName + ".pvp", true);
        boolean newState = !currentState;
        clansConfig.set("clans." + clanName + ".pvp", newState);
        try {
            clansConfig.save(clansFile);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Error saving PvP toggle state.");
            return;
        }

        String status = newState ? "enabled" : "disabled";

        for (String memberName : getClanMembers(clanName)) {
            Player member = plugin.getServer().getPlayer(memberName);
            if (member != null && member.isOnline()) {
                member.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.GOLD + "Clan PvP has been " + status + " by your leader.");
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
