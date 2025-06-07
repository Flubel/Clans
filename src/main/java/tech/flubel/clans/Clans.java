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
import tech.flubel.clans.LanguageManager.LanguageManager;
import tech.flubel.clans.Utils.*;
import tech.flubel.clans.metrics.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Clans extends JavaPlugin implements Listener {

    private Economy economy;
    private LanguageManager languageManager;


    public LanguageManager getLanguageManager() {
        return languageManager;
    }

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


        saveLangFile("cn.yml");
        saveLangFile("de.yml");
        saveLangFile("en.yml");
        saveLangFile("fr.yml");
        saveLangFile("ru.yml");
        saveLangFile("tr.yml");


        this.languageManager = new LanguageManager(this);


        getLogger().info("\u001B[38;2;23;138;214m================================================\u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ██████╗██╗      █████╗ ███╗   ██╗███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██╔════╝██║     ██╔══██╗████╗  ██║██╔════╝\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ███████║██╔██╗ ██║███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ██╔══██║██║╚██╗██║╚════██║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ╚██████╗███████╗██║  ██║██║ ╚████║███████║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ╚═════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝\u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;225;215;0m                  Version: 1.3                \u001B[0m");
        getLogger().info("\u001B[38;2;0;255;0m                 Plugin Started               \u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m                (Made by Flubel)              \u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m================================================\u001B[0m");
    }

    private void saveLangFile(String fileName) {
        File file = new File(getDataFolder(), "lang/" + fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs(); // create lang/ directory if it doesn't exist
            saveResource("lang/" + fileName, false);
        }
    }

    public void setLanguageManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }


    @Override
    public void onDisable() {
//        saveConfig();
        getLogger().info("\u001B[38;2;23;138;214m================================================\u001B[0m");
        getLogger().info(" \u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ██████╗██╗      █████╗ ███╗   ██╗███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██╔════╝██║     ██╔══██╗████╗  ██║██╔════╝\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ███████║██╔██╗ ██║███████╗\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ██║     ██║     ██╔══██║██║╚██╗██║╚════██║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m   ╚██████╗███████╗██║  ██║██║ ╚████║███████║\u001B[0m");
        getLogger().info("\u001B[38;2;23;138;214m    ╚═════╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═══╝╚══════╝\u001B[0m");
        getLogger().info(" \u001B[0m");
         getLogger().info("\u001B[38;2;225;215;0m                  Version: 1.3                \u001B[0m");
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
            sender.sendMessage(languageManager.get("clan.info.console"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.usage"));
            return true;
        }

        if (command.getName().equalsIgnoreCase("cc")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.short-chat"));
                return true;
            }
            String message = String.join(" ", args);
            ClanChat clanChat = new ClanChat(this, this.languageManager);
            clanChat.sendClanChatMessage(player,message);
            return true;
        }

        if (args[0].equalsIgnoreCase("chat")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.chat"));
                return true;
            }

            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            ClanChat clanChat = new ClanChat(this, this.languageManager);
            clanChat.sendClanChatMessage(player, message);
            return true;
        }

        switch (args[0]) {
            case "top":
                ListClans.ClanLister(player, languageManager);
                return true;
            case "create":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.name-req"));
                    return true;
                }
                if (args[1].length() >= 20) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.name-limit"));
                    return true;
                }
                String clanName = args[1];
                createClan(player, clanName);
                return true;
            case "invite": {
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.player-name"));
                    return true;
                }
                String playerNameToBeInvited = args[1];
                InvitePlayer invitePlayer = new InvitePlayer(this, this.languageManager);
                invitePlayer.sendInvite(player, playerNameToBeInvited);
                return true;
            }
            case "accept": {
                InvitePlayer invitePlayer = new InvitePlayer(this, this.languageManager);
                invitePlayer.AcceptInvite(player);
                return true;
            }
            case "deny": {
                InvitePlayer invitePlayer = new InvitePlayer(this, this.languageManager);
                invitePlayer.DenyInvite(player);
                return true;
            }
            case "info":
                ListPlayers listPlayers = new ListPlayers(this, this.languageManager);
                listPlayers.PlayerLister(player);
                return true;
            case "leave":
                LeaveClan leaveClan = new LeaveClan(this, this.languageManager);
                leaveClan.clanleaver(player);
                return true;
            case "promote":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.nprom"));
                    return true;
                }
                Promote promote = new Promote(this, this.languageManager);
                promote.promotePlayer(player, args[1]);
                return true;
            case "demote":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.ndemo"));
                    return true;
                }
                Demote demote = new Demote(this, this.languageManager);
                demote.demotePlayer(player, args[1]);
                return true;
            case "kick":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.mem-kick"));
                    return true;
                }
                Kick kick = new Kick(this, this.languageManager);
                kick.kickPlayer(player, args[1]);
                return true;
            case "transfer":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.transfer"));
                    return true;
                }
                TransferLeadership transferLeadership = new TransferLeadership(this, this.languageManager);
                transferLeadership.transferLeadership(player, args[1]);
                return true;
            case "join":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.null-clan"));
                    return true;
                }
                ClanJoin clanJoin = new ClanJoin(this, this.languageManager);
                clanJoin.requestJoinClan(player, args[1]);
                return true;
            case "raccept":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.naccept"));
                    return true;
                }
                AcceptorJoinReq acceptorJoinReq = new AcceptorJoinReq(this, this.languageManager);
                acceptorJoinReq.acceptJoinRequest(player, args[1]);
                return true;
            case "rdeny":
                if (args.length < 2 || args[1].isEmpty()) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " + ChatColor.WHITE + languageManager.get("clan.info.ndeny"));
                    return true;
                }
                DenyJoinReq denyJoinReq = new DenyJoinReq(this, this.languageManager);
                denyJoinReq.denyRequest(player, args[1]);
                return true;
            case "sethome":
                ClanHomeSet clanHomeSet = new ClanHomeSet(this, this.languageManager);
                clanHomeSet.setClanHome(player);
                return true;
            case "home":
                TpClanHome tpClanHome = new TpClanHome(this, this.languageManager);
                tpClanHome.teleportToClanHome(player);
                return true;
            case "requests":
                Requests requests = new Requests(this, this.languageManager);
                requests.showRequests(player);
                return true;
            case "reload":
                ReloadConfig reloadConfig = new ReloadConfig(this, this.languageManager);
                reloadConfig.reloadConfig(player);
                return true;
            case "upgrade":
                UpgradeClan upgradeClan = new UpgradeClan(this,this.economy, this.languageManager);
                try {
                    upgradeClan.ClanUpgrader(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            case "help":
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + languageManager.get("clan.clan_help_descriptions.title"));
                player.sendMessage(ChatColor.YELLOW + "/clan top " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.top"));
                player.sendMessage(ChatColor.YELLOW + "/clan create <name> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.create"));
                player.sendMessage(ChatColor.GREEN + "/clan invite <player> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.invite"));
                player.sendMessage(ChatColor.GREEN + "/clan accept " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.accept"));
                player.sendMessage(ChatColor.GREEN + "/clan deny " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.deny"));
                player.sendMessage(ChatColor.GREEN + "/clan info " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.info"));
                player.sendMessage(ChatColor.GREEN + "/clan leave " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.leave"));
                player.sendMessage(ChatColor.GREEN + "/clan balance " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.balance"));
                player.sendMessage(ChatColor.GREEN + "/clan promote <player> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.promote"));
                player.sendMessage(ChatColor.GREEN + "/clan demote <player> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.demote"));
                player.sendMessage(ChatColor.GREEN + "/clan kick <player> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.kick"));
                player.sendMessage(ChatColor.GREEN + "/clan deposit <amount> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.deposit"));
                player.sendMessage(ChatColor.GREEN + "/clan withdraw <amount> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.withdraw"));
                player.sendMessage(ChatColor.DARK_GREEN + "/clan transfer <player> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.transfer"));
                player.sendMessage(ChatColor.YELLOW + "/clan join <name> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.join"));
                player.sendMessage(ChatColor.GREEN + "/clan raccept <player> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.raccept"));
                player.sendMessage(ChatColor.GREEN + "/clan rdeny <player> " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.rdeny"));
                player.sendMessage(ChatColor.DARK_GREEN + "/clan sethome " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.sethome"));
                player.sendMessage(ChatColor.GREEN + "/clan home " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.home"));
                player.sendMessage(ChatColor.GREEN + "/clan requests " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.requests"));
                player.sendMessage(ChatColor.GREEN + "/clan upgrade " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.upgrade"));
                player.sendMessage(ChatColor.DARK_GREEN + "/clan pvp " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.pvp"));
                player.sendMessage(ChatColor.RED + "/clan reload " + ChatColor.WHITE + languageManager.get("clan.clan_help_descriptions.reload"));
                return true;
            case "pinfo":
                player.sendMessage(ChatColor.YELLOW + languageManager.get("clan.general"));
                player.sendMessage(ChatColor.YELLOW + languageManager.get("clan.version"));
                return true;
            case "deposit":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " +  ChatColor.WHITE + languageManager.get("clan.info.deposit"));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " +  ChatColor.WHITE + languageManager.get("clan.info.bad-amount"));
                    return true;
                }

                ClanBankDeposit clanBankDeposit = new ClanBankDeposit(this, this.economy, this.languageManager);
                clanBankDeposit.BankClanDep(player, amount);
                return true;
            case "withdraw":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " +  ChatColor.WHITE + languageManager.get("clan.info.withdrawal"));
                    return true;
                }

                int amount1;
                try {
                    amount1 = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "| " +  ChatColor.WHITE + languageManager.get("clan.info.bad-amount"));
                    return true;
                }

                ClanBankWithdraw clanBankWithdraw = new ClanBankWithdraw(this, this.economy, this.languageManager);
                clanBankWithdraw.withdrawFromClan(player, amount1);
                return true;
            case "pvp":
                ClanPvPToggle clanPvPToggle = new ClanPvPToggle(this, this.languageManager);
                clanPvPToggle.pvptoggler(player);
                return true;
            case "balance":
                ClanBalanceViewer clanBalanceViewer = new ClanBalanceViewer(this, this.languageManager);
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
            attacker.sendMessage(ChatColor.RED + languageManager.get("pvp.attacker-msg"));

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("attacker", attacker.getName());
            victim.sendMessage(ChatColor.RED + languageManager.get("pvp.victim-msg", placeholders));
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
            player.sendMessage(ChatColor.RED + "" +ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan.error.already-in-clan"));
            return;
        }

        if (economy.getBalance(player) < requiredBalance) {
            double amount = this.getConfig().getDouble("Amount", 50000.0);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("amount", String.valueOf(amount));

            player.sendMessage(ChatColor.RED +""+ ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan.error.low-amount", placeholders));
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
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan.error.name-taken"));
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
            player.sendMessage(languageManager.get("clan.error.save-clan"));
            return;
        }
        String TranslatedClanName = ChatColor.translateAlternateColorCodes('&', clanName);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("clan_name", TranslatedClanName);

        String message = languageManager.get("clan.success.created", placeholders);
        message = message.replace(TranslatedClanName, TranslatedClanName + ChatColor.GREEN);
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + message);
    }


}
