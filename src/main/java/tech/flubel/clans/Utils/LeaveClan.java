package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LeaveClan {
    private final JavaPlugin plugin;

    public LeaveClan(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void clanleaver(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        if (!config.contains("clans")) {
            player.sendMessage(ChatColor.RED + "No clans found.");
            return;
        }

        for (String clanName : config.getConfigurationSection("clans").getKeys(false)) {
            List<String> members = config.getStringList("clans." + clanName + ".members");
            List<String> coleaders = config.getStringList("clans." + clanName + ".co_leader");
            String leader = config.getString("clans." + clanName + ".leader");


                if (player.getName().equals(leader)) {
                    player.sendMessage(ChatColor.RED + "Leaders cannot leave the clan. Transfer leadership or disband.");
                    return;
                }

                if (members.contains(player.getName())) {
                    members.remove(player.getName());
                    config.set("clans." + clanName + ".members", members);
                }

                // Remove from co-leaders
                if (coleaders.contains(player.getName())) {
                    coleaders.remove(player.getName());
                    config.set("clans." + clanName + ".co_leader", coleaders);
                }


                try {
                    config.save(clansFile);
                    player.sendMessage(ChatColor.RED + "You have left the clan " + clanName + ".");
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Error leaving the clan.");
                    e.printStackTrace();
                }
                return;

        }

//        player.sendMessage(ChatColor.RED + "You are not in a clan.");


    }


}