package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPlayer {
    private final JavaPlugin plugin;

    public AddPlayer(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public void PlayerAdder(String clanName, Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        ArrayList<String> membersList = (ArrayList<String>) clansConfig.getStringList("clans." + clanName + ".members");

        MemberCount memberCount = new MemberCount(plugin);
        int currentMembers = memberCount.getClanMembersCount(clanName);

        if(currentMembers >= clansConfig.getInt("clans." + clanName + ".max_members")){
            player.sendMessage(ChatColor.RED + "Clan is Full.");
            membersList.remove(player.getName());
            clansConfig.set("clans." + clanName + ".members", membersList);
            return;
        }


        if (!membersList.contains(player.getName())) {
            membersList.add(player.getName());
            clansConfig.set("clans." + clanName + ".members", membersList);

            try {
                clansConfig.save(clansFile);

                String prefix = clansConfig.getString("clans." + clanName + ".prefix");
                String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
                player.sendMessage(ChatColor.GREEN + "You have joined the clan " + TranslatedClanName +ChatColor.GREEN + "!");
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "An error occurred while adding you to the clan.");
                e.printStackTrace();
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "You are already a member of the clan " + clanName + "!");
        }
    }

}
