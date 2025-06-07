package tech.flubel.clans.LanguageManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class LanguageManager {
    private YamlConfiguration lang;

    public LanguageManager(JavaPlugin plugin) {
        String selectedLang = plugin.getConfig().getString("language", "en");
        File langFile = new File(plugin.getDataFolder(), "lang/" + selectedLang + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file not found: " + selectedLang + ".yml");
            // fallback to English or another default
            langFile = new File(plugin.getDataFolder(), "lang/en.yml");
        }
        this.lang = YamlConfiguration.loadConfiguration(langFile);
    }

    public String get(String path) {
        return lang.getString(path, "Message not found: " + path);
    }

    public String get(String path, Map<String, String> placeholders) {
        String message = lang.getString(path, "Message not found: " + path);
        if (message == null) return "Message not found: " + path;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }
}
