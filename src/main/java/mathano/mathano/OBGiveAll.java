package mathano.mathano;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class OBGiveAll extends JavaPlugin {

    private static OBGiveAll instance;

    private FileConfiguration dataKitsConfig;
    private File dataKitsConfigFile;

    private FileConfiguration rewardsConfig;
    private File rewardsConfigFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Caching of the yml file
        dataKitsConfigFile = new File(getDataFolder(), "dataKits.yml");
        reloadDataKitsConfig();

        rewardsConfigFile = new File(getDataFolder(), "rewards.yml");
        reloadRewardsConfig();

        // Init of the various commands
        getCommand("kitsgui").setExecutor(new KitsGui());
        getCommand("obgiveall").setExecutor(new OBGiveAllCommand());
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

    public void reloadRewardsConfig() {
        if(!rewardsConfigFile.exists()) {
            saveResource("rewards.yml", false);
        }
        rewardsConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(rewardsConfigFile);
    }

    public FileConfiguration getRewardsConfig() {
        return rewardsConfig;
    }
}
