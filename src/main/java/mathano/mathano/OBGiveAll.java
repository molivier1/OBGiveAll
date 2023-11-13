package mathano.mathano;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class OBGiveAll extends JavaPlugin {

    private static OBGiveAll instance;

    private FileConfiguration dataKitsConfig;
    private File dataKitsConfigFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Caching of the yml file
        dataKitsConfigFile = new File(getDataFolder(), "dataKits.yml");
        reloadDataKitsConfig();

        // Init of the various commands
        getCommand("kitsgui").setExecutor(new KitsGuiCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static OBGiveAll getInstance() {
        return instance;
    }

    public void reloadDataKitsConfig() {
        if (!dataKitsConfigFile.exists()) {
            saveResource("dataKits.yml", false);
        }
        dataKitsConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(dataKitsConfigFile);
    }

    public FileConfiguration getDataKitsConfig() {
        return dataKitsConfig;
    }
}
