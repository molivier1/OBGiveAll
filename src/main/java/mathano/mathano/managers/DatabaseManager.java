package mathano.mathano.managers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mathano.mathano.database.DataKits;
import mathano.mathano.database.jsondata.RewardsJson;
import mathano.mathano.database.statements.DatakitsStatements;
import mathano.mathano.database.statements.RewardsStatements;
import mathano.mathano.database.jsondata.DataKitsJson;
import mathano.mathano.database.serialization.Serialization;
import mathano.mathano.utils.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static mathano.mathano.managers.JsonManager.*;

public class DatabaseManager {

    public static HikariDataSource DATABASE;

    public static DatabaseManager INSTANCE;

    public static Map<String, String> kitJSONCache = new HashMap<>();

    public static Map<String, DataKits> dataKits = new HashMap<>();

    public static  Map<UUID, HashMap<String, Integer>> rewards = new HashMap<>();

    public DatabaseManager() {
        INSTANCE = this;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + Utils.getText("database", "url"));
        config.setUsername(Utils.getText("database", "username"));
        config.setPassword(Utils.getText("database", "password"));
        config.setDriverClassName("com.mysql.jdbc.Driver");

        DATABASE = new HikariDataSource(config);

        createTables();
        loadKitsIntoCache();
    }

    public Connection getConnection() throws SQLException {
        return DATABASE.getConnection();
    }

    public void close() {
        DATABASE.close();
    }

    public void createTables() {
        try(Connection connection = getConnection()) {
            connection.prepareStatement(RewardsStatements.CREATE_REWARDS_TABLE).executeUpdate();
            connection.prepareStatement(DatakitsStatements.CREATE_DATAKITS_TABLE).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addKit(DataKitsJson dataKitsJson) {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DatakitsStatements.INSERT_KIT);

            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, dataKitsJson.getName());
            preparedStatement.setString(3, JsonManager.INSTANCE.createJsonKit(dataKitsJson));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadKitsIntoCache() {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DatakitsStatements.LOAD_KIT_CACHE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String kitName = resultSet.getString("kit_name");
                String content = resultSet.getString("content");

                kitJSONCache.put(kitName, content);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        kitJSONCache.forEach((kitName, content) -> {
            DataKits data = new DataKits();
            data.setName(kitName);
            try {
                data.setIcon(Serialization.INSTANCE.deserializeAndDecodeItemStack(JsonManager.INSTANCE.reader.readValue(content, DataKitsJson.class).getIcon()));
                data.setItems(Serialization.INSTANCE.deserializeAndDecodeItemStackList(JsonManager.INSTANCE.reader.readValue(content, DataKitsJson.class).getItems()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            dataKits.put(kitName, data);
        });
    }

    public void saveKitsFromCache() {
        try(Connection connection = getConnection()) {
            PreparedStatement insertStatement = connection.prepareStatement(DatakitsStatements.INSERT_KIT);
            PreparedStatement updateStatement = connection.prepareStatement(DatakitsStatements.UPDATE_KIT);
            PreparedStatement selectContentStatement = connection.prepareStatement(DatakitsStatements.SELECT_CONTENT);
            PreparedStatement deleteStatement = connection.prepareStatement(DatakitsStatements.DELETE_KIT);

            for (Map.Entry<String, DataKits> entry : dataKits.entrySet()) {
                String kitName = entry.getKey();
                DataKits datakit = entry.getValue();

                DataKitsJson dataKitsJson = new DataKitsJson();
                dataKitsJson.setName(kitName);
                dataKitsJson.setIcon(Serialization.INSTANCE.serializeAndEncodeItemStack(datakit.getIcon()));

                List<String> itemStackJsonList = new ArrayList<>();
                datakit.getItems().forEach(itemStack -> {
                    itemStackJsonList.add(Serialization.INSTANCE.serializeAndEncodeItemStack(itemStack));
                });

                dataKitsJson.setItems(itemStackJsonList);

                String content = JsonManager.INSTANCE.createJsonKit(dataKitsJson);

                if (kitExistsAndChangedInDatabase(kitName, content, selectContentStatement)) {
                    updateStatement.setString(1, content);
                    updateStatement.setString(2, kitName);
                    updateStatement.executeUpdate();

                } else if (!kitExistsInDatabase(kitName, selectContentStatement)) {
                    insertStatement.setString(1, UUID.randomUUID().toString());
                    insertStatement.setString(2, kitName);
                    insertStatement.setString(3, content);
                    insertStatement.executeUpdate();
                }
            }

            deleteKitsNotInCache(connection, deleteStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean kitExistsAndChangedInDatabase(String kitName, String newContent, PreparedStatement selectContentStatement) throws SQLException {
        selectContentStatement.setString(1, kitName);
        ResultSet resultSet = selectContentStatement.executeQuery();

        if (resultSet.next()) {
            String existingContent = resultSet.getString("content");
            return !existingContent.equals(newContent);
        }

        return false;
    }

    private boolean kitExistsInDatabase(String kitName, PreparedStatement selectContentStatement) throws SQLException {
        selectContentStatement.setString(1, kitName);
        ResultSet resultSet = selectContentStatement.executeQuery();
        return resultSet.next();
    }

    private void deleteKitsNotInCache(Connection connection, PreparedStatement deleteStatement) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement(DatakitsStatements.SELECT_KIT_NAME);
        ResultSet resultSet = selectStatement.executeQuery();

        while (resultSet.next()) {
            String kitName = resultSet.getString("kit_name");
            if (!dataKits.containsKey(kitName)) {
                deleteStatement.setString(1, kitName);
                deleteStatement.executeUpdate();
            }
        }
    }

    public void loadRewardsIntoCache() {
        try(Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(RewardsStatements.LOAD_REWARDS_CACHE);
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));

                String rewardsJson = resultSet.getString("rewards");
                HashMap<String, Integer> playerRewards = JsonManager.INSTANCE.reader.readValue(rewardsJson, RewardsJson.class).getRewards();

                rewards.put(playerUUID, playerRewards);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRewardsFromCache() {
        try(Connection connection = getConnection()) {
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














    private JsonObject rewardsJsonBuilder(UUID player_uuid) {
        JsonObject rewardsJsonObject = new JsonObject();
        RewardsManager.PLAYERS_REWARD_CACHE.get(player_uuid).forEach(rewardsJsonObject::addProperty);
        return rewardsJsonObject;
    }
}
