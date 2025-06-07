package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferLeadership {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public TransferLeadership(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void transferLeadership(Player leader, String targetName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        String clanName = getClanName(leader);
        if (clanName == null) {
            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("transfer.no-clan"));
            return;
        }

        // Get clan information
        List<String> members = config.getStringList("clans." + clanName + ".members");
        List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
        String currentLeader = config.getString("clans." + clanName + ".leader");

        // Check if the current player is the leader
        if (!leader.getName().equals(currentLeader)) {
            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("transfer.leader-req"));
            return;
        }

        // Check if the target player is in the clan (either a member or co-leader)
        if (!members.contains(targetName) && !coLeaders.contains(targetName)) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);

            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("transfer.not-member", placeholders));
            return;
        }

        // Transfer leadership to the target player
        config.set("clans." + clanName + ".leader", targetName);
        if (coLeaders.contains(targetName)) {
            coLeaders.remove(targetName);
            config.set("clans." + clanName + ".co_leader", coLeaders);
        }
        if (members.contains(targetName)) {
            members.remove(targetName);
            config.set("clans." + clanName + ".members", members);
        }

        if (!coLeaders.contains(leader.getName())) {
            coLeaders.add(leader.getName());
            config.set("clans." + clanName + ".co_leader", coLeaders);
        }


        List<String> clanMembers = getClanMembers(clanName);
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {

            String prefix = config.getString("clans." + clanName + ".prefix");
            String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);


            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("clan_name", TranslatedClanName);

            String message = languageManager.get("transfer.success-new-leader", placeholders);
            message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);

            target.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
        }


        for (String memberName : clanMembers) {
            Player member = Bukkit.getPlayer(memberName);
            if (member != null && member.isOnline()) {

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("player", targetName);

                String message = languageManager.get("transfer.success-members", placeholders);

                member.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
            }
        }
        try {
            config.save(clansFile);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("new_leader", targetName);

            leader.sendMessage(ChatColor.GREEN +""+ ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("transfer.success-old-leader"));
        } catch (Exception e) {
            leader.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " +  ChatColor.RED + languageManager.get("transfer.error"));
            e.printStackTrace();
        }
    }

    private String getClanName(Player player) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        for (String clanName : config.getConfigurationSection("clans").getKeys(false)) {
            List<String> members = config.getStringList("clans." + clanName + ".members");
            List<String> coLeaders = config.getStringList("clans." + clanName + ".co_leader");
            String leader = config.getString("clans." + clanName + ".leader");

            if (members.contains(player.getName()) || coLeaders.contains(player.getName()) || player.getName().equals(leader)) {
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
