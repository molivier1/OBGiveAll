package mathano.mathano.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import mathano.mathano.database.statements.RewardsStatements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardsManager {
    public static RewardsManager INSTANCE;

    public static  Map<UUID, HashMap<String, Integer>> rewards = new HashMap<>();

    public RewardsManager() {
        INSTANCE = this;
    }

    public void loadRewardsIntoCache() {
        try(Connection connection = DatabaseManager.INSTANCE.getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(RewardsStatements.LOAD_REWARDS_CACHE);
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));

                String rewardsJson = resultSet.getString("rewards");

                HashMap<String, Integer> playerRewards = JsonManager.INSTANCE.mapper.readValue(rewardsJson, new TypeReference<HashMap<String, Integer>>() {});

                rewards.put(playerUUID, playerRewards);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRewardsFromCache() {
        try(Connection connection = DatabaseManager.INSTANCE.getConnection()) {
            PreparedStatement insertUpdateStatement = connection.prepareStatement(RewardsStatements.INSERT_REWARDS);
            PreparedStatement selectStatement = connection.prepareStatement(RewardsStatements.SELECT_REWARDS);
            PreparedStatement deleteStatement = connection.prepareStatement(RewardsStatements.DELETE_REWARDS);

            ResultSet resultSet = connection.createStatement().executeQuery(RewardsStatements.SELECT_PLAYER_UUID);
            while (resultSet.next()) {
                UUID player_UUID = UUID.fromString(resultSet.getString("player_uuid"));
                if (!rewards.containsKey(player_UUID)) {
                    deleteStatement.setString(1, player_UUID.toString());
                    deleteStatement.executeUpdate();
                }
            }

            for (Map.Entry<UUID, HashMap<String, Integer>> entry : rewards.entrySet()) {
                UUID playerUUID = entry.getKey();
                HashMap<String, Integer> playerRewards = entry.getValue();

                String rewardsJson = JsonManager.INSTANCE.writer.writeValueAsString(playerRewards);

                selectStatement.setString(1, playerUUID.toString());
                ResultSet selectResult = selectStatement.executeQuery();

                if (selectResult.next()) {
                    String dbRewardsJson = selectResult.getString("rewards");
                    if (!rewardsJson.equals(dbRewardsJson)) {
                        insertUpdateStatement.setString(1, playerUUID.toString());
                        insertUpdateStatement.setString(2, rewardsJson);
                        insertUpdateStatement.setString(3, rewardsJson);
                        insertUpdateStatement.executeUpdate();
                    }
                } else {
                    insertUpdateStatement.setString(1, playerUUID.toString());
                    insertUpdateStatement.setString(2, rewardsJson);
                    insertUpdateStatement.setString(3, rewardsJson);
                    insertUpdateStatement.executeUpdate();
                }
            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
