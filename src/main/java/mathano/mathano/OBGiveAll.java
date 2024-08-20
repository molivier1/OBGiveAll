package mathano.mathano;

import mathano.mathano.database.serialization.Serialization;
import mathano.mathano.handlers.Give;
import mathano.mathano.handlers.KitsGui;
import mathano.mathano.listeners.CommandListener;
import mathano.mathano.managers.*;
import mathano.mathano.utils.AutoCompletion;
import org.bukkit.plugin.java.JavaPlugin;

public final class OBGiveAll extends JavaPlugin {
    public static OBGiveAll INSTANCE;

    private ConfigManager configManager;
    private DataKitsManager dataKitsManager;
    private RewardsManager rewardsManager;
    private LogsManager logsManager;
    private Give give;
    private DatabaseManager databaseManager;
    private KitsGui kitsGui;
    private JsonManager jsonManager;
    private Serialization serialization;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;

        configManager = new ConfigManager();

        give = new Give();

        serialization = new Serialization();

        jsonManager = new JsonManager();

        dataKitsManager = new DataKitsManager();
        rewardsManager = new RewardsManager();
        logsManager = new LogsManager();

        databaseManager = new DatabaseManager();

        kitsGui = new KitsGui();

        initCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        rewardsManager.saveRewardsFromCache();
        dataKitsManager.saveKitsFromCache();
        databaseManager.close();
    }

    private void initCommands () {
        // Init of the various commands
        getCommand("kitsgui").setExecutor(new CommandListener());
        getCommand("obgiveall").setExecutor(new CommandListener());
        getCommand("recompense").setExecutor(new CommandListener());
        getCommand("obreload").setExecutor(new CommandListener());

        // Tab completions
        getCommand("obgiveall").setTabCompleter(new AutoCompletion());
    }
}
