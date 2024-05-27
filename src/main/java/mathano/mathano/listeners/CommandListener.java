package mathano.mathano.listeners;

import mathano.mathano.handlers.KitsGui;
import mathano.mathano.OBGiveAll;
import mathano.mathano.handlers.Give;
import mathano.mathano.handlers.Rewards;
import mathano.mathano.utils.ItemGui;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "kitsgui":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "La console ne peut pas utiliser cette commande.");
                    return true;
                }

                Player playerKits = ((Player) sender).getPlayer();

                new ItemGui();

                KitsGui.mainGui(playerKits);
                break;
            case "obgiveall":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "La console ne peut pas utiliser cette commande.");
                    return true;
                }

                Player playerGive = ((Player) sender).getPlayer();

                if (args.length <= 1) {
                    playerGive.sendMessage(ChatColor.RED + "Mauvais usage de la commande.");
                    return true;
                }

                FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

                if (!dataKits.contains(args[1])) {
                    playerGive.sendMessage(ChatColor.RED + "Le kit " + args[1] + " n'existe pas !");
                    return true;
                }

                Server server = sender.getServer();
                if (args[0].equals("*")) {
                    // Gives to everyone
                    Give.toEveryone(playerGive, args[1], server);
                } else {
                    // Gives to specific player
                    Give.toSpecificPlayer(playerGive, args[1], server, args[0]);
                }
                break;

            case "recompense":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "La console ne peut pas utiliser cette commande.");
                    return true;
                }

                FileConfiguration rewardsFile = OBGiveAll.getInstance().getRewardsConfig();

                Player playerRecompense = ((Player) sender).getPlayer();

                new ItemGui();

                if(rewardsFile.contains(playerRecompense.getUniqueId().toString())) {
                    Rewards.rewardsGui(playerRecompense);
                } else {
                    playerRecompense.sendMessage(ChatColor.RED + "Vous n'avez pas de récompenses en attente.");
                }
                break;
        }
        return true;
    }
}
