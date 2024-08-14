package mathano.mathano.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mathano.mathano.OBGiveAll;
import mathano.mathano.database.statements.DatakitsStatements;
import mathano.mathano.database.statements.RewardsStatements;
import mathano.mathano.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatabaseManager {

    public static HikariDataSource DATABASE;

    public static DatabaseManager INSTANCE;

    public DatabaseManager() {
        INSTANCE = this;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + Utils.getText("database", "url"));
        config.setUsername(Utils.getText("database", "username"));
        config.setPassword(Utils.getText("database", "password"));
        config.setDriverClassName("com.mysql.jdbc.Driver");

        DATABASE = new HikariDataSource(config);

        createTables();
        DataKitsManager.INSTANCE.loadKitsIntoCache();
        RewardsManager.INSTANCE.loadRewardsIntoCache();
        scheduleSave();
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

    public void scheduleSave() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            OBGiveAll.INSTANCE.getLogger().info(Utils.getText("schedule", "saveMessage"));
            DataKitsManager.INSTANCE.saveKitsFromCache();
            RewardsManager.INSTANCE.saveRewardsFromCache();
        }, ConfigManager.CONFIG.getConfigurationSection("schedule").getInt("initialDelay"), ConfigManager.CONFIG.getConfigurationSection("schedule").getInt("time"), TimeUnit.MINUTES);
    }
}
