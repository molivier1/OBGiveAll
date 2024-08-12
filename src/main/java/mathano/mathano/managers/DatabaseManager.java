package mathano.mathano.managers;


import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mathano.mathano.database.DataKits;
import mathano.mathano.database.DatakitsStatements;
import mathano.mathano.database.RewardsStatements;
import mathano.mathano.database.jsondata.DataKitsJson;
import mathano.mathano.database.serialization.Serialization;
import mathano.mathano.utils.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {

    public static HikariDataSource DATABASE;

    public static DatabaseManager INSTANCE;

    public static Map<String, String> kitJSONCache = new HashMap<>();

    public static Map<String, DataKits> dataKits = new HashMap<>();

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
            PreparedStatement preparedStatement = connection.prepareStatement(DatakitsStatements.ADD_KIT);

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

    public String getKit(String kitName) {
        String kit = null;

        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(DatakitsStatements.GET_KIT);

            preparedStatement.setString(1, kitName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    kit = resultSet.getString("content");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kit;
    }

    private JsonObject rewardsJsonBuilder(UUID player_uuid) {
        JsonObject rewardsJsonObject = new JsonObject();
        RewardsManager.PLAYERS_REWARD_CACHE.get(player_uuid).forEach(rewardsJsonObject::addProperty);
        return rewardsJsonObject;
    }
}
