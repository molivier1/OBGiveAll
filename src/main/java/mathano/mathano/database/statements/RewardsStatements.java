package mathano.mathano.database.statements;

public class RewardsStatements {
    public static final String CREATE_REWARDS_TABLE = "CREATE TABLE IF NOT EXISTS rewards ("
            + "player_uuid      VARCHAR(36)     NOT NULL PRIMARY KEY, "
            + "rewards          JSON            NOT NULL);";

    public static final String INSERT_REWARDS = "INSERT INTO rewards (player_uuid, rewards) VALUES (?, ?) ON DUPLICATE KEY UPDATE rewards = ?;";

    public static final String SELECT_REWARDS = "SELECT rewards FROM rewards WHERE player_uuid = ?;";

    public static final String DELETE_REWARDS = "DELETE FROM rewards WHERE player_uuid = ?;";

    public static final String SELECT_PLAYER_UUID = "SELECT player_uuid FROM rewards;";

    public static final String LOAD_REWARDS_CACHE = "SELECT player_uuid, rewards FROM rewards;";
}
