package mathano.mathano;

import mathano.mathano.handlers.Give;
import mathano.mathano.listeners.CommandListener;
import mathano.mathano.managers.ConfigManager;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.AutoCompletion;
import org.bukkit.plugin.java.JavaPlugin;

public final class OBGiveAll extends JavaPlugin {
    public static OBGiveAll INSTANCE;

    private ConfigManager configManager;
    private DataKitsManager dataKitsManager;
    private RewardsManager rewardsManager;
    private Give give;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;

        configManager = new ConfigManager();
        dataKitsManager = new DataKitsManager();
        rewardsManager = new RewardsManager();

        give = new Give();

        initCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        rewardsManager.save();
        dataKitsManager.save();
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
