package tech.flubel.clans.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InvitePlayer {


    private final JavaPlugin plugin;

    public InvitePlayer(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void sendInvite(Player inviter, String invitedPlayerName) {

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);
        String clanName = getClanName(inviter);
        if (clanName == null || clanName.isEmpty()) {
            inviter.sendMessage(ChatColor.RED + "You must be in a clan to invite players!");
            return;
        }


        if (!clansConfig.getString("clans." + clanName + ".leader").equals(inviter.getName()) &&
                !clansConfig.getStringList("clans." + clanName + ".co_leader").contains(inviter.getName())) {
            inviter.sendMessage(ChatColor.RED + "You must be a leader or co-leader of your clan to invite players!");
            return;
        }



        Player invitedPlayer = Bukkit.getPlayer(invitedPlayerName);

        if (invitedPlayer == null) {
            inviter.sendMessage(ChatColor.RED + "Player " + invitedPlayerName + " is not online!");
            return;
        }
        if (getClanName(invitedPlayer) != null) {
            inviter.sendMessage(ChatColor.RED + "Player is already in a clan.");
            return;
        }


        if(Objects.equals(invitedPlayer.getName(), inviter.getName())){
            inviter.sendMessage(ChatColor.RED + "You can not invite yourself to the clan you are in Duck head");
            return;
        }

        MemberCount memberCount = new MemberCount(plugin);
        int currentMembers = memberCount.getClanMembersCount(clanName);

        if(currentMembers >= clansConfig.getInt("clans." + clanName + ".max_members")){
            inviter.sendMessage(ChatColor.RED + "Clan is Full.");
            return;
        }



        if (clansConfig.getStringList("clans." + clanName + ".members").contains(invitedPlayer.getName())
            || clansConfig.getStringList("clans." + clanName + ".co_leader").contains(invitedPlayer.getName())
                || clansConfig.getString("clans." + clanName + ".leader").equals(invitedPlayer.getName())
        )  {
            inviter.sendMessage(ChatColor.RED + invitedPlayerName + " is already a member of your clan!");
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


        inviter.sendMessage(ChatColor.GREEN + "Invite send Successfully!");

        String prefix = clansConfig.getString("clans." + clanName + ".prefix");
        String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', prefix);
        invitedPlayer.sendMessage(ChatColor.GREEN + "You are invited to clan " + TranslatedClanName + ChatColor.GREEN + " by " + inviter.getName());
        invitedPlayer.sendMessage(ChatColor.YELLOW + "Type /clan accept to accept or /clan deny to reject the invitation.");



        int expireTimeInSeconds = plugin.getConfig().getInt("invite_expire", 30);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            InvitesConfig.set("invites." + invitedPlayerName, null);
            try {
                InvitesConfig.save(invitationsFile);
                invitedPlayer.sendMessage(ChatColor.RED + "Your clan invite has expired.");
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
            invitedplayer.sendMessage(ChatColor.RED + " You don't have any pending clan invites.");
            return;
        }

        String clannam = getClanName(Bukkit.getPlayer(Inviter));

        plugin.getLogger().info(clannam);

        AddPlayer addPlayer = new AddPlayer(plugin);
        addPlayer.PlayerAdder(clannam,invitedplayer);
        InvitesConfig.set("invites." + invitedplayer.getName() , null);
    }

    public void DenyInvite(Player invitedplayer) {
        File InvitesFile = new File(plugin.getDataFolder(), "invites.yml");
        FileConfiguration InvitesConfig = YamlConfiguration.loadConfiguration(InvitesFile);

        String Inviter = InvitesConfig.getString("invites." + invitedplayer.getName());
        if(Inviter == null){
            invitedplayer.sendMessage(ChatColor.RED + " You don't have any pending clan invites.");
            return;
        }
        Player inviter = Bukkit.getPlayer(Inviter);

        inviter.sendMessage(ChatColor.RED +""+ChatColor.BOLD + "| "+ ChatColor.RED +"Player " + invitedplayer.getName() + " has rejected your clan Invite.");
        invitedplayer.sendMessage(ChatColor.RED +""+ChatColor.BOLD + "| "+ ChatColor.RED + "Clan Invite rejected.");

        InvitesConfig.set("invites." + invitedplayer.getName() , "null");
    }


}
