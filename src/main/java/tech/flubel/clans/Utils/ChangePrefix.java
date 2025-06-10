package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChangePrefix {
    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public ChangePrefix(JavaPlugin plugin, LanguageManager languageManager){
        this.plugin = plugin;
        this.languageManager = languageManager;
    }


    public void ChangeClanName(String NewPrefix, Player player) throws IOException {
        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        String clanName = getClanName(player);
        if (clanName == null) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("change_prefix.no-clan"));
            return;
        }

        String old_prefix = config.getString("clans." + clanName + ".prefix");
        String currentLeader = config.getString("clans." + clanName + ".leader");

        if (!player.getName().equals(currentLeader)) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("change_prefix.leader-req"));
            return;
        }

        String cleanedClanName = NewPrefix.replaceAll("&[a-zA-Z0-9]", "").toLowerCase();

        Set<String> existingClanNames = config.getConfigurationSection("clans").getKeys(false);

        for (String existingClan : existingClanNames) {
            String cleanedExistingClanName = existingClan.replaceAll("&[a-zA-Z0-9]", "").toLowerCase();

            if (cleanedClanName.equals(cleanedExistingClanName) && !cleanedClanName.equals(clanName.toLowerCase())) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("change_prefix.name-taken"));
                return;
            }
        }

        Set<String> BannedPrefixes =  new HashSet<>(plugin.getConfig().getStringList("banned_prefixes"));

        for (String BannedPrefix : BannedPrefixes) {
            String cleanedExistingClanName = BannedPrefix.toLowerCase();

            if (cleanedClanName.equals(cleanedExistingClanName)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan.error.banned-prefix"));
                return;
            }
        }

        if(cleanedClanName.contains(" ")){
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan.error.no-space"));
            return;
        }

        if(config.getInt("clans." + clanName + ".prefix_change") <= 0){
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("change_prefix.no-try"));
            return;
        }


        String oldPath = "clans." + clanName;
        String newPath = "clans." + cleanedClanName;


        int currentTries = config.getInt("clans." + clanName + ".prefix_change");
        config.set("clans." + clanName + ".prefix_change", currentTries - 1);
        config.set("clans." + clanName + ".prefix", NewPrefix);

        config.save(new File(plugin.getDataFolder(), "clans.yml"));

        config.set(newPath, config.getConfigurationSection(oldPath).getValues(true));

        try {
            if(cleanedClanName.equals(clanName.toLowerCase())){
                config.save(new File(plugin.getDataFolder(), "clans.yml"));
            }else {
                config.set(oldPath, null);
                config.save(new File(plugin.getDataFolder(), "clans.yml"));
            }

            List<String> clanMembers = getClanMembers(cleanedClanName);

            for (String memberName : clanMembers) {
                Player member = Bukkit.getPlayer(memberName);
                if (member != null && member.isOnline()) {

                    Map<String, String> placeholders = new HashMap<>();

                    String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', old_prefix);
                    String TranslatedClanName1 = ChatColor.translateAlternateColorCodes('&', NewPrefix);

                    placeholders.put("old_name", TranslatedClanName);
                    placeholders.put("new_name", TranslatedClanName1);

                    String message = languageManager.get("change_prefix.success", placeholders);
                    message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);
                    message = message.replace(TranslatedClanName1, TranslatedClanName1 + ChatColor.GREEN);


                    member.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
                }
            }

        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("change_prefix.error"));
            plugin.getLogger().info(e.getMessage());
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
