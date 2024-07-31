package mathano.mathano.handlers;

import mathano.mathano.enums.Placeholders;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;


public class Give {
    public static Give INSTANCE;

    private final String section = "give";

    public Give() {
        INSTANCE = this;
    }

    // Adds specified kit in the rewards config to every connected players
    public void toEveryone(Player admin, String kitName, Server server) {
        if (DataKitsManager.DATA_KITS_CONFIG.contains(kitName)) {
            int playersOnline = server.getOnlinePlayers().size();
            Player[] listPlayer = server.getOnlinePlayers().toArray(new Player[playersOnline]);
            Player currentPlayer;

            if (playersOnline <= 0) {
                admin.sendMessage(Utils.getText(section, "noPlayerOnline"));
                return;
            }

            for (int i = 0; i < playersOnline; i++) {
                currentPlayer = listPlayer[i];

                int numberOfKits = 1;

                if (RewardsManager.REWARDS_CONFIG.contains(currentPlayer.getUniqueId() + "." + kitName)) {
                    numberOfKits = RewardsManager.REWARDS_CONFIG.getInt(currentPlayer.getUniqueId() + "." + kitName);
                    numberOfKits++;
                }

                RewardsManager.REWARDS_CONFIG.set(currentPlayer.getUniqueId() + "." + kitName, numberOfKits);

                currentPlayer.sendMessage(Utils.getText(section, "kitReceived", Placeholders.KIT_NAME.set(kitName)));
                currentPlayer.sendMessage((Utils.getText(section, "rewardsMessage")));
            }

            admin.sendMessage(Utils.getText(section, "givenToEveryone", Placeholders.KIT_NAME.set(kitName)));
        } else {
            admin.sendMessage(Utils.getText(section, "invalidKit", Placeholders.KIT_NAME.set(kitName)));
        }
    }

    // Adds specified kit in the rewards config to a player that already played on the server
    public void toSpecificPlayer(Player admin, String kitName, Server server, String playerName) {
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

                admin.sendMessage(Utils.getText(section, "givenToSpecific", Placeholders.KIT_NAME.set(kitName), Placeholders.PLAYER_NAME.set(playerName)));

                RewardsManager.REWARDS_CONFIG.set(uuid + "." + kitName, numberOfKits);

                if (givenPlayer != null && givenPlayer.isOnline()) {
                    givenPlayer.sendMessage(Utils.getText(section, "kitReceived", Placeholders.KIT_NAME.set(kitName)));

                    givenPlayer.sendMessage((Utils.getText(section, "rewardsMessage")));
                }
            } else {
                admin.sendMessage(Utils.getText(section, "invalidKit", Placeholders.KIT_NAME.set(kitName)));
            }
        } else {
            // Player never joined the server
            admin.sendMessage(Utils.getText(section, "playerUnfounded", Placeholders.KIT_NAME.set(kitName)));
        }
    }

}
