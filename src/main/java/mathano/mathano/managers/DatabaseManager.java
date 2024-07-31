package mathano.mathano.managers;


import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mathano.mathano.database.DatakitsStatements;
import mathano.mathano.database.RewardsStatements;
import mathano.mathano.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {

    public static HikariDataSource DATABASE;

    public DatabaseManager() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + Utils.getText("database", "url"));
        config.setUsername(Utils.getText("database", "username"));
        config.setPassword(Utils.getText("database", "password"));
        config.setDriverClassName("com.mysql.jdbc.Driver");

        DATABASE = new HikariDataSource(config);

        Utils.asyncSingleTask(this::createTables);
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

    private JsonObject rewardsJsonBuilder(UUID player_uuid) {
        JsonObject rewardsJsonObject = new JsonObject();
        RewardsManager.PLAYERS_REWARD_CACHE.get(player_uuid).forEach(rewardsJsonObject::addProperty);
        return rewardsJsonObject;
    }
}
