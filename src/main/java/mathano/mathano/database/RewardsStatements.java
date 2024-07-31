package mathano.mathano.database;

public class RewardsStatements {
    public static final String CREATE_REWARDS_TABLE = "CREATE TABLE IF NOT EXISTS rewards ("
            + "player_uuid      VARCHAR(36)     NOT NULL PRIMARY KEY, "
            + "rewards          JSON            NOT NULL);";

            //+ "kits             VARCHAR(30)     NOT NULL FOREIGN KEY REFERENCES datakits(kit_name));";

    public static final String INSERT_JSON= "REPLACE INTO rewards (player_uuid) VALUES(?);";
}
