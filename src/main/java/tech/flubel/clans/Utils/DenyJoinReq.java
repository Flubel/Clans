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

public class DenyJoinReq {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public DenyJoinReq(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void denyRequest(Player denier, String targetName) {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        String playerName = denier.getName();
        String targetLower = targetName.toLowerCase();

        if (!clansConfig.contains("clans")) {
            denier.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.deny.no-clan"));
            return;
        }

        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            String leader = clansConfig.getString("clans." + clanName + ".leader");
            List<String> coLeaders = clansConfig.getStringList("clans." + clanName + ".co_leader");

            // Check if denier is a leader or co-leader of this clan
            if (playerName.equals(leader) || coLeaders.contains(playerName)) {
                File requestsFile = new File(plugin.getDataFolder(), "join_requests.yml");
                FileConfiguration requestsConfig = YamlConfiguration.loadConfiguration(requestsFile);

                List<String> requests = requestsConfig.getStringList("requests." + clanName);

                // Check if player actually requested to join
                if (requests.contains(targetName)) {
                    requests.remove(targetName);
                    requestsConfig.set("requests." + clanName, requests);

                    try {
                        requestsConfig.save(requestsFile);
                    } catch (Exception e) {
                        denier.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.deny.failure"));
                        e.printStackTrace();
                        return;
                    }


                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", targetName);

                    denier.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("invite.deny.denier-msg", placeholders));

                    Player target = Bukkit.getPlayer(targetName);
                    if (target != null && target.isOnline()) {
                        Map<String, String> placeholders1 = new HashMap<>();
                        placeholders1.put("clan_name", clanName);
                        placeholders1.put("denier", denier.getName());

                        target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.deny.denied-msg", placeholders1));
                    }


                    List<String> clanMembers = getClanMembers(clanName);

                    for (String memberName : clanMembers) {
                        Player member = Bukkit.getPlayer(memberName);
                        if (member != null && member.isOnline()) {

                            Map<String, String> placeholders2 = new HashMap<>();
                            placeholders2.put("player", target.getName());
                            placeholders2.put("denier", denier.getName());

                            member.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("invite.deny.clanmem", placeholders2));
                        }
                    }

                    return;
                } else {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("player", targetName);

                    denier.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.deny.no-req", placeholders));
                    return;
                }
            } else {
                denier.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.deny.no-auth"));
            }
        }

        denier.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.deny.no-auth-any"));




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
