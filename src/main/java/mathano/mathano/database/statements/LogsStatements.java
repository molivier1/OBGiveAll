package mathano.mathano.database.statements;

public class LogsStatements {
    public static final String CREATE_LOGS_TABLE = "CREATE TABLE IF NOT EXISTS logs ("
            + "id               INT UNSIGNED    NOT NULL AUTO_INCREMENT PRIMARY KEY, "
            + "player_uuid      VARCHAR(36)     NOT NULL, "
            + "reward_obtained  VARCHAR(30)     NOT NULL, "
            + "timestamp        TIMESTAMP       NOT NULL);";

    public static final String INSERT_LOG = "INSERT INTO logs (player_uuid, reward_obtained, timestamp) VALUES (?, ?, ?);";
}
