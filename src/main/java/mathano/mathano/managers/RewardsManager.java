package mathano.mathano.managers;

import mathano.mathano.OBGiveAll;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.bukkit.configuration.file.YamlConfiguration;

public class RewardsManager {
    public static RewardsManager INSTANCE;

    public static FileConfiguration REWARDS_CONFIG;

    private File rewardsConfigFile;

    public RewardsManager() {
        INSTANCE = this;

        // Caching of the yml file
        rewardsConfigFile = new File(OBGiveAll.INSTANCE.getDataFolder(), "rewards.yml");
        reloadRewardsConfig();

        // Scheduler that saves the cached data into the files every 15 minutes
        scheduleSave();
    }

    public void reloadRewardsConfig() {
        if(!rewardsConfigFile.exists()) {
            OBGiveAll.INSTANCE.saveResource("rewards.yml", false);
        }
        REWARDS_CONFIG = YamlConfiguration.loadConfiguration(rewardsConfigFile);
    }

    // Saves cached rewards into rewards.yml
    public void saveRewardsConfig () { // "./plugins/OBGiveAll/rewards.yml"
        try {
            REWARDS_CONFIG.save(rewardsConfigFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleSave() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            OBGiveAll.INSTANCE.getLogger().info("Sauvegarde OBGiveall");
            saveRewardsConfig();
        }, 0, 15, TimeUnit.MINUTES);
    }
}
