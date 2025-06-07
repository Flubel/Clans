package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPlayer {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public AddPlayer(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }


    public void PlayerAdder(String clanName, Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        ArrayList<String> membersList = (ArrayList<String>) clansConfig.getStringList("clans." + clanName + ".members");

        MemberCount memberCount = new MemberCount(plugin);
        int currentMembers = memberCount.getClanMembersCount(clanName);

        if(currentMembers >= clansConfig.getInt("clans." + clanName + ".max_members")){
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("add.full"));
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


                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("clan_name", TranslatedClanName);

                String message = languageManager.get("add.success", placeholders);
                message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);

                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("add.error"));
                e.printStackTrace();
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("add.alr-mem"));
        }
    }

}
