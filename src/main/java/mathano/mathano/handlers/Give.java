package mathano.mathano.handlers;

import mathano.mathano.OBGiveAll;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.RewardsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;


public class Give {
    // Adds specified kit in the rewards config to every connected players
    public static void toEveryone(Player admin, String kitName, Server server) {
        if (DataKitsManager.DATA_KITS_CONFIG.contains(kitName)) {
            int playersOnline = server.getOnlinePlayers().size();
            Player[] listPlayer = server.getOnlinePlayers().toArray(new Player[playersOnline]);
            Player currentPlayer;

            for (int i = 0; i < playersOnline; i++) {
                currentPlayer = listPlayer[i];

                int numberOfKits = 1;

                if (RewardsManager.REWARDS_CONFIG.contains(currentPlayer.getUniqueId() + "." + kitName)) {
                    numberOfKits = RewardsManager.REWARDS_CONFIG.getInt(currentPlayer.getUniqueId() + "." + kitName);
                    numberOfKits++;
                }

                RewardsManager.REWARDS_CONFIG.set(currentPlayer.getUniqueId() + "." + kitName, numberOfKits);

                currentPlayer.sendMessage(ChatColor.GREEN + "Vous avez reçu le kit " + kitName + " !");
                currentPlayer.sendMessage("/rewards pour récupérer votre récompense.");
            }

            admin.sendMessage("Le kit " + kitName + " a été donné à tous les joueurs !");
        } else {
            admin.sendMessage(ChatColor.RED + "Le kit " + kitName + " n'existe pas !");
        }
    }

    // Adds specified kit in the rewards config to a player that already played on the server
    public static void toSpecificPlayer(Player admin, String kitName, Server server, String playerName) {
        if((server.getPlayer(playerName) != null && server.getPlayer(playerName).isOnline()) || Bukkit.getOfflinePlayer(playerName).hasPlayedBefore()) {
            if (DataKitsManager.DATA_KITS_CONFIG.contains(kitName)) {

                final UUID uuid;
                final Player givenPlayer = server.getPlayer(playerName);

                if (givenPlayer != null && givenPlayer.isOnline()) {
                    uuid = givenPlayer.getUniqueId();
                } else {
                    uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                }

                int numberOfKits = 1;

                if(RewardsManager.REWARDS_CONFIG.contains(uuid + "." + kitName)){
                    numberOfKits = RewardsManager.REWARDS_CONFIG.getInt(uuid + "." + kitName);
                    numberOfKits++;
                }

                admin.sendMessage("Le kit " + kitName + " a été donné à " + playerName);

                RewardsManager.REWARDS_CONFIG.set(uuid + "." + kitName, numberOfKits);

                if (givenPlayer != null && givenPlayer.isOnline()) {
                    givenPlayer.sendMessage(ChatColor.GREEN + "Vous avez reçu le kit " + kitName + " !");
                    givenPlayer.sendMessage("/rewards pour récupérer votre récompense.");
                }
            } else {
                admin.sendMessage(ChatColor.RED + "Le kit " + kitName + " n'existe pas !");
            }
        } else {
            admin.sendMessage(ChatColor.RED + "Le joueur " + playerName + " n'a jamais joué sur ce serveur !");
        }
    }
}
