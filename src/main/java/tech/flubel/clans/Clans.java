package tech.flubel.clans;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.Utils.*;
import tech.flubel.clans.metrics.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Clans extends JavaPlugin implements Listener {

    private Economy economy;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault is not found or no economy plugin found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new CreateClanFolder(this);

        getServer().getPluginManager().registerEvents(this, this);

        this.getCommand("clan").setExecutor(this);
        this.getCommand("cc").setExecutor(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ClanPlaceholderExpansion(this).register();
        }

        Metrics metrics = new Metrics(this,25416);


        saveDefaultConfig();
        getLogger().info("\u001B[38;2;23;138;214m================================================\u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ██████╗██╗      █████╗ ███╗   ██╗███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██╔════╝██║     ██╔══██╗████╗  ██║██╔════╝\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ███████║██╔██╗ ██║███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ██╔══██║██║╚██╗██║╚════██║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ╚██████╗███████╗██║  ██║██║ ╚████║███████║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ╚═════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝\u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;225;215;0m                  Version: 1.2                \u001B[0m");
        getLogger().info("\u001B[38;2;0;255;0m                 Plugin Started               \u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m                (Made by Flubel)              \u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m================================================\u001B[0m");
    }


    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("\u001B[38;2;23;138;214m================================================\u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ██████╗██╗      █████╗ ███╗   ██╗███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██╔════╝██║     ██╔══██╗████╗  ██║██╔════╝\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ███████║██╔██╗ ██║███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ██╔══██║██║╚██╗██║╚════██║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ╚██████╗███████╗██║  ██║██║ ╚████║███████║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ╚═════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝\u001B[0m");
        getLogger().info(" \u001B[0m");
         getLogger().info("\u001B[38;2;225;215;0m                  Version: 1.2                \u001B[0m");
           getLogger().info("\u001B[38;2;255;0;0m                 Plugin Stopped               \u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m                (Made by Flubel)              \u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m================================================\u001B[0m");
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
        return economy != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + "Usage: /clan create | list | invite | accept | deny | info and many more.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("cc")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + "Usage: /cc <message>");
                return true;
            }
            String message = String.join(" ", args);
            ClanChat clanChat = new ClanChat(this);
            clanChat.sendClanChatMessage(player,message);
            return true;
        }

        if (args[0].equalsIgnoreCase("chat")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + "Usage: /clan chat <message>");
                return true;
            }

            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            ClanChat clanChat = new ClanChat(this);
            clanChat.sendClanChatMessage(player, message);
            return true;
        }

        switch (args[0]) {
            case "top":
                ListClans.ClanLister(player);
                return true;
            case "create":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + "Clan Name cannot be empty.");
                    return true;
                }
                if (args[1].length() >= 20) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + "Clan Name cannot be longer than 20 characters.");
                    return true;
                }
                String clanName = args[1];
                createClan(player, clanName);
                return true;
            case "invite": {
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter the players name to invite.");
                    return true;
                }
                String playerNameToBeInvited = args[1];
                InvitePlayer invitePlayer = new InvitePlayer(this);
                invitePlayer.sendInvite(player, playerNameToBeInvited);
                return true;
            }
            case "accept": {
                InvitePlayer invitePlayer = new InvitePlayer(this);
                invitePlayer.AcceptInvite(player);
                return true;
            }
            case "deny": {
                InvitePlayer invitePlayer = new InvitePlayer(this);
                invitePlayer.DenyInvite(player);
                return true;
            }
            case "info":
                ListPlayers listPlayers = new ListPlayers(this);
                listPlayers.PlayerLister(player);
                return true;
            case "leave":
                LeaveClan leaveClan = new LeaveClan(this);
                leaveClan.clanleaver(player);
                return true;
            case "promote":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter a clan members name to Promote.");
                    return true;
                }
                Promote promote = new Promote(this);
                promote.promotePlayer(player, args[1]);
                return true;
            case "demote":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter a clan members name to Demote.");
                    return true;
                }
                Demote demote = new Demote(this);
                demote.demotePlayer(player, args[1]);
                return true;
            case "kick":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter a clan members name to Kick.");
                    return true;
                }
                Kick kick = new Kick(this);
                kick.kickPlayer(player, args[1]);
                return true;
            case "transfer":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter a clan members name to Transfer leadership to.");
                    return true;
                }
                TransferLeadership transferLeadership = new TransferLeadership(this);
                transferLeadership.transferLeadership(player, args[1]);
                return true;
            case "join":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter a clan Name.");
                    return true;
                }
                ClanJoin clanJoin = new ClanJoin(this);
                clanJoin.requestJoinClan(player, args[1]);
                return true;
            case "raccept":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter the players name to accept the request.");
                    return true;
                }
                AcceptorJoinReq acceptorJoinReq = new AcceptorJoinReq(this);
                acceptorJoinReq.acceptJoinRequest(player, args[1]);
                return true;
            case "rdeny":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "|" + ChatColor.WHITE + " Enter the players name to deny the request.");
                    return true;
                }
                DenyJoinReq denyJoinReq = new DenyJoinReq(this);
                denyJoinReq.denyRequest(player, args[1]);
                return true;
            case "sethome":
                ClanHomeSet clanHomeSet = new ClanHomeSet(this);
                clanHomeSet.setClanHome(player);
                return true;
            case "home":
                TpClanHome tpClanHome = new TpClanHome(this);
                tpClanHome.teleportToClanHome(player);
                return true;
            case "requests":
                Requests requests = new Requests(this);
                requests.showRequests(player);
                return true;
            case "reload":
                ReloadConfig reloadConfig = new ReloadConfig(this);
                reloadConfig.reloadConfig(player);
                return true;
            case "upgrade":
                UpgradeClan upgradeClan = new UpgradeClan(this,this.economy);
                try {
                    upgradeClan.ClanUpgrader(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            case "help":
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| Clans Help");
                player.sendMessage(ChatColor.YELLOW + "/clan top" + ChatColor.WHITE + " - Listranking for all top clans.");
                player.sendMessage(ChatColor.YELLOW + "/clan create <name>" + ChatColor.WHITE + " - Create a new clan with the specified name.");
                player.sendMessage(ChatColor.GREEN + "/clan invite <player>" + ChatColor.WHITE + " - Invite a player to your clan.");
                player.sendMessage(ChatColor.GREEN + "/clan accept" + ChatColor.WHITE + " - Accept a clan invite.");
                player.sendMessage(ChatColor.GREEN + "/clan deny" + ChatColor.WHITE + " - Deny a clan invite.");
                player.sendMessage(ChatColor.GREEN + "/clan info" + ChatColor.WHITE + " - View information about the clan you're in.");
                player.sendMessage(ChatColor.GREEN + "/clan leave" + ChatColor.WHITE + " - Leave your current clan.");
                player.sendMessage(ChatColor.GREEN + "/clan balance" + ChatColor.WHITE + " - View the total clans balance.");
                player.sendMessage(ChatColor.GREEN + "/clan promote <player>" + ChatColor.WHITE + " - Promote a clan member.");
                player.sendMessage(ChatColor.GREEN + "/clan demote <player>" + ChatColor.WHITE + " - Demote a clan member.");
                player.sendMessage(ChatColor.GREEN + "/clan kick <player>" + ChatColor.WHITE + " - Kick a player from your clan.");
                player.sendMessage(ChatColor.GREEN + "/clan deposit <amount>" + ChatColor.WHITE + " - Deposits money to clans bank balance.");
                player.sendMessage(ChatColor.GREEN + "/clan withdraw <amount>" + ChatColor.WHITE + " - Withdraws money from clans bank balance.");
                player.sendMessage(ChatColor.DARK_GREEN + "/clan transfer <player>" + ChatColor.WHITE + " - Transfer clan leadership to another player.");
                player.sendMessage(ChatColor.YELLOW + "/clan join <name>" + ChatColor.WHITE + " - Request to join a clan.");
                player.sendMessage(ChatColor.GREEN + "/clan raccept <player>" + ChatColor.WHITE + " - Accept a player's request to join the clan.");
                player.sendMessage(ChatColor.GREEN + "/clan rdeny <player>" + ChatColor.WHITE + " - Deny a player's request to join the clan.");
                player.sendMessage(ChatColor.DARK_GREEN + "/clan sethome" + ChatColor.WHITE + " - Set the home location for the clan.");
                player.sendMessage(ChatColor.GREEN + "/clan home" + ChatColor.WHITE + " - Teleport to the clan home.");
                player.sendMessage(ChatColor.GREEN + "/clan requests" + ChatColor.WHITE + " - View pending requests to join your clan.");
                player.sendMessage(ChatColor.GREEN + "/clan upgrade" + ChatColor.WHITE + " - Upgrades the clan player slot.");
                player.sendMessage(ChatColor.DARK_GREEN + "/clan pvp" + ChatColor.WHITE + " - Toggles pvp between clan members.");
                player.sendMessage(ChatColor.RED + "/clan reload" + ChatColor.WHITE + " - Reload the plugin configuration.");
                return true;
            case "pinfo":
                player.sendMessage(ChatColor.YELLOW + "Made at Flubel by Fiend.");
                player.sendMessage(ChatColor.YELLOW + "Version: 1.2.0");
                return true;
            case "deposit":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /clan deposit <amount>");
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Please enter a valid number.");
                    return true;
                }

                ClanBankDeposit clanBankDeposit = new ClanBankDeposit(this, this.economy);
                clanBankDeposit.BankClanDep(player, amount);
                return true;
            case "withdraw":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /clan withdraw <amount>");
                    return true;
                }

                int amount1;
                try {
                    amount1 = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Please enter a valid number.");
                    return true;
                }

                ClanBankWithdraw clanBankWithdraw = new ClanBankWithdraw(this, this.economy);
                clanBankWithdraw.withdrawFromClan(player, amount1);
                return true;
            case "pvp":
                ClanPvPToggle clanPvPToggle = new ClanPvPToggle(this);
                clanPvPToggle.pvptoggler(player);
                return true;
            case "balance":
                ClanBalanceViewer clanBalanceViewer = new ClanBalanceViewer(this);
                clanBalanceViewer.balanceviewer(player);
                return true;
        }


        return true;
    }



    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        String victimClan = getClanName(victim);
        String attackerClan = getClanName(attacker);

        if (victimClan == null || !victimClan.equals(attackerClan)) return;

        File clansFile = new File(this.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        boolean pvpEnabled = clansConfig.getBoolean("clans." + victimClan + ".pvp");

        if (!pvpEnabled) {
            event.setCancelled(true);
            attacker.sendMessage(ChatColor.RED + "PvP is disabled between clan members.");
            victim.sendMessage(ChatColor.RED + attacker.getName() + " tried to attack you but clan pvp is disabled.");
        }
    }

    private String getClanName(Player player) {
        File clansFile = new File(this.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (!clansConfig.contains("clans")) return null;

        for (String clanName : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            if (clansConfig.getString("clans." + clanName + ".leader").equals(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".co_leader").contains(player.getName()) ||
                    clansConfig.getStringList("clans." + clanName + ".members").contains(player.getName())) {
                return clanName;
            }
        }
        return null;
    }


    public void createClan(Player player, String clanName) {
        double requiredBalance = this.getConfig().getDouble("Amount", 50000.0);

        SearchPlayer searchPlayer = new SearchPlayer(this);

        if (searchPlayer.isPlayerInClan(player)) {
            player.sendMessage(ChatColor.RED + "" +ChatColor.BOLD + "| " + ChatColor.RED + "You cannot create a new clan while you are a member/owner of another clan.");
            return;
        }

        if (economy.getBalance(player) < requiredBalance) {
            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + "You need " + this.getConfig().getDouble("Amount", 50000.0) + " to create a clan!");
            return;
        }

        File clansFile = new File(getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        String cleanedClanName = clanName.replaceAll("&[a-zA-Z0-9]", "");

        String cleanedClanName1 = clanName.replaceAll("&[a-zA-Z0-9]", "").toLowerCase();

        Set<String> existingClanNames = clansConfig.getConfigurationSection("clans").getKeys(false);

        for (String existingClan : existingClanNames) {
            String cleanedExistingClanName = existingClan.replaceAll("&[a-zA-Z0-9]", "").toLowerCase();

            if (cleanedClanName1.equals(cleanedExistingClanName)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + "Clan name is already taken.");
                return;
            }
        }


        economy.withdrawPlayer(player, requiredBalance);

        clansConfig.set("clans." + cleanedClanName + ".leader", player.getName());
        clansConfig.set("clans." + cleanedClanName + ".co_leader", new ArrayList<>());
        clansConfig.set("clans." + cleanedClanName + ".members", new ArrayList<>());
        clansConfig.set("clans." + cleanedClanName + ".prefix", clanName);
        clansConfig.set("clans." + cleanedClanName + ".pvp", true);
        clansConfig.set("clans." + cleanedClanName + ".balance", this.getConfig().getInt("initial_balance",25000));
        clansConfig.set("clans." + cleanedClanName + ".max_members", this.getConfig().getInt("max_members",50));

        try {
            clansConfig.save(clansFile);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("An error occurred while saving the clan.");
            return;
        }
        String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', clanName);

        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + "Clan " + TranslatedClanName + ChatColor.GREEN + " created successfully!");
    }


}
