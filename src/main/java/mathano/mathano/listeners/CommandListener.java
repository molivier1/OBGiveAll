package mathano.mathano.listeners;

import mathano.mathano.enums.Placeholders;
import mathano.mathano.handlers.KitsGui;
import mathano.mathano.handlers.Give;
import mathano.mathano.handlers.Rewards;
import mathano.mathano.managers.ConfigManager;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.ItemGui;
import mathano.mathano.utils.Utils;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "kitsgui":
                if (!(sender instanceof Player)) {
                    // Message sent when console cannot use said command
                    sender.sendMessage(Utils.getText("technic", "console"));
                    return true;
                }

                new ItemGui();

                KitsGui.mainGui(((Player) sender).getPlayer());
                break;
            case "obgiveall":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.getText("technic", "console"));
                    return true;
                }

                if (args.length <= 1) {
                    // Message sent when the admin made a mistake in the command
                    sender.sendMessage(Utils.getText("technic", "badUsage"));
                    return true;
                }

                if (!DataKitsManager.DATA_KITS_CONFIG.contains(args[1])) {
                    // Message sent when the kit doesn't exist
                    sender.sendMessage(Utils.getText("give", "invalidKit", Placeholders.KIT_NAME.set(args[1])));
                    return true;
                }

                Player playerGive = ((Player) sender).getPlayer();

                Server server = sender.getServer();
                if (args[0].equals("*")) {
                    // Gives to everyone
                    Give.INSTANCE.toEveryone(playerGive, args[1], server);
                } else {
                    // Gives to specific player
                    Give.INSTANCE.toSpecificPlayer(playerGive, args[1], server, args[0]);
                }
                break;

            case "recompense":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.getText("technic", "console"));
                    return true;
                }

                Player playerRecompense = ((Player) sender).getPlayer();

                new ItemGui();

                if(RewardsManager.REWARDS_CONFIG.contains(playerRecompense.getUniqueId().toString())) {
                    Rewards.rewardsGui(playerRecompense);
                } else {
                    playerRecompense.sendMessage(Utils.getText("rewards", "noRewards"));
                }
                break;

            case "obreload":
                ConfigManager.INSTANCE.reload();

                // Message sent when the config is reloaded
                sender.sendMessage(Utils.getText("technic", "reload"));

                break;
        }
        return true;
    }
}
