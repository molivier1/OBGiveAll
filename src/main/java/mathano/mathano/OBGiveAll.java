package mathano.mathano;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
        getCommand("rewards").setExecutor(new Rewards());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveRewardsConfig();
        saveDataKitsConfig();
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

    public void setDataKitsConfig(FileConfiguration newDataKitsConfig) {
        dataKitsConfig = newDataKitsConfig;
    }

    public void saveDataKitsConfig () {
        try {
            dataKitsConfig.save("./plugins/OBGiveAll/dataKits.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void setRewardsConfig(FileConfiguration newRewardsConfig) {
        rewardsConfig = newRewardsConfig;
    }

    public void saveRewardsConfig () {
        try {
            rewardsConfig.save("./plugins/OBGiveAll/rewards.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
