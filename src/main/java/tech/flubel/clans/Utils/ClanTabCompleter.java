package tech.flubel.clans.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClanTabCompleter implements TabCompleter {

    private final JavaPlugin plugin;

    public ClanTabCompleter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        File clansFile = new File(plugin.getDataFolder(), "clans.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(clansFile);

        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("clan") && args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            Set<String> clans = config.getConfigurationSection("clans").getKeys(false);
            for (String clan : clans) {
                if (clan.toLowerCase().startsWith(args[1].toLowerCase())) {
                    suggestions.add(clan);
                }
            }
        }

        return suggestions;
    }
}
