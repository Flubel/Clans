package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AcceptorJoinReq {
    private final JavaPlugin plugin;

    public AcceptorJoinReq(JavaPlugin plugin) {
        this.plugin = plugin;
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
            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + "You are not a leader or co-leader of any clan.");
            return;
        }

        // Check if the request exists
        List<String> requests = reqConfig.getStringList("requests." + targetClan);
        if (!requests.contains(targetName)) {
            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + "No join request found from player " + targetName + ".");
            return;
        }

        // Check if the player is online
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + targetName + " is not online.");
            return;
        }

        // Make sure the target is not in any other clan
        for (String clan : clanConfig.getConfigurationSection("clans").getKeys(false)) {
            String leader = clanConfig.getString("clans." + clan + ".leader");
            List<String> members = clanConfig.getStringList("clans." + clan + ".members");
            List<String> coLeaders = clanConfig.getStringList("clans." + clan + ".co_leader");

            if (leader.equals(targetName) || members.contains(targetName) || coLeaders.contains(targetName)) {
                staff.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE +  targetName + " is already in a clan.");
                return;
            }
        }

        MemberCount memberCount = new MemberCount(plugin);
        int currentMembers = memberCount.getClanMembersCount(targetClan);

        if(currentMembers >= clanConfig.getInt("clans." + targetClan + ".max_members")){
            staff.sendMessage(ChatColor.RED + "Clan is Full.");
            targetPlayer.sendMessage(ChatColor.RED + "Invite denied as clan is Full.");
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
            staff.sendMessage(ChatColor.RED + "Error saving data.");
            e.printStackTrace();
            return;
        }

        staff.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.WHITE +  "You accepted " + targetName + "'s request to join the clan.");
        String prefix = clanConfig.getString("clans." + targetClan + ".prefix");
        String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
        targetPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + "You have been accepted into the clan " + TranslatedClanName + ChatColor.WHITE + "!");
    }
}
