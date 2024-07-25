package mathano.mathano;

import mathano.mathano.listeners.CommandListener;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.AutoCompletion;
import org.bukkit.plugin.java.JavaPlugin;

public final class OBGiveAll extends JavaPlugin {
    public static OBGiveAll INSTANCE;

    private DataKitsManager dataKitsManager;
    private RewardsManager rewardsManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;

        dataKitsManager = new DataKitsManager();
        rewardsManager = new RewardsManager();

        initCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        rewardsManager.saveRewardsConfig();
        dataKitsManager.saveDataKitsConfig();
    }

    private void initCommands () {
        // Init of the various commands
        getCommand("kitsgui").setExecutor(new CommandListener());
        getCommand("obgiveall").setExecutor(new CommandListener());
        getCommand("recompense").setExecutor(new CommandListener());

        // Tab completions
        getCommand("obgiveall").setTabCompleter(new AutoCompletion());
    }
}
