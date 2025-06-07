package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import tech.flubel.clans.LanguageManager.LanguageManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InvitePlayer {


    private final JavaPlugin plugin;
    private final LanguageManager languageManager;

    public InvitePlayer(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }
    public void sendInvite(Player inviter, String invitedPlayerName) {

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);
        String clanName = getClanName(inviter);
        if (clanName == null || clanName.isEmpty()) {
            inviter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.no-clan"));
            return;
        }


        if (!clansConfig.getString("clans." + clanName + ".leader").equals(inviter.getName()) &&
                !clansConfig.getStringList("clans." + clanName + ".co_leader").contains(inviter.getName())) {
            inviter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.no-auth"));
            return;
        }



        Player invitedPlayer = Bukkit.getPlayer(invitedPlayerName);

        if (invitedPlayer == null) {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", invitedPlayerName);

            inviter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.not-online", placeholders));
            return;
        }
        if (getClanName(invitedPlayer) != null) {
            inviter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.in-clan"));
            return;
        }


        if(Objects.equals(invitedPlayer.getName(), inviter.getName())){
            inviter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.one-self"));
            return;
        }

        MemberCount memberCount = new MemberCount(plugin);
        int currentMembers = memberCount.getClanMembersCount(clanName);

        if(currentMembers >= clansConfig.getInt("clans." + clanName + ".max_members")){
            inviter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.clan-full"));
            return;
        }



        if (clansConfig.getStringList("clans." + clanName + ".members").contains(invitedPlayer.getName())
            || clansConfig.getStringList("clans." + clanName + ".co_leader").contains(invitedPlayer.getName())
                || clansConfig.getString("clans." + clanName + ".leader").equals(invitedPlayer.getName())
        )  {

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", invitedPlayerName);

            inviter.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.already-member", placeholders));
            return;
        }


        File invitationsFile = new File(plugin.getDataFolder(), "invites.yml");
        if (!invitationsFile.exists()) {
            try {
                invitationsFile.createNewFile();
                if (invitationsFile.length() == 0) {
                    plugin.getConfig().createSection("invites");
                    plugin.getConfig().save(invitationsFile);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create clans.yml file: " + e.getMessage());
            }
        }
        FileConfiguration InvitesConfig = YamlConfiguration.loadConfiguration(invitationsFile);

        InvitesConfig.set("invites." + invitedPlayerName , inviter.getName());
        try {
            InvitesConfig.save(invitationsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save invites file: " + e.getMessage());
            return;
        }


        inviter.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("invite.success"));

        String prefix = clansConfig.getString("clans." + clanName + ".prefix");
        String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);


        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("clan_name", TranslatedClanName);
        placeholders.put("inviter", inviter.getName());

        String message = languageManager.get("invite.invited-msg", placeholders);
        message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);


        invitedPlayer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
        invitedPlayer.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "| " + ChatColor.YELLOW + languageManager.get("invite.invited-actions"));



        int expireTimeInSeconds = plugin.getConfig().getInt("invite_expire", 30);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            InvitesConfig.set("invites." + invitedPlayerName, null);
            try {
                InvitesConfig.save(invitationsFile);
                invitedPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.expired"));
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save invites file after expiration: " + e.getMessage());
            }
        }, expireTimeInSeconds * 20L);


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

    public void AcceptInvite(Player invitedplayer) {
        File InvitesFile = new File(plugin.getDataFolder(), "invites.yml");
        FileConfiguration InvitesConfig = YamlConfiguration.loadConfiguration(InvitesFile);

        String Inviter = InvitesConfig.getString("invites." + invitedplayer.getName());
        if(Inviter == null){
            invitedplayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.null-invited"));
            return;
        }

        String clannam = getClanName(Bukkit.getPlayer(Inviter));

        plugin.getLogger().info(clannam);

        AddPlayer addPlayer = new AddPlayer(plugin, languageManager);
        addPlayer.PlayerAdder(clannam,invitedplayer);
        InvitesConfig.set("invites." + invitedplayer.getName() , null);
    }

    public void DenyInvite(Player invitedplayer) {
        File InvitesFile = new File(plugin.getDataFolder(), "invites.yml");
        FileConfiguration InvitesConfig = YamlConfiguration.loadConfiguration(InvitesFile);

        String Inviter = InvitesConfig.getString("invites." + invitedplayer.getName());
        if(Inviter == null){
            invitedplayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("invite.null-invited"));
            return;
        }
        Player inviter = Bukkit.getPlayer(Inviter);


        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", invitedplayer.getName());

        inviter.sendMessage(ChatColor.RED +""+ChatColor.BOLD + "| "+ ChatColor.RED + languageManager.get("invite.reject-invite", placeholders));
        invitedplayer.sendMessage(ChatColor.RED +""+ChatColor.BOLD + "| "+ ChatColor.RED + languageManager.get("invite.reject-invite-player"));

        InvitesConfig.set("invites." + invitedplayer.getName() , "null");
    }


}
