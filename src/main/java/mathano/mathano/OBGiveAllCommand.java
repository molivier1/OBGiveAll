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
            sender.sendMessage(ChatColor.RED + "La console ne peut pas utiliser cette commande.");
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        if (args.length <= 1) {
            player.sendMessage(ChatColor.RED + "Mauvais usage de la commande.");
            return true;
        }

        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

        if (!dataKits.contains(args[1])) {
            player.sendMessage(ChatColor.RED + "Le kit " + args[1] + " n'existe pas !");
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

    // Adds specified kit in the rewards config to every connected players
    public void toEveryone(Player admin, String kitName, Server server) {
        FileConfiguration rewards = OBGiveAll.getInstance().getRewardsConfig();
        FileConfiguration dataKits = OBGiveAll.getInstance().getDataKitsConfig();

        if (dataKits.contains(kitName)) {
            int playersOnline = server.getOnlinePlayers().size();
            Player[] listPlayer = server.getOnlinePlayers().toArray(new Player[playersOnline]);
            Player currentPlayer;

            for (int i = 0; i < playersOnline; i++) {
                currentPlayer = listPlayer[i];

                int numberOfKits = 1;

                if (rewards.contains(currentPlayer.getUniqueId() + "." + kitName)) {
                    numberOfKits = rewards.getInt(currentPlayer.getUniqueId() + "." + kitName);
                    numberOfKits++;
                }

                rewards.set(currentPlayer.getUniqueId() + "." + kitName, numberOfKits);

                currentPlayer.sendMessage(ChatColor.GREEN + "Vous avez reçu le kit " + kitName + " !");
                currentPlayer.sendMessage("/rewards pour récuperer votre récompense.");
            }

            admin.sendMessage("Le kit " + kitName + " a été donné à tous les joueurs !");

            OBGiveAll.getInstance().setRewardsConfig(rewards);
        } else {
            admin.sendMessage(ChatColor.RED + "Le kit " + kitName + " n'existe pas !");
        }
    }

    // Adds specified kit in the rewards config to a player that already played on the server
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

                admin.sendMessage("Le kit " + kitName + " a été donné à " + playerName);

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
