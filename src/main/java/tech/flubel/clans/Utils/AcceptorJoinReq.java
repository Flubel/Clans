package tech.flubel.clans.Utils;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptorJoinReq {
    private final Clans plugin;
    private final LanguageManager languageManager;

    public AcceptorJoinReq(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = (Clans) plugin;
        this.languageManager = languageManager;
    }

    public void acceptJoinRequest(Player staff, String targetName) {
        File clanFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clanConfig = YamlConfiguration.loadConfiguration(clanFile);

        File requestsFile = new File(plugin.getDataFolder(), "join_requests.yml");
        FileConfiguration reqConfig = YamlConfiguration.loadConfiguration(requestsFile);

        String staffName = staff.getName();
        String targetClan = null;

        // Identify the clan of the staff
        for (String clan : clanConfig.getConfigurationSection("clans").getKeys(false)) {
            String leader = clanConfig.getString("clans." + clan + ".leader");
            List<String> coLeaders = clanConfig.getStringList("clans." + clan + ".co_leader");

            if (staffName.equals(leader) || coLeaders.contains(staffName)) {
                targetClan = clan;
                break;
            }
        }

        if (targetClan == null) {
            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.accept.no-clan"));

            return;
        }

        // Check if the request exists
        List<String> requests = reqConfig.getStringList("requests." + targetClan);
        if (!requests.contains(targetName)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);

            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.accept.no-req", placeholders));

            return;
        }

        // Check if the player is online
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", targetName);
            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.accept.not-online", placeholders));
            return;
        }

        // Make sure the target is not in any other clan
        for (String clan : clanConfig.getConfigurationSection("clans").getKeys(false)) {
            String leader = clanConfig.getString("clans." + clan + ".leader");
            List<String> members = clanConfig.getStringList("clans." + clan + ".members");
            List<String> coLeaders = clanConfig.getStringList("clans." + clan + ".co_leader");

            if (leader.equals(targetName) || members.contains(targetName) || coLeaders.contains(targetName)) {

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("player", targetName);
                staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED +  languageManager.get("invite.accept.alr-clan", placeholders));
                return;
            }
        }

        MemberCount memberCount = new MemberCount(plugin);
        int currentMembers = memberCount.getClanMembersCount(targetClan);

        if(currentMembers >= clanConfig.getInt("clans." + targetClan + ".max_members")){
            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.accept.full"));
            targetPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.accept.full-player"));
            requests.remove(targetName);
            reqConfig.set("requests." + targetClan, requests);
            return;
        }

        // Accept and add player to the clan
        List<String> members = clanConfig.getStringList("clans." + targetClan + ".members");
        members.add(targetName);
        clanConfig.set("clans." + targetClan + ".members", members);

        // Remove request
        requests.remove(targetName);
        reqConfig.set("requests." + targetClan, requests);

        try {
            clanConfig.save(clanFile);
            reqConfig.save(requestsFile);
        } catch (IOException e) {
            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.accept.error"));
            e.printStackTrace();
            return;
        }


        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", targetName);

        staff.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("invite.accept.success", placeholders));
        String prefix = clanConfig.getString("clans." + targetClan + ".prefix");
        String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);


        Map<String, String> placeholders1 = new HashMap<>();
        placeholders1.put("clan_name", TranslatedClanName);

        String message = languageManager.get("invite.accept.success-mem", placeholders1);
        message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);


        targetPlayer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
    }
}
