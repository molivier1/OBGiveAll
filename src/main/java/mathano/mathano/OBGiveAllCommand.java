package mathano.mathano;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


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
            toSpecificPlayer(player, args[1], server, args[0]);
        }

        return true;
    }

    public void toEveryone(Player admin, String kitName, Server server) {
        FileConfiguration rewards = OBGiveAll.getInstance().getRewardsConfig();
        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

        if (dataKits.contains(kitName)) {
            int playersOnline = server.getOnlinePlayers().size();
            Player[] listPlayer = server.getOnlinePlayers().toArray(new Player[playersOnline]);
            Player currentPlayer;

            for (int i = 0; i < playersOnline; i++) {
                currentPlayer = listPlayer[i];

                rewards.set(currentPlayer.getUniqueId() + "." + kitName, 1);

                currentPlayer.sendMessage(ChatColor.GREEN + "Vous avez reçu le kit " + kitName + " !");
                currentPlayer.sendMessage("/rewards pour récuperer votre récompense.");
            }

            admin.sendMessage("Kit : " + kitName + " given to everyone");

            OBGiveAll.getInstance().setRewardsConfig(rewards);
        } else {
            admin.sendMessage(ChatColor.RED + "Le kit " + kitName + " n'existe pas !");
        }
    }

    public void toSpecificPlayer(Player admin, String kitName, Server server, String playerName) {
        FileConfiguration rewards = OBGiveAll.getInstance().getRewardsConfig();

        if(server.getPlayer(playerName).hasPlayedBefore() || server.getPlayer(playerName).isOnline()) {

            FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

            if (dataKits.contains(kitName)) {

                Player givenPlayer = server.getPlayer(playerName);

                int numberOfKits = 1;

                if(rewards.contains(givenPlayer.getUniqueId() + "." + kitName)){
                    numberOfKits = rewards.getInt(givenPlayer.getUniqueId() + "." + kitName);
                    numberOfKits++;
                }

                admin.sendMessage("Kit " + kitName + " given to player ");

                rewards.set(givenPlayer.getUniqueId() + "." + kitName, numberOfKits);

                givenPlayer.sendMessage(ChatColor.GREEN + "Vous avez reçu le kit " + kitName + " !");
                givenPlayer.sendMessage("/rewards pour récuperer votre récompense.");

                OBGiveAll.getInstance().setRewardsConfig(rewards);
            } else {
                admin.sendMessage(ChatColor.RED + "Le kit " + kitName + " n'existe pas !");
            }
        } else {
            admin.sendMessage(ChatColor.RED + "Le joueur " + playerName + " n'a jamais joué sur ce serveur !");
        }
    }
}
