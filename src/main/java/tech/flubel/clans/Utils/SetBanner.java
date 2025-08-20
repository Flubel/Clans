package tech.flubel.clans.Utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.java.JavaPlugin;
import tech.flubel.clans.Clans;
import tech.flubel.clans.LanguageManager.LanguageManager;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetBanner {
    private final Clans plugin;
    private final LanguageManager languageManager;

    public SetBanner(JavaPlugin plugin, LanguageManager languageManager) {
        this.plugin = (Clans) plugin;
        this.languageManager = languageManager;
    }


    public void SetClanBanner(Player player) {
        String clanName = getClanName(player);

        if (clanName == null) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_banner.no-clan"));
            return;
        }

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (!clansConfig.getString("clans." + clanName + ".leader").equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_banner.no-auth"));
            return;
        }

        // Check if player is holding a banner
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().name().endsWith("_BANNER")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_banner.no-banner"));
            return;
        }

        BannerMeta meta = (BannerMeta) item.getItemMeta();

        // ✅ Get base color from Material (not BannerMeta)
        DyeColor baseColor = getBaseColorFromMaterial(item.getType());
        clansConfig.set("clans." + clanName + ".banner.base", baseColor.name());

        // Save patterns as strings
        List<String> serializedPatterns = new ArrayList<>();
        for (Pattern pattern : meta.getPatterns()) {
            serializedPatterns.add(pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier());
        }
        clansConfig.set("clans." + clanName + ".banner.patterns", serializedPatterns);

        try {
            clansConfig.save(clansFile);
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("clan_banner.success"));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_banner.error"));
        }
    }

    private DyeColor getBaseColorFromMaterial(Material material) {
        String name = material.name();
        if (name.endsWith("_BANNER")) {
            String colorName = name.replace("_BANNER", "");
            return DyeColor.valueOf(colorName);
        }
        return DyeColor.WHITE; // fallback
    }



    public void getClanBanner(Player player) {
        String clanName = getClanName(player);


        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration clansConfig = YamlConfiguration.loadConfiguration(clansFile);

        if (!clansConfig.contains("clans." + clanName + ".banner.base")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "| " + ChatColor.RED + languageManager.get("clan_banner.not-available"));
            return;
        }

        // Get base color
        String baseName = clansConfig.getString("clans." + clanName + ".banner.base", "WHITE");
        DyeColor baseColor = DyeColor.valueOf(baseName.toUpperCase());

        // Create banner ItemStack (needs a base material like WHITE_BANNER)
        ItemStack banner = new ItemStack(Material.valueOf(baseColor.name() + "_BANNER"));
        BannerMeta meta = (BannerMeta) banner.getItemMeta();

        // Load patterns
        List<String> serializedPatterns = clansConfig.getStringList("clans." + clanName + ".banner.patterns");
        List<Pattern> patterns = new ArrayList<>();

        for (String s : serializedPatterns) {
            String[] split = s.split(":");
            if (split.length != 2) continue;

            try {
                DyeColor color = DyeColor.valueOf(split[0].toUpperCase());
                PatternType type = PatternType.getByIdentifier(split[1]);
                if (type != null) {
                    patterns.add(new Pattern(color, type));
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid pattern in banner for clan " + clanName + ": " + s);
            }
        }

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',clansConfig.getString("clans." + clanName + ".prefix", clanName) + "'s &fClan Banner"));
        meta.setPatterns(patterns);
        banner.setItemMeta(meta);

        player.getInventory().addItem(banner);
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "| " + ChatColor.GREEN + languageManager.get("clan_banner.success-giver"));

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
