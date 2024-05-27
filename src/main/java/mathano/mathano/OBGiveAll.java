package mathano.mathano;

import mathano.mathano.listeners.CommandListener;
import mathano.mathano.utils.AutoCompletion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        // Scheduler that saves the cached data into the files every 15 minutes
        scheduleSave();

        // Init of the various commands
        getCommand("kitsgui").setExecutor(new CommandListener());
        getCommand("obgiveall").setExecutor(new CommandListener());
        getCommand("recompense").setExecutor(new CommandListener());

        // Tab completions
        getCommand("obgiveall").setTabCompleter(new AutoCompletion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveRewardsConfig();
        saveDataKitsConfig();
    }

    // Returns the instance of the main class, so it can be used in the other classes
    public static OBGiveAll getInstance() {
        return instance;
    }

    public void reloadDataKitsConfig() {
        if (!dataKitsConfigFile.exists()) {
            saveResource("dataKits.yml", false);
        }
        dataKitsConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(dataKitsConfigFile);
    }

    // Getter for dataKits...
    public FileConfiguration getDataKitsConfig() {
        return dataKitsConfig;
    }

    // ...and Setter for dataKits
    public void setDataKitsConfig(FileConfiguration newDataKitsConfig) {
        dataKitsConfig = newDataKitsConfig;
    }

    // Saves cached dataKits into dataKits.yml
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

    // Getter for rewards...
    public FileConfiguration getRewardsConfig() {
        return rewardsConfig;
    }

    // ...and Setter for rewards
    public void setRewardsConfig(FileConfiguration newRewardsConfig) {
        rewardsConfig = newRewardsConfig;
    }

    // Saves cached rewards into rewards.yml
    public void saveRewardsConfig () {
        try {
            rewardsConfig.save("./plugins/OBGiveAll/rewards.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleSave() {
        ScheduledExecutorService executors = Executors.newScheduledThreadPool(1);

        executors.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                    System.out.println("Sauvegarde OBGiveall");
                    saveRewardsConfig();
            }
        }, 0, 15, TimeUnit.MINUTES);
    }
}
