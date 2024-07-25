package mathano.mathano.managers;

import mathano.mathano.OBGiveAll;
import mathano.mathano.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.bukkit.configuration.file.YamlConfiguration;

public class RewardsManager {
    public static RewardsManager INSTANCE;

    public static FileConfiguration REWARDS_CONFIG;

    private final File rewardsConfigFile;

    private static ConfigurationSection scheduleSection;

    public RewardsManager() {
        INSTANCE = this;

        // Caching of the yml file
        rewardsConfigFile = new File(OBGiveAll.INSTANCE.getDataFolder(), "rewards.yml");
        reload();

        scheduleSection = ConfigManager.CONFIG.getConfigurationSection("schedule");

        scheduleSave();
    }

    public void reload() {
        if(!rewardsConfigFile.exists()) {
            OBGiveAll.INSTANCE.saveResource("rewards.yml", false);
        }
        REWARDS_CONFIG = YamlConfiguration.loadConfiguration(rewardsConfigFile);
    }

    // Saves cached rewards into rewards.yml
    public void save() { // "./plugins/OBGiveAll/rewards.yml"
        try {
            REWARDS_CONFIG.save(rewardsConfigFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleSave() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            OBGiveAll.INSTANCE.getLogger().info(Utils.getText("schedule", "saveMessage"));
            save();
        }, scheduleSection.getInt("initialDelay"), scheduleSection.getInt("time"), TimeUnit.MINUTES);
    }
}
