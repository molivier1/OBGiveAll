package mathano.mathano.database.statements;

public class DatakitsStatements {
    public static final String CREATE_DATAKITS_TABLE = "CREATE TABLE IF NOT EXISTS datakits ("
            + "uuid             VARCHAR(36)         NOT NULL PRIMARY KEY,"
            + "kit_name         VARCHAR(30)         NOT NULL,"
            + "content          JSON                NOT NULL,"
            + "commands          JSON                NOT NULL);";

    public static final String LOAD_KIT_CACHE = "SELECT kit_name, content, commands FROM datakits;";

    public static final String INSERT_KIT = "INSERT INTO datakits VALUES (?, ?, ?, ?);";

    public static final String UPDATE_KIT = "UPDATE datakits SET content = ?, commands = ? WHERE kit_name = ?;";

    public static final String DELETE_KIT = "DELETE FROM datakits WHERE kit_name = ?;";

    public static final String SELECT_CONTENT_COMMANDS = "SELECT content, commands FROM datakits WHERE kit_name = ?;";

    public static final String SELECT_KIT_NAME = "SELECT kit_name FROM datakits;";
}
