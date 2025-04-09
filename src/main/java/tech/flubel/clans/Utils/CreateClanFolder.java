package tech.flubel.clans.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CreateClanFolder {

    private final JavaPlugin plugin;

    public CreateClanFolder(JavaPlugin plugin) {
        this.plugin = plugin;
        createFolders();
    }
    private void createFolders() {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        File clansFile = new File(pluginFolder, "clans.yml");
        if (!clansFile.exists()) {
            try {
                clansFile.createNewFile();
                if (clansFile.length() == 0) {
                    plugin.getConfig().createSection("clans");
                    plugin.getConfig().save(clansFile);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create clans.yml file: " + e.getMessage());
            }
        }
    }
}
