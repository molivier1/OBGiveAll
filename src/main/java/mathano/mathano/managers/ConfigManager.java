package mathano.mathano.managers;

import mathano.mathano.OBGiveAll;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigManager {
    public static ConfigManager INSTANCE;

    public static FileConfiguration CONFIG;

    private final File configFile;

    public ConfigManager() {
        INSTANCE = this;

        // Caching of the yml file
        configFile = new File(OBGiveAll.INSTANCE.getDataFolder(), "config.yml");
        reload();
    }

    public void reload() {
        if (!configFile.exists()) {
            OBGiveAll.INSTANCE.saveResource("config.yml", false);
        }
        CONFIG = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile);
    }
}
