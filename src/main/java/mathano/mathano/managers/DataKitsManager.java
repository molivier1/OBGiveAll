package mathano.mathano.managers;

import mathano.mathano.OBGiveAll;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class DataKitsManager {
    public static DataKitsManager INSTANCE;

    public static FileConfiguration DATA_KITS_CONFIG;

    private final File dataKitsConfigFile;

    public DataKitsManager() {
        INSTANCE = this;

        // Caching of the yml file
        dataKitsConfigFile = new File(OBGiveAll.INSTANCE.getDataFolder(), "dataKits.yml");
        reload();
    }

    public void reload() {
        if (!dataKitsConfigFile.exists()) {
            OBGiveAll.INSTANCE.saveResource("dataKits.yml", false);
        }
        DATA_KITS_CONFIG = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(dataKitsConfigFile);
    }

    // Saves cached dataKits into dataKits.yml
    public void save() {
        try {
            DATA_KITS_CONFIG.save("./plugins/OBGiveAll/dataKits.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
