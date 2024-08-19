package mathano.mathano.managers;

import mathano.mathano.database.Logs;
import mathano.mathano.database.statements.LogsStatements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogsManager {
    public static LogsManager INSTANCE;

    public LogsManager() {
        INSTANCE = this;
    }

    public void insertLogs(Logs logs) {
        try(Connection connection = DatabaseManager.INSTANCE.getConnection()) {
            PreparedStatement insertLogsStatement = connection.prepareStatement(LogsStatements.INSERT_LOG);

            try {
                insertLogsStatement.setString(1, logs.getPlayer_uuid().toString());
                insertLogsStatement.setString(2, logs.getKit_name());
                insertLogsStatement.setTimestamp(3, logs.getTimestamp());
                insertLogsStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
