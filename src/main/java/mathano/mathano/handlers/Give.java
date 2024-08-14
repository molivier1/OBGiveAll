package mathano.mathano.handlers;

import mathano.mathano.enums.Placeholders;
import mathano.mathano.managers.DataKitsManager;
import mathano.mathano.managers.RewardsManager;
import mathano.mathano.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;


public class Give {
    public static Give INSTANCE;

    private final String section = "give";

    public Give() {
        INSTANCE = this;
    }

    // Adds specified kit in the rewards config to every connected players
    public void toEveryone(Player admin, String kitName, Server server) {
        if (DataKitsManager.dataKits.containsKey(kitName)) {
            int playersOnline = server.getOnlinePlayers().size();

            if (playersOnline <= 0) {
                admin.sendMessage(Utils.getText(section, "noPlayerOnline"));
                return;
            }

            Player[] listPlayer = server.getOnlinePlayers().toArray(new Player[playersOnline]);
            Player currentPlayer;

            for (int i = 0; i < playersOnline; i++) {
                currentPlayer = listPlayer[i];
                UUID uuid = currentPlayer.getUniqueId();

                incrementRewardForPlayer(uuid, kitName);

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
            if (DataKitsManager.dataKits.containsKey(kitName)) {

                UUID uuid;
                Player givenPlayer = server.getPlayer(playerName);

                if (givenPlayer != null && givenPlayer.isOnline()) {
                    uuid = givenPlayer.getUniqueId();
                } else {
                    uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                }

                incrementRewardForPlayer(uuid, kitName);

                admin.sendMessage(Utils.getText(section, "givenToSpecific", Placeholders.KIT_NAME.set(kitName), Placeholders.PLAYER_NAME.set(playerName)));

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

    private void incrementRewardForPlayer(UUID uuid, String kitName) {
        if (!RewardsManager.rewards.containsKey(uuid)) {
            // If the player does not exist in the cache, initialize an empty HashMap for them
            RewardsManager.rewards.put(uuid, new HashMap<>());
        }

        // Get the player's rewards from the cache
        HashMap<String, Integer> playerRewards = RewardsManager.rewards.get(uuid);

        // Increment the value for the specific kit
        playerRewards.put(kitName, playerRewards.getOrDefault(kitName, 0) + 1);

        // Update the cache
        RewardsManager.rewards.put(uuid, playerRewards);
    }
}
