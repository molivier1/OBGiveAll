package mathano.mathano;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

public class OBGiveAllCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot use this command.");
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        if (args.length <= 1) {
            player.sendMessage(ChatColor.RED + "Bad usage");
            return true;
        }

        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

        if (!dataKits.contains(args[1])) {
            player.sendMessage(ChatColor.RED + "Kit " + args[1] + " doesn't exists !");
            return true;
        }

        Server server = sender.getServer();
        if (args[0].equals("*")) {
            // Gives to everyone
            toEveryone(player, args[1], server);
        } else {
            // Gives to specific player
            toSpecificPlayer(player, args[1], server);
        }

        return true;
    }

    public void toEveryone(Player player, String kitName, Server server) {
        FileConfiguration rewards = OBGiveAll.getInstance().getRewardsConfig();

        player.sendMessage("Kit : " + kitName + " given to everyone");

        int playersOnline = server.getOnlinePlayers().size();
        Player[] listPlayer = server.getOnlinePlayers().toArray(new Player[playersOnline]);
        Player currentPlayer;

        for (int i = 0; i < playersOnline; i++) {
            currentPlayer = listPlayer[i];

            rewards.set(currentPlayer.getUniqueId() + "." + kitName, 1);

            try {
                rewards.save("./plugins/OBGiveAll/rewards.yml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            OBGiveAll.getInstance().reloadRewardsConfig();
        }
    }

    public void toSpecificPlayer(Player player, String kitName, Server server) {

    }
}
